package com.adobe.target.edge.client.model;

import java.util.Map;

public class LocalDecisioningRule {

    private Map<String, Object> condition;
    private Map<String, Object> consequence;
    private Map<String, Object> meta;

    public LocalDecisioningRule() {
    }

    public LocalDecisioningRule(Map<String, Object> condition, Map<String, Object> consequence, Map<String, Object> meta) {
        this.condition = condition;
        this.consequence = consequence;
        this.meta = meta;
    }

    public Map<String, Object> getCondition() {
        return condition;
    }

    public void setCondition(Map<String, Object> condition) {
        this.condition = condition;
    }

    public Map<String, Object> getConsequence() {
        return consequence;
    }

    public void setConsequence(Map<String, Object> consequence) {
        this.consequence = consequence;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, Object> meta) {
        this.meta = meta;
    }

    @Override
    public String toString() {
        return "LocalDecisioningRule{" +
                "condition=" + condition +
                ",consequence=" + consequence +
                ",meta=" + meta +
                '}';
    }
}
