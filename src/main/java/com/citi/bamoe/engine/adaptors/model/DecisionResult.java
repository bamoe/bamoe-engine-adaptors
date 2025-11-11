package com.citi.bamoe.engine.adaptors.model;

public class DecisionResult {

    private String decision;
    private Object result;

    public String getDecision() {
        return this.decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public Object getResult() {
        return this.result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "[decision name=" + decision + ", result=" + result + "]";
    }
}