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
 *
 * NOTE: This is an auto generated file. Do not edit directly.
 */
package com.adobe.target.delivery.v1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/** Metric */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Metric {
  @JsonProperty("type")
  private MetricType type = null;

  @JsonProperty("selector")
  private String selector;

  @JsonProperty("eventToken")
  private String eventToken;

  public Metric type(MetricType type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   *
   * @return type
   */
  public MetricType getType() {
    return type;
  }

  public void setType(MetricType type) {
    this.type = type;
  }

  public Metric selector(String selector) {
    this.selector = selector;
    return this;
  }

  /**
   * Get selector
   *
   * @return selector
   */
  public String getSelector() {
    return selector;
  }

  public void setSelector(String selector) {
    this.selector = selector;
  }

  public Metric eventToken(String eventToken) {
    this.eventToken = eventToken;
    return this;
  }

  /**
   * The event token that should be sent with the notifications in case the click occurred.
   *
   * @return eventToken
   */
  public String getEventToken() {
    return eventToken;
  }

  public void setEventToken(String eventToken) {
    this.eventToken = eventToken;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Metric metric = (Metric) o;
    return Objects.equals(this.type, metric.type)
        && Objects.equals(this.selector, metric.selector)
        && Objects.equals(this.eventToken, metric.eventToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, selector, eventToken);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Metric {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    selector: ").append(toIndentedString(selector)).append("\n");
    sb.append("    eventToken: ").append(toIndentedString(eventToken)).append("\n");
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
