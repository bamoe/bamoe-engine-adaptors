package com.ibm.bamoe.engine.adaptors.model;

import java.util.List;

public class DecisionModelProperties {

    private String name;
    private String releaseId;
    private String kieBaseName;
    private KieContainerType kieContainerType = KieContainerType.CLASSPATH;
    private String modelNamespace;
    private String modelName;
    private boolean listenerEnabled = false;

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

    public KieContainerType getKieContainerType() {
        return this.kieContainerType;
    }

    public void setKieContainerType(KieContainerType kieContainerType) {
        this.kieContainerType = kieContainerType;
    }

    public String getModelNamespace() {
        return this.modelNamespace;
    }

    public void setModelNamespace(String modelNamespace) {
        this.modelNamespace = modelNamespace;
    }

    public String getModelName() {
        return this.modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public boolean isListenerEnabled() {
        return this.listenerEnabled;
    }

    public boolean getListenerEnabled() {
        return this.listenerEnabled;
    }

    public void setListenerEnabled(boolean listenerEnabled) {
        this.listenerEnabled = listenerEnabled;
    }

    public String toString() {
        return "[RuleSetProperties: name=" + name + ", releaseId=" + releaseId + ", kieBaseName=" + kieBaseName + ", kieContainerType=" + kieContainerType + ", modelNamespace=" + modelNamespace + ", modelName=" + modelName + ", listenerEnabled=" + listenerEnabled + "]";
    }
}