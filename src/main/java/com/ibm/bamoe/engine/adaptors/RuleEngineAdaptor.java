package com.ibm.bamoe.engine.adaptors;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.config.SmallRyeConfig;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.command.Command;
import org.kie.api.builder.ReleaseId;
import org.kie.util.maven.support.ReleaseIdImpl;
import org.kie.internal.command.CommandFactory;

import com.ibm.bamoe.engine.adaptors.model.ExecutionDuration;
import com.ibm.bamoe.engine.adaptors.model.RuleResults;
import com.ibm.bamoe.engine.adaptors.model.RuleSetProperties;
import com.ibm.bamoe.engine.adaptors.model.KieSessionType;
import com.ibm.bamoe.engine.adaptors.model.KieContainerType;
import com.ibm.bamoe.engine.adaptors.listeners.RuleEngineAgendaListener;
import com.ibm.bamoe.engine.adaptors.listeners.RuleEngineWorkingMemoryListener;
import com.ibm.bamoe.engine.adaptors.listeners.ProcessEventListener;

public class RuleEngineAdaptor {

    private static final Logger logger = LoggerFactory.getLogger(RuleEngineAdaptor.class);

    private static final String DATE_TIME_FORMAT        = "yyyy-MM-dd HH:mm:ss";
    private static final String KIE_BASE_NAME           = "kie-base.name";
    private static final String RELEASE_ID              = "release.id";
    private static final String KIE_SESSION_NAME        = "kie-session.name";
    private static final String KIE_SESSION_TYPE        = "kie-session.type";
    private static final String KIE_CONTAINER_TYPE      = "kie-container.type";
    private static final String RULEFLOW_NAME           = "ruleflow.name";
    private static final String ENABLE_AGENDA_LISTENER  = "enable.agenda.listener";
    private static final String ENABLE_WM_LISTENER      = "enable.working-memory.listener";
    private static final String ENABLE_PROCESS_LISTENER = "enable.process.listener";

    public RuleResults execute(final String ruleSetName, Map<String,Object> facts) throws Exception {

        // Load all the properties we need for execution
        logger.debug("Loading ruleset properties for: " + ruleSetName);

        // Load from various property files in the classpath 
        var smallRyeConfig = ConfigProvider.getConfig().unwrap(SmallRyeConfig.class);

        // Package as a set of properties for the ruleset
        RuleSetProperties properties = new RuleSetProperties();
        properties.setName(ruleSetName);
        properties.setReleaseId(smallRyeConfig.getValue(ruleSetName + "." + RELEASE_ID, String.class));
        properties.setKieBaseName(smallRyeConfig.getValue(ruleSetName + "." + KIE_BASE_NAME, String.class));
        properties.setKieSessionName(smallRyeConfig.getValue(ruleSetName + "." + KIE_SESSION_NAME, String.class));
        properties.setKieSessionType(smallRyeConfig.getValue(ruleSetName + "." + KIE_SESSION_TYPE, KieSessionType.class));
        properties.setKieContainerType(smallRyeConfig.getValue(ruleSetName + "." + KIE_CONTAINER_TYPE, KieContainerType.class));
        properties.setRuleFlowName(smallRyeConfig.getValue(ruleSetName + "." + RULEFLOW_NAME, String.class));
        properties.setRuleAgendaListenerEnabled(smallRyeConfig.getValue(ruleSetName + "." + ENABLE_AGENDA_LISTENER, Boolean.class));
        properties.setRuleWorkingMemoryListenerEnabled(smallRyeConfig.getValue(ruleSetName + "." + ENABLE_WM_LISTENER, Boolean.class));
        properties.setProcessListenerEnabled(smallRyeConfig.getValue(ruleSetName + "." + ENABLE_PROCESS_LISTENER, Boolean.class));

        return execute(properties, facts);
    }

    public RuleResults execute(final RuleSetProperties properties, Map<String,Object> facts) throws Exception {

        // Mark the start time
        LocalDateTime startedOn = LocalDateTime.now();
        logger.debug("Executing ruleset: name=" + properties.getName() + "...");

        // Create the kieSesion, kieContainer, and kieBase
        logger.debug("Creating KIE services and container objects for kieBase=" + properties.getKieBaseName()  + "...");
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = createKieContainer(kieServices, properties.getKieContainerType(), properties.getReleaseId());
        KieBase kieBase = kieContainer.getKieBase(properties.getKieBaseName());          

        // Prepare the facts for the engine
        logger.debug("Inserting facts into rule engine instance...");

        List<Command> commands = new ArrayList<Command>();
        for (Map.Entry<String, Object> fact : facts.entrySet()) {

            logger.debug(fact.getKey() + " -> " + fact.getValue());
            commands.add(CommandFactory.newInsert(fact.getValue(), fact.getKey()));
        }

        // Add a stateless workflow, if it exists
        if (properties.getRuleFlowName() != null && !properties.getRuleFlowName().equalsIgnoreCase("none")) {
            commands.add(CommandFactory.newStartProcess(properties.getRuleFlowName()));
        }

        // Add other batch commands
        commands.add(CommandFactory.newFireAllRules());        

        // Execute the session
        ExecutionResults executionResults = executeSession(kieContainer, properties, commands);

        // Mark completion time        
        LocalDateTime completedOn = LocalDateTime.now();

        // Report
        ExecutionDuration duration = calculateExecutionDuration(startedOn, completedOn);
        logger.debug("Rule Execution started: " + startedOn + ", ended: " + completedOn + ", duration: " + duration + "...");

        // Prepare the execution results
        RuleResults results = new RuleResults();
        results.setStartedOn(formatLocalDateTime(startedOn));
        results.setCompletedOn(formatLocalDateTime(completedOn));
        results.setExecutionDuration(duration);

        // Add the updated facts
        results.getFacts().add(facts);
        logger.debug("Rule Execution Results: " + results);

        // Return the results
        return results;
    }

    private ExecutionResults executeSession(final KieContainer kieContainer, final RuleSetProperties properties, List<Command> commands) throws Exception {

        logger.debug("Creating KIE session: name=" + properties.getKieSessionName() + ", type=" + properties.getKieSessionType() + "...");
        ExecutionResults results = null;

        // Stateless sessions are the default
        if (properties.getKieSessionType() == KieSessionType.STATELESS) {

            StatelessKieSession kieSession = kieContainer.newStatelessKieSession(properties.getKieSessionName());

            // Add event listeners
            if (properties.isRuleAgendaListenerEnabled()) {

                logger.debug("Attaching rule engine agenda listener...");
                kieSession.addEventListener(new RuleEngineAgendaListener());
            }

            if (properties.isRuleWorkingMemoryListenerEnabled()) {

                logger.debug("Attaching rule engine working memory listener...");
                kieSession.addEventListener(new RuleEngineWorkingMemoryListener());
            }

            if (properties.isProcessListenerEnabled()) {

                logger.debug("Attaching process listener...");
                kieSession.addEventListener(new ProcessEventListener());
            }

            // Execute the rules
            logger.debug("Executing ruleset...");
            results = kieSession.execute(CommandFactory.newBatchExecution(commands));

        } else if (properties.getKieSessionType() == KieSessionType.STATEFUL) {

            KieSession kieSession = kieContainer.newKieSession(properties.getKieSessionName());

            // Add event listeners
            if (properties.isRuleAgendaListenerEnabled()) {

                logger.debug("Attaching rule engine agenda listener...");
                kieSession.addEventListener(new RuleEngineAgendaListener());
            }

            if (properties.isRuleWorkingMemoryListenerEnabled()) {

                logger.debug("Attaching rule engine working memory listener...");
                kieSession.addEventListener(new RuleEngineWorkingMemoryListener());
            }

            if (properties.isProcessListenerEnabled()) {

                logger.debug("Attaching process listener...");
                kieSession.addEventListener(new ProcessEventListener());
            }

            // Execute the rules
            logger.debug("Executing ruleset...");
            results = kieSession.execute(CommandFactory.newBatchExecution(commands));

            // Cleanup the kieSession
            kieSession.dispose();
        } else {
            throw new Exception("Unsupported KIE Session type: " + properties.getKieSessionType());
        }

        return results;
    }

    private KieContainer createKieContainer(final KieServices kieServices, final KieContainerType kieContainerType, final String releaseId) throws Exception {

        KieContainer kieContainer = null;

        if (kieContainerType == KieContainerType.CLASSPATH) {

            logger.debug("Creating classpath container...");
            kieContainer = kieServices.getKieClasspathContainer();        
        } else if (kieContainerType == KieContainerType.RELEASE_ID) {

            logger.debug("Creating releaseId container...");
            kieContainer = kieServices.newKieContainer(new ReleaseIdImpl(releaseId));
        } else {
           throw new Exception("Unsupported KIE container type: " + kieContainerType);
        }

        return kieContainer;
    }

    private ExecutionDuration calculateExecutionDuration(LocalDateTime begin, LocalDateTime end) {

        ExecutionDuration ed = new ExecutionDuration();
        ed.setDays(ChronoUnit.DAYS.between(begin, end));
        ed.setHours(ChronoUnit.HOURS.between(begin, end));
        ed.setMinutes(ChronoUnit.MINUTES.between(begin, end));
        ed.setSeconds(ChronoUnit.SECONDS.between(begin, end));
        ed.setMilliseconds(ChronoUnit.MILLIS.between(begin, end));
        return ed;
   }

   private String formatLocalDateTime(LocalDateTime ldt) {

       DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
       return ldt.format(formatter);
   }
}
