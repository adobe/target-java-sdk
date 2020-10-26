/*
 * Copyright 2019 Adobe. All rights reserved.
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The object that contains responses from execute &#x60;pageLoad&#x60; and/or execute regional
 * &#x60;mboxes&#x60; request.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExecuteResponse {
  @JsonProperty("pageLoad")
  private PageLoadResponse pageLoad = null;

  @JsonProperty("mboxes")
  private List<MboxResponse> mboxes = new ArrayList<>();

  public ExecuteResponse pageLoad(PageLoadResponse pageLoad) {
    this.pageLoad = pageLoad;
    return this;
  }

  /**
   * Get pageLoad
   *
   * @return pageLoad
   */
  public PageLoadResponse getPageLoad() {
    return pageLoad;
  }

  public void setPageLoad(PageLoadResponse pageLoad) {
    this.pageLoad = pageLoad;
  }

  public ExecuteResponse mboxes(List<MboxResponse> mboxes) {
    this.mboxes = mboxes;
    return this;
  }

  public ExecuteResponse addMboxesItem(MboxResponse mboxesItem) {
    if (this.mboxes == null) {
      this.mboxes = new ArrayList<>();
    }
    this.mboxes.add(mboxesItem);
    return this;
  }

  /**
   * The list of responses for requested regional mboxes.
   *
   * @return mboxes
   */
  public List<MboxResponse> getMboxes() {
    return mboxes;
  }

  public void setMboxes(List<MboxResponse> mboxes) {
    this.mboxes = mboxes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExecuteResponse executeResponse = (ExecuteResponse) o;
    return Objects.equals(this.pageLoad, executeResponse.pageLoad)
        && Objects.equals(this.mboxes, executeResponse.mboxes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageLoad, mboxes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExecuteResponse {\n");
    sb.append("    pageLoad: ").append(toIndentedString(pageLoad)).append("\n");
    sb.append("    mboxes: ").append(toIndentedString(mboxes)).append("\n");
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
