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

import kong.unirest.HttpResponse;

public final class ResponseWrapper<R> {

  private double parsingTime;

  private long responseSize;

  private HttpResponse<R> httpResponse;

  public ResponseWrapper() {}

  public double getParsingTime() {
    return parsingTime;
  }

  public void setParsingTime(double parsingTime) {
    this.parsingTime = parsingTime;
  }

  public long getResponseSize() {
    return responseSize;
  }

  public void setResponseSize(long responseSize) {
    this.responseSize = responseSize;
  }

  public HttpResponse<R> getHttpResponse() {
    return httpResponse;
  }

  public void setHttpResponse(HttpResponse<R> httpResponse) {
    this.httpResponse = httpResponse;
  }
}
