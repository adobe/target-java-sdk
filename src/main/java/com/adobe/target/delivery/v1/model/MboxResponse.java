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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Mbox response object. */
@ApiModel(description = "Mbox response object. ")
@JsonPropertyOrder({
  MboxResponse.JSON_PROPERTY_INDEX,
  MboxResponse.JSON_PROPERTY_NAME,
  MboxResponse.JSON_PROPERTY_OPTIONS,
  MboxResponse.JSON_PROPERTY_METRICS,
  MboxResponse.JSON_PROPERTY_ANALYTICS,
  MboxResponse.JSON_PROPERTY_TRACE
})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-11T11:10:29.904-07:00[America/Los_Angeles]")
public class MboxResponse {
  public static final String JSON_PROPERTY_INDEX = "index";
  private Integer index;

  public static final String JSON_PROPERTY_NAME = "name";
  private String name;

  public static final String JSON_PROPERTY_OPTIONS = "options";
  private List<Option> options = null;

  public static final String JSON_PROPERTY_METRICS = "metrics";
  private List<Metric> metrics = null;

  public static final String JSON_PROPERTY_ANALYTICS = "analytics";
  private AnalyticsResponse analytics;

  public static final String JSON_PROPERTY_TRACE = "trace";
  private Map<String, Object> trace = null;

  public MboxResponse index(Integer index) {

    this.index = index;
    return this;
  }

  /**
   * Indicates that the response is for mbox with the same index, as was specified in the prefetch
   * or execute request.
   *
   * @return index
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "Indicates that the response is for mbox with the same index, as was specified in the prefetch or execute request. ")
  @JsonProperty(JSON_PROPERTY_INDEX)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Integer getIndex() {
    return index;
  }

  public void setIndex(Integer index) {
    this.index = index;
  }

  public MboxResponse name(String name) {

    this.name = name;
    return this;
  }

  /**
   * The name of the mbox. Since the same mbox name can be specified multiple times in the request
   * it should be used in conjunction with the index.
   *
   * @return name
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "The name of the mbox. Since the same mbox name can be specified multiple times in the request it should be used in conjunction with the index. ")
  @JsonProperty(JSON_PROPERTY_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public MboxResponse options(List<Option> options) {

    this.options = options;
    return this;
  }

  public MboxResponse addOptionsItem(Option optionsItem) {
    if (this.options == null) {
      this.options = new ArrayList<>();
    }
    this.options.add(optionsItem);
    return this;
  }

  /**
   * The option that was matched for the current mbox. Cannot be an offer with templates or a visual
   * offer.
   *
   * @return options
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "The option that was matched for the current mbox. Cannot be an offer with templates or a visual offer. ")
  @JsonProperty(JSON_PROPERTY_OPTIONS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public List<Option> getOptions() {
    return options;
  }

  public void setOptions(List<Option> options) {
    this.options = options;
  }

  public MboxResponse metrics(List<Metric> metrics) {

    this.metrics = metrics;
    return this;
  }

  public MboxResponse addMetricsItem(Metric metricsItem) {
    if (this.metrics == null) {
      this.metrics = new ArrayList<>();
    }
    this.metrics.add(metricsItem);
    return this;
  }

  /**
   * Click metrics.
   *
   * @return metrics
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Click metrics.")
  @JsonProperty(JSON_PROPERTY_METRICS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public List<Metric> getMetrics() {
    return metrics;
  }

  public void setMetrics(List<Metric> metrics) {
    this.metrics = metrics;
  }

  public MboxResponse analytics(AnalyticsResponse analytics) {

    this.analytics = analytics;
    return this;
  }

  /**
   * Get analytics
   *
   * @return analytics
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_ANALYTICS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public AnalyticsResponse getAnalytics() {
    return analytics;
  }

  public void setAnalytics(AnalyticsResponse analytics) {
    this.analytics = analytics;
  }

  public MboxResponse trace(Map<String, Object> trace) {

    this.trace = trace;
    return this;
  }

  public MboxResponse putTraceItem(String key, Object traceItem) {
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
    MboxResponse mboxResponse = (MboxResponse) o;
    return Objects.equals(this.index, mboxResponse.index)
        && Objects.equals(this.name, mboxResponse.name)
        && Objects.equals(this.options, mboxResponse.options)
        && Objects.equals(this.metrics, mboxResponse.metrics)
        && Objects.equals(this.analytics, mboxResponse.analytics)
        && Objects.equals(this.trace, mboxResponse.trace);
  }

  @Override
  public int hashCode() {
    return Objects.hash(index, name, options, metrics, analytics, trace);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MboxResponse {\n");
    sb.append("    index: ").append(toIndentedString(index)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    options: ").append(toIndentedString(options)).append("\n");
    sb.append("    metrics: ").append(toIndentedString(metrics)).append("\n");
    sb.append("    analytics: ").append(toIndentedString(analytics)).append("\n");
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
