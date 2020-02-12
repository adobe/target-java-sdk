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
    private int socketTimeout;
    private int connectTimeout;
    private int maxConnectionsPerHost;
    private int maxConnectionsTotal;
    private boolean enableRetries;
    private boolean logRequests;
    private boolean logRequestStatus;
    private HttpRequestInterceptor requestInterceptor;
    private ClientProxyConfig proxyConfig;
    private String workspace;
    private String environment;
    private int localDecisioningPollingIntSecs;

    public String getClient() {
        return client;
    }

    public String getOrganizationId() {
        return organizationId;
    }

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

    public static ClientConfigBuilder builder() {
        return new ClientConfigBuilder();
    }

    public String getWorkspace() { return workspace; }

    public String getEnvironment() { return environment; }

    public int getLocalDecisioningPollingIntSecs() { return localDecisioningPollingIntSecs; }

    public static final class ClientConfigBuilder {
        private static final String CLUSTER_PREFIX = "mboxedge";
        private static final String DELIVERY_PATH_SUFFIX = "/rest/v1/delivery";
        private String client;
        private String organizationId;
        private String serverDomain = "tt.omtrdc.net";
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
        private String workspace;
        private String environment;
        private int localDecisioningPollingIntSecs = 300;

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

        public ClientConfigBuilder workspace(String workspace) {
            this.workspace = workspace;
            return this;
        }

        public ClientConfigBuilder environment(String environment) {
            this.environment = environment;
            return this;
        }

        public ClientConfigBuilder localDecisioningPollingIntSecs(int pollingInterval) {
            this.localDecisioningPollingIntSecs = pollingInterval;
            return this;
        }

        public ClientConfig build() {
            ClientConfig clientConfig = new ClientConfig();
            Objects.requireNonNull(client, "client id cannot be null");
            Objects.requireNonNull(organizationId, "organization id cannot be null");
            clientConfig.client = client;
            clientConfig.organizationId = this.organizationId;
            clientConfig.protocol = secure ? "https://" : "http://";
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
            clientConfig.workspace = this.workspace;
            clientConfig.environment = this.environment;
            clientConfig.localDecisioningPollingIntSecs = this.localDecisioningPollingIntSecs;
            return clientConfig;
        }
    }

}
