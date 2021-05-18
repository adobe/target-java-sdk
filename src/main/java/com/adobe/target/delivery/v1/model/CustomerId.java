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
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

/** CustomerId */
@JsonPropertyOrder({
  CustomerId.JSON_PROPERTY_ID,
  CustomerId.JSON_PROPERTY_INTEGRATION_CODE,
  CustomerId.JSON_PROPERTY_AUTHENTICATED_STATE
})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-11T11:10:29.904-07:00[America/Los_Angeles]")
public class CustomerId {
  public static final String JSON_PROPERTY_ID = "id";
  private String id;

  public static final String JSON_PROPERTY_INTEGRATION_CODE = "integrationCode";
  private String integrationCode;

  public static final String JSON_PROPERTY_AUTHENTICATED_STATE = "authenticatedState";
  private AuthenticatedState authenticatedState;

  public CustomerId id(String id) {

    this.id = id;
    return this;
  }

  /**
   * Get id
   *
   * @return id
   */
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_ID)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public CustomerId integrationCode(String integrationCode) {

    this.integrationCode = integrationCode;
    return this;
  }

  /**
   * This is the **alias** used when setting up a CRS datasource in the Marketing Cloud UI.
   *
   * @return integrationCode
   */
  @ApiModelProperty(
      required = true,
      value =
          "This is the **alias** used when setting up a CRS datasource in the Marketing Cloud UI.")
  @JsonProperty(JSON_PROPERTY_INTEGRATION_CODE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public String getIntegrationCode() {
    return integrationCode;
  }

  public void setIntegrationCode(String integrationCode) {
    this.integrationCode = integrationCode;
  }

  public CustomerId authenticatedState(AuthenticatedState authenticatedState) {

    this.authenticatedState = authenticatedState;
    return this;
  }

  /**
   * Get authenticatedState
   *
   * @return authenticatedState
   */
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_AUTHENTICATED_STATE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public AuthenticatedState getAuthenticatedState() {
    return authenticatedState;
  }

  public void setAuthenticatedState(AuthenticatedState authenticatedState) {
    this.authenticatedState = authenticatedState;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CustomerId customerId = (CustomerId) o;
    return Objects.equals(this.id, customerId.id)
        && Objects.equals(this.integrationCode, customerId.integrationCode)
        && Objects.equals(this.authenticatedState, customerId.authenticatedState);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, integrationCode, authenticatedState);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CustomerId {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    integrationCode: ").append(toIndentedString(integrationCode)).append("\n");
    sb.append("    authenticatedState: ").append(toIndentedString(authenticatedState)).append("\n");
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
