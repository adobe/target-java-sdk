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
 * Use this object to prefetch the content for &#x60;views&#x60; and/or &#x60;pageLoad&#x60; and/or &#x60;
 * mboxes&#x60;.   * &#x60;views&#x60; - the request to prefetch selectors grouped per view.   * &#x60;pageLoad&#x60;
 * - the request to prefetch selectors not assigned to any view.   * &#x60;mboxes&#x60; - the request to prefetch
 * mbox content.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrefetchRequest {
    @JsonProperty("views")
    private List<ViewRequest> views = new ArrayList<>();

    @JsonProperty("pageLoad")
    private RequestDetails pageLoad = null;

    @JsonProperty("mboxes")
    private List<MboxRequest> mboxes = new ArrayList<>();

    public PrefetchRequest views(List<ViewRequest> views) {
        if (views == null) {
            return this;
        }
        // wrap passed in list with our own ArrayList to make sure it is mutable
        this.views = new ArrayList<>(views);
        return this;
    }

    public PrefetchRequest addViewsItem(ViewRequest viewsItem) {
        if (this.views == null) {
            this.views = new ArrayList<>();
        }
        this.views.add(viewsItem);
        return this;
    }

    /**
     * Currenly only 1 view can be set in the request. All views matching the request will be returned. In future it
     * will be possible to request 1 or several concrete views.
     *
     * @return views
     **/

    public List<ViewRequest> getViews() {
        return views;
    }

    public void setViews(List<ViewRequest> views) {
        if (views != null) {
            // wrap passed in list with our own ArrayList to make sure it is mutable
            this.views = new ArrayList<>(views);
        }
        else {
            this.views = views;
        }
    }

    public PrefetchRequest pageLoad(RequestDetails pageLoad) {
        this.pageLoad = pageLoad;
        return this;
    }

    /**
     * Get pageLoad
     *
     * @return pageLoad
     **/

    public RequestDetails getPageLoad() {
        return pageLoad;
    }

    public void setPageLoad(RequestDetails pageLoad) {
        this.pageLoad = pageLoad;
    }

    public PrefetchRequest mboxes(List<MboxRequest> mboxes) {
        if (mboxes == null) {
            return this;
        }
        // wrap passed in list with our own ArrayList to make sure it is mutable
        this.mboxes = new ArrayList<>(mboxes);
        return this;
    }

    public PrefetchRequest addMboxesItem(MboxRequest mboxesItem) {
        if (this.mboxes == null) {
            this.mboxes = new ArrayList<>();
        }
        this.mboxes.add(mboxesItem);
        return this;
    }

    /**
     * Prefetch the content for the regional mbox. Can be used as a replacement to batch mbox v2 API.
     *
     * @return mboxes
     **/

    public List<MboxRequest> getMboxes() {
        return mboxes;
    }

    public void setMboxes(List<MboxRequest> mboxes) {
        if (mboxes != null) {
            // wrap passed in list with our own ArrayList to make sure it is mutable
            this.mboxes = new ArrayList<>(mboxes);
        }
        else {
            this.mboxes = mboxes;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PrefetchRequest prefetchRequest = (PrefetchRequest) o;
        return Objects.equals(this.views, prefetchRequest.views) &&
                Objects.equals(this.pageLoad, prefetchRequest.pageLoad) &&
                Objects.equals(this.mboxes, prefetchRequest.mboxes);
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
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}

