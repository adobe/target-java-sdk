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
 * QAModePreviewIndex
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QAModePreviewIndex {
    @JsonProperty("activityIndex")
    private Integer activityIndex;

    @JsonProperty("experienceIndex")
    private Integer experienceIndex;

    public QAModePreviewIndex activityIndex(Integer activityIndex) {
        this.activityIndex = activityIndex;
        return this;
    }

    /**
     * Index references the activity in the activity list (from the encrypted token). Validation   * If the activity
     * list index is out of boundaries of the activities list in the token or if it is null it will  be ignored.   *
     * Activity list index starts with 1.   * Should be at least one activity index, and should reference an activity
     * specified in the token.
     * minimum: 1
     *
     * @return activityIndex
     **/

    public Integer getActivityIndex() {
        return activityIndex;
    }

    public void setActivityIndex(Integer activityIndex) {
        this.activityIndex = activityIndex;
    }

    public QAModePreviewIndex experienceIndex(Integer experienceIndex) {
        this.experienceIndex = experienceIndex;
        return this;
    }

    /**
     * When specified, the experience with this index in the activity definition will be selected. Validation   * Can
     * be null (unspecified)   * If index is not specified or out of bounds, the experience will be selected via
     * activity experience selector strategy.   * Experience index starts with 1.
     * minimum: 1
     *
     * @return experienceIndex
     **/

    public Integer getExperienceIndex() {
        return experienceIndex;
    }

    public void setExperienceIndex(Integer experienceIndex) {
        this.experienceIndex = experienceIndex;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QAModePreviewIndex qaModePreviewIndex = (QAModePreviewIndex) o;
        return Objects.equals(this.activityIndex, qaModePreviewIndex.activityIndex) &&
                Objects.equals(this.experienceIndex, qaModePreviewIndex.experienceIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityIndex, experienceIndex);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class QAModePreviewIndex {\n");
        sb.append("    activityIndex: ").append(toIndentedString(activityIndex)).append("\n");
        sb.append("    experienceIndex: ").append(toIndentedString(experienceIndex)).append("\n");
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

