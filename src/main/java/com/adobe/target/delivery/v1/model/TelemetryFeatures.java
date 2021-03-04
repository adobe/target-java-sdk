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
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TelemetryFeatures {
  @JsonProperty("decisioningMethod")
  private String decisioningMethod;

  public TelemetryFeatures decisioningMethod(DecisioningMethod decisioningMethod) {
    this.decisioningMethod = decisioningMethod.getName();
    return this;
  }

  public String getDecisioningMethod() {
    return decisioningMethod;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TelemetryFeatures that = (TelemetryFeatures) o;
    return Objects.equals(decisioningMethod, that.decisioningMethod);
  }

  @Override
  public int hashCode() {
    return Objects.hash(decisioningMethod);
  }

  @Override
  public String toString() {
    return "TelemetryFeatures{" + "decisioningMethod='" + decisioningMethod + '\'' + '}';
  }
}
