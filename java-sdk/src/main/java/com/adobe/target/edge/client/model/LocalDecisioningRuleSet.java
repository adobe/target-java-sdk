/*
 * Copyright 2019 Adobe. All rights reserved.
 * This file is licensed to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.adobe.target.edge.client.model;

import java.util.List;
import java.util.Map;

public class LocalDecisioningRuleSet {

    private String version;
    private String globalMbox;
    private List<String> remoteMboxes;
    private List<String> responseTokens;
    private LocalDecisioningRules rules;
    private Map<String, Object> meta;

    public LocalDecisioningRuleSet() { }

    public String getVersion() {
        return version;
    }

    public String getGlobalMbox() {
        return globalMbox;
    }

    public List<String> getRemoteMboxes() {
        return remoteMboxes;
    }

    public List<String> getResponseTokens() {
        return responseTokens;
    }

    public LocalDecisioningRules getRules() {
        return rules;
    }

    public Map<String, Object> getMeta() { return meta; }

    @Override
    public String toString() {
        return "LocalDecisioningRuleSet{" +
                "version='" + version + '\'' +
                ", globalMbox='" + globalMbox + '\'' +
                ", remoteMboxes=" + remoteMboxes +
                ", responseTokens=" + responseTokens +
                ", rules=" + rules +
                ", meta=" + meta +
                '}';
    }

}
