package com.ibm.bamoe.engine.adaptors.model;

import java.util.List;

public class RuleSetProperties {

    private String name;
    private String releaseId;
    private String kieBaseName;
    private String kieSessionName;
    private KieSessionType kieSessionType = KieSessionType.STATELESS;
    private KieContainerType kieContainerType = KieContainerType.CLASSPATH;
    private String ruleFlowName;
    private boolean ruleAgendaListenerEnabled = false;
    private boolean ruleWorkingMemoryListenerEnabled = false;
    private boolean processListenerEnabled = false;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReleaseId() {
        return this.releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getKieBaseName() {
        return this.kieBaseName;
    }

    public void setKieBaseName(String kieBaseName) {
        this.kieBaseName = kieBaseName;
    }

    public String getKieSessionName() {
        return this.kieSessionName;
    }

    public void setKieSessionName(String kieSessionName) {
        this.kieSessionName = kieSessionName;
    }

    public KieSessionType getKieSessionType() {
        return this.kieSessionType;
    }

    public void setKieSessionType(KieSessionType kieSessionType) {
        this.kieSessionType = kieSessionType;
    }

    public KieContainerType getKieContainerType() {
        return this.kieContainerType;
    }

    public void setKieContainerType(KieContainerType kieContainerType) {
        this.kieContainerType = kieContainerType;
    }

    public String getRuleFlowName() {
        return this.ruleFlowName;
    }

    public void setRuleFlowName(String ruleFlowName) {
        this.ruleFlowName = ruleFlowName;
    }

    public boolean isRuleAgendaListenerEnabled() {
        return this.ruleAgendaListenerEnabled;
    }

    public boolean getRuleAgendaListenerEnabled() {
        return this.ruleAgendaListenerEnabled;
    }

    public void setRuleAgendaListenerEnabled(boolean ruleAgendaListenerEnabled) {
        this.ruleAgendaListenerEnabled = ruleAgendaListenerEnabled;
    }

    public boolean isRuleWorkingMemoryListenerEnabled() {
        return this.ruleWorkingMemoryListenerEnabled;
    }

    public boolean getRuleWorkingMemoryListenerEnabled() {
        return this.ruleWorkingMemoryListenerEnabled;
    }

    public void setRuleWorkingMemoryListenerEnabled(boolean ruleWorkingMemoryListenerEnabled) {
        this.ruleWorkingMemoryListenerEnabled = ruleWorkingMemoryListenerEnabled;
    }

    public boolean isProcessListenerEnabled() {
        return this.processListenerEnabled;
    }

    public boolean getProcessListenerEnabled() {
        return this.processListenerEnabled;
    }

    public void setProcessListenerEnabled(boolean processListenerEnabled) {
        this.processListenerEnabled = processListenerEnabled;
    }
    
    @Override
    public String toString() {
        return "[RuleSetProperties: name=" + name + ", releaseId=" + releaseId + ", kieBaseName=" + kieBaseName + ", kieSessionName=" + kieSessionName + ", kieSessionType=" + kieSessionType + ", kieContainerType=" + kieContainerType + ", ruleFlowName=" + ruleFlowName + ", ruleAgendaListenerEnabled=" + ruleAgendaListenerEnabled + ", ruleWorkingMemoryListenerEnabled=" + ruleWorkingMemoryListenerEnabled + ", processListenerEnabled=" + processListenerEnabled + "]";
    }
}