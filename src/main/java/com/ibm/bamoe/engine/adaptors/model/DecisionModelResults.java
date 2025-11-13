package com.ibm.bamoe.engine.adaptors.model;

import java.util.List;
import java.util.ArrayList;

public class DecisionModelResults {

    private String startedOn;
    private String completedOn;
    private ExecutionDuration executionDuration;
    private List<DecisionResult> results = new ArrayList<DecisionResult>();

    public String getStartedOn() {
        return this.startedOn;
    }

    public void setStartedOn(String startedOn) {
        this.startedOn = startedOn;
    }

    public String getCompletedOn() {
        return this.completedOn;
    }

    public void setCompletedOn(String completedOn) {
        this.completedOn = completedOn;
    }

    public ExecutionDuration getExecutionDuration() {
        return this.executionDuration;
    }

    public void setExecutionDuration(ExecutionDuration executionDuration) {
        this.executionDuration = executionDuration;
    }

    public List<DecisionResult> getResults() {
        return this.results;
    }

    public void setResults(List<DecisionResult> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "startedOn=" + startedOn + ", completedOn=" + completedOn + ", executionDuration=" + executionDuration + ", results=" + getResults();
    }
}