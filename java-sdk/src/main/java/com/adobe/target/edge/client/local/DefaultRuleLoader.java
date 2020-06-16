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
import com.adobe.target.edge.client.ClientProxyConfig;
import com.adobe.target.edge.client.http.JacksonObjectMapper;
import com.adobe.target.edge.client.model.local.LocalDecisioningRuleSet;
import com.adobe.target.edge.client.model.local.LocalExecutionHandler;
import com.adobe.target.edge.client.service.TargetClientException;
import com.adobe.target.edge.client.service.TargetExceptionHandler;
import kong.unirest.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class DefaultRuleLoader implements RuleLoader {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRuleLoader.class);

    private static final String MAJOR_VERSION = "1";

    private static final long MIN_POLLING_INTERVAL = 5 * 60 * 1000; // 5 minutes
    private static final int MAX_RETRIES = 10;

    private LocalDecisioningRuleSet latestRules;
    private String lastETag;
    private ClientConfig clientConfig;

    private UnirestInstance unirestInstance = Unirest.spawnInstance();
    private Timer timer = new Timer(this.getClass().getCanonicalName());
    private boolean started = false;
    private int retries = 0;
    private int numFetches = 0;
    private Date lastFetch = null;

    public DefaultRuleLoader() {}

    @Override
    public LocalDecisioningRuleSet getLatestRules() {
        return latestRules;
    }

    @Override
    public synchronized void start(final ClientConfig clientConfig) {
        if (!clientConfig.isLocalExecutionEnabled()) {
            return;
        }
        if (started) {
            return;
        }
        started = true;
        retries = 0;
        if (unirestInstance != null) {
            unirestInstance.config()
                .socketTimeout(clientConfig.getSocketTimeout())
                .connectTimeout(clientConfig.getConnectTimeout())
                .concurrency(clientConfig.getMaxConnectionsTotal(), clientConfig.getMaxConnectionsPerHost())
                .automaticRetries(clientConfig.isEnabledRetries())
                .enableCookieManagement(false)
                .setObjectMapper(new JacksonObjectMapper())
                .setDefaultHeader("Accept", "application/json");
            if (clientConfig.isProxyEnabled()) {
                ClientProxyConfig proxyConfig = clientConfig.getProxyConfig();
                if (proxyConfig.isAuthProxy()) {
                    unirestInstance.config().proxy(proxyConfig.getHost(), proxyConfig.getPort(),
                            proxyConfig.getUsername(), proxyConfig.getPassword());
                } else {
                    unirestInstance.config().proxy(proxyConfig.getHost(), proxyConfig.getPort());
                }
            }
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
        this.scheduleTimer(getPollingInterval());
    }

    private void scheduleTimer(long delay) {
        if (this.timer != null) {
            this.timer.cancel();
        }
        this.timer = new Timer(this.getClass().getCanonicalName());
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                boolean success = DefaultRuleLoader.this.loadRules(clientConfig);
                LocalExecutionHandler handler = clientConfig.getLocalExecutionHandler();
                if (!success && DefaultRuleLoader.this.latestRules == null) {
                    // retry if initial rules file download fails
                    String message;
                    if (DefaultRuleLoader.this.retries++ < MAX_RETRIES) {
                        long retryDelay = DefaultRuleLoader.this.retries * 1000;
                        message = String.format("Download of local-decisioning rules failed, retying in %s ms", retryDelay);
                        logger.debug(message);
                        scheduleTimer(retryDelay);
                    }
                    else {
                        message = "Exhausted retries trying to download local-decisioning rules.";
                        logger.warn(message);
                    }
                    if (handler != null) {
                        handler.artifactDownloadFailed(new TargetClientException(message));
                    }
                }
                else {
                    if (handler != null &&
                            DefaultRuleLoader.this.numFetches == 0) {
                        handler.localExecutionReady();
                    }
                    DefaultRuleLoader.this.numFetches++;
                    DefaultRuleLoader.this.lastFetch = new Date();
                }
            }
        }, delay, getPollingInterval());
    }

    public long getPollingInterval() {
        return Math.max(MIN_POLLING_INTERVAL, clientConfig.getLocalDecisioningPollingIntSecs() * 1000);
    }

    public int getNumFetches() {
        return numFetches;
    }

    public Date getLastFetch() {
        return lastFetch;
    }

    public String getLocation() {
        return this.getLocalDecisioningUrl(this.clientConfig);
    }

    // For unit test mocking
    protected GetRequest generateRequest(ClientConfig clientConfig) {
        GetRequest getRequest = unirestInstance.get(getLocalDecisioningUrl(clientConfig));
        if (this.lastETag != null) {
            getRequest.header("If-None-Match", this.lastETag);
        }
        return getRequest;
    }

    // For unit test mocking
    protected HttpResponse<LocalDecisioningRuleSet> executeRequest(GetRequest getRequest) {
        return getRequest.asObject(new GenericType<LocalDecisioningRuleSet>(){});
    }

    // For unit test mocking
    protected void setLatestRules(LocalDecisioningRuleSet ruleSet) {
        this.latestRules = ruleSet;
    }

    // For unit test mocking
    protected void setLatestETag(String etag) {
        this.lastETag = etag;
    }

    // For unit test mocking
    protected boolean loadRules(ClientConfig clientConfig) {
        try {
            TargetExceptionHandler handler = clientConfig.getExceptionHandler();
            GetRequest request = generateRequest(clientConfig);
            HttpResponse<LocalDecisioningRuleSet> response = executeRequest(request);
            if (response.getStatus() != 200) {
                if (response.getStatus() == 304) {
                    // Not updated, skip
                    return true;
                }
                String message = "Received invalid HTTP response while getting local-decisioning rule set: "
                        + response.getStatus() + " : " + response.getStatusText()
                        + " from " + getLocalDecisioningUrl(clientConfig);
                logger.warn(message);
                if (handler != null) {
                    handler.handleException(new TargetClientException(message));
                }
                return false;
            }
            LocalDecisioningRuleSet ruleSet = response.getBody();
            if (ruleSet != null && ruleSet.getRules() != null &&
                    ruleSet.getVersion() != null &&
                    ruleSet.getVersion().startsWith(MAJOR_VERSION + ".")) {
                setLatestETag(response.getHeaders().getFirst("ETag"));
                setLatestRules(ruleSet);
                LocalExecutionHandler localHandler = clientConfig.getLocalExecutionHandler();
                if (localHandler != null) {
                    localHandler.artifactDownloadSucceeded(request == null ? null : request.asBytes().getBody());
                }
                logger.trace("rulesList={}", latestRules);
                return true;
            }
            if (ruleSet != null && ruleSet.getVersion() != null) {
                String message = "Unknown rules version: " + ruleSet.getVersion();
                logger.warn(message);
                if (handler != null) {
                    handler.handleException(new TargetClientException(message));
                }
                return false;
            }
            String message = "Unable to parse local-decisioning rule set from: "
                    + getLocalDecisioningUrl(clientConfig)
                    + ", error: " + response.getParsingError();
            logger.warn(message);
            if (handler != null) {
                handler.handleException(new TargetClientException(message));
            }
            return false;
        }
        catch (Throwable t) {
            String message = "Hit exception while getting local-decisioning rule set from: "
                    + getLocalDecisioningUrl(clientConfig);
            logger.warn(message, t);
            TargetExceptionHandler handler = clientConfig.getExceptionHandler();
            if (handler != null) {
                handler.handleException(new TargetClientException(message, t));
            }
            return false;
        }
    }

    private String getLocalDecisioningUrl(ClientConfig clientConfig) {
        return "https://" +
                clientConfig.getLocalConfigHostname() + "/" +
                clientConfig.getClient() + "/" +
                clientConfig.getLocalEnvironment().toLowerCase() +
                "/v" + MAJOR_VERSION +
                "/rules.json";
    }
}
