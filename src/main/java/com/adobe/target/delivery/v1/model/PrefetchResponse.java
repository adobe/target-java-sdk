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
import java.util.List;
import java.util.Objects;

/** The object that will return the prefetched content based on the request, active activites etc */
@ApiModel(
    description =
        "The object that will return the prefetched content based on the request, active activites etc ")
@JsonPropertyOrder({
  PrefetchResponse.JSON_PROPERTY_VIEWS,
  PrefetchResponse.JSON_PROPERTY_PAGE_LOAD,
  PrefetchResponse.JSON_PROPERTY_MBOXES,
  PrefetchResponse.JSON_PROPERTY_METRICS
})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-11T11:10:29.904-07:00[America/Los_Angeles]")
public class PrefetchResponse {
  public static final String JSON_PROPERTY_VIEWS = "views";
  private List<View> views = null;

  public static final String JSON_PROPERTY_PAGE_LOAD = "pageLoad";
  private PageLoadResponse pageLoad;

  public static final String JSON_PROPERTY_MBOXES = "mboxes";
  private List<PrefetchMboxResponse> mboxes = null;

  public static final String JSON_PROPERTY_METRICS = "metrics";
  private List<Metric> metrics = null;

  public PrefetchResponse views(List<View> views) {

    this.views = views;
    return this;
  }

  public PrefetchResponse addViewsItem(View viewsItem) {
    if (this.views == null) {
      this.views = new ArrayList<>();
    }
    this.views.add(viewsItem);
    return this;
  }

  /**
   * Contains all the views matching the request.
   *
   * @return views
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Contains all the views matching the request. ")
  @JsonProperty(JSON_PROPERTY_VIEWS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public List<View> getViews() {
    return views;
  }

  public void setViews(List<View> views) {
    this.views = views;
  }

  public PrefetchResponse pageLoad(PageLoadResponse pageLoad) {

    this.pageLoad = pageLoad;
    return this;
  }

  /**
   * Get pageLoad
   *
   * @return pageLoad
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_PAGE_LOAD)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public PageLoadResponse getPageLoad() {
    return pageLoad;
  }

  public void setPageLoad(PageLoadResponse pageLoad) {
    this.pageLoad = pageLoad;
  }

  public PrefetchResponse mboxes(List<PrefetchMboxResponse> mboxes) {

    this.mboxes = mboxes;
    return this;
  }

  public PrefetchResponse addMboxesItem(PrefetchMboxResponse mboxesItem) {
    if (this.mboxes == null) {
      this.mboxes = new ArrayList<>();
    }
    this.mboxes.add(mboxesItem);
    return this;
  }

  /**
   * Prefetched mboxes, including content and notification tokens to be sent back when the mboxes
   * are displayed.
   *
   * @return mboxes
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "Prefetched mboxes, including content and notification tokens to be sent back when the mboxes are displayed. ")
  @JsonProperty(JSON_PROPERTY_MBOXES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public List<PrefetchMboxResponse> getMboxes() {
    return mboxes;
  }

  public void setMboxes(List<PrefetchMboxResponse> mboxes) {
    this.mboxes = mboxes;
  }

  public PrefetchResponse metrics(List<Metric> metrics) {

    this.metrics = metrics;
    return this;
  }

  public PrefetchResponse addMetricsItem(Metric metricsItem) {
    if (this.metrics == null) {
      this.metrics = new ArrayList<>();
    }
    this.metrics.add(metricsItem);
    return this;
  }

  /**
   * The click track metrics that are not assigned to a view but are present in activites that have
   * views, except in case the same activity is serving content for selectors both assinged to a
   * view and selectors without any views, and having click track metrics that are not assotiated
   * with any view within the activity, then: * in case of a prefetch only request, these metrics
   * (tokens) will be set in the prefetch response&#39;s metrics. * in case of an execute only
   * request, the metrics will be set in the page load response&#39;s metrics. * in case of a
   * request, with both, execute and prefetch, metrics will be set in the page load response&#39;s
   * metrics only.
   *
   * @return metrics
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "The click track metrics that are not assigned to a view but are present in activites that have views, except in case the same activity is serving content for selectors both assinged to a view and selectors without any views, and having click track metrics that are not assotiated with any view within the activity, then:   * in case of a prefetch only request, these metrics (tokens) will be set in the prefetch response's metrics.   * in case of an execute only request, the metrics will be set in the page load response's metrics.   * in case of a request, with both, execute and prefetch, metrics will be set in the page load response's     metrics only. ")
  @JsonProperty(JSON_PROPERTY_METRICS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public List<Metric> getMetrics() {
    return metrics;
  }

  public void setMetrics(List<Metric> metrics) {
    this.metrics = metrics;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PrefetchResponse prefetchResponse = (PrefetchResponse) o;
    return Objects.equals(this.views, prefetchResponse.views)
        && Objects.equals(this.pageLoad, prefetchResponse.pageLoad)
        && Objects.equals(this.mboxes, prefetchResponse.mboxes)
        && Objects.equals(this.metrics, prefetchResponse.metrics);
  }

  @Override
  public int hashCode() {
    return Objects.hash(views, pageLoad, mboxes, metrics);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PrefetchResponse {\n");
    sb.append("    views: ").append(toIndentedString(views)).append("\n");
    sb.append("    pageLoad: ").append(toIndentedString(pageLoad)).append("\n");
    sb.append("    mboxes: ").append(toIndentedString(mboxes)).append("\n");
    sb.append("    metrics: ").append(toIndentedString(metrics)).append("\n");
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
