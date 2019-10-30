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
 * Notification for the displayed view.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationView {
    @JsonProperty("name")
    private String name;

    @JsonProperty("key")
    private String key;

    @JsonProperty("state")
    private String state;

    public NotificationView name(String name) {
        this.name = name;
        return this;
    }

    /**
     * View Name - Unique view name. If the activity has a metric with a view with this name it will be matched,
     * providing the Key matches as well or is null and view and metric targeting is matched.
     *
     * @return name
     **/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NotificationView key(String key) {
        this.key = key;
        return this;
    }

    /**
     * View Key - An optional encoded String identifier used in advanced scenarios, such as View fingerprinting. Same
     * matching conditions as for View Name.
     *
     * @return key
     **/

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public NotificationView state(String state) {
        this.state = state;
        return this;
    }

    /**
     * View state token.
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
        NotificationView notificationView = (NotificationView) o;
        return Objects.equals(this.name, notificationView.name) &&
                Objects.equals(this.key, notificationView.key) &&
                Objects.equals(this.state, notificationView.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, key, state);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class NotificationView {\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    key: ").append(toIndentedString(key)).append("\n");
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

