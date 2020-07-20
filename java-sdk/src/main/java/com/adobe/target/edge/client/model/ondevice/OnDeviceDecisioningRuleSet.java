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
package com.adobe.target.edge.client.model.ondevice;

import java.util.List;
import java.util.Map;

public class OnDeviceDecisioningRuleSet {

    private String version;
    private String globalMbox;
    private boolean geoTargetingEnabled;
    private List<String> remoteMboxes;
    private List<String> remoteViews;
    private List<String> localMboxes;
    private List<String> localViews;
    private List<String> responseTokens;
    private OnDeviceDecisioningRules rules;
    private Map<String, Object> meta;

    public OnDeviceDecisioningRuleSet() { }

    public String getVersion() {
        return version;
    }

    public String getGlobalMbox() {
        return globalMbox;
    }

    public boolean isGeoTargetingEnabled() {
        return geoTargetingEnabled;
    }

    public List<String> getRemoteMboxes() {
        return remoteMboxes;
    }

    public List<String> getRemoteViews() {
        return remoteViews;
    }

    public List<String> getResponseTokens() {
        return responseTokens;
    }

    public List<String> getLocalMboxes() {
        return localMboxes;
    }

    public List<String> getLocalViews() {
        return localViews;
    }

    public OnDeviceDecisioningRules getRules() {
        return rules;
    }

    public Map<String, Object> getMeta() { return meta; }

    @Override
    public String toString() {
        return "LocalDecisioningRuleSet{" +
                "version='" + version + '\'' +
                ", globalMbox='" + globalMbox + '\'' +
                ", geoTargetingEnabled=" + geoTargetingEnabled +
                ", remoteMboxes=" + remoteMboxes +
                ", remoteViews=" + remoteViews +
                ", localMboxes=" + localMboxes +
                ", localViews=" + localViews +
                ", responseTokens=" + responseTokens +
                ", rules=" + rules +
                ", meta=" + meta +
                '}';
    }

}
