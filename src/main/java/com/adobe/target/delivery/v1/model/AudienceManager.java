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

/** Audience Manager Integration (AAM). */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AudienceManager {
  @JsonProperty("locationHint")
  private Integer locationHint;

  @JsonProperty("blob")
  private String blob;

  public AudienceManager locationHint(Integer locationHint) {
    this.locationHint = locationHint;
    return this;
  }

  /**
   * DCS location hint. Used to determine which AAM DCS Endpoint to hit in order to retrieve the
   * profile. minimum: 1
   *
   * @return locationHint
   */
  public Integer getLocationHint() {
    return locationHint;
  }

  public void setLocationHint(Integer locationHint) {
    this.locationHint = locationHint;
  }

  public AudienceManager blob(String blob) {
    this.blob = blob;
    return this;
  }

  /**
   * AAM Blob. Used to send additional data to AAM. Validation * Cannot be blank.
   *
   * @return blob
   */
  public String getBlob() {
    return blob;
  }

  public void setBlob(String blob) {
    this.blob = blob;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AudienceManager audienceManager = (AudienceManager) o;
    return Objects.equals(this.locationHint, audienceManager.locationHint)
        && Objects.equals(this.blob, audienceManager.blob);
  }

  @Override
  public int hashCode() {
    return Objects.hash(locationHint, blob);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AudienceManager {\n");
    sb.append("    locationHint: ").append(toIndentedString(locationHint)).append("\n");
    sb.append("    blob: ").append(toIndentedString(blob)).append("\n");
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
