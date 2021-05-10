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
 * Use this object to prefetch the content for &#x60;views&#x60; and/or &#x60;pageLoad&#x60; and/or
 * &#x60;mboxes&#x60;. * &#x60;views&#x60; - the request to prefetch selectors grouped per view. *
 * &#x60;pageLoad&#x60; - the request to prefetch selectors not assigned to any view. *
 * &#x60;mboxes&#x60; - the request to prefetch mbox content.
 */
@ApiModel(
    description =
        "Use this object to prefetch the content for `views` and/or `pageLoad` and/or `mboxes`.   * `views` - the request to prefetch selectors grouped per view.   * `pageLoad` - the request to prefetch selectors not assigned to any view.   * `mboxes` - the request to prefetch mbox content. ")
@JsonPropertyOrder({
  PrefetchRequest.JSON_PROPERTY_VIEWS,
  PrefetchRequest.JSON_PROPERTY_PAGE_LOAD,
  PrefetchRequest.JSON_PROPERTY_MBOXES
})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-10T11:24:27.013-07:00[America/Los_Angeles]")
public class PrefetchRequest {
  public static final String JSON_PROPERTY_VIEWS = "views";
  private List<ViewRequest> views = null;

  public static final String JSON_PROPERTY_PAGE_LOAD = "pageLoad";
  private RequestDetails pageLoad;

  public static final String JSON_PROPERTY_MBOXES = "mboxes";
  private List<MboxRequest> mboxes = null;

  public PrefetchRequest views(List<ViewRequest> views) {

    this.views = views;
    return this;
  }

  public PrefetchRequest addViewsItem(ViewRequest viewsItem) {
    if (this.views == null) {
      this.views = new ArrayList<ViewRequest>();
    }
    this.views.add(viewsItem);
    return this;
  }

  /**
   * An array of views
   *
   * @return views
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "An array of views ")
  @JsonProperty(JSON_PROPERTY_VIEWS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public List<ViewRequest> getViews() {
    return views;
  }

  public void setViews(List<ViewRequest> views) {
    this.views = views;
  }

  public PrefetchRequest pageLoad(RequestDetails pageLoad) {

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

  public PrefetchRequest mboxes(List<MboxRequest> mboxes) {

    this.mboxes = mboxes;
    return this;
  }

  public PrefetchRequest addMboxesItem(MboxRequest mboxesItem) {
    if (this.mboxes == null) {
      this.mboxes = new ArrayList<MboxRequest>();
    }
    this.mboxes.add(mboxesItem);
    return this;
  }

  /**
   * Prefetch the content for the regional mbox.
   *
   * @return mboxes
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Prefetch the content for the regional mbox.")
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
    PrefetchRequest prefetchRequest = (PrefetchRequest) o;
    return Objects.equals(this.views, prefetchRequest.views)
        && Objects.equals(this.pageLoad, prefetchRequest.pageLoad)
        && Objects.equals(this.mboxes, prefetchRequest.mboxes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(views, pageLoad, mboxes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PrefetchRequest {\n");
    sb.append("    views: ").append(toIndentedString(views)).append("\n");
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
