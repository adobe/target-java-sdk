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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Notification response. Contains the result of a processed notification. */
@ApiModel(description = "Notification response. Contains the result of a processed notification. ")
@JsonPropertyOrder({
  NotificationResponse.JSON_PROPERTY_ID,
  NotificationResponse.JSON_PROPERTY_TRACE
})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-11T11:10:29.904-07:00[America/Los_Angeles]")
public class NotificationResponse {
  public static final String JSON_PROPERTY_ID = "id";
  private String id;

  public static final String JSON_PROPERTY_TRACE = "trace";
  private Map<String, Object> trace = new HashMap<>();

  public NotificationResponse id(String id) {

    this.id = id;
    return this;
  }

  /**
   * Notification id which indicates that the notification was processed successfully.
   *
   * @return id
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value = "Notification id which indicates that the notification was processed successfully. ")
  @JsonProperty(JSON_PROPERTY_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public NotificationResponse trace(Map<String, Object> trace) {

    this.trace = trace;
    return this;
  }

  public NotificationResponse putTraceItem(String key, Object traceItem) {
    if (this.trace == null) {
      this.trace = new HashMap<>();
    }
    this.trace.put(key, traceItem);
    return this;
  }

  /**
   * The object containing all trace data for the request, only present if the trace token was
   * provided in the request.
   *
   * @return trace
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "The object containing all trace data for the request, only present if the trace token was provided in the request. ")
  @JsonProperty(JSON_PROPERTY_TRACE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Map<String, Object> getTrace() {
    return trace;
  }

  public void setTrace(Map<String, Object> trace) {
    this.trace = trace;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NotificationResponse notificationResponse = (NotificationResponse) o;
    return Objects.equals(this.id, notificationResponse.id)
        && Objects.equals(this.trace, notificationResponse.trace);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, trace);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NotificationResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    trace: ").append(toIndentedString(trace)).append("\n");
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
