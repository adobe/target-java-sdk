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
package com.adobe.target.edge.client.http;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import kong.unirest.HttpResponse;

public interface TargetHttpClient extends AutoCloseable {

  <T, R> HttpResponse<R> execute(
      Map<String, Object> queryParams, String url, T request, Class<R> response);

  <T, R> CompletableFuture<HttpResponse<R>> executeAsync(
      Map<String, Object> queryParams, String url, T request, Class<R> response);

  void addDefaultHeader(String key, String value);

  static TargetHttpClient createLoggingHttpClient(TargetHttpClient targetHttpClient) {
    return new TargetHttpClientLoggingDecorator(targetHttpClient);
  }
}
