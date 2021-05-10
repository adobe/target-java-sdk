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
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** DeliveryRequest */
@JsonPropertyOrder({
  DeliveryRequest.JSON_PROPERTY_REQUEST_ID,
  DeliveryRequest.JSON_PROPERTY_IMPRESSION_ID,
  DeliveryRequest.JSON_PROPERTY_ID,
  DeliveryRequest.JSON_PROPERTY_ENVIRONMENT_ID,
  DeliveryRequest.JSON_PROPERTY_PROPERTY,
  DeliveryRequest.JSON_PROPERTY_TRACE,
  DeliveryRequest.JSON_PROPERTY_CONTEXT,
  DeliveryRequest.JSON_PROPERTY_EXPERIENCE_CLOUD,
  DeliveryRequest.JSON_PROPERTY_EXECUTE,
  DeliveryRequest.JSON_PROPERTY_PREFETCH,
  DeliveryRequest.JSON_PROPERTY_TELEMETRY,
  DeliveryRequest.JSON_PROPERTY_NOTIFICATIONS,
  DeliveryRequest.JSON_PROPERTY_QA_MODE,
  DeliveryRequest.JSON_PROPERTY_PREVIEW
})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-10T11:24:27.013-07:00[America/Los_Angeles]")
public class DeliveryRequest {
  public static final String JSON_PROPERTY_REQUEST_ID = "requestId";
  private String requestId;

  public static final String JSON_PROPERTY_IMPRESSION_ID = "impressionId";
  private String impressionId;

  public static final String JSON_PROPERTY_ID = "id";
  private VisitorId id;

  public static final String JSON_PROPERTY_ENVIRONMENT_ID = "environmentId";
  private Long environmentId;

  public static final String JSON_PROPERTY_PROPERTY = "property";
  private Property property;

  public static final String JSON_PROPERTY_TRACE = "trace";
  private Trace trace;

  public static final String JSON_PROPERTY_CONTEXT = "context";
  private Context context;

  public static final String JSON_PROPERTY_EXPERIENCE_CLOUD = "experienceCloud";
  private ExperienceCloud experienceCloud;

  public static final String JSON_PROPERTY_EXECUTE = "execute";
  private ExecuteRequest execute;

  public static final String JSON_PROPERTY_PREFETCH = "prefetch";
  private PrefetchRequest prefetch;

  public static final String JSON_PROPERTY_TELEMETRY = "telemetry";
  private Telemetry telemetry;

  public static final String JSON_PROPERTY_NOTIFICATIONS = "notifications";
  private List<Notification> notifications = null;

  public static final String JSON_PROPERTY_QA_MODE = "qaMode";
  private QAMode qaMode;

  public static final String JSON_PROPERTY_PREVIEW = "preview";
  private Preview preview;

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
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "The request ID that will be returned in the response. In case it is not provided, an UUID is generated and returned automatically. ")
  @JsonProperty(JSON_PROPERTY_REQUEST_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "If not present it will be automatically generated (UUID). If present,  second and subsequent requests with the same id will not increment impressions to activities/metrics. Similar to page id. ")
  @JsonProperty(JSON_PROPERTY_IMPRESSION_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "Valid client environment id. If not specified host will be determined base on the provided host.")
  @JsonProperty(JSON_PROPERTY_ENVIRONMENT_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_PROPERTY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_TRACE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_CONTEXT)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
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
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_EXPERIENCE_CLOUD)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_EXECUTE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_PREFETCH)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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

  /**
   * Get telemetry
   *
   * @return telemetry
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_TELEMETRY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Telemetry getTelemetry() {
    return telemetry;
  }

  public void setTelemetry(Telemetry telemetry) {
    this.telemetry = telemetry;
  }

  public DeliveryRequest notifications(List<Notification> notifications) {

    this.notifications = notifications;
    return this;
  }

  public DeliveryRequest addNotificationsItem(Notification notificationsItem) {
    if (this.notifications == null) {
      this.notifications = new ArrayList<Notification>();
    }
    this.notifications.add(notificationsItem);
    return this;
  }

  /**
   * Notifications for the displayed content, clicked selectors, and/or visited views or mboxes.
   *
   * @return notifications
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "Notifications for the displayed content, clicked selectors, and/or visited views or mboxes.")
  @JsonProperty(JSON_PROPERTY_NOTIFICATIONS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_QA_MODE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public QAMode getQaMode() {
    return qaMode;
  }

  public void setQaMode(QAMode qaMode) {
    this.qaMode = qaMode;
  }

  public DeliveryRequest preview(Preview preview) {

    this.preview = preview;
    return this;
  }

  /**
   * Get preview
   *
   * @return preview
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_PREVIEW)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Preview getPreview() {
    return preview;
  }

  public void setPreview(Preview preview) {
    this.preview = preview;
  }

  @Override
  public boolean equals(java.lang.Object o) {
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
        && Objects.equals(this.telemetry, deliveryRequest.telemetry)
        && Objects.equals(this.notifications, deliveryRequest.notifications)
        && Objects.equals(this.qaMode, deliveryRequest.qaMode)
        && Objects.equals(this.preview, deliveryRequest.preview);
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
        telemetry,
        notifications,
        qaMode,
        preview);
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
    sb.append("    telemetry: ").append(toIndentedString(telemetry)).append("\n");
    sb.append("    notifications: ").append(toIndentedString(notifications)).append("\n");
    sb.append("    qaMode: ").append(toIndentedString(qaMode)).append("\n");
    sb.append("    preview: ").append(toIndentedString(preview)).append("\n");
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
