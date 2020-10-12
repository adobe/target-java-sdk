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
import java.util.Objects;

/**
 * Analytics payload for client side integration that should be sent to Analytics after content has
 * been applied.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalyticsPayload {
  @JsonProperty("pe")
  private String pe;

  @JsonProperty("tnta")
  private String tnta;

  public AnalyticsPayload pe(String pe) {
    this.pe = pe;
    return this;
  }

  /**
   * Get pe
   *
   * @return pe
   */
  public String getPe() {
    return pe;
  }

  public void setPe(String pe) {
    this.pe = pe;
  }

  public AnalyticsPayload tnta(String tnta) {
    this.tnta = tnta;
    return this;
  }

  /**
   * Get tnta
   *
   * @return tnta
   */
  public String getTnta() {
    return tnta;
  }

  public void setTnta(String tnta) {
    this.tnta = tnta;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AnalyticsPayload analyticsPayload = (AnalyticsPayload) o;
    return Objects.equals(this.pe, analyticsPayload.pe)
        && Objects.equals(this.tnta, analyticsPayload.tnta);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pe, tnta);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AnalyticsPayload {\n");
    sb.append("    pe: ").append(toIndentedString(pe)).append("\n");
    sb.append("    tnta: ").append(toIndentedString(tnta)).append("\n");
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
