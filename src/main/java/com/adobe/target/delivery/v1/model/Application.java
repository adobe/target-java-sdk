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
import java.util.Objects;

/** Application identifiers. If specified, should match the with the one from the activity. */
@ApiModel(
    description =
        "Application identifiers. If specified, should match the with the one from the activity.")
@JsonPropertyOrder({
  Application.JSON_PROPERTY_ID,
  Application.JSON_PROPERTY_NAME,
  Application.JSON_PROPERTY_VERSION
})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-11T11:10:29.904-07:00[America/Los_Angeles]")
public class Application {
  public static final String JSON_PROPERTY_ID = "id";
  private String id;

  public static final String JSON_PROPERTY_NAME = "name";
  private String name;

  public static final String JSON_PROPERTY_VERSION = "version";
  private String version;

  public Application id(String id) {

    this.id = id;
    return this;
  }

  /**
   * Application ID. If not specified - all activities with any applicationId will be evaluated. If
   * specified - only activities with the matching applicationId will be evaluated.
   *
   * @return id
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "Application ID. If not specified - all activities with any applicationId will be evaluated. If specified - only activities with the matching applicationId will be evaluated. ")
  @JsonProperty(JSON_PROPERTY_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Application name(String name) {

    this.name = name;
    return this;
  }

  /**
   * Application name. If not specified - all activities with any applicationName will be evaluated.
   * If specified - only activities with specified applicationName will be evaluated.
   *
   * @return name
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "Application name. If not specified - all activities with any applicationName will be evaluated. If specified - only activities with specified applicationName will be evaluated. ")
  @JsonProperty(JSON_PROPERTY_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Application version(String version) {

    this.version = version;
    return this;
  }

  /**
   * Application version If not specified - all activities with any applicationVersion will not be
   * evaluated. If specified - only activities with specific applicationVersion will be evaluated.
   *
   * @return version
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "Application version If not specified - all activities with any applicationVersion will not be evaluated. If specified - only activities with specific applicationVersion will be evaluated. ")
  @JsonProperty(JSON_PROPERTY_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Application application = (Application) o;
    return Objects.equals(this.id, application.id)
        && Objects.equals(this.name, application.name)
        && Objects.equals(this.version, application.version);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, version);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Application {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
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
