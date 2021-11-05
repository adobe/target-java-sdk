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

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import kong.unirest.HttpResponse;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TargetHttpClientLoggingDecorator implements TargetHttpClient {

  private static final Logger logger = LoggerFactory.getLogger(DefaultTargetHttpClient.class);
  private final TargetHttpClient delegate;

  public TargetHttpClientLoggingDecorator(TargetHttpClient delegate) {
    this.delegate = delegate;
  }

  @Override
  public <T, R> ResponseWrapper<R> execute(
      Map<String, Object> queryParams, String url, T request, Class<R> response) {
    logger.debug("Request: Url:{} QueryParams:{} RequestBody:{}", url, queryParams, request);
    ResponseWrapper<R> execute = delegate.execute(queryParams, url, request, response);
    logResponse(execute.getHttpResponse());
    return execute;
  }

  @Override
  public <T, R> CompletableFuture<ResponseWrapper<R>> executeAsync(
      Map<String, Object> queryParams, String url, T request, Class<R> response) {
    logger.debug("AsyncRequest: Url:{} QueryParams:{} RequestBody:{}", url, queryParams, request);
    CompletableFuture<ResponseWrapper<R>> executeAsync =
        delegate.executeAsync(queryParams, url, request, response);
    executeAsync.thenAccept(
        responseWrapper -> {
          logResponse(responseWrapper.getHttpResponse());
        });
    return executeAsync;
  }

  private <R> void logResponse(HttpResponse<R> execute) {
    R body = execute.getBody();
    if (execute.getStatus() == HttpStatus.SC_OK && body != null) {
      logger.debug("Response: ResponseBody:{}", body);
      return;
    }
    logger.error(
        "Error occurred while fetching response from target: "
            + "Status: {} Message: {} ParsingError: {} ResponseBody:{} ",
        execute.getStatus(),
        execute.getStatusText(),
        execute.getParsingError(),
        body);
  }

  @Override
  public void addDefaultHeader(String key, String value) {
    logger.debug("Adding default header: key:{}, value:{}", key, value);
    delegate.addDefaultHeader(key, value);
  }

  @Override
  public void close() throws Exception {
    delegate.close();
  }
}
