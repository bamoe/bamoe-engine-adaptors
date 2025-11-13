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
import com.ibm.bamoe.engine.adaptors.listeners.RuleEngineAgendaListener;
import com.ibm.bamoe.engine.adaptors.listeners.RuleEngineWorkingMemoryListener;
import com.ibm.bamoe.engine.adaptors.listeners.ProcessEventListener;

public class RuleEngineAdaptor {

    private static final Logger logger = LoggerFactory.getLogger(RuleEngineAdaptor.class);

    private static final String DATE_TIME_FORMAT        = "yyyy-MM-dd HH:mm:ss";
    private static final String KIE_BASE_NAME           = "kiebase.name";
    private static final String KIE_SESSION_NAME        = "kiesession.name";
    private static final String KIE_SESSION_TYPE        = "kiesession.type";
    private static final String RULEFLOW_NAME           = "ruleflow.name";
    private static final String EXECUTION_MODE          = "execution.mode";
    private static final String ENABLE_AGENDA_LISTENER  = "enable.agenda.listener";
    private static final String ENABLE_WM_LISTENER      = "enable.working-memory.listener";
    private static final String ENABLE_PROCESS_LISTENER = "enable.process.listener";

    public RuleResults execute(final String ruleSet, Map<String,Object> facts) throws Exception {

        // Load all the properties we need for execution
        logger.debug("Loading ruleset properties...");

        // Load from various property files in the classpath 
        var smallRyeConfig = ConfigProvider.getConfig().unwrap(SmallRyeConfig.class);
        String kieBaseName = smallRyeConfig.getValue(ruleSet + "." + KIE_BASE_NAME, String.class);
        String kieSessionName = smallRyeConfig.getValue(ruleSet + "." + KIE_SESSION_NAME, String.class);
        String kieSessionType = smallRyeConfig.getValue(ruleSet + "." + KIE_SESSION_TYPE, String.class);
        String executionMode = smallRyeConfig.getValue(ruleSet + "." + EXECUTION_MODE, String.class);
        String ruleFlowName = smallRyeConfig.getValue(ruleSet + "." + RULEFLOW_NAME, String.class);
        boolean enableAgendaListener = smallRyeConfig.getValue(ruleSet + "." + ENABLE_AGENDA_LISTENER, Boolean.class);
        boolean enableWorkingMemoryListener = smallRyeConfig.getValue(ruleSet + "." + ENABLE_WM_LISTENER, Boolean.class);
        boolean enableProcessListener = smallRyeConfig.getValue(ruleSet + "." + ENABLE_PROCESS_LISTENER, Boolean.class);

        return execute(ruleSet, ruleFlowName, executionMode, kieBaseName, kieSessionName, kieSessionType, facts, enableAgendaListener, enableWorkingMemoryListener, enableProcessListener);
    }

    public RuleResults execute(final String ruleSet, final String ruleFlowName, final String executionMode, final String kieBaseName, final String kieSessionName, final String kieSessionType, Map<String,Object> facts, final boolean enableAgendaListener, final boolean enableWorkingMemoryListener, final boolean enableProcessListener) throws Exception {

        // Mark the start time
        LocalDateTime startedOn = LocalDateTime.now();
        logger.debug("Executing ruleset: name=" + ruleSet + "...");

        // Create the kieSesion, kieContainer, and kieBase
        logger.debug("Creating KIE services and container objects for kieBase=" + kieBaseName  + "...");
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = createKieContainer(kieServices, executionMode);
        KieBase kieBase = kieContainer.getKieBase(kieBaseName);          

        // Prepare the facts for the engine
        logger.debug("Inserting facts into rule engine instance...");

        List<Command> commands = new ArrayList<Command>();
        for (Map.Entry<String, Object> fact : facts.entrySet()) {

            logger.debug(fact.getKey() + " -> " + fact.getValue());
            commands.add(CommandFactory.newInsert(fact.getValue(), fact.getKey()));
        }

        // Add a stateless workflow, if it exists
        if (ruleFlowName != null && !ruleFlowName.equalsIgnoreCase("none")) {
            commands.add(CommandFactory.newStartProcess(ruleFlowName));
        }

        // Add other batch commands
        commands.add(CommandFactory.newFireAllRules());        

        // Execute the session
        ExecutionResults executionResults = executeSession(kieContainer, kieSessionName, kieSessionType, commands, enableAgendaListener, enableWorkingMemoryListener, enableProcessListener);

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

    private ExecutionResults executeSession(final KieContainer kieContainer, final String kieSessionName, final String kieSessionType, List<Command> commands, final boolean enableAgendaListener, final boolean enableWorkingMemoryListener, final boolean enableProcessListener) {

        logger.debug("Creating KIE session: name=" + kieSessionName + ", type=" + kieSessionType + "...");
        ExecutionResults results = null;

        // Stateful sessions are the default
        if (kieSessionType == null || kieSessionType.isEmpty() || kieSessionType.equalsIgnoreCase("stateful")) {

            KieSession kieSession = kieContainer.newKieSession(kieSessionName);

            // Add event listeners
            if (enableAgendaListener) {

                logger.debug("Attaching rule engine agenda listener...");
                kieSession.addEventListener(new RuleEngineAgendaListener());
            }

            if (enableWorkingMemoryListener) {

                logger.debug("Attaching rule engine working memory listener...");
                kieSession.addEventListener(new RuleEngineWorkingMemoryListener());
            }

            if (enableProcessListener) {

                logger.debug("Attaching process listener...");
                kieSession.addEventListener(new ProcessEventListener());
            }

            // Execute the rules
            logger.debug("Executing ruleset...");
            results = kieSession.execute(CommandFactory.newBatchExecution(commands));

            // Cleanup the kieSession
            kieSession.dispose();
        } else {

            StatelessKieSession kieSession = kieContainer.newStatelessKieSession(kieSessionName);

            // Add event listeners
            if (enableAgendaListener) {

                logger.debug("Attaching rule engine agenda listener...");
                kieSession.addEventListener(new RuleEngineAgendaListener());
            }

            if (enableWorkingMemoryListener) {

                logger.debug("Attaching rule engine working memory listener...");
                kieSession.addEventListener(new RuleEngineWorkingMemoryListener());
            }

            if (enableProcessListener) {

                logger.debug("Attaching process listener...");
                kieSession.addEventListener(new ProcessEventListener());
            }

            // Execute the rules
            logger.debug("Executing ruleset...");
            results = kieSession.execute(CommandFactory.newBatchExecution(commands));
        }

        return results;
    }

    private KieContainer createKieContainer(final KieServices kieServices, final String executionMode) {

        KieContainer kieContainer = null;

        if (executionMode == null || executionMode.isEmpty() || executionMode.equalsIgnoreCase("classpath")) {
            logger.debug("Creating classpath container...");
            kieContainer = kieServices.getKieClasspathContainer();        
        } else {
            logger.debug("Creating releaseId container...");
            ReleaseId releaseId = new ReleaseIdImpl(executionMode);
            kieContainer = kieServices.newKieContainer(releaseId);
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
