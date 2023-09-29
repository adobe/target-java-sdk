/*
 * Copyright 2023 Adobe. All rights reserved.
 * This file is licensed to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.adobe.target.edge.client.http;

import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.exception.TargetClientException;
import kong.unirest.Config;
import kong.unirest.Proxy;
import kong.unirest.apache.ApacheAsyncClient;
import kong.unirest.apache.ApacheClient;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.protocol.HttpContext;

import java.util.concurrent.TimeUnit;

public class ApacheClientFactory {
  static class ApacheNoRedirectStrategy implements RedirectStrategy {
    @Override
    public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) {
      return false;
    }

    @Override
    public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) {
      return null;
    }
  }

  public static ApacheClient initializeSyncClient(ClientConfig clientConfig, Config config) {
    PoolingHttpClientConnectionManager manager = createConnectionManager(clientConfig, config);
    HttpClient newClient = createCustomClient(clientConfig, config, manager);
    return new ApacheClient(newClient, config, manager);
  }

  public static ApacheAsyncClient initializeAsyncClient(ClientConfig clientConfig, Config config) {
    ApacheAsyncClient client;
    try {
      PoolingNHttpClientConnectionManager manager = createConnectionManager(config);
      TargetAsyncIdleConnectionMonitorThread monitor = new TargetAsyncIdleConnectionMonitorThread(manager, clientConfig);
      CloseableHttpAsyncClient asyncClient = createCustomClient(config, manager);
      asyncClient.start();
      monitor.tryStart();
      client = new ApacheAsyncClient(asyncClient, config, manager, monitor);
    } catch (Exception e) {
      throw new TargetClientException("Error occurred while initializing ApacheAsyncClient", e);
    }
    return client;
  }

  private static RequestConfig toRequestConfig(Config config) {
    HttpHost proxy = toApacheProxy(config.getProxy());
    return RequestConfig.custom()
      .setConnectTimeout(config.getConnectionTimeout())
      .setSocketTimeout(config.getSocketTimeout())
      .setConnectionRequestTimeout(config.getSocketTimeout())
      .setProxy(proxy)
      .build();
  }

  private static CredentialsProvider toApacheCreds(Proxy proxy) {
    if (proxy != null && proxy.isAuthenticated()) {
      CredentialsProvider proxyCredentials = new BasicCredentialsProvider();
      proxyCredentials.setCredentials(new AuthScope(proxy.getHost(), proxy.getPort()),
        new UsernamePasswordCredentials(proxy.getUsername(), proxy.getPassword()));
      return proxyCredentials;
    }
    return null;
  }

  private static PoolingHttpClientConnectionManager createConnectionManager(ClientConfig clientConfig, Config config) {
    PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(createDefaultSyncRegistry(),
      null, null, null,
      config.getTTL(), TimeUnit.MILLISECONDS);

    manager.setMaxTotal(config.getMaxConnections());
    manager.setDefaultMaxPerRoute(config.getMaxPerRoutes());
    manager.setValidateAfterInactivity(clientConfig.getIdleConnectionValidationMs());

    return manager;
  }

  private static HttpClient createCustomClient(ClientConfig clientConfig, Config config, PoolingHttpClientConnectionManager manager) {
    HttpClientBuilder cb = HttpClients.custom()
      .setDefaultRequestConfig(toRequestConfig(config))
      .setDefaultCredentialsProvider(toApacheCreds(config.getProxy()))
      .setConnectionManager(manager)
      .evictIdleConnections(clientConfig.getEvictIdleConnectionsAfterSecs(), TimeUnit.SECONDS)
      .useSystemProperties();

    setOptions(cb, config);
    return cb.build();
  }

  private static Registry<ConnectionSocketFactory> createDefaultSyncRegistry() {
    return RegistryBuilder.<ConnectionSocketFactory>create()
      .register("http", PlainConnectionSocketFactory.getSocketFactory())
      .register("https", SSLConnectionSocketFactory.getSocketFactory())
      .build();
  }

  private static HttpHost toApacheProxy(Proxy proxy){
    if (proxy == null){
      return null;
    }
    return new HttpHost(proxy.getHost(), proxy.getPort());
  }

  private static void setOptions(HttpClientBuilder cb, Config config) {
    if (!config.isAutomaticRetries()) {
      cb.disableAutomaticRetries();
    }
    if (!config.isRequestCompressionOn()) {
      cb.disableContentCompression();
    }
    if (config.useSystemProperties()) {
      cb.useSystemProperties();
    }
    if (!config.getFollowRedirects()) {
      cb.disableRedirectHandling();
    }
    if (!config.getEnabledCookieManagement()) {
      cb.disableCookieManagement();
    }
    config.getInterceptor().stream().forEach(cb::addInterceptorFirst);
  }

  private static PoolingNHttpClientConnectionManager createConnectionManager(Config config) throws Exception {
    PoolingNHttpClientConnectionManager manager = new PoolingNHttpClientConnectionManager(new DefaultConnectingIOReactor(),
      null,
      createDefaultAsyncRegistry(),
      null,
      null,
      config.getTTL(), TimeUnit.MILLISECONDS);

    manager.setMaxTotal(config.getMaxConnections());
    manager.setDefaultMaxPerRoute(config.getMaxPerRoutes());

    return manager;
  }

  private static CloseableHttpAsyncClient createCustomClient(Config config, PoolingNHttpClientConnectionManager manager) {
    HttpAsyncClientBuilder ab = HttpAsyncClientBuilder.create()
      .setDefaultRequestConfig(toRequestConfig(config))
      .setConnectionManager(manager)
      .setDefaultCredentialsProvider(toApacheCreds(config.getProxy()))
      .useSystemProperties();

    setOptions(ab, config);

    return ab.build();
  }

  private static Registry<SchemeIOSessionStrategy> createDefaultAsyncRegistry() {
    return RegistryBuilder.<SchemeIOSessionStrategy>create()
      .register("http", NoopIOSessionStrategy.INSTANCE)
      .register("https", SSLIOSessionStrategy.getDefaultStrategy())
      .build();
  }

  private static void setOptions(HttpAsyncClientBuilder ab, Config config) {
    if (config.useSystemProperties()) {
      ab.useSystemProperties();
    }
    if (!config.getFollowRedirects()) {
      ab.setRedirectStrategy(new ApacheNoRedirectStrategy());
    }
    if (!config.getEnabledCookieManagement()) {
      ab.disableCookieManagement();
    }
    config.getInterceptor().forEach(ab::addInterceptorFirst);
  }
}
