package com.ibm.bamoe.engine.adaptors.listeners;

import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleEngineWorkingMemoryListener implements RuleRuntimeEventListener {

    private static final Logger logger = LoggerFactory.getLogger(RuleEngineWorkingMemoryListener.class);

    public void objectInserted(ObjectInsertedEvent event) {
        logger.debug("Fact inserted: type=" + event.getObject().getClass() + ": " + event.getObject().toString());
    }

    public void objectUpdated(ObjectUpdatedEvent event) {
        logger.debug("Fact updated by rule=" + event.getRule().getName() + ", type=" + event.getObject().getClass() + ": " + event.getObject().toString());
    }

    public void objectDeleted(ObjectDeletedEvent event) {
        logger.debug("Fact deleted: type=" + event.getOldObject().getClass() + ": " + event.getOldObject().getClass().toString());
    }
}



