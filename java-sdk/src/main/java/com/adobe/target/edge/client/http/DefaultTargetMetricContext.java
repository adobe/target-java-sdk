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

public class DefaultTargetMetricContext implements TargetMetricContext {

  private final String url;
  private final String message;
  private final int status;
  private final int executionTime;

  public DefaultTargetMetricContext(String url, int status, String message, int executionTime) {
    this.url = url;
    this.status = status;
    this.message = message;
    this.executionTime = executionTime;
  }

  @Override
  public String getUrl() {
    return url;
  }

  @Override
  public int getStatus() {
    return status;
  }

  @Override
  public String getStatusMessage() {
    return message;
  }

  @Override
  public int getExecutionTime() {
    return executionTime;
  }
}
