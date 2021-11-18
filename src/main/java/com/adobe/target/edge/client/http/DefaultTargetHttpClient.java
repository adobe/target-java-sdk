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

import static com.adobe.target.edge.client.ondevice.OnDeviceDecisioningService.TIMING_EXECUTE_REQUEST;

import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.ClientProxyConfig;
import com.adobe.target.edge.client.utils.MathUtils;
import com.adobe.target.edge.client.utils.StringUtils;
import com.adobe.target.edge.client.utils.TimingTool;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kong.unirest.HttpResponse;
import kong.unirest.ObjectMapper;
import kong.unirest.RawResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.UnirestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTargetHttpClient implements TargetHttpClient {

  private static final Logger logger = LoggerFactory.getLogger(DefaultTargetHttpClient.class);
  private static final Pattern CHARSET_PATTERN =
      Pattern.compile("(?i)\\bcharset=\\s*\"?([^\\s;\"]*)");

  private UnirestInstance unirestInstance = Unirest.spawnInstance();
  private ObjectMapper serializer = new JacksonObjectMapper();
  private static final int DECIMAL_PLACES = 2;

  public DefaultTargetHttpClient(ClientConfig clientConfig) {
    unirestInstance
        .config()
        .socketTimeout(clientConfig.getSocketTimeout())
        .connectTimeout(clientConfig.getConnectTimeout())
        .concurrency(clientConfig.getMaxConnectionsTotal(), clientConfig.getMaxConnectionsPerHost())
        .automaticRetries(clientConfig.isEnabledRetries())
        .enableCookieManagement(false)
        .setObjectMapper(getObjectMapper())
        .setDefaultHeader("Accept", "application/json");

    if (clientConfig.isLogRequestStatus()) {
      unirestInstance.config().instrumentWith(new TargetMetrics(new LoggingMetricConsumer()));
    }

    if (clientConfig.getRequestInterceptor() != null) {
      unirestInstance.config().addInterceptor(clientConfig.getRequestInterceptor());
    }

    if (clientConfig.isProxyEnabled()) {
      ClientProxyConfig proxyConfig = clientConfig.getProxyConfig();
      if (proxyConfig.isAuthProxy()) {
        unirestInstance
            .config()
            .proxy(
                proxyConfig.getHost(),
                proxyConfig.getPort(),
                proxyConfig.getUsername(),
                proxyConfig.getPassword());
      } else {
        unirestInstance.config().proxy(proxyConfig.getHost(), proxyConfig.getPort());
      }
    }
  }

  private ObjectMapper getObjectMapper() {
    logger.debug("using json serializer: {}", serializer.getClass().getSimpleName());
    return serializer;
  }

  @Override
  public void addDefaultHeader(String key, String value) {
    unirestInstance.config().setDefaultHeader(key, value);
  }

  @Override
  public <T, R> ResponseWrapper<R> execute(
      Map<String, Object> queryParams, String url, T request, Class<R> responseClass) {
    ResponseWrapper<R> responseWrapper = new ResponseWrapper<>();

    HttpResponse<Object> httpResponse =
        unirestInstance
            .post(url)
            .queryString(queryParams)
            .body(request)
            .asObject(
                (Function<RawResponse, Object>)
                    getRawResponseFunction(responseClass, responseWrapper));
    responseWrapper.setHttpResponse((HttpResponse<R>) httpResponse);
    return responseWrapper;
  }

  @Override
  public <T, R> CompletableFuture<ResponseWrapper<R>> executeAsync(
      Map<String, Object> queryParams, String url, T request, Class<R> responseClass) {
    ResponseWrapper<R> responseWrapper = new ResponseWrapper<>();
    CompletableFuture<ResponseWrapper<R>> completableFutureResponseWrapper =
        new CompletableFuture<>();

    unirestInstance
        .post(url)
        .queryString(queryParams)
        .body(request)
        .asObjectAsync(getRawResponseFunction(responseClass, responseWrapper))
        .thenApply(getCompletableFutureFunction(responseWrapper, completableFutureResponseWrapper));
    return completableFutureResponseWrapper;
  }

  private <R>
      Function<HttpResponse<R>, CompletableFuture<ResponseWrapper<R>>> getCompletableFutureFunction(
          ResponseWrapper<R> responseWrapper,
          CompletableFuture<ResponseWrapper<R>> completableFutureResponseWrapper) {
    return httpResponse -> {
      responseWrapper.setHttpResponse(httpResponse);
      completableFutureResponseWrapper.complete(responseWrapper);
      return completableFutureResponseWrapper;
    };
  }

  private <R> Function<RawResponse, R> getRawResponseFunction(
      Class<R> responseClass, ResponseWrapper<R> responseWrapper) {
    return rawResponse -> {
      TimingTool timer = new TimingTool();
      timer.timeStart(TIMING_EXECUTE_REQUEST);
      byte[] responseAsByte = rawResponse.getContentAsBytes();
      String rawResponseContent;
      try {
        rawResponseContent = new String(responseAsByte, getCharSet(rawResponse));
      } catch (UnsupportedEncodingException e) {
        throw new UnirestException(e);
      }
      R responseBody = getObjectMapper().readValue(rawResponseContent, responseClass);
      double parsingTime = timer.timeEnd(TIMING_EXECUTE_REQUEST);
      responseWrapper.setParsingTime(MathUtils.roundDouble(parsingTime, DECIMAL_PLACES));
      responseWrapper.setResponseSize(responseAsByte.length);
      return responseBody;
    };
  }

  protected String getCharSet(RawResponse rawResponse) {
    String contentType = rawResponse.getContentType();
    String responseCharset = getCharsetFromContentType(contentType);
    if (StringUtils.isNotEmpty(responseCharset)) {
      return responseCharset;
    }
    return rawResponse.getConfig().getDefaultResponseEncoding();
  }

  private String getCharsetFromContentType(String contentType) {
    if (contentType == null) {
      return null;
    }

    Matcher m = CHARSET_PATTERN.matcher(contentType);
    if (m.find()) {
      return m.group(1).trim().toUpperCase();
    }
    return null;
  }

  @Override
  public void close() {
    unirestInstance.shutDown();
  }

  UnirestInstance getUnirestInstance() {
    return unirestInstance;
  }
}
