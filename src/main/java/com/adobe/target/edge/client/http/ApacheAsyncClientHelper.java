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
import kong.unirest.apache.ApacheAsyncClient;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.protocol.HttpContext;

import java.util.concurrent.TimeUnit;

import static com.adobe.target.edge.client.http.ApacheClientHelper.toApacheCreds;
import static com.adobe.target.edge.client.http.ApacheClientHelper.toRequestConfig;

public class ApacheAsyncClientHelper {
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

  public static ApacheAsyncClient initializeClient(ClientConfig clientConfig, Config config) {
    ApacheAsyncClient client;
    try {
      PoolingNHttpClientConnectionManager manager = ApacheAsyncClientHelper.createConnectionManager(config);
      TargetAsyncIdleConnectionMonitorThread monitor = new TargetAsyncIdleConnectionMonitorThread(manager, clientConfig);
      CloseableHttpAsyncClient asyncClient = ApacheAsyncClientHelper.createCustomClient(config, manager);
      asyncClient.start();
      monitor.tryStart();
      client = new ApacheAsyncClient(asyncClient, config, manager, monitor);
    } catch (Exception e) {
      throw new TargetClientException("Error occurred while initializing ApacheAsyncClient", e);
    }
    return client;
  }

  private static PoolingNHttpClientConnectionManager createConnectionManager(Config config) throws Exception {
    PoolingNHttpClientConnectionManager manager = new PoolingNHttpClientConnectionManager(new DefaultConnectingIOReactor(),
      null,
      createDefaultRegistry(),
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

  private static Registry<SchemeIOSessionStrategy> createDefaultRegistry() {
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
