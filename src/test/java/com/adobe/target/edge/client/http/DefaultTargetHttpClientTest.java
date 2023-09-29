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
package com.adobe.target.edge.client.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.adobe.target.delivery.v1.model.DeliveryRequest;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.ClientProxyConfig;
import com.adobe.target.edge.client.utils.MockRawResponse;
import com.adobe.target.edge.client.utils.TargetTestDeliveryRequestUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.net.ssl.SSLContext;
import kong.unirest.*;
import kong.unirest.apache.ApacheClient;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DefaultTargetHttpClientTest {
  static final String TEST_ORG_ID = "0DD934B85278256B0A490D44@AdobeOrg";
  static final String PROXY_HOST = "localhost";
  static final Integer PROXY_PORT = 3128;
  static final String PROXY_USERNAME = "test-user-name";
  static final String PROXY_PASSWORD = "test-password";

  @Test
  void testProxyConfigNotSet() {
    ClientConfig clientConfig = ClientConfig.builder().organizationId(TEST_ORG_ID).build();
    DefaultTargetHttpClient targetClient = new DefaultTargetHttpClient(clientConfig);
    UnirestInstance unirestInstance = targetClient.getUnirestInstance();
    Proxy unirestProxy = unirestInstance.config().getProxy();
    assertNull(unirestProxy);
    targetClient.close();
  }

  @Test
  void testProxyConfigSetWithNoAuthentication() {
    ClientConfig clientConfig =
        ClientConfig.builder()
            .organizationId(TEST_ORG_ID)
            .proxyConfig(new ClientProxyConfig(PROXY_HOST, PROXY_PORT))
            .build();
    DefaultTargetHttpClient targetClient = new DefaultTargetHttpClient(clientConfig);
    UnirestInstance unirestInstance = targetClient.getUnirestInstance();
    Proxy unirestProxy = unirestInstance.config().getProxy();
    assertNotNull(unirestProxy);
    assertEquals(PROXY_HOST, unirestProxy.getHost());
    assertEquals(PROXY_PORT, unirestProxy.getPort());
    assertNull(unirestProxy.getUsername());
    assertNull(unirestProxy.getPassword());
    targetClient.close();
  }

  @Test
  void testProxyConfigSetWithAuthentication() {
    ClientConfig clientConfig =
        ClientConfig.builder()
            .organizationId(TEST_ORG_ID)
            .proxyConfig(
                new ClientProxyConfig(PROXY_HOST, PROXY_PORT, PROXY_USERNAME, PROXY_PASSWORD))
            .build();
    DefaultTargetHttpClient targetClient = new DefaultTargetHttpClient(clientConfig);
    UnirestInstance unirestInstance = targetClient.getUnirestInstance();
    Proxy unirestProxy = unirestInstance.config().getProxy();
    assertNotNull(unirestProxy);
    assertEquals(PROXY_HOST, unirestProxy.getHost());
    assertEquals(PROXY_PORT, unirestProxy.getPort());
    assertEquals(PROXY_USERNAME, unirestProxy.getUsername());
    assertEquals(PROXY_PASSWORD, unirestProxy.getPassword());
    targetClient.close();
  }

  @Test
  void testConfigSetWithSSLFactory() throws Exception {
    SSLContext context = SSLContextBuilder.create().build();
    SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(context);
    CloseableHttpClient httpClient =
        HttpClients.custom().setSSLSocketFactory(sslSocketFactory).build();
    ClientConfig clientConfig =
        ClientConfig.builder().organizationId(TEST_ORG_ID).httpClient(httpClient).build();
    DefaultTargetHttpClient targetClient = new DefaultTargetHttpClient(clientConfig);
    assertEquals(targetClient.getUnirestInstance().config().getClient().getClient(), httpClient);
  }

  @Test
  void testConfigSetConnectionPoolParams() {
    ClientConfig clientConfig =
        ClientConfig.builder()
          .organizationId(TEST_ORG_ID)
          .connectionTtlMs(123)
          .idleConnectionValidationMs(456)
          .evictIdleConnectionsAfterSecs(78)
          .build();
    DefaultTargetHttpClient targetClient = new DefaultTargetHttpClient(clientConfig);
    Config config = targetClient.getUnirestInstance().config();
    assertEquals(config.getTTL(), 123);
    assertEquals(clientConfig.getIdleConnectionValidationMs(), 456);
    assertEquals(clientConfig.getEvictIdleConnectionsAfterSecs(), 78);
  }

  @Test
  void testExecute() throws NoSuchFieldException {
    ClientConfig clientConfig =
        ClientConfig.builder().organizationId(TEST_ORG_ID).telemetryEnabled(false).build();
    DefaultTargetHttpClient defaultTargetHttpClient = new DefaultTargetHttpClient(clientConfig);
    UnirestInstance unirestInstance =
        Mockito.mock(UnirestInstance.class, Mockito.RETURNS_DEEP_STUBS);
    FieldSetter.setField(
        defaultTargetHttpClient,
        defaultTargetHttpClient.getClass().getDeclaredField("unirestInstance"),
        unirestInstance);

    Map<String, Object> queryParams = new HashMap<>();
    String url = "/url";
    DeliveryRequest deliveryRequest = new DeliveryRequest();
    HttpResponse<Object> mockHttpResponse = Mockito.mock(HttpResponse.class);
    when(unirestInstance
            .post(eq(url))
            .queryString(eq(queryParams))
            .body(eq(deliveryRequest))
            .asObject(ArgumentMatchers.<Function<RawResponse, Object>>any()))
        .thenAnswer(
            invocation -> {
              RawResponse rawResponse =
                  TargetTestDeliveryRequestUtils.getRawTestResponse(HttpStatus.SC_OK);
              Function<RawResponse, Object> function =
                  (Function<RawResponse, Object>) invocation.getArguments()[0];
              function.apply(rawResponse);
              return mockHttpResponse;
            });

    ResponseWrapper<MockRawResponse> responseWrapper =
        defaultTargetHttpClient.execute(queryParams, url, deliveryRequest, MockRawResponse.class);
    assertNotNull(responseWrapper);
    assertEquals(mockHttpResponse, responseWrapper.getHttpResponse());
  }

  @Test
  void testExecuteAsync() throws NoSuchFieldException {
    ClientConfig clientConfig =
        ClientConfig.builder().organizationId(TEST_ORG_ID).telemetryEnabled(false).build();
    DefaultTargetHttpClient defaultTargetHttpClient = new DefaultTargetHttpClient(clientConfig);
    UnirestInstance unirestInstance =
        Mockito.mock(UnirestInstance.class, Mockito.RETURNS_DEEP_STUBS);
    FieldSetter.setField(
        defaultTargetHttpClient,
        defaultTargetHttpClient.getClass().getDeclaredField("unirestInstance"),
        unirestInstance);

    Map<String, Object> queryParams = new HashMap<>();
    String url = "/testUrl";
    DeliveryRequest deliveryRequest = new DeliveryRequest();

    when(unirestInstance
            .post(eq(url))
            .queryString(eq(queryParams))
            .body(eq(deliveryRequest))
            .asObjectAsync(ArgumentMatchers.<Function<RawResponse, Object>>any())
            .thenApply(
                ArgumentMatchers
                    .<Function<HttpResponse<Object>, CompletableFuture<ResponseWrapper<Object>>>>
                        any()))
        .thenAnswer(
            invocation -> {
              HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
              Function<HttpResponse, Object> function =
                  (Function<HttpResponse, Object>) invocation.getArguments()[0];
              function.apply(httpResponse);
              return null;
            });

    CompletableFuture<ResponseWrapper<MockRawResponse>> completableFuture =
        defaultTargetHttpClient.executeAsync(
            queryParams, url, deliveryRequest, MockRawResponse.class);
    assertNotNull(completableFuture);
  }
}
