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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Telemetry */
@ApiModel(description = "Telemetry")
@JsonPropertyOrder({Telemetry.JSON_PROPERTY_ENTRIES})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-11T11:10:29.904-07:00[America/Los_Angeles]")
public class Telemetry {
  public static final String JSON_PROPERTY_ENTRIES = "entries";
  private List<TelemetryEntry> entries = new ArrayList<>();

  public Telemetry entries(List<TelemetryEntry> entries) {
    this.entries = entries;
    return this;
  }

  public Telemetry addEntriesItem(TelemetryEntry entriesItem) {
    if (this.entries == null) {
      this.entries = new ArrayList<>();
    }
    this.entries.add(entriesItem);
    return this;
  }

  /**
   * An array of Telemetry Entries.
   *
   * @return entries
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "An array of Telemetry Entries.")
  @JsonProperty(JSON_PROPERTY_ENTRIES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public List<TelemetryEntry> getEntries() {
    return entries;
  }

  public void setEntries(List<TelemetryEntry> entries) {
    this.entries = entries;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Telemetry telemetry = (Telemetry) o;
    return Objects.equals(this.entries, telemetry.entries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entries);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Telemetry {\n");
    sb.append("    entries: ").append(toIndentedString(entries)).append("\n");
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
