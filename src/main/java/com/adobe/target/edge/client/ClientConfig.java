/*
 * Copyright 2021 Adobe. All rights reserved.
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

import static com.adobe.target.edge.client.utils.StringUtils.isNotEmpty;

import com.adobe.target.edge.client.exception.TargetExceptionHandler;
import com.adobe.target.edge.client.model.DecisioningMethod;
import com.adobe.target.edge.client.model.ondevice.OnDeviceDecisioningHandler;
import java.util.List;
import java.util.Objects;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.HttpClient;

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
  private int connectionTtlMs;
  private int idleConnectionValidationMs;
  private int evictIdleConnectionsAfterSecs;
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
  private boolean telemetryEnabled;
  private List<String> onDeviceAllMatchingRulesMboxes;
  private HttpClient httpClient;
  private boolean shouldArtifactRequestBypassProxyCache;

  public String getClient() {
    return client;
  }

  public String getOrganizationId() {
    return organizationId;
  }

  public String getDefaultPropertyToken() {
    return defaultPropertyToken;
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

  public int getConnectionTtlMs() {
    return connectionTtlMs;
  }

  public int getIdleConnectionValidationMs() {
    return idleConnectionValidationMs;
  }

  public int getEvictIdleConnectionsAfterSecs() {
    return evictIdleConnectionsAfterSecs;
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

  public HttpClient getHttpClient() {
    return httpClient;
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

  public TargetExceptionHandler getExceptionHandler() {
    return exceptionHandler;
  }

  public OnDeviceDecisioningHandler getOnDeviceDecisioningHandler() {
    return onDeviceDecisioningHandler;
  }

  public DecisioningMethod getDefaultDecisioningMethod() {
    return defaultDecisioningMethod;
  }

  public String getOnDeviceEnvironment() {
    return onDeviceEnvironment;
  }

  public String getOnDeviceConfigHostname() {
    return onDeviceConfigHostname;
  }

  public int getOnDeviceDecisioningPollingIntSecs() {
    return onDeviceDecisioningPollingIntSecs;
  }

  public byte[] getOnDeviceArtifactPayload() {
    return onDeviceArtifactPayload;
  }

  public List<String> getOnDeviceAllMatchingRulesMboxes() {
    return onDeviceAllMatchingRulesMboxes;
  }

  public boolean isOnDeviceDecisioningEnabled() {
    return defaultDecisioningMethod != DecisioningMethod.SERVER_SIDE;
  }

  public static ClientConfigBuilder builder() {
    return new ClientConfigBuilder();
  }

  public boolean isTelemetryEnabled() {
    return telemetryEnabled;
  }

  public boolean shouldArtifactRequestBypassProxyCache() {
    return shouldArtifactRequestBypassProxyCache;
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
    private int connectionTtlMs = -1;
    private int idleConnectionValidationMs = 1000;
    private int evictIdleConnectionsAfterSecs = 20;
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
    private boolean telemetryEnabled = true;
    private List<String> onDeviceAllMatchingRulesMboxes;
    private HttpClient httpClient;
    private boolean shouldArtifactRequestBypassProxyCache = false;

    private ClientConfigBuilder() {}

    /**
     * Client Code
     *
     * @param client
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder client(String client) {
      this.client = client;
      return this;
    }

    /**
     * Organization ID
     *
     * @param organizationId
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder organizationId(String organizationId) {
      this.organizationId = organizationId;
      return this;
    }

    /**
     * Server Domain
     *
     * @param serverDomain
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder serverDomain(String serverDomain) {
      this.serverDomain = serverDomain;
      return this;
    }

    /**
     * Default Property Token
     *
     * @param defaultPropertyToken
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder defaultPropertyToken(String defaultPropertyToken) {
      this.defaultPropertyToken = defaultPropertyToken;
      return this;
    }

    /**
     * Secure (HTTPS) or not Default value is <b>true</b>
     *
     * @param secure
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder secure(boolean secure) {
      this.secure = secure;
      return this;
    }

    /**
     * Socket Timeout Default value is <b>10000</b>
     *
     * @param socketTimeout
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder socketTimeout(int socketTimeout) {
      this.socketTimeout = socketTimeout;
      return this;
    }

    /**
     * Connect Timeout Default value is <b>10000</b>
     *
     * @param connectTimeout
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder connectTimeout(int connectTimeout) {
      this.connectTimeout = connectTimeout;
      return this;
    }

    /**
     * Max Connections Per Host Default value is <b>100</b>
     *
     * @param maxConnectionsPerHost
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder maxConnectionsPerHost(int maxConnectionsPerHost) {
      this.maxConnectionsPerHost = maxConnectionsPerHost;
      return this;
    }

    /**
     * Max Connections Total Default value is <b>200</b>
     *
     * @param maxConnectionsTotal
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder maxConnectionsTotal(int maxConnectionsTotal) {
      this.maxConnectionsTotal = maxConnectionsTotal;
      return this;
    }

    /**
     * Total time to live (TTL) defines maximum life span of persistent connections regardless of
     * their expiration setting. No persistent connection will be re-used past its TTL value.
     * Default value is <b>-1</b> which means that connections will be kept alive indefinitely.
     *
     * @param connectionTtlMs
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder connectionTtlMs(int connectionTtlMs) {
      this.connectionTtlMs = connectionTtlMs;
      return this;
    }

    /**
     * Idle connection validation interval defines period of inactivity in milliseconds after which
     * persistent connections must be re-validated prior to being leased to the consumer.
     * Non-positive value effectively disables idle connection validation. Note: Only available for
     * the Apache sync client Default value is <b>1000</b>
     *
     * @param idleConnectionValidationMs
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder idleConnectionValidationMs(int idleConnectionValidationMs) {
      this.idleConnectionValidationMs = idleConnectionValidationMs;
      return this;
    }

    /**
     * The time in seconds to evict idle connections from the connection pool. Default value is
     * <b>20</b>
     *
     * @param evictIdleConnectionsAfterSecs
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder evictIdleConnectionsAfterSecs(int evictIdleConnectionsAfterSecs) {
      this.evictIdleConnectionsAfterSecs = evictIdleConnectionsAfterSecs;
      return this;
    }

    /**
     * Enable retries Default value is <b>true</b>
     *
     * @param enableRetries
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder enableRetries(boolean enableRetries) {
      this.enableRetries = enableRetries;
      return this;
    }

    /**
     * Log requests Default value is <b>false</b>
     *
     * @param logRequests
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder logRequests(boolean logRequests) {
      this.logRequests = logRequests;
      return this;
    }

    /**
     * Telemetry Enabled Default value is <b>true</b>
     *
     * @param telemetryEnabled
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder telemetryEnabled(boolean telemetryEnabled) {
      this.telemetryEnabled = telemetryEnabled;
      return this;
    }

    /**
     * Log request status Default value is <b>false</b>
     *
     * @param logRequestStatus
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder logRequestStatus(boolean logRequestStatus) {
      this.logRequestStatus = logRequestStatus;
      return this;
    }

    /**
     * Request Interceptor
     *
     * @param requestInterceptor
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder requestInterceptor(HttpRequestInterceptor requestInterceptor) {
      this.requestInterceptor = requestInterceptor;
      return this;
    }

    /**
     * Proxy Configuration
     *
     * @param proxyConfig
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder proxyConfig(ClientProxyConfig proxyConfig) {
      this.proxyConfig = proxyConfig;
      return this;
    }

    /**
     * Exception Handler
     *
     * @param handler
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder exceptionHandler(TargetExceptionHandler handler) {
      this.exceptionHandler = handler;
      return this;
    }

    /**
     * On Device Decisioning Handler
     *
     * @param handler
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder onDeviceDecisioningHandler(OnDeviceDecisioningHandler handler) {
      this.onDeviceDecisioningHandler = handler;
      return this;
    }

    /**
     * Default Decisioning Method Default value is <b>server-side</b>
     *
     * @param decisioningMethod
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder defaultDecisioningMethod(DecisioningMethod decisioningMethod) {
      this.defaultDecisioningMethod = decisioningMethod;
      return this;
    }

    /**
     * On Device Environment Default value is <b>production</b>
     *
     * @param environment
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder onDeviceEnvironment(String environment) {
      this.onDeviceEnvironment = environment;
      return this;
    }

    /**
     * On Device Config Hostname Default value is <b>assets.adobetarget.com</b>
     *
     * @param hostname
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder onDeviceConfigHostname(String hostname) {
      this.onDeviceConfigHostname = hostname;
      return this;
    }

    /**
     * On Device Decisioning Polling Interval in seconds Default value is <b>300</b>
     *
     * @param pollingInterval
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder onDeviceDecisioningPollingIntSecs(int pollingInterval) {
      this.onDeviceDecisioningPollingIntSecs = pollingInterval;
      return this;
    }

    /**
     * On Device Artifact Payload
     *
     * @param payload
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder onDeviceArtifactPayload(byte[] payload) {
      this.onDeviceArtifactPayload = payload;
      return this;
    }

    /**
     * On Device All Matching Rules Mboxes
     *
     * @param mboxes
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder onDeviceAllMatchingRulesMboxes(List<String> mboxes) {
      this.onDeviceAllMatchingRulesMboxes = mboxes;
      return this;
    }

    /**
     * HTTP Client
     *
     * @param httpClient
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder httpClient(HttpClient httpClient) {
      this.httpClient = httpClient;
      return this;
    }

    /**
     * On Device Decisioning - artifact request behind a proxy will include an empty Authorization
     * header in order to bypass the proxy's internal cache
     *
     * @param shouldArtifactRequestBypassProxyCache
     * @return ClientConfigBuilder
     */
    public ClientConfigBuilder shouldArtifactRequestBypassProxyCache(
        boolean shouldArtifactRequestBypassProxyCache) {
      this.shouldArtifactRequestBypassProxyCache = shouldArtifactRequestBypassProxyCache;
      return this;
    }

    public ClientConfig build() {
      ClientConfig clientConfig = new ClientConfig();
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
      clientConfig.connectionTtlMs = this.connectionTtlMs;
      clientConfig.idleConnectionValidationMs = this.idleConnectionValidationMs;
      clientConfig.evictIdleConnectionsAfterSecs = this.evictIdleConnectionsAfterSecs;
      clientConfig.defaultUrl =
          clientConfig.protocol + client + "." + serverDomain + DELIVERY_PATH_SUFFIX;
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
      clientConfig.onDeviceAllMatchingRulesMboxes = this.onDeviceAllMatchingRulesMboxes;
      clientConfig.telemetryEnabled = this.telemetryEnabled;
      clientConfig.httpClient = this.httpClient;
      clientConfig.shouldArtifactRequestBypassProxyCache =
          this.shouldArtifactRequestBypassProxyCache;
      return clientConfig;
    }
  }
}
