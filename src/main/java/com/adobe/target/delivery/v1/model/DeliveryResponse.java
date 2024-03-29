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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Delivery response. Returned content will be based upon the request and client&#39;s active
 * activities.
 */
public class DeliveryResponse {
  @JsonProperty("status")
  private Integer status;

  @JsonProperty("requestId")
  private String requestId;

  @JsonProperty("id")
  private VisitorId id;

  @JsonProperty("client")
  private String client;

  @JsonProperty("edgeHost")
  private String edgeHost;

  @JsonProperty("execute")
  private ExecuteResponse execute;

  @JsonProperty("prefetch")
  private PrefetchResponse prefetch;

  @JsonProperty("notifications")
  private List<NotificationResponse> notifications = new ArrayList<>();

  @JsonProperty("telemetryServerToken")
  private String telemetryServerToken;

  public DeliveryResponse status(Integer status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   *
   * @return status
   */
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
  public PrefetchResponse getPrefetch() {
    return prefetch;
  }

  public void setPrefetch(PrefetchResponse prefetch) {
    this.prefetch = prefetch;
  }

  public DeliveryResponse notifications(List<NotificationResponse> notifications) {
    this.notifications = notifications;
    return this;
  }

  public DeliveryResponse addNotificationsItem(NotificationResponse notificationsItem) {
    if (this.notifications == null) {
      this.notifications = new ArrayList<>();
    }
    this.notifications.add(notificationsItem);
    return this;
  }

  /**
   * Get notifications
   *
   * @return notifications
   */
  public List<NotificationResponse> getNotifications() {
    return notifications;
  }

  public void setNotifications(List<NotificationResponse> notifications) {
    this.notifications = notifications;
  }

  public DeliveryResponse telemetryServerToken(String telemetryServerToken) {
    this.telemetryServerToken = telemetryServerToken;
    return this;
  }

  /**
   * Encoded data with request telemetry collected from Delivery API
   *
   * @return telemetryServerToken
   */
  public String getTelemetryServerToken() {
    return telemetryServerToken;
  }

  public void setTelemetryServerToken(String telemetryServerToken) {
    this.telemetryServerToken = telemetryServerToken;
  }

  @Override
  public boolean equals(Object o) {
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
        && Objects.equals(this.notifications, deliveryResponse.notifications)
        && Objects.equals(this.telemetryServerToken, deliveryResponse.telemetryServerToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        status,
        requestId,
        id,
        client,
        edgeHost,
        execute,
        prefetch,
        notifications,
        telemetryServerToken);
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
    sb.append("    telemetryServerToken: ")
        .append(toIndentedString(telemetryServerToken))
        .append("\n");
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
