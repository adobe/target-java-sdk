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
import java.util.Objects;

/** Screen */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Screen {
  @JsonProperty("width")
  private Double width;

  @JsonProperty("height")
  private Double height;

  @JsonProperty("colorDepth")
  private Double colorDepth;

  @JsonProperty("pixelRatio")
  private Double pixelRatio;

  @JsonProperty("orientation")
  private ScreenOrientationType orientation = null;

  public Screen width(Double width) {
    this.width = width;
    return this;
  }

  /**
   * Get width
   *
   * @return width
   */
  public Double getWidth() {
    return width;
  }

  public void setWidth(Double width) {
    this.width = width;
  }

  public Screen height(Double height) {
    this.height = height;
    return this;
  }

  /**
   * Get height
   *
   * @return height
   */
  public Double getHeight() {
    return height;
  }

  public void setHeight(Double height) {
    this.height = height;
  }

  public Screen colorDepth(Double colorDepth) {
    this.colorDepth = colorDepth;
    return this;
  }

  /**
   * Get colorDepth
   *
   * @return colorDepth
   */
  public Double getColorDepth() {
    return colorDepth;
  }

  public void setColorDepth(Double colorDepth) {
    this.colorDepth = colorDepth;
  }

  public Screen pixelRatio(Double pixelRatio) {
    this.pixelRatio = pixelRatio;
    return this;
  }

  /**
   * Optional, Used for device detection using the device atlas
   *
   * @return pixelRatio
   */
  public Double getPixelRatio() {
    return pixelRatio;
  }

  public void setPixelRatio(Double pixelRatio) {
    this.pixelRatio = pixelRatio;
  }

  public Screen orientation(ScreenOrientationType orientation) {
    this.orientation = orientation;
    return this;
  }

  /**
   * Get orientation
   *
   * @return orientation
   */
  public ScreenOrientationType getOrientation() {
    return orientation;
  }

  public void setOrientation(ScreenOrientationType orientation) {
    this.orientation = orientation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Screen screen = (Screen) o;
    return Objects.equals(this.width, screen.width)
        && Objects.equals(this.height, screen.height)
        && Objects.equals(this.colorDepth, screen.colorDepth)
        && Objects.equals(this.pixelRatio, screen.pixelRatio)
        && Objects.equals(this.orientation, screen.orientation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(width, height, colorDepth, pixelRatio, orientation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Screen {\n");
    sb.append("    width: ").append(toIndentedString(width)).append("\n");
    sb.append("    height: ").append(toIndentedString(height)).append("\n");
    sb.append("    colorDepth: ").append(toIndentedString(colorDepth)).append("\n");
    sb.append("    pixelRatio: ").append(toIndentedString(pixelRatio)).append("\n");
    sb.append("    orientation: ").append(toIndentedString(orientation)).append("\n");
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
