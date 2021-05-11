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

import com.adobe.target.edge.client.model.DecisioningMethod;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

/** Telemetry Features */
@ApiModel(description = "Telemetry Features")
@JsonPropertyOrder({TelemetryFeatures.JSON_PROPERTY_DECISIONING_METHOD})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-11T11:10:29.904-07:00[America/Los_Angeles]")
public class TelemetryFeatures {
  public static final String JSON_PROPERTY_DECISIONING_METHOD = "decisioningMethod";
  private DecisioningMethod decisioningMethod;

  public TelemetryFeatures decisioningMethod(DecisioningMethod decisioningMethod) {

    this.decisioningMethod = decisioningMethod;
    return this;
  }

  /**
   * Get decisioningMethod
   *
   * @return decisioningMethod
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_DECISIONING_METHOD)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public DecisioningMethod getDecisioningMethod() {
    return decisioningMethod;
  }

  public void setDecisioningMethod(DecisioningMethod decisioningMethod) {
    this.decisioningMethod = decisioningMethod;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TelemetryFeatures telemetryFeatures = (TelemetryFeatures) o;
    return Objects.equals(this.decisioningMethod, telemetryFeatures.decisioningMethod);
  }

  @Override
  public int hashCode() {
    return Objects.hash(decisioningMethod);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TelemetryFeatures {\n");
    sb.append("    decisioningMethod: ").append(toIndentedString(decisioningMethod)).append("\n");
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
