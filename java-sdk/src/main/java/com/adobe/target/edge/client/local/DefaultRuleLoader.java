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
package com.adobe.target.edge.client.local;

import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.http.JacksonObjectMapper;
import com.adobe.target.edge.client.model.LocalDecisioningRuleSet;
import com.adobe.target.edge.client.service.TargetClientException;
import com.adobe.target.edge.client.service.TargetExceptionHandler;
import kong.unirest.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class DefaultRuleLoader implements RuleLoader {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRuleLoader.class);

    private static final long MIN_POLLING_INTERVAL = 5 * 60 * 1000; // 5 minutes

    private LocalDecisioningRuleSet latestRules;
    private String lastETag;
    private ClientConfig clientConfig;

    private UnirestInstance unirestInstance = Unirest.spawnInstance();
    private Timer timer = new Timer(this.getClass().getCanonicalName());
    private boolean started = false;

    public DefaultRuleLoader() {}

    @Override
    public LocalDecisioningRuleSet getLatestRules() {
        return latestRules;
    }

    @Override
    public synchronized void start(final ClientConfig clientConfig) {
        if (clientConfig.getLocalEnvironment() == null) {
            return;
        }
        if (started) {
            return;
        }
        started = true;
        if (unirestInstance != null) {
            unirestInstance.config()
                .socketTimeout(clientConfig.getSocketTimeout())
                .connectTimeout(clientConfig.getConnectTimeout())
                .concurrency(clientConfig.getMaxConnectionsTotal(), clientConfig.getMaxConnectionsPerHost())
                .automaticRetries(clientConfig.isEnabledRetries())
                .enableCookieManagement(false)
                .setObjectMapper(new JacksonObjectMapper())
                .setDefaultHeader("Accept", "application/json");
        }
        this.clientConfig = clientConfig;
        this.scheduleTimer(0);
    }

    public void stop() {
        this.timer.cancel();
        this.started = false;
    }

    public void refresh() {
        this.loadRules(this.clientConfig);
        this.timer.cancel();
        this.scheduleTimer(pollingInterval());
    }

    private void scheduleTimer(long delay) {
        this.timer = new Timer(this.getClass().getCanonicalName());
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                DefaultRuleLoader.this.loadRules(clientConfig);
            }
        }, delay, pollingInterval());
    }

    private long pollingInterval() {
        return Math.max(MIN_POLLING_INTERVAL, clientConfig.getLocalDecisioningPollingIntSecs() * 1000);
    }

    // package-private For unit test mocking
    HttpResponse<LocalDecisioningRuleSet> executeRequest(ClientConfig clientConfig) {
        GetRequest getRequest = unirestInstance.get(getLocalDecisioningUrl(clientConfig));
        if (this.lastETag != null) {
            getRequest.header("If-None-Match", this.lastETag);
        }
        return getRequest.asObject(new GenericType<LocalDecisioningRuleSet>(){});
    }

    // package-private For unit test mocking
    void setLatestRules(LocalDecisioningRuleSet ruleSet) {
        this.latestRules = ruleSet;
    }

    // package-private For unit test mocking
    void setLatestETag(String etag) {
        this.lastETag = etag;
    }

    // package-private For unit test mocking
    void loadRules(ClientConfig clientConfig) {
        HttpResponse<LocalDecisioningRuleSet> response = executeRequest(clientConfig);
        if (response.getStatus() != 200) {
            if (response.getStatus() == 304) {
                // Not updated, skip
                return;
            }
            String message = "Received invalid HTTP response while getting local-decisioning rule set: " + response.getStatus() + " : " + response.getStatusText();
            logger.warn(message);
            TargetExceptionHandler handler = clientConfig.getExceptionHandler();
            if (handler != null) {
                handler.handleException(new TargetClientException(message));
            }
            return;
        }
        LocalDecisioningRuleSet ruleSet = response.getBody();
        if (ruleSet != null && ruleSet.getVersion() != null && ruleSet.getVersion().startsWith("1.")) {
            setLatestETag(response.getHeaders().getFirst("ETag"));
            setLatestRules(ruleSet);
            logger.trace("rulesList={}", latestRules);
        }
        else if (ruleSet != null) {
            String message = "Unknown rules version: " + ruleSet.getVersion();
            logger.warn(message);
            TargetExceptionHandler handler = clientConfig.getExceptionHandler();
            if (handler != null) {
                handler.handleException(new TargetClientException(message));
            }
        }
        else {
            String message = "Unable to parse local-decisioning rule set";
            logger.warn(message);
            TargetExceptionHandler handler = clientConfig.getExceptionHandler();
            if (handler != null) {
                handler.handleException(new TargetClientException(message));
            }
        }
    }

    private String getLocalDecisioningUrl(ClientConfig clientConfig) {
        return "https://target-local-decisioning-test.s3-us-west-2.amazonaws.com/" +
                clientConfig.getClient() + "/" +
                clientConfig.getLocalEnvironment().toLowerCase() + "/" +
                "rules.json";
    }
}
