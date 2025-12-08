package com.ibm.bamoe.engine.adaptors.listeners;

import org.kie.dmn.api.core.event.DMNRuntimeEventListener;

import org.kie.dmn.api.core.event.AfterEvaluateAllEvent;
import org.kie.dmn.api.core.event.AfterEvaluateContextEntryEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateAllEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateContextEntryEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionTableEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecisionModelEventListener implements DMNRuntimeEventListener {

    private static final Logger logger = LoggerFactory.getLogger(DecisionModelEventListener.class);

    public void cleanup() {
    }

    @Override
    public void beforeEvaluateDecision(BeforeEvaluateDecisionEvent event) {
        logger.debug("beforeEvaluateDecision: {}" + event);
    }

    @Override
    public void afterEvaluateDecision(AfterEvaluateDecisionEvent event) {
        logger.debug("beforeEvaluateDecision: {}" + event);
    }

    @Override
    public void beforeEvaluateContextEntry(BeforeEvaluateContextEntryEvent event) {
        logger.debug("afterEvaluateDecision: {}" + event);
    }

    @Override
    public void afterEvaluateContextEntry(AfterEvaluateContextEntryEvent event) {
        logger.debug("afterEvaluateContextEntry: {}" + event);
    }

    @Override
    public void beforeEvaluateDecisionTable(BeforeEvaluateDecisionTableEvent event) {
        logger.debug("beforeEvaluateDecisionTable: {}" + event);
    }

    @Override
    public void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {
        logger.debug("afterEvaluateDecisionTable: {}" + event);
    }

    @Override
    public void beforeEvaluateAll(BeforeEvaluateAllEvent event) {
        logger.debug("beforeEvaluateAll: {}" + event);
    }

    @Override
    public void afterEvaluateAll(AfterEvaluateAllEvent event) {
        logger.debug("afterEvaluateAll: {}" + event);
    }
}



