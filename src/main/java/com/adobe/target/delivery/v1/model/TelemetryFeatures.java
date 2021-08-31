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
import java.util.Objects;

/** Telemetry Features */
public class TelemetryFeatures {
  @JsonProperty("decisioningMethod")
  private DecisioningMethod decisioningMethod;

  @JsonProperty("executeMboxCount")
  private Integer executeMboxCount;

  @JsonProperty("executePageLoad")
  private Boolean executePageLoad;

  @JsonProperty("prefetchMboxCount")
  private Integer prefetchMboxCount;

  @JsonProperty("prefetchPageLoad")
  private Boolean prefetchPageLoad;

  @JsonProperty("prefetchViewCount")
  private Integer prefetchViewCount;

  public TelemetryFeatures decisioningMethod(DecisioningMethod decisioningMethod) {
    this.decisioningMethod = decisioningMethod;
    return this;
  }

  /**
   * Get decisioningMethod
   *
   * @return decisioningMethod
   */
  public String getDecisioningMethod() {
    return decisioningMethod.getValue();
  }

  public void setDecisioningMethod(DecisioningMethod decisioningMethod) {
    this.decisioningMethod = decisioningMethod;
  }

  public TelemetryFeatures executeMboxCount(Integer executeMboxCount) {
    this.executeMboxCount = executeMboxCount;
    return this;
  }

  /**
   * Number of mboxes in execute request
   *
   * @return executeMboxCount
   */
  public Integer getExecuteMboxCount() {
    return executeMboxCount;
  }

  public void setExecuteMboxCount(Integer executeMboxCount) {
    this.executeMboxCount = executeMboxCount;
  }

  public TelemetryFeatures executePageLoad(Boolean executePageLoad) {
    this.executePageLoad = executePageLoad;
    return this;
  }

  /**
   * Indicates if execute pageLoad was used
   *
   * @return executePageLoad
   */
  public Boolean getExecutePageLoad() {
    return executePageLoad;
  }

  public void setExecutePageLoad(Boolean executePageLoad) {
    this.executePageLoad = executePageLoad;
  }

  public TelemetryFeatures prefetchMboxCount(Integer prefetchMboxCount) {
    this.prefetchMboxCount = prefetchMboxCount;
    return this;
  }

  /**
   * Number of prefetched mboxes
   *
   * @return prefetchMboxCount
   */
  public Integer getPrefetchMboxCount() {
    return prefetchMboxCount;
  }

  public void setPrefetchMboxCount(Integer prefetchMboxCount) {
    this.prefetchMboxCount = prefetchMboxCount;
  }

  public TelemetryFeatures prefetchPageLoad(Boolean prefetchPageLoad) {
    this.prefetchPageLoad = prefetchPageLoad;
    return this;
  }

  /**
   * Indicates if prefetch pageLoad was used
   *
   * @return prefetchPageLoad
   */
  public Boolean getPrefetchPageLoad() {
    return prefetchPageLoad;
  }

  public void setPrefetchPageLoad(Boolean prefetchPageLoad) {
    this.prefetchPageLoad = prefetchPageLoad;
  }

  public TelemetryFeatures prefetchViewCount(Integer prefetchViewCount) {
    this.prefetchViewCount = prefetchViewCount;
    return this;
  }

  /**
   * Number of prefetched views
   *
   * @return prefetchViewCount
   */
  public Integer getPrefetchViewCount() {
    return prefetchViewCount;
  }

  public void setPrefetchViewCount(Integer prefetchViewCount) {
    this.prefetchViewCount = prefetchViewCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TelemetryFeatures telemetryFeatures = (TelemetryFeatures) o;
    return Objects.equals(this.decisioningMethod, telemetryFeatures.decisioningMethod)
        && Objects.equals(this.executeMboxCount, telemetryFeatures.executeMboxCount)
        && Objects.equals(this.executePageLoad, telemetryFeatures.executePageLoad)
        && Objects.equals(this.prefetchMboxCount, telemetryFeatures.prefetchMboxCount)
        && Objects.equals(this.prefetchPageLoad, telemetryFeatures.prefetchPageLoad)
        && Objects.equals(this.prefetchViewCount, telemetryFeatures.prefetchViewCount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        decisioningMethod,
        executeMboxCount,
        executePageLoad,
        prefetchMboxCount,
        prefetchPageLoad,
        prefetchViewCount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TelemetryFeatures {\n");
    sb.append("    decisioningMethod: ").append(toIndentedString(decisioningMethod)).append("\n");
    sb.append("    executeMboxCount: ").append(toIndentedString(executeMboxCount)).append("\n");
    sb.append("    executePageLoad: ").append(toIndentedString(executePageLoad)).append("\n");
    sb.append("    prefetchMboxCount: ").append(toIndentedString(prefetchMboxCount)).append("\n");
    sb.append("    prefetchPageLoad: ").append(toIndentedString(prefetchPageLoad)).append("\n");
    sb.append("    prefetchViewCount: ").append(toIndentedString(prefetchViewCount)).append("\n");
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
