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
 */
package com.adobe.target.edge.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Gets or Sets DecisioningMethod */
public enum DecisioningMethod {
  SERVER_SIDE(com.adobe.target.delivery.v1.model.DecisioningMethod.SERVER_SIDE.toString()),

  ON_DEVICE(com.adobe.target.delivery.v1.model.DecisioningMethod.ON_DEVICE.toString()),

  HYBRID(com.adobe.target.delivery.v1.model.DecisioningMethod.HYBRID.toString());

  private String value;

  DecisioningMethod(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static DecisioningMethod fromValue(String value) {
    for (DecisioningMethod b : DecisioningMethod.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
