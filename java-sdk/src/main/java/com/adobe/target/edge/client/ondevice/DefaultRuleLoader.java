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
import com.adobe.target.edge.client.ClientProxyConfig;
import com.adobe.target.edge.client.http.JacksonObjectMapper;
import com.adobe.target.edge.client.model.ondevice.OnDeviceDecisioningRuleSet;
import com.adobe.target.edge.client.model.ondevice.OnDeviceDecisioningHandler;
import com.adobe.target.edge.client.service.TargetClientException;
import com.adobe.target.edge.client.service.TargetExceptionHandler;
import kong.unirest.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class DefaultRuleLoader implements RuleLoader {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRuleLoader.class);

    private static final String MAJOR_VERSION = "1";

    private static final long MIN_POLLING_INTERVAL = 5 * 60 * 1000; // 5 minutes
    private static final int MAX_RETRIES = 10;

    private OnDeviceDecisioningRuleSet latestRules;
    private String lastETag;
    private ClientConfig clientConfig;

    private UnirestInstance unirestInstance = Unirest.spawnInstance();
    private Timer timer = new Timer(this.getClass().getCanonicalName());
    private boolean started = false;
    private boolean succeeded = false;
    private int retries = 0;
    private int numFetches = 0;
    private Date lastFetch = null;

    public DefaultRuleLoader() {}

    @Override
    public OnDeviceDecisioningRuleSet getLatestRules() {
        return latestRules;
    }

    @Override
    public synchronized void start(final ClientConfig clientConfig) {
        if (!clientConfig.isOnDeviceDecisioningEnabled()) {
            return;
        }
        if (started) {
            return;
        }
        ObjectMapper mapper = new JacksonObjectMapper();
        byte[] artifactPayload = clientConfig.getOnDeviceArtifactPayload();
        if (artifactPayload != null) {
            String payload = new String(artifactPayload, StandardCharsets.UTF_8);
            OnDeviceDecisioningRuleSet ruleSet = mapper.readValue(payload, new GenericType<OnDeviceDecisioningRuleSet>() {});
            String invalidMessage = invalidRuleSetMessage(ruleSet, null);
            if (invalidMessage == null) {
                setLatestRules(ruleSet);
                OnDeviceDecisioningHandler handler = clientConfig.getOnDeviceDecisioningHandler();
                if (handler != null && !succeeded) {
                    succeeded = true;
                    handler.onDeviceDecisioningReady();
                }
            }
            else {
                logger.warn(invalidMessage);
                TargetExceptionHandler handler = clientConfig.getExceptionHandler();
                if (handler != null) {
                    handler.handleException(new TargetClientException(invalidMessage));
                }
            }
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
                .setObjectMapper(mapper)
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
        if (this.unirestInstance != null) {
            this.unirestInstance.shutDown();
        }
        this.reset();
    }

    public void refresh() {
        this.loadRules(this.clientConfig);
        this.scheduleTimer(getPollingInterval());
    }

    private void reset() {
        this.started = false;
        this.succeeded = false;
        this.retries = 0;
        this.numFetches = 0;
        this.lastFetch = null;
        this.lastETag = null;
        this.latestRules = null;
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
                OnDeviceDecisioningHandler handler = clientConfig.getOnDeviceDecisioningHandler();
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
                    if (handler != null && !succeeded) {
                        succeeded = true;
                        handler.onDeviceDecisioningReady();
                    }
                    DefaultRuleLoader.this.numFetches++;
                    DefaultRuleLoader.this.lastFetch = new Date();
                }
            }
        }, delay, getPollingInterval());
    }

    public long getPollingInterval() {
        return Math.max(MIN_POLLING_INTERVAL, clientConfig.getOnDeviceDecisioningPollingIntSecs() * 1000);
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
    protected HttpResponse<OnDeviceDecisioningRuleSet> executeRequest(GetRequest getRequest) {
        return getRequest.asObject(new GenericType<OnDeviceDecisioningRuleSet>(){});
    }

    // For unit test mocking
    protected void setLatestRules(OnDeviceDecisioningRuleSet ruleSet) {
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
            HttpResponse<OnDeviceDecisioningRuleSet> response = executeRequest(request);
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
            OnDeviceDecisioningRuleSet ruleSet = response.getBody();
            String invalidMessage = invalidRuleSetMessage(ruleSet, response);
            if (invalidMessage == null) {
                setLatestETag(response.getHeaders().getFirst("ETag"));
                setLatestRules(ruleSet);
                OnDeviceDecisioningHandler localHandler = clientConfig.getOnDeviceDecisioningHandler();
                if (localHandler != null) {
                    localHandler.artifactDownloadSucceeded(request == null ? null : request.asBytes().getBody());
                }
                logger.trace("rulesList={}", latestRules);
                return true;
            }
            else {
                logger.warn(invalidMessage);
                if (handler != null) {
                    handler.handleException(new TargetClientException(invalidMessage));
                }
                return false;
            }
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

    private String invalidRuleSetMessage(OnDeviceDecisioningRuleSet ruleSet,
                                         HttpResponse<OnDeviceDecisioningRuleSet> response) {
        if (ruleSet == null || ruleSet.getRules() == null) {
            String message = "Unable to parse local-decisioning rule set";
            if (response != null) {
                message += " from: " + getLocalDecisioningUrl(clientConfig) +
                        ", error: " + response.getParsingError();
            }
            return message;
        }
        if (ruleSet.getVersion() == null || !ruleSet.getVersion().startsWith(MAJOR_VERSION + ".")) {
            return "Unknown rules version: " + ruleSet.getVersion();
        }
        return null;
    }

    private String getLocalDecisioningUrl(ClientConfig clientConfig) {
        return "https://" +
                clientConfig.getOnDeviceConfigHostname() + "/" +
                clientConfig.getClient() + "/" +
                clientConfig.getOnDeviceEnvironment().toLowerCase() +
                "/v" + MAJOR_VERSION +
                "/rules.json";
    }
}
