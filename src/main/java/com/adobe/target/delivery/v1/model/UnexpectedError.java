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

/** UnexpectedError */
@JsonPropertyOrder({UnexpectedError.JSON_PROPERTY_STATUS, UnexpectedError.JSON_PROPERTY_MESSAGE})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-11T11:10:29.904-07:00[America/Los_Angeles]")
public class UnexpectedError {
  public static final String JSON_PROPERTY_STATUS = "status";
  private Integer status;

  public static final String JSON_PROPERTY_MESSAGE = "message";
  private String message;

  public UnexpectedError status(Integer status) {

    this.status = status;
    return this;
  }

  /**
   * HTTP response codes: * &#x60;400&#x60; - Validation error (ex. invalid token) * &#x60;500&#x60;
   * - An internal server error during processing
   *
   * @return status
   */
  @ApiModelProperty(
      required = true,
      value =
          "HTTP response codes: * `400` - Validation error (ex. invalid token) * `500` - An internal server error during processing ")
  @JsonProperty(JSON_PROPERTY_STATUS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public UnexpectedError message(String message) {

    this.message = message;
    return this;
  }

  /**
   * error message
   *
   * @return message
   */
  @ApiModelProperty(required = true, value = "error message")
  @JsonProperty(JSON_PROPERTY_MESSAGE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UnexpectedError unexpectedError = (UnexpectedError) o;
    return Objects.equals(this.status, unexpectedError.status)
        && Objects.equals(this.message, unexpectedError.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UnexpectedError {\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
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
