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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/** Telemetry Entry. */
public class TelemetryEntry {
  @JsonProperty("requestId")
  private String requestId;

  @JsonProperty("timestamp")
  private Long timestamp;

  @JsonProperty("mode")
  private ExecutionMode mode;

  @JsonProperty("execution")
  private Double execution;

  @JsonProperty("parsing")
  private Double parsing;

  @JsonProperty("features")
  private TelemetryFeatures features;

  @JsonProperty("request")
  private TelemetryRequest request;

  public TelemetryEntry requestId(String requestId) {
    this.requestId = requestId;
    return this;
  }

  /**
   * Request Id
   *
   * @return requestId
   */
  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public TelemetryEntry timestamp(Long timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * Timestamp of the entry, in milliseconds elapsed since UNIX epoch.
   *
   * @return timestamp
   */
  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public TelemetryEntry mode(ExecutionMode mode) {
    this.mode = mode;
    return this;
  }

  /**
   * Get mode
   *
   * @return mode
   */
  public ExecutionMode getMode() {
    return mode;
  }

  public void setMode(ExecutionMode mode) {
    this.mode = mode;
  }

  public TelemetryEntry execution(Double execution) {
    this.execution = execution;
    return this;
  }

  /**
   * Execution time in milliseconds.
   *
   * @return execution
   */
  public Double getExecution() {
    return execution;
  }

  public void setExecution(Double execution) {
    this.execution = execution;
  }

  public TelemetryEntry parsing(Double parsing) {
    this.parsing = parsing;
    return this;
  }

  /**
   * Response parsing time, in milliseconds elapsed since UNIX epoch.
   *
   * @return parsing
   */
  public Double getParsing() {
    return parsing;
  }

  public void setParsing(Double parsing) {
    this.parsing = parsing;
  }

  public TelemetryEntry features(TelemetryFeatures features) {
    this.features = features;
    return this;
  }

  /**
   * Get features
   *
   * @return features
   */
  public TelemetryFeatures getFeatures() {
    return features;
  }

  public void setFeatures(TelemetryFeatures features) {
    this.features = features;
  }

  public TelemetryEntry request(TelemetryRequest request) {
    this.request = request;
    return this;
  }

  /**
   * Get request
   *
   * @return request
   */
  public TelemetryRequest getRequest() {
    return request;
  }

  public void setRequest(TelemetryRequest request) {
    this.request = request;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TelemetryEntry telemetryEntry = (TelemetryEntry) o;
    return Objects.equals(this.requestId, telemetryEntry.requestId)
        && Objects.equals(this.timestamp, telemetryEntry.timestamp)
        && Objects.equals(this.mode, telemetryEntry.mode)
        && Objects.equals(this.execution, telemetryEntry.execution)
        && Objects.equals(this.parsing, telemetryEntry.parsing)
        && Objects.equals(this.features, telemetryEntry.features)
        && Objects.equals(this.request, telemetryEntry.request);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, timestamp, mode, execution, parsing, features, request);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TelemetryEntry {\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    mode: ").append(toIndentedString(mode)).append("\n");
    sb.append("    execution: ").append(toIndentedString(execution)).append("\n");
    sb.append("    parsing: ").append(toIndentedString(parsing)).append("\n");
    sb.append("    features: ").append(toIndentedString(features)).append("\n");
    sb.append("    request: ").append(toIndentedString(request)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
