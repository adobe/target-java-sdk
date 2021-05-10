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

/** Audience Manager Integration (AAM). */
@ApiModel(description = "Audience Manager Integration (AAM).")
@JsonPropertyOrder({
  AudienceManager.JSON_PROPERTY_LOCATION_HINT,
  AudienceManager.JSON_PROPERTY_BLOB
})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-10T11:24:27.013-07:00[America/Los_Angeles]")
public class AudienceManager {
  public static final String JSON_PROPERTY_LOCATION_HINT = "locationHint";
  private Integer locationHint;

  public static final String JSON_PROPERTY_BLOB = "blob";
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
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "DCS location hint. Used to determine which AAM DCS Endpoint to hit in order to retrieve the profile. ")
  @JsonProperty(JSON_PROPERTY_LOCATION_HINT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  @javax.annotation.Nullable
  @ApiModelProperty(
      value = "AAM Blob. Used to send additional data to AAM. Validation   * Cannot be blank. ")
  @JsonProperty(JSON_PROPERTY_BLOB)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getBlob() {
    return blob;
  }

  public void setBlob(String blob) {
    this.blob = blob;
  }

  @Override
  public boolean equals(java.lang.Object o) {
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
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
