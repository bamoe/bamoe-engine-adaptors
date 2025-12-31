package com.ibm.bamoe.engine.adaptors.listeners;

import java.util.List;
import java.util.ArrayList;

import org.drools.core.event.DefaultAgendaEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleEngineTraceListener extends DefaultAgendaEventListener {

    private static final Logger logger = LoggerFactory.getLogger(RuleEngineTraceListener.class);

    private List<String> rulesFired = new ArrayList<String>();

    public List<String> getRulesFired() {
        return this.rulesFired;
    }

    public void setRulesFired(List<String> rulesFired) {
        this.rulesFired = rulesFired;
    }

    public void afterMatchFired(AfterMatchFiredEvent event) {

        logger.debug("Firing Rule: rule=" + event.getMatch().getRule().getName());
		rulesFired.add(event.getMatch().getRule().getName());
	}
}



