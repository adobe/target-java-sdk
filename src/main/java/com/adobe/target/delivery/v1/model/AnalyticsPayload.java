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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

/**
 * Analytics payload for client side integration that should be sent to Analytics after content has
 * been applied.
 */
@ApiModel(
    description =
        "Analytics payload for client side integration that should be sent to Analytics after content has been applied. ")
@JsonPropertyOrder({AnalyticsPayload.JSON_PROPERTY_PE, AnalyticsPayload.JSON_PROPERTY_TNTA})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-10T11:24:27.013-07:00[America/Los_Angeles]")
public class AnalyticsPayload {
  public static final String JSON_PROPERTY_PE = "pe";
  private String pe;

  public static final String JSON_PROPERTY_TNTA = "tnta";
  private String tnta;

  public AnalyticsPayload pe(String pe) {

    this.pe = pe;
    return this;
  }

  /**
   * Indicates to Adobe Analytics that the payload is an Adobe Target type
   *
   * @return pe
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Indicates to Adobe Analytics that the payload is an Adobe Target type")
  @JsonProperty(JSON_PROPERTY_PE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
   * Contains Target metadata that describes the activity and experience
   *
   * @return tnta
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Contains Target metadata that describes the activity and experience")
  @JsonProperty(JSON_PROPERTY_TNTA)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getTnta() {
    return tnta;
  }

  public void setTnta(String tnta) {
    this.tnta = tnta;
  }

  @Override
  public boolean equals(java.lang.Object o) {
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
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
