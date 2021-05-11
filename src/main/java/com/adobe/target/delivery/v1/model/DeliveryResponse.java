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
import java.util.Objects;

/**
 * Delivery response. Returned content will be based upon the request and client&#39;s active
 * activities.
 */
@ApiModel(
    description =
        "Delivery response. Returned content will be based upon the request and client's active activities.")
@JsonPropertyOrder({
  DeliveryResponse.JSON_PROPERTY_STATUS,
  DeliveryResponse.JSON_PROPERTY_REQUEST_ID,
  DeliveryResponse.JSON_PROPERTY_ID,
  DeliveryResponse.JSON_PROPERTY_CLIENT,
  DeliveryResponse.JSON_PROPERTY_EDGE_HOST,
  DeliveryResponse.JSON_PROPERTY_EXECUTE,
  DeliveryResponse.JSON_PROPERTY_PREFETCH,
  DeliveryResponse.JSON_PROPERTY_NOTIFICATIONS
})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-11T11:10:29.904-07:00[America/Los_Angeles]")
public class DeliveryResponse {
  public static final String JSON_PROPERTY_STATUS = "status";
  private Integer status;

  public static final String JSON_PROPERTY_REQUEST_ID = "requestId";
  private String requestId;

  public static final String JSON_PROPERTY_ID = "id";
  private VisitorId id;

  public static final String JSON_PROPERTY_CLIENT = "client";
  private String client;

  public static final String JSON_PROPERTY_EDGE_HOST = "edgeHost";
  private String edgeHost;

  public static final String JSON_PROPERTY_EXECUTE = "execute";
  private ExecuteResponse execute;

  public static final String JSON_PROPERTY_PREFETCH = "prefetch";
  private PrefetchResponse prefetch;

  public static final String JSON_PROPERTY_NOTIFICATIONS = "notifications";
  private NotificationResponse notifications;

  public DeliveryResponse status(Integer status) {

    this.status = status;
    return this;
  }

  /**
   * Get status
   *
   * @return status
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_STATUS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public DeliveryResponse requestId(String requestId) {

    this.requestId = requestId;
    return this;
  }

  /**
   * ID of the processed request. If it&#39;s not sent in the request, a random ID (UUID) is
   * generated and returned with the response.
   *
   * @return requestId
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "ID of the processed request. If it's not sent in the request, a random ID (UUID) is generated and returned with the response. ")
  @JsonProperty(JSON_PROPERTY_REQUEST_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public DeliveryResponse id(VisitorId id) {

    this.id = id;
    return this;
  }

  /**
   * Get id
   *
   * @return id
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public VisitorId getId() {
    return id;
  }

  public void setId(VisitorId id) {
    this.id = id;
  }

  public DeliveryResponse client(String client) {

    this.client = client;
    return this;
  }

  /**
   * Client&#39;s code. The one which was sent in the request&#39;s path.
   *
   * @return client
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Client's code. The one which was sent in the request's path.")
  @JsonProperty(JSON_PROPERTY_CLIENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getClient() {
    return client;
  }

  public void setClient(String client) {
    this.client = client;
  }

  public DeliveryResponse edgeHost(String edgeHost) {

    this.edgeHost = edgeHost;
    return this;
  }

  /**
   * Cluster host name that served the response. Ideally, all subsequent requests should be made to
   * that host.
   *
   * @return edgeHost
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "Cluster host name that served the response. Ideally, all subsequent requests should be made to that host.")
  @JsonProperty(JSON_PROPERTY_EDGE_HOST)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getEdgeHost() {
    return edgeHost;
  }

  public void setEdgeHost(String edgeHost) {
    this.edgeHost = edgeHost;
  }

  public DeliveryResponse execute(ExecuteResponse execute) {

    this.execute = execute;
    return this;
  }

  /**
   * Get execute
   *
   * @return execute
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_EXECUTE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public ExecuteResponse getExecute() {
    return execute;
  }

  public void setExecute(ExecuteResponse execute) {
    this.execute = execute;
  }

  public DeliveryResponse prefetch(PrefetchResponse prefetch) {

    this.prefetch = prefetch;
    return this;
  }

  /**
   * Get prefetch
   *
   * @return prefetch
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_PREFETCH)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public PrefetchResponse getPrefetch() {
    return prefetch;
  }

  public void setPrefetch(PrefetchResponse prefetch) {
    this.prefetch = prefetch;
  }

  public DeliveryResponse notifications(NotificationResponse notifications) {

    this.notifications = notifications;
    return this;
  }

  /**
   * Get notifications
   *
   * @return notifications
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_NOTIFICATIONS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public NotificationResponse getNotifications() {
    return notifications;
  }

  public void setNotifications(NotificationResponse notifications) {
    this.notifications = notifications;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeliveryResponse deliveryResponse = (DeliveryResponse) o;
    return Objects.equals(this.status, deliveryResponse.status)
        && Objects.equals(this.requestId, deliveryResponse.requestId)
        && Objects.equals(this.id, deliveryResponse.id)
        && Objects.equals(this.client, deliveryResponse.client)
        && Objects.equals(this.edgeHost, deliveryResponse.edgeHost)
        && Objects.equals(this.execute, deliveryResponse.execute)
        && Objects.equals(this.prefetch, deliveryResponse.prefetch)
        && Objects.equals(this.notifications, deliveryResponse.notifications);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, requestId, id, client, edgeHost, execute, prefetch, notifications);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DeliveryResponse {\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    client: ").append(toIndentedString(client)).append("\n");
    sb.append("    edgeHost: ").append(toIndentedString(edgeHost)).append("\n");
    sb.append("    execute: ").append(toIndentedString(execute)).append("\n");
    sb.append("    prefetch: ").append(toIndentedString(prefetch)).append("\n");
    sb.append("    notifications: ").append(toIndentedString(notifications)).append("\n");
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
