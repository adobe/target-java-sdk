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

/** Integrations with Audience Manager and Analytics */
@ApiModel(description = "Integrations with Audience Manager and Analytics")
@JsonPropertyOrder({
  ExperienceCloud.JSON_PROPERTY_AUDIENCE_MANAGER,
  ExperienceCloud.JSON_PROPERTY_ANALYTICS
})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-11T11:10:29.904-07:00[America/Los_Angeles]")
public class ExperienceCloud {
  public static final String JSON_PROPERTY_AUDIENCE_MANAGER = "audienceManager";
  private AudienceManager audienceManager;

  public static final String JSON_PROPERTY_ANALYTICS = "analytics";
  private AnalyticsRequest analytics;

  public ExperienceCloud audienceManager(AudienceManager audienceManager) {

    this.audienceManager = audienceManager;
    return this;
  }

  /**
   * Get audienceManager
   *
   * @return audienceManager
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_AUDIENCE_MANAGER)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public AudienceManager getAudienceManager() {
    return audienceManager;
  }

  public void setAudienceManager(AudienceManager audienceManager) {
    this.audienceManager = audienceManager;
  }

  public ExperienceCloud analytics(AnalyticsRequest analytics) {

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
  public AnalyticsRequest getAnalytics() {
    return analytics;
  }

  public void setAnalytics(AnalyticsRequest analytics) {
    this.analytics = analytics;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExperienceCloud experienceCloud = (ExperienceCloud) o;
    return Objects.equals(this.audienceManager, experienceCloud.audienceManager)
        && Objects.equals(this.analytics, experienceCloud.analytics);
  }

  @Override
  public int hashCode() {
    return Objects.hash(audienceManager, analytics);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExperienceCloud {\n");
    sb.append("    audienceManager: ").append(toIndentedString(audienceManager)).append("\n");
    sb.append("    analytics: ").append(toIndentedString(analytics)).append("\n");
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
