package com.ibm.bamoe.engine.adaptors.listeners;

import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.event.process.DefaultProcessEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessEventListener extends DefaultProcessEventListener {

    private static final Logger logger = LoggerFactory.getLogger(ProcessEventListener.class);

    public void beforeProcessStarted(ProcessStartedEvent event) {
        logger.debug("Before process started: process=" + event.getProcessInstance().toString());
    }

    public void afterProcessStarted(ProcessStartedEvent event) {
        logger.debug("After process started: process=" + event.getProcessInstance().toString());
    }

    public void beforeProcessCompleted(ProcessCompletedEvent event) {
        logger.debug("Before process completed: process=" + event.getProcessInstance().toString());
    }

    public void afterProcessCompleted(ProcessCompletedEvent event) {
        logger.debug("After process completed: process=" + event.getProcessInstance().toString());
    }

    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
        logger.debug("Before node left: node=" + event.getNodeInstance().toString());
    }

    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        logger.debug("After node left: node=" + event.getNodeInstance().toString());
    }

    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        logger.debug("Before node triggered: process=" + event.getProcessInstance().toString() + ", node=" + event.getNodeInstance().toString());
    }

    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
        logger.debug("After node triggered: process=" + event.getProcessInstance().toString() + ", node=" + event.getNodeInstance().toString());
    }

    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
        logger.debug("Before process variable changed: process=" + event.getProcessInstance().toString() + ", variableId=" + event.getVariableId().toString() + ", instanceId=" + event.getVariableInstanceId().toString() + ", oldValue=" + event.getOldValue().toString() + ", newValue=" + event.getNewValue().toString());
    }

    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        logger.debug("After process variable changed: process="  + event.getProcessInstance().toString() + ", variableId=" + event.getVariableId().toString() + ", instanceId=" +  event.getVariableInstanceId().toString() + ", oldValue=" + event.getOldValue().toString() + ", newValue=" + event.getNewValue().toString());
    }
}



