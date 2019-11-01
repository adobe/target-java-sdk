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

import java.util.Objects;


/**
 * NotificationMbox
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationMbox {
    @JsonProperty("name")
    private String name;

    @JsonProperty("state")
    private String state;

    public NotificationMbox name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Mbox name. Validation   * No blank values allowed.   * A &#39;-clicked&#39; suffix or the following names are
     * not allowed for mbox names: ** display mboxes **, ** any mbox **, ** click from display mbox **   * Allowed
     * chars: - &#39;-, ./&#x3D;&#x60;:;&amp;!@#$%^&amp;*()+|?~[]{}&#39;
     *
     * @return name
     **/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NotificationMbox state(String state) {
        this.state = state;
        return this;
    }

    /**
     * Mbox state token
     *
     * @return state
     **/

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NotificationMbox notificationMbox = (NotificationMbox) o;
        return Objects.equals(this.name, notificationMbox.name) &&
                Objects.equals(this.state, notificationMbox.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, state);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class NotificationMbox {\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    state: ").append(toIndentedString(state)).append("\n");
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

