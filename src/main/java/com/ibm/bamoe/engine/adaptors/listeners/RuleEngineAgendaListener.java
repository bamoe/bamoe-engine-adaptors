package com.ibm.bamoe.engine.adaptors.listeners;

import org.drools.core.event.DefaultAgendaEventListener;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleEngineAgendaListener extends DefaultAgendaEventListener {

    private static final Logger logger = LoggerFactory.getLogger(RuleEngineAgendaListener.class);

    public void beforeMatchFired(BeforeMatchFiredEvent event) {
        logger.debug("Firing Rule: rule=" + event.getMatch().getRule().getName());
	}

    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
        logger.debug("Agenda group popped from agenda: agendaGroup=" + event.getAgendaGroup().getName());
	}

    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
        logger.debug("Agenda group pushed to agenda: agendaGroup=" + event.getAgendaGroup().getName());
	}

    public void matchCreated(MatchCreatedEvent event) {
        logger.debug("Rule added to agenda: rule=" + event.getMatch().getRule().getName());
	}

    public void matchCancelled(MatchCancelledEvent event) {
        logger.debug("Rule removed from agenda: rule=" + event.getMatch().getRule().getName());
	}
}



