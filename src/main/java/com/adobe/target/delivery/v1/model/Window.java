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

/** The dimensions that dictate a window */
@ApiModel(description = "The dimensions that dictate a window")
@JsonPropertyOrder({Window.JSON_PROPERTY_WIDTH, Window.JSON_PROPERTY_HEIGHT})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-11T11:10:29.904-07:00[America/Los_Angeles]")
public class Window {
  public static final String JSON_PROPERTY_WIDTH = "width";
  private Double width;

  public static final String JSON_PROPERTY_HEIGHT = "height";
  private Double height;

  public Window width(Double width) {

    this.width = width;
    return this;
  }

  /**
   * width
   *
   * @return width
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "width")
  @JsonProperty(JSON_PROPERTY_WIDTH)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Double getWidth() {
    return width;
  }

  public void setWidth(Double width) {
    this.width = width;
  }

  public Window height(Double height) {

    this.height = height;
    return this;
  }

  /**
   * height
   *
   * @return height
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "height")
  @JsonProperty(JSON_PROPERTY_HEIGHT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Double getHeight() {
    return height;
  }

  public void setHeight(Double height) {
    this.height = height;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Window window = (Window) o;
    return Objects.equals(this.width, window.width) && Objects.equals(this.height, window.height);
  }

  @Override
  public int hashCode() {
    return Objects.hash(width, height);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Window {\n");
    sb.append("    width: ").append(toIndentedString(width)).append("\n");
    sb.append("    height: ").append(toIndentedString(height)).append("\n");
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
