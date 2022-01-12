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
package com.adobe.target.edge.client.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import kong.unirest.Config;
import kong.unirest.Headers;
import kong.unirest.HttpResponseSummary;
import kong.unirest.RawResponse;

public class MockRawResponse implements RawResponse {

  @Override
  public int getStatus() {
    return 200;
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
  public String getContentType() {
    return "application/json;charset=UTF-8";
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
