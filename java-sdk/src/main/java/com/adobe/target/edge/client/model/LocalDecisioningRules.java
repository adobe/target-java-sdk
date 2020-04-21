package com.adobe.target.edge.client.model;

import java.util.List;
import java.util.Map;

public class LocalDecisioningRules {
    private Map<String, List<LocalDecisioningRule>> mboxes;
    private Map<String, List<LocalDecisioningRule>> views;

    public Map<String, List<LocalDecisioningRule>> getMboxes() {
        return mboxes;
    }

    public Map<String, List<LocalDecisioningRule>> getViews() {
        return views;
    }

    @Override
    public String toString() {
        return "LocalDecisioningRules{" +
                "mboxes=" + mboxes +
                ", views=" + views +
                '}';
    }
}
