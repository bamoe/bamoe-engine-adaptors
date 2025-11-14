package com.ibm.bamoe.engine.adaptors;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
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
import org.kie.api.builder.ReleaseId;
import org.kie.util.maven.support.ReleaseIdImpl;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;

import com.ibm.bamoe.engine.adaptors.model.ExecutionDuration;
import com.ibm.bamoe.engine.adaptors.model.DecisionResult;
import com.ibm.bamoe.engine.adaptors.model.DecisionModelResults;
import com.ibm.bamoe.engine.adaptors.model.DecisionModelProperties;
import com.ibm.bamoe.engine.adaptors.model.KieContainerType;
import com.ibm.bamoe.engine.adaptors.listeners.DecisionModelEventListener;

public class DecisionModelAdaptor {

    private static final Logger logger = LoggerFactory.getLogger(DecisionModelAdaptor.class);

    private static final String DATE_TIME_FORMAT    = "yyyy-MM-dd HH:mm:ss";
    private static final String KIE_BASE_NAME       = "kie-base.name";
    private static final String KIE_CONTAINER_TYPE  = "kie-container.type";
    private static final String RELEASE_ID          = "release.id";
    private static final String DMN_MODEL_NAME      = "dmn.model.name";
    private static final String DMN_MODEL_NAMESPACE = "dmn.model.namespace";
    private static final String ENABLE_LISTENER     = "enable.listener";

    public DecisionModelResults execute(final String decisionModelName, Map<String,Object> facts) throws Exception {

        // Load all the properties we need for execution
        logger.debug("Loading decision model properties for: ..." + decisionModelName);

        // Load from various property files in the classpath 
        var smallRyeConfig = ConfigProvider.getConfig().unwrap(SmallRyeConfig.class);

        // Package as a set of properties for the decision model
        DecisionModelProperties properties = new DecisionModelProperties();
        properties.setName(decisionModelName);
        properties.setReleaseId(smallRyeConfig.getValue(decisionModelName + "." + RELEASE_ID, String.class));
        properties.setKieBaseName(smallRyeConfig.getValue(decisionModelName + "." + KIE_BASE_NAME, String.class));
        properties.setKieContainerType(smallRyeConfig.getValue(decisionModelName + "." + KIE_CONTAINER_TYPE, KieContainerType.class));
        properties.setModelName(smallRyeConfig.getValue(decisionModelName + "." + DMN_MODEL_NAME, String.class));
        properties.setModelNamespace(smallRyeConfig.getValue(decisionModelName + "." + DMN_MODEL_NAMESPACE, String.class));
        properties.setListenerEnabled(smallRyeConfig.getValue(decisionModelName + "." + ENABLE_LISTENER, Boolean.class));

        return execute(properties, facts);
    }

    public DecisionModelResults execute(final DecisionModelProperties properties, Map<String,Object> facts) throws Exception {

        // Mark the start time
        LocalDateTime startedOn = LocalDateTime.now();
        logger.debug("Executing decision model: name=" + properties.getName() + "...");

        // Create the KIE session and classpath container
        logger.debug("\r\nCreating kie session and classpath container, using kieBase=" + properties.getKieBaseName()  + "...");
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = createKieContainer(kieServices, properties.getKieContainerType(), properties.getReleaseId());
        KieBase kieBase = kieContainer.getKieBase(properties.getKieBaseName());          

        // Obtain the DMN runtime from the KIE container and reference to the DMN model
        logger.debug("\r\nObtaining the DMN runtime, using kieBase=" + properties.getKieBaseName() + "...");
        DMNRuntime runtime = KieRuntimeFactory.of(kieBase).get(DMNRuntime.class);
        DMNModel model = runtime.getModel(properties.getModelNamespace(), properties.getModelName());

        // Add an event listener
        if (properties.isListenerEnabled()) {

            logger.debug("--> Attaching event listener...");
            runtime.addListener(new DecisionModelEventListener());
        }

        // Setting the DMN context
        logger.debug("\r\nSetting the DMN context...");
        DMNContext context = runtime.newContext();

        // Add facts to the DMN context
        for (Map.Entry<String, Object> fact : facts.entrySet()) {

            logger.debug(fact.getKey() + " -> " + fact.getValue());
            context.set(fact.getKey(), fact.getValue());
        }

        // Execute the decision model
        logger.debug("\r\nExecuting the decision model...");
        DMNResult results = runtime.evaluateAll(model, context);

        // Mark completion time        
        LocalDateTime completedOn = LocalDateTime.now();

        // Report
        ExecutionDuration duration = calculateExecutionDuration(startedOn, completedOn);
        logger.debug("Decision Model Execution started: " + startedOn + ", ended: " + completedOn + ", duration: " + duration + "...");

        // Prepare the execution results
        DecisionModelResults executionResults = new DecisionModelResults();
        executionResults.setStartedOn(formatLocalDateTime(startedOn));
        executionResults.setCompletedOn(formatLocalDateTime(completedOn));
        executionResults.setExecutionDuration(duration);

        // Format the decision results into the execution results
        for (DMNDecisionResult result : results.getDecisionResults()) {  

            DecisionResult dr = new DecisionResult();
            dr.setDecision(result.getDecisionName());
            dr.setResult(result.getResult());
            executionResults.getResults().add(dr);
        }

        // Return execution results
        return executionResults;
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
