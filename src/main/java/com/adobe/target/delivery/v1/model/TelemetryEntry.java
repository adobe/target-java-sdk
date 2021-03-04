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
 *
 * NOTE: This is an auto generated file. Do not edit directly.
 */
package com.adobe.target.delivery.v1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TelemetryEntry {
  @JsonProperty("requestId")
  private String requestId;

  @JsonProperty("timestamp")
  private Long timestamp;

  @JsonProperty("execution")
  private double execution;

  @JsonProperty("features")
  private TelemetryFeatures features;

  public TelemetryEntry requestId(String requestId) {
    this.requestId = requestId;
    return this;
  }

  public TelemetryEntry timestamp(Long timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  public TelemetryEntry execution(double execution) {
    this.execution = execution;
    return this;
  }

  public TelemetryEntry features(TelemetryFeatures features) {
    this.features = features;
    return this;
  }

  public String getRequestId() {
    return requestId;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public double getExecution() {
    return execution;
  }

  public TelemetryFeatures getFeatures() {
    return features;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TelemetryEntry that = (TelemetryEntry) o;
    return Objects.equals(requestId, that.requestId)
        && Objects.equals(timestamp, that.timestamp)
        && Objects.equals(execution, that.execution)
        && Objects.equals(features, that.features);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, timestamp, execution, features);
  }

  @Override
  public String toString() {
    return "TelemetryEntry{"
        + "requestId='"
        + requestId
        + '\''
        + ", timestamp="
        + timestamp
        + ", execution="
        + execution
        + ", features="
        + features
        + '}';
  }
}
