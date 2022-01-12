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
import com.adobe.target.edge.client.utils.TargetTestDeliveryRequestUtils;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import kong.unirest.Callback;
import kong.unirest.Config;
import kong.unirest.Cookie;
import kong.unirest.Empty;
import kong.unirest.GenericType;
import kong.unirest.Headers;
import kong.unirest.HttpMethod;
import kong.unirest.HttpRequest;
import kong.unirest.HttpRequestSummary;
import kong.unirest.HttpResponse;
import kong.unirest.HttpResponseSummary;
import kong.unirest.JsonNode;
import kong.unirest.ObjectMapper;
import kong.unirest.PagedList;
import kong.unirest.ProgressMonitor;
import kong.unirest.Proxy;
import kong.unirest.RawResponse;
import kong.unirest.RequestBodyEntity;
import kong.unirest.UnirestInstance;
import kong.unirest.json.JSONElement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    RequestBodyEntity mockRequestBodyEntity = new MockRequestBodyEntity(mockHttpResponse);
    // when(mockRequestBodyEntity.asObject(ArgumentMatchers.<Function<RawResponse,
    // Object>>any())).thenCallRealMethod();
    when(unirestInstance.post(eq(url)).queryString(eq(queryParams)).body(eq(deliveryRequest)))
        .thenReturn(mockRequestBodyEntity);

    /*when(unirestInstance
            .post(eq(url))
            .queryString(eq(queryParams))
            .body(eq(deliveryRequest))
            .asObject(ArgumentMatchers.<Function<RawResponse, Object>>any()))
          .thenReturn(mockHttpResponse);
    */
    ResponseWrapper<MockRawResponse> responseWrapper =
        defaultTargetHttpClient.execute(queryParams, url, deliveryRequest, MockRawResponse.class);
    // ResponseWrapper<RawResponse> responseWrapper =
    // TargetTestDeliveryRequestUtils.getRawTestResponse();

    assertNotNull(responseWrapper);
    assertEquals(mockHttpResponse, responseWrapper.getHttpResponse());
  }

  public static class MockRequestBodyEntity implements RequestBodyEntity {
    // getMockRequestBodyEntity(HttpResponse<Object> mockHttpResponse) {
    private HttpResponse<Object> mockHttpResponse;

    MockRequestBodyEntity(HttpResponse<Object> mockHttpResponse) {
      this.mockHttpResponse = mockHttpResponse;
    }

    @Override
    public <T> HttpResponse<T> asObject(Function<RawResponse, T> function) {
      // RawResponse rawResponse = new MockRawResponse();
      RawResponse rawResponse = TargetTestDeliveryRequestUtils.getRawTestResponse();
      function.apply(rawResponse);
      return (HttpResponse<T>) mockHttpResponse;
    }

    @Override
    public RequestBodyEntity routeParam(String name, String value) {
      return null;
    }

    @Override
    public RequestBodyEntity routeParam(Map<String, Object> params) {
      return null;
    }

    @Override
    public RequestBodyEntity basicAuth(String username, String password) {
      return null;
    }

    @Override
    public RequestBodyEntity accept(String value) {
      return null;
    }

    @Override
    public RequestBodyEntity responseEncoding(String encoding) {
      return null;
    }

    @Override
    public RequestBodyEntity header(String name, String value) {
      return null;
    }

    @Override
    public RequestBodyEntity headerReplace(String name, String value) {
      return null;
    }

    @Override
    public RequestBodyEntity headers(Map<String, String> headerMap) {
      return null;
    }

    @Override
    public RequestBodyEntity cookie(String name, String value) {
      return null;
    }

    @Override
    public RequestBodyEntity cookie(Cookie cookie) {
      return null;
    }

    @Override
    public RequestBodyEntity cookie(Collection<Cookie> cookies) {
      return null;
    }

    @Override
    public RequestBodyEntity queryString(String name, Object value) {
      return null;
    }

    @Override
    public RequestBodyEntity queryString(String name, Collection<?> value) {
      return null;
    }

    @Override
    public RequestBodyEntity queryString(Map<String, Object> parameters) {
      return null;
    }

    @Override
    public RequestBodyEntity withObjectMapper(ObjectMapper mapper) {
      return null;
    }

    @Override
    public RequestBodyEntity socketTimeout(int millies) {
      return null;
    }

    @Override
    public RequestBodyEntity connectTimeout(int millies) {
      return null;
    }

    @Override
    public RequestBodyEntity proxy(String host, int port) {
      return null;
    }

    @Override
    public RequestBodyEntity downloadMonitor(ProgressMonitor monitor) {
      return null;
    }

    @Override
    public HttpResponse<String> asString() {
      return null;
    }

    @Override
    public CompletableFuture<HttpResponse<String>> asStringAsync() {
      return null;
    }

    @Override
    public CompletableFuture<HttpResponse<String>> asStringAsync(Callback<String> callback) {
      return null;
    }

    @Override
    public HttpResponse<byte[]> asBytes() {
      return null;
    }

    @Override
    public CompletableFuture<HttpResponse<byte[]>> asBytesAsync() {
      return null;
    }

    @Override
    public CompletableFuture<HttpResponse<byte[]>> asBytesAsync(Callback<byte[]> callback) {
      return null;
    }

    @Override
    public HttpResponse<JsonNode> asJson() {
      return null;
    }

    @Override
    public CompletableFuture<HttpResponse<JsonNode>> asJsonAsync() {
      return null;
    }

    @Override
    public CompletableFuture<HttpResponse<JsonNode>> asJsonAsync(Callback<JsonNode> callback) {
      return null;
    }

    @Override
    public <T> HttpResponse<T> asObject(Class<? extends T> responseClass) {
      return null;
    }

    @Override
    public <T> HttpResponse<T> asObject(GenericType<T> genericType) {
      return null;
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(Class<? extends T> responseClass) {
      return null;
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(
        Class<? extends T> responseClass, Callback<T> callback) {
      return null;
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(GenericType<T> genericType) {
      return null;
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(
        GenericType<T> genericType, Callback<T> callback) {
      return null;
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> asObjectAsync(Function<RawResponse, T> function) {
      return null;
    }

    @Override
    public HttpResponse<File> asFile(String path, CopyOption... copyOptions) {
      return null;
    }

    @Override
    public CompletableFuture<HttpResponse<File>> asFileAsync(
        String path, CopyOption... copyOptions) {
      return null;
    }

    @Override
    public CompletableFuture<HttpResponse<File>> asFileAsync(
        String path, Callback<File> callback, CopyOption... copyOptions) {
      return null;
    }

    @Override
    public <T> PagedList<T> asPaged(
        Function<HttpRequest, HttpResponse> mappingFunction,
        Function<HttpResponse<T>, String> linkExtractor) {
      return null;
    }

    @Override
    public HttpResponse asEmpty() {
      return null;
    }

    @Override
    public CompletableFuture<HttpResponse<Empty>> asEmptyAsync() {
      return null;
    }

    @Override
    public CompletableFuture<HttpResponse<Empty>> asEmptyAsync(Callback<Empty> callback) {
      return null;
    }

    @Override
    public void thenConsume(Consumer<RawResponse> consumer) {}

    @Override
    public void thenConsumeAsync(Consumer<RawResponse> consumer) {}

    @Override
    public HttpMethod getHttpMethod() {
      return null;
    }

    @Override
    public String getUrl() {
      return null;
    }

    @Override
    public Headers getHeaders() {
      return null;
    }

    @Override
    public int getSocketTimeout() {
      return 0;
    }

    @Override
    public int getConnectTimeout() {
      return 0;
    }

    @Override
    public Proxy getProxy() {
      return null;
    }

    @Override
    public HttpRequestSummary toSummary() {
      return null;
    }

    @Override
    public Instant getCreationTime() {
      return null;
    }

    @Override
    public boolean isMultiPart() {
      return false;
    }

    @Override
    public boolean isEntityBody() {
      return false;
    }

    @Override
    public RequestBodyEntity body(byte[] bodyBytes) {
      return null;
    }

    @Override
    public RequestBodyEntity body(String bodyAsString) {
      return null;
    }

    @Override
    public RequestBodyEntity body(JsonNode jsonBody) {
      return null;
    }

    @Override
    public RequestBodyEntity body(JSONElement body) {
      return null;
    }

    @Override
    public RequestBodyEntity body(Object body) {
      return null;
    }

    @Override
    public RequestBodyEntity charset(Charset charset) {
      return null;
    }
  }

  public static class MockRawResponse implements RawResponse {
    @Override
    public int getStatus() {
      return 200;
    }

    @Override
    public String getContentType() {
      return "application/json;charset=UTF-8";
    }

    @Override
    public String getStatusText() {
      return null;
    }

    @Override
    public Headers getHeaders() {
      return null;
    }

    @Override
    public InputStream getContent() {
      return null;
    }

    @Override
    public byte[] getContentAsBytes() {
      return "{\"msg\":\"success\"}".getBytes();
    }

    @Override
    public String getContentAsString() {
      return null;
    }

    @Override
    public String getContentAsString(String charset) {
      return null;
    }

    @Override
    public InputStreamReader getContentReader() {
      return null;
    }

    @Override
    public boolean hasContent() {
      return false;
    }

    @Override
    public String getEncoding() {
      return null;
    }

    @Override
    public Config getConfig() {
      return null;
    }

    @Override
    public HttpResponseSummary toSummary() {
      return null;
    }
  }
}
