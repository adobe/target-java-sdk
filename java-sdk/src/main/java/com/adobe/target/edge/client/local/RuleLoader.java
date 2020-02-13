package com.adobe.target.edge.client.local;

import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.model.LocalDecisioningRuleSet;

public interface RuleLoader {

    void start(ClientConfig clientConfig);

    void stop();

    LocalDecisioningRuleSet getLatestRules();

}
