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
package Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

/** Telemetry Entry. */
@ApiModel(description = "Telemetry Entry.")
@JsonPropertyOrder({
  TelemetryEntry.JSON_PROPERTY_REQUEST_ID,
  TelemetryEntry.JSON_PROPERTY_TIMESTAMP,
  TelemetryEntry.JSON_PROPERTY_EXECUTION,
  TelemetryEntry.JSON_PROPERTY_FEATURES
})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-10T11:24:27.013-07:00[America/Los_Angeles]")
public class TelemetryEntry {
  public static final String JSON_PROPERTY_REQUEST_ID = "requestId";
  private String requestId;

  public static final String JSON_PROPERTY_TIMESTAMP = "timestamp";
  private Long timestamp;

  public static final String JSON_PROPERTY_EXECUTION = "execution";
  private Integer execution;

  public static final String JSON_PROPERTY_FEATURES = "features";
  private TelemetryFeatures features;

  public TelemetryEntry requestId(String requestId) {

    this.requestId = requestId;
    return this;
  }

  /**
   * Request Id
   *
   * @return requestId
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Request Id")
  @JsonProperty(JSON_PROPERTY_REQUEST_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Timestamp of the entry, in milliseconds elapsed since UNIX epoch.")
  @JsonProperty(JSON_PROPERTY_TIMESTAMP)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public TelemetryEntry execution(Integer execution) {

    this.execution = execution;
    return this;
  }

  /**
   * Execution time in milliseconds.
   *
   * @return execution
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Execution time in milliseconds.")
  @JsonProperty(JSON_PROPERTY_EXECUTION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Integer getExecution() {
    return execution;
  }

  public void setExecution(Integer execution) {
    this.execution = execution;
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
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_FEATURES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public TelemetryFeatures getFeatures() {
    return features;
  }

  public void setFeatures(TelemetryFeatures features) {
    this.features = features;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TelemetryEntry telemetryEntry = (TelemetryEntry) o;
    return Objects.equals(this.requestId, telemetryEntry.requestId)
        && Objects.equals(this.timestamp, telemetryEntry.timestamp)
        && Objects.equals(this.execution, telemetryEntry.execution)
        && Objects.equals(this.features, telemetryEntry.features);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, timestamp, execution, features);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TelemetryEntry {\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    execution: ").append(toIndentedString(execution)).append("\n");
    sb.append("    features: ").append(toIndentedString(features)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
