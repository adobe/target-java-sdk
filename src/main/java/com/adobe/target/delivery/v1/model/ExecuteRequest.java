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

/**
 * The execute part of the request that will be evaluated on the server side immediately.
 * Impressions will be incremented for the matching activities.
 */
@ApiModel(
    description =
        "The execute part of the request that will be evaluated on the server side immediately. Impressions will be incremented for the matching activities. ")
@JsonPropertyOrder({ExecuteRequest.JSON_PROPERTY_PAGE_LOAD, ExecuteRequest.JSON_PROPERTY_MBOXES})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-11T11:10:29.904-07:00[America/Los_Angeles]")
public class ExecuteRequest {
  public static final String JSON_PROPERTY_PAGE_LOAD = "pageLoad";
  private RequestDetails pageLoad;

  public static final String JSON_PROPERTY_MBOXES = "mboxes";
  private List<MboxRequest> mboxes = null;

  public ExecuteRequest pageLoad(RequestDetails pageLoad) {

    this.pageLoad = pageLoad;
    return this;
  }

  /**
   * Get pageLoad
   *
   * @return pageLoad
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_PAGE_LOAD)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public RequestDetails getPageLoad() {
    return pageLoad;
  }

  public void setPageLoad(RequestDetails pageLoad) {
    this.pageLoad = pageLoad;
  }

  public ExecuteRequest mboxes(List<MboxRequest> mboxes) {

    this.mboxes = mboxes;
    return this;
  }

  public ExecuteRequest addMboxesItem(MboxRequest mboxesItem) {
    if (this.mboxes == null) {
      this.mboxes = new ArrayList<>();
    }
    this.mboxes.add(mboxesItem);
    return this;
  }

  /**
   * An array of mboxes other than global mbox.
   *
   * @return mboxes
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "An array of mboxes other than global mbox.")
  @JsonProperty(JSON_PROPERTY_MBOXES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public List<MboxRequest> getMboxes() {
    return mboxes;
  }

  public void setMboxes(List<MboxRequest> mboxes) {
    this.mboxes = mboxes;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExecuteRequest executeRequest = (ExecuteRequest) o;
    return Objects.equals(this.pageLoad, executeRequest.pageLoad)
        && Objects.equals(this.mboxes, executeRequest.mboxes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageLoad, mboxes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExecuteRequest {\n");
    sb.append("    pageLoad: ").append(toIndentedString(pageLoad)).append("\n");
    sb.append("    mboxes: ").append(toIndentedString(mboxes)).append("\n");
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
