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

/** Regional mbox request. */
@ApiModel(description = "Regional mbox request.")
@JsonPropertyOrder({MboxRequest.JSON_PROPERTY_INDEX, MboxRequest.JSON_PROPERTY_NAME})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-11T11:10:29.904-07:00[America/Los_Angeles]")
public class MboxRequest extends RequestDetails {
  public static final String JSON_PROPERTY_INDEX = "index";
  private Integer index;

  public static final String JSON_PROPERTY_NAME = "name";
  private String name;

  public MboxRequest index(Integer index) {

    this.index = index;
    return this;
  }

  /**
   * An index for the mboxes to be executed or prefetched. Mbox index is used for correlation
   * between the mbox request with the mbox response, for either prefetch or execute responses.
   * Index should be unique in the mbox list.
   *
   * @return index
   */
  @ApiModelProperty(
      required = true,
      value =
          "An index for the mboxes to be executed or prefetched. Mbox index is used for correlation between the mbox request with the mbox response, for either prefetch or execute responses. Index should be unique in the mbox list. ")
  @JsonProperty(JSON_PROPERTY_INDEX)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public Integer getIndex() {
    return index;
  }

  public void setIndex(Integer index) {
    this.index = index;
  }

  public MboxRequest name(String name) {

    this.name = name;
    return this;
  }

  /**
   * The name of the regional mbox to be evaluated.
   *
   * @return name
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The name of the regional mbox to be evaluated. ")
  @JsonProperty(JSON_PROPERTY_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MboxRequest mboxRequest = (MboxRequest) o;
    return Objects.equals(this.index, mboxRequest.index)
        && Objects.equals(this.name, mboxRequest.name)
        && super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(index, name, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MboxRequest {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    index: ").append(toIndentedString(index)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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
