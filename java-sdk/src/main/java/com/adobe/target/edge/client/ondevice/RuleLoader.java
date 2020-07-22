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
package com.adobe.target.edge.client.ondevice;

import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.model.ondevice.OnDeviceDecisioningRuleSet;

import java.util.Date;

public interface RuleLoader {

    void start(ClientConfig clientConfig);

    void stop();

    void refresh();

    OnDeviceDecisioningRuleSet getLatestRules();

    long getPollingInterval();

    int getNumFetches();

    Date getLastFetch();

    String getLocation();

}
