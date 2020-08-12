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
package com.adobe.target.edge.client;

import com.adobe.target.edge.client.model.DecisioningMethod;
import com.adobe.target.edge.client.model.ondevice.OnDeviceDecisioningHandler;
import com.adobe.target.edge.client.service.TargetExceptionHandler;
import org.apache.http.HttpRequestInterceptor;

import java.util.Objects;

import static com.adobe.target.edge.client.utils.StringUtils.isNotEmpty;

public class ClientConfig {

    private String client;
    private String organizationId;
    private String protocol;
    private String defaultUrl;
    private String clusterUrlPrefix;
    private String clusterUrlSuffix;
    private String defaultPropertyToken;
    private int socketTimeout;
    private int connectTimeout;
    private int maxConnectionsPerHost;
    private int maxConnectionsTotal;
    private boolean enableRetries;
    private boolean logRequests;
    private boolean logRequestStatus;
    private HttpRequestInterceptor requestInterceptor;
    private ClientProxyConfig proxyConfig;
    private TargetExceptionHandler exceptionHandler;
    private OnDeviceDecisioningHandler onDeviceDecisioningHandler;
    private DecisioningMethod defaultDecisioningMethod;
    private String onDeviceEnvironment;
    private String onDeviceConfigHostname;
    private int onDeviceDecisioningPollingIntSecs;
    private byte[] onDeviceArtifactPayload;

    public String getClient() {
        return client;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getDefaultPropertyToken() { return defaultPropertyToken; }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getMaxConnectionsPerHost() {
        return maxConnectionsPerHost;
    }

    public int getMaxConnectionsTotal() {
        return maxConnectionsTotal;
    }

    public boolean isEnabledRetries() {
        return enableRetries;
    }

    public boolean isLogRequests() {
        return logRequests;
    }

    public boolean isLogRequestStatus() {
        return logRequestStatus;
    }

    public HttpRequestInterceptor getRequestInterceptor() {
        return requestInterceptor;
    }

    public String getUrl(String locationHint) {
        if (isNotEmpty(locationHint)) {
            return clusterUrlPrefix + locationHint + clusterUrlSuffix;
        }
        return defaultUrl;
    }
    
    public ClientProxyConfig getProxyConfig() {
    	return proxyConfig;
    }

    public boolean isProxyEnabled() {
        return proxyConfig != null;
    }

    public TargetExceptionHandler getExceptionHandler() { return exceptionHandler; }

    public OnDeviceDecisioningHandler getOnDeviceDecisioningHandler() { return onDeviceDecisioningHandler; }

    public DecisioningMethod getDefaultDecisioningMethod() { return defaultDecisioningMethod; }

    public String getOnDeviceEnvironment() { return onDeviceEnvironment; }

    public String getOnDeviceConfigHostname() { return onDeviceConfigHostname; }

    public int getOnDeviceDecisioningPollingIntSecs() { return onDeviceDecisioningPollingIntSecs; }

    public byte[] getOnDeviceArtifactPayload() { return onDeviceArtifactPayload; }

    public boolean isOnDeviceDecisioningEnabled() { return defaultDecisioningMethod != DecisioningMethod.SERVER_SIDE;}

    public static ClientConfigBuilder builder() {
        return new ClientConfigBuilder();
    }

    public static final class ClientConfigBuilder {
        private static final String CLUSTER_PREFIX = "mboxedge";
        private static final String DELIVERY_PATH_SUFFIX = "/rest/v1/delivery";
        private String client;
        private String organizationId;
        private String serverDomain = "tt.omtrdc.net";
        private String defaultPropertyToken;
        private boolean secure = true;
        private int socketTimeout = 10000;
        private int connectTimeout = 10000;
        private int maxConnectionsPerHost = 100;
        private int maxConnectionsTotal = 200;
        private boolean enableRetries = true;
        private boolean logRequests = false;
        private boolean logRequestStatus = false;
        private HttpRequestInterceptor requestInterceptor;
        private ClientProxyConfig proxyConfig;
        private TargetExceptionHandler exceptionHandler;
        private OnDeviceDecisioningHandler onDeviceDecisioningHandler;
        private DecisioningMethod defaultDecisioningMethod = DecisioningMethod.SERVER_SIDE;
        private String onDeviceEnvironment = "production";
        private String onDeviceConfigHostname = "assets.adobetarget.com";
        private int onDeviceDecisioningPollingIntSecs = 300;
        private byte[] onDeviceArtifactPayload;

        private ClientConfigBuilder() {
        }

        public ClientConfigBuilder client(String client) {
            this.client = client;
            return this;
        }

        public ClientConfigBuilder organizationId(String organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public ClientConfigBuilder serverDomain(String serverDomain) {
            this.serverDomain = serverDomain;
            return this;
        }

        public ClientConfigBuilder defaultPropertyToken(String defaultPropertyToken) {
            this.defaultPropertyToken = defaultPropertyToken;
            return this;
        }

        public ClientConfigBuilder secure(boolean secure) {
            this.secure = secure;
            return this;
        }

        public ClientConfigBuilder socketTimeout(int socketTimeout) {
            this.socketTimeout = socketTimeout;
            return this;
        }

        public ClientConfigBuilder connectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public ClientConfigBuilder maxConnectionsPerHost(int maxConnectionsPerHost) {
            this.maxConnectionsPerHost = maxConnectionsPerHost;
            return this;
        }

        public ClientConfigBuilder maxConnectionsTotal(int maxConnectionsTotal) {
            this.maxConnectionsTotal = maxConnectionsTotal;
            return this;
        }

        public ClientConfigBuilder enableRetries(boolean enableRetries) {
            this.enableRetries = enableRetries;
            return this;
        }

        public ClientConfigBuilder logRequests(boolean logRequests) {
            this.logRequests = logRequests;
            return this;
        }

        public ClientConfigBuilder logRequestStatus(boolean logRequestStatus) {
            this.logRequestStatus = logRequestStatus;
            return this;
        }

        public void requestInterceptor(HttpRequestInterceptor requestInterceptor) {
            this.requestInterceptor = requestInterceptor;
        }
        
        public ClientConfigBuilder proxyConfig(ClientProxyConfig proxyConfig) {
        	this.proxyConfig = proxyConfig;
        	return this;
        }

        public ClientConfigBuilder exceptionHandler(TargetExceptionHandler handler) {
            this.exceptionHandler = handler;
            return this;
        }

        public ClientConfigBuilder onDeviceDecisioningHandler(OnDeviceDecisioningHandler handler) {
            this.onDeviceDecisioningHandler = handler;
            return this;
        }

        public ClientConfigBuilder defaultDecisioningMethod(DecisioningMethod decisioningMethod) {
            this.defaultDecisioningMethod = decisioningMethod;
            return this;
        }

        public ClientConfigBuilder onDeviceEnvironment(String environment) {
            this.onDeviceEnvironment = environment;
            return this;
        }

        public ClientConfigBuilder onDeviceConfigHostname(String hostname) {
            this.onDeviceConfigHostname = hostname;
            return this;
        }

        public ClientConfigBuilder onDeviceDecisioningPollingIntSecs(int pollingInterval) {
            this.onDeviceDecisioningPollingIntSecs = pollingInterval;
            return this;
        }

        public ClientConfigBuilder onDeviceArtifactPayload(byte[] payload) {
            this.onDeviceArtifactPayload = payload;
            return this;
        }

        public ClientConfig build() {
            ClientConfig clientConfig = new ClientConfig();
            Objects.requireNonNull(client, "client id cannot be null");
            Objects.requireNonNull(organizationId, "organization id cannot be null");
            clientConfig.client = client;
            clientConfig.organizationId = this.organizationId;
            clientConfig.protocol = secure ? "https://" : "http://";
            clientConfig.defaultPropertyToken = this.defaultPropertyToken;
            clientConfig.connectTimeout = this.connectTimeout;
            clientConfig.maxConnectionsTotal = this.maxConnectionsTotal;
            clientConfig.socketTimeout = this.socketTimeout;
            clientConfig.enableRetries = this.enableRetries;
            clientConfig.maxConnectionsPerHost = this.maxConnectionsPerHost;
            clientConfig.defaultUrl = clientConfig.protocol + client + "." + serverDomain + DELIVERY_PATH_SUFFIX;
            clientConfig.clusterUrlPrefix = clientConfig.protocol + CLUSTER_PREFIX;
            clientConfig.clusterUrlSuffix = "." + serverDomain + DELIVERY_PATH_SUFFIX;
            clientConfig.requestInterceptor = this.requestInterceptor;
            clientConfig.logRequests = this.logRequests;
            clientConfig.logRequestStatus = this.logRequestStatus;
            clientConfig.proxyConfig = this.proxyConfig;
            clientConfig.exceptionHandler = this.exceptionHandler;
            clientConfig.onDeviceDecisioningHandler = this.onDeviceDecisioningHandler;
            clientConfig.defaultDecisioningMethod = this.defaultDecisioningMethod;
            clientConfig.onDeviceEnvironment = this.onDeviceEnvironment;
            clientConfig.onDeviceConfigHostname = this.onDeviceConfigHostname;
            clientConfig.onDeviceDecisioningPollingIntSecs = this.onDeviceDecisioningPollingIntSecs;
            clientConfig.onDeviceArtifactPayload = this.onDeviceArtifactPayload;
            return clientConfig;
        }
    }

}
