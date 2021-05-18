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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Enables the trace for delivery API. At present it is not be possible to set the metrics and
 * packages for the trace.
 */
@ApiModel(
    description =
        "Enables the trace for delivery API. At present it is not be possible to set the metrics and packages for the trace. ")
@JsonPropertyOrder({Trace.JSON_PROPERTY_AUTHORIZATION_TOKEN, Trace.JSON_PROPERTY_USAGE})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-11T11:10:29.904-07:00[America/Los_Angeles]")
public class Trace {
  public static final String JSON_PROPERTY_AUTHORIZATION_TOKEN = "authorizationToken";
  private String authorizationToken;

  public static final String JSON_PROPERTY_USAGE = "usage";
  private Map<String, String> usage = null;

  public Trace authorizationToken(String authorizationToken) {

    this.authorizationToken = authorizationToken;
    return this;
  }

  /**
   * Get authorizationToken
   *
   * @return authorizationToken
   */
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_AUTHORIZATION_TOKEN)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public String getAuthorizationToken() {
    return authorizationToken;
  }

  public void setAuthorizationToken(String authorizationToken) {
    this.authorizationToken = authorizationToken;
  }

  public Trace usage(Map<String, String> usage) {

    this.usage = usage;
    return this;
  }

  public Trace putUsageItem(String key, String usageItem) {
    if (this.usage == null) {
      this.usage = new HashMap<>();
    }
    this.usage.put(key, usageItem);
    return this;
  }

  /**
   * A String dictionary of client SDK usage tracking and internal diagnostics metadata.
   *
   * @return usage
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "A String dictionary of client SDK usage tracking and internal diagnostics metadata. ")
  @JsonProperty(JSON_PROPERTY_USAGE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Map<String, String> getUsage() {
    return usage;
  }

  public void setUsage(Map<String, String> usage) {
    this.usage = usage;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Trace trace = (Trace) o;
    return Objects.equals(this.authorizationToken, trace.authorizationToken)
        && Objects.equals(this.usage, trace.usage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(authorizationToken, usage);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Trace {\n");
    sb.append("    authorizationToken: ").append(toIndentedString(authorizationToken)).append("\n");
    sb.append("    usage: ").append(toIndentedString(usage)).append("\n");
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
