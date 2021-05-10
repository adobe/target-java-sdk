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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The object that contains responses from execute &#x60;pageLoad&#x60; and/or execute regional
 * &#x60;mboxes&#x60; request.
 */
@ApiModel(
    description =
        "The object that contains responses from execute `pageLoad` and/or execute regional `mboxes` request.")
@JsonPropertyOrder({ExecuteResponse.JSON_PROPERTY_PAGE_LOAD, ExecuteResponse.JSON_PROPERTY_MBOXES})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-10T11:24:27.013-07:00[America/Los_Angeles]")
public class ExecuteResponse {
  public static final String JSON_PROPERTY_PAGE_LOAD = "pageLoad";
  private PageLoadResponse pageLoad;

  public static final String JSON_PROPERTY_MBOXES = "mboxes";
  private List<MboxResponse> mboxes = null;

  public ExecuteResponse pageLoad(PageLoadResponse pageLoad) {

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
      this.mboxes = new ArrayList<MboxResponse>();
    }
    this.mboxes.add(mboxesItem);
    return this;
  }

  /**
   * The list of responses for requested regional mboxes.
   *
   * @return mboxes
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The list of responses for requested regional mboxes.")
  @JsonProperty(JSON_PROPERTY_MBOXES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public List<MboxResponse> getMboxes() {
    return mboxes;
  }

  public void setMboxes(List<MboxResponse> mboxes) {
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
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
