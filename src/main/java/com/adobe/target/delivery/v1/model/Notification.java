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

/**
 * Notification object is used to sent notificaitons regarding what prefetched content was displayed
 * (for views, pageLoad or regional mboxes), which views, pages and mboxes were visited (triggered),
 * and which elements were clicked. Notification request detail will be validated and in case there
 * are validation errors with any token or timestamp the entire request will be invalidated, so
 * there are no cases of inconsistent data, caused by a partially processed notification. that may
 * happen with a partially processed notification. This is different from the approach in the batch
 * mbox v2 API. Mboxes and views are mutually exclusive.
 */
@ApiModel(
    description =
        "Notification object is used to sent notificaitons regarding what prefetched content was displayed (for views, pageLoad or regional mboxes), which views, pages and mboxes were visited (triggered), and which elements were clicked. Notification request detail will be validated and in case there are validation errors with any token or timestamp the entire request will be invalidated, so there are no cases of inconsistent data, caused by a partially processed notification. that may happen with a partially processed notification. This is different from the approach in the batch mbox v2 API. Mboxes and views are mutually exclusive. ")
@JsonPropertyOrder({
  Notification.JSON_PROPERTY_ID,
  Notification.JSON_PROPERTY_IMPRESSION_ID,
  Notification.JSON_PROPERTY_TYPE,
  Notification.JSON_PROPERTY_TIMESTAMP,
  Notification.JSON_PROPERTY_TOKENS,
  Notification.JSON_PROPERTY_MBOX,
  Notification.JSON_PROPERTY_VIEW,
  Notification.JSON_PROPERTY_PAGE_LOAD
})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-11T11:10:29.904-07:00[America/Los_Angeles]")
public class Notification extends RequestDetails {
  public static final String JSON_PROPERTY_ID = "id";
  private String id;

  public static final String JSON_PROPERTY_IMPRESSION_ID = "impressionId";
  private String impressionId;

  public static final String JSON_PROPERTY_TYPE = "type";
  private MetricType type;

  public static final String JSON_PROPERTY_TIMESTAMP = "timestamp";
  private Long timestamp;

  public static final String JSON_PROPERTY_TOKENS = "tokens";
  private List<String> tokens = new ArrayList<>();

  public static final String JSON_PROPERTY_MBOX = "mbox";
  private NotificationMbox mbox;

  public static final String JSON_PROPERTY_VIEW = "view";
  private NotificationView view;

  public static final String JSON_PROPERTY_PAGE_LOAD = "pageLoad";
  private NotificationPageLoad pageLoad;

  public Notification id(String id) {

    this.id = id;
    return this;
  }

  /**
   * Notification id will be returned in response and will indicate that the notification was
   * processed successfully.
   *
   * @return id
   */
  @ApiModelProperty(
      required = true,
      value =
          "Notification id will be returned in response and will indicate that the notification was processed successfully. ")
  @JsonProperty(JSON_PROPERTY_ID)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Notification impressionId(String impressionId) {

    this.impressionId = impressionId;
    return this;
  }

  /**
   * Impression id is used to stitch (link) the current notification with a previous notification or
   * execute request. In case they both of them match, the second and other subsequent requests will
   * not generate a new impression to the activity, experience etc.
   *
   * @return impressionId
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "Impression id is used to stitch (link) the current notification with a previous notification or execute request. In case they both of them match, the second and other subsequent requests will not generate a new impression to the activity, experience etc. ")
  @JsonProperty(JSON_PROPERTY_IMPRESSION_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getImpressionId() {
    return impressionId;
  }

  public void setImpressionId(String impressionId) {
    this.impressionId = impressionId;
  }

  public Notification type(MetricType type) {

    this.type = type;
    return this;
  }

  /**
   * Get type
   *
   * @return type
   */
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_TYPE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public MetricType getType() {
    return type;
  }

  public void setType(MetricType type) {
    this.type = type;
  }

  public Notification timestamp(Long timestamp) {

    this.timestamp = timestamp;
    return this;
  }

  /**
   * Timestamp of the notification, in milliseconds elapsed since UNIX epoch.
   *
   * @return timestamp
   */
  @ApiModelProperty(
      required = true,
      value = "Timestamp of the notification, in milliseconds elapsed since UNIX epoch.")
  @JsonProperty(JSON_PROPERTY_TIMESTAMP)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public Notification tokens(List<String> tokens) {

    this.tokens = tokens;
    return this;
  }

  public Notification addTokensItem(String tokensItem) {
    if (this.tokens == null) {
      this.tokens = new ArrayList<>();
    }
    this.tokens.add(tokensItem);
    return this;
  }

  /**
   * A list of tokens for displayed content or clicked selectors, based on the type of notification.
   *
   * @return tokens
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "A list of tokens for displayed content or clicked selectors, based on the type of notification.")
  @JsonProperty(JSON_PROPERTY_TOKENS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public List<String> getTokens() {
    return tokens;
  }

  public void setTokens(List<String> tokens) {
    this.tokens = tokens;
  }

  public Notification mbox(NotificationMbox mbox) {

    this.mbox = mbox;
    return this;
  }

  /**
   * Get mbox
   *
   * @return mbox
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_MBOX)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public NotificationMbox getMbox() {
    return mbox;
  }

  public void setMbox(NotificationMbox mbox) {
    this.mbox = mbox;
  }

  public Notification view(NotificationView view) {

    this.view = view;
    return this;
  }

  /**
   * Get view
   *
   * @return view
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_VIEW)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public NotificationView getView() {
    return view;
  }

  public void setView(NotificationView view) {
    this.view = view;
  }

  public Notification pageLoad(NotificationPageLoad pageLoad) {

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
  public NotificationPageLoad getPageLoad() {
    return pageLoad;
  }

  public void setPageLoad(NotificationPageLoad pageLoad) {
    this.pageLoad = pageLoad;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Notification notification = (Notification) o;
    return Objects.equals(this.id, notification.id)
        && Objects.equals(this.impressionId, notification.impressionId)
        && Objects.equals(this.type, notification.type)
        && Objects.equals(this.timestamp, notification.timestamp)
        && Objects.equals(this.tokens, notification.tokens)
        && Objects.equals(this.mbox, notification.mbox)
        && Objects.equals(this.view, notification.view)
        && Objects.equals(this.pageLoad, notification.pageLoad)
        && super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id, impressionId, type, timestamp, tokens, mbox, view, pageLoad, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Notification {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    impressionId: ").append(toIndentedString(impressionId)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    tokens: ").append(toIndentedString(tokens)).append("\n");
    sb.append("    mbox: ").append(toIndentedString(mbox)).append("\n");
    sb.append("    view: ").append(toIndentedString(view)).append("\n");
    sb.append("    pageLoad: ").append(toIndentedString(pageLoad)).append("\n");
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
