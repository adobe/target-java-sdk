package com.adobe.target.edge.client.model;

import java.util.List;
import java.util.Map;

public class LocalDecisioningRuleSet {

    private String version;
    private List<LocalDecisioningRule> rules;
    private Map<String, Object> meta;

    public LocalDecisioningRuleSet() { }

    public String getVersion() {
        return version;
    }

    public List<LocalDecisioningRule> getRules() {
        return rules;
    }

    public Map<String, Object> getMeta() { return meta; }

    @Override
    public String toString() {
        return "LocalDecisioningRuleSet{" +
                "version='" + version + "'" +
                ", meta=" + meta +
                ", rules=" + rules +
                '}';
    }

}
