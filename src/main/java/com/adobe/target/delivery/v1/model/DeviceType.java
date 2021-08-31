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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Field is mandatory when Mobile Platform is specified. Only activities that match the specified
 * deviceType or have the device type set to &#39;null&#39; will be evaluated. Ex if device type is
 * &#39;phone&#39; in the delivery request, then only activities that have the device type equal to
 * &#39;phone&#39; or set to &#39;null&#39; will be evaluated. An activity with &#39;null&#39;
 * device type will be evaluated for requests for both, &#39;phone&#39; and &#39;tablet&#39;.
 */
public enum DeviceType {
  PHONE("phone"),

  TABLET("tablet");

  private String value;

  DeviceType(String value) {
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
  public static DeviceType fromValue(String value) {
    for (DeviceType b : DeviceType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
