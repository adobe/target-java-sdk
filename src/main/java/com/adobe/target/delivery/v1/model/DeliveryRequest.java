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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** DeliveryRequest */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeliveryRequest {
  @JsonProperty("requestId")
  private String requestId;

  @JsonProperty("impressionId")
  private String impressionId;

  @JsonProperty("id")
  private VisitorId id = null;

  @JsonProperty("environmentId")
  private Long environmentId;

  @JsonProperty("property")
  private Property property = null;

  @JsonProperty("trace")
  private Trace trace = null;

  @JsonProperty("context")
  private Context context = null;

  @JsonProperty("experienceCloud")
  private ExperienceCloud experienceCloud = null;

  @JsonProperty("execute")
  private ExecuteRequest execute = null;

  @JsonProperty("prefetch")
  private PrefetchRequest prefetch = null;

  @JsonProperty("notifications")
  private List<Notification> notifications = new ArrayList<>();

  @JsonProperty("telemetry")
  private Telemetry telemetry = null;

  @JsonProperty("qaMode")
  private QAMode qaMode = null;

  public DeliveryRequest requestId(String requestId) {
    this.requestId = requestId;
    return this;
  }

  /**
   * The request ID that will be returned in the response. In case it is not provided, an UUID is
   * generated and returned automatically.
   *
   * @return requestId
   */
  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public DeliveryRequest impressionId(String impressionId) {
    this.impressionId = impressionId;
    return this;
  }

  /**
   * If not present it will be automatically generated (UUID). If present, second and subsequent
   * requests with the same id will not increment impressions to activities/metrics. Similar to page
   * id.
   *
   * @return impressionId
   */
  public String getImpressionId() {
    return impressionId;
  }

  public void setImpressionId(String impressionId) {
    this.impressionId = impressionId;
  }

  public DeliveryRequest id(VisitorId id) {
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

  public DeliveryRequest environmentId(Long environmentId) {
    this.environmentId = environmentId;
    return this;
  }

  /**
   * Valid client environment id. If not specified host will be determined base on the provided
   * host.
   *
   * @return environmentId
   */
  public Long getEnvironmentId() {
    return environmentId;
  }

  public void setEnvironmentId(Long environmentId) {
    this.environmentId = environmentId;
  }

  public DeliveryRequest property(Property property) {
    this.property = property;
    return this;
  }

  /**
   * Get property
   *
   * @return property
   */
  public Property getProperty() {
    return property;
  }

  public void setProperty(Property property) {
    this.property = property;
  }

  public DeliveryRequest trace(Trace trace) {
    this.trace = trace;
    return this;
  }

  /**
   * Get trace
   *
   * @return trace
   */
  public Trace getTrace() {
    return trace;
  }

  public void setTrace(Trace trace) {
    this.trace = trace;
  }

  public DeliveryRequest context(Context context) {
    this.context = context;
    return this;
  }

  /**
   * Get context
   *
   * @return context
   */
  public Context getContext() {
    return context;
  }

  public void setContext(Context context) {
    this.context = context;
  }

  public DeliveryRequest experienceCloud(ExperienceCloud experienceCloud) {
    this.experienceCloud = experienceCloud;
    return this;
  }

  /**
   * Get experienceCloud
   *
   * @return experienceCloud
   */
  public ExperienceCloud getExperienceCloud() {
    return experienceCloud;
  }

  public void setExperienceCloud(ExperienceCloud experienceCloud) {
    this.experienceCloud = experienceCloud;
  }

  public DeliveryRequest execute(ExecuteRequest execute) {
    this.execute = execute;
    return this;
  }

  /**
   * Get execute
   *
   * @return execute
   */
  public ExecuteRequest getExecute() {
    return execute;
  }

  public void setExecute(ExecuteRequest execute) {
    this.execute = execute;
  }

  public DeliveryRequest prefetch(PrefetchRequest prefetch) {
    this.prefetch = prefetch;
    return this;
  }

  /**
   * Get prefetch
   *
   * @return prefetch
   */
  public PrefetchRequest getPrefetch() {
    return prefetch;
  }

  public void setPrefetch(PrefetchRequest prefetch) {
    this.prefetch = prefetch;
  }

  public DeliveryRequest telemetry(Telemetry telemetry) {
    this.telemetry = telemetry;
    return this;
  }

  public Telemetry getTelemetry() {
    return telemetry;
  }

  public DeliveryRequest addTelemetryEntry(TelemetryEntry telemetryEntry) {
    if (this.telemetry == null) {
      this.telemetry = new Telemetry();
    }
    this.telemetry.addTelemetryEntry(telemetryEntry);
    return this;
  }

  public DeliveryRequest notifications(List<Notification> notifications) {
    this.notifications = notifications;
    return this;
  }

  public DeliveryRequest addNotificationsItem(Notification notificationsItem) {
    if (this.notifications == null) {
      this.notifications = new ArrayList<>();
    }
    this.notifications.add(notificationsItem);
    return this;
  }

  /**
   * Notifications for the displayed content, clicked selectors, and/or visited views or mboxes.
   *
   * @return notifications
   */
  public List<Notification> getNotifications() {
    return notifications;
  }

  public void setNotifications(List<Notification> notifications) {
    this.notifications = notifications;
  }

  public DeliveryRequest qaMode(QAMode qaMode) {
    this.qaMode = qaMode;
    return this;
  }

  /**
   * Get qaMode
   *
   * @return qaMode
   */
  public QAMode getQaMode() {
    return qaMode;
  }

  public void setQaMode(QAMode qaMode) {
    this.qaMode = qaMode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeliveryRequest deliveryRequest = (DeliveryRequest) o;
    return Objects.equals(this.requestId, deliveryRequest.requestId)
        && Objects.equals(this.impressionId, deliveryRequest.impressionId)
        && Objects.equals(this.id, deliveryRequest.id)
        && Objects.equals(this.environmentId, deliveryRequest.environmentId)
        && Objects.equals(this.property, deliveryRequest.property)
        && Objects.equals(this.trace, deliveryRequest.trace)
        && Objects.equals(this.context, deliveryRequest.context)
        && Objects.equals(this.experienceCloud, deliveryRequest.experienceCloud)
        && Objects.equals(this.execute, deliveryRequest.execute)
        && Objects.equals(this.prefetch, deliveryRequest.prefetch)
        && Objects.equals(this.notifications, deliveryRequest.notifications)
        && Objects.equals(this.qaMode, deliveryRequest.qaMode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        requestId,
        impressionId,
        id,
        environmentId,
        property,
        trace,
        context,
        experienceCloud,
        execute,
        prefetch,
        notifications,
        qaMode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DeliveryRequest {\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    impressionId: ").append(toIndentedString(impressionId)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    environmentId: ").append(toIndentedString(environmentId)).append("\n");
    sb.append("    property: ").append(toIndentedString(property)).append("\n");
    sb.append("    trace: ").append(toIndentedString(trace)).append("\n");
    sb.append("    context: ").append(toIndentedString(context)).append("\n");
    sb.append("    experienceCloud: ").append(toIndentedString(experienceCloud)).append("\n");
    sb.append("    execute: ").append(toIndentedString(execute)).append("\n");
    sb.append("    prefetch: ").append(toIndentedString(prefetch)).append("\n");
    sb.append("    notifications: ").append(toIndentedString(notifications)).append("\n");
    sb.append("    qaMode: ").append(toIndentedString(qaMode)).append("\n");
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
