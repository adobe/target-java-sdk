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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Use this object to enable the QA mode in the request. Use the QA mode to test the look and feel
 * of your site or application for various activities in isolation or together, with the possibilty
 * to match or not match specified audiences, select a specific experience, count the
 * impressions/visits for the reporting or not.
 */
public class QAMode {
  @JsonProperty("token")
  private String token;

  @JsonProperty("listedActivitiesOnly")
  private Boolean listedActivitiesOnly;

  @JsonProperty("evaluateAsTrueAudienceIds")
  private List<Long> evaluateAsTrueAudienceIds = new ArrayList<>();

  @JsonProperty("evaluateAsFalseAudienceIds")
  private List<Long> evaluateAsFalseAudienceIds = new ArrayList<>();

  @JsonProperty("previewIndexes")
  private List<QAModePreviewIndex> previewIndexes = new ArrayList<>();

  public QAMode token(String token) {
    this.token = token;
    return this;
  }

  /**
   * The encrypted token for the QA mode. It contains the list of the activity ids that are allowed
   * to be executed in QA mode. Validation * After decryption, the client code from the token should
   * match the one from the request. * After decryption, activities with the ids specified in the
   * token should belong to the client.
   *
   * @return token
   */
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public QAMode listedActivitiesOnly(Boolean listedActivitiesOnly) {
    this.listedActivitiesOnly = listedActivitiesOnly;
    return this;
  }

  /**
   * Specifies whether qa_mode campaigns should be executed in isolation or if they should be
   * evaluated along other active campaigns for current environment.
   *
   * @return listedActivitiesOnly
   */
  public Boolean getListedActivitiesOnly() {
    return listedActivitiesOnly;
  }

  public void setListedActivitiesOnly(Boolean listedActivitiesOnly) {
    this.listedActivitiesOnly = listedActivitiesOnly;
  }

  public QAMode evaluateAsTrueAudienceIds(List<Long> evaluateAsTrueAudienceIds) {
    this.evaluateAsTrueAudienceIds = evaluateAsTrueAudienceIds;
    return this;
  }

  public QAMode addEvaluateAsTrueAudienceIdsItem(Long evaluateAsTrueAudienceIdsItem) {
    if (this.evaluateAsTrueAudienceIds == null) {
      this.evaluateAsTrueAudienceIds = new ArrayList<>();
    }
    this.evaluateAsTrueAudienceIds.add(evaluateAsTrueAudienceIdsItem);
    return this;
  }

  /**
   * List of audience ids that should be always evaluated as TRUE in the scope of the delivery
   * request
   *
   * @return evaluateAsTrueAudienceIds
   */
  public List<Long> getEvaluateAsTrueAudienceIds() {
    return evaluateAsTrueAudienceIds;
  }

  public void setEvaluateAsTrueAudienceIds(List<Long> evaluateAsTrueAudienceIds) {
    this.evaluateAsTrueAudienceIds = evaluateAsTrueAudienceIds;
  }

  public QAMode evaluateAsFalseAudienceIds(List<Long> evaluateAsFalseAudienceIds) {
    this.evaluateAsFalseAudienceIds = evaluateAsFalseAudienceIds;
    return this;
  }

  public QAMode addEvaluateAsFalseAudienceIdsItem(Long evaluateAsFalseAudienceIdsItem) {
    if (this.evaluateAsFalseAudienceIds == null) {
      this.evaluateAsFalseAudienceIds = new ArrayList<>();
    }
    this.evaluateAsFalseAudienceIds.add(evaluateAsFalseAudienceIdsItem);
    return this;
  }

  /**
   * List of audience ids that should be always evaluated as FALSE in the scope of the delivery
   * request
   *
   * @return evaluateAsFalseAudienceIds
   */
  public List<Long> getEvaluateAsFalseAudienceIds() {
    return evaluateAsFalseAudienceIds;
  }

  public void setEvaluateAsFalseAudienceIds(List<Long> evaluateAsFalseAudienceIds) {
    this.evaluateAsFalseAudienceIds = evaluateAsFalseAudienceIds;
  }

  public QAMode previewIndexes(List<QAModePreviewIndex> previewIndexes) {
    this.previewIndexes = previewIndexes;
    return this;
  }

  public QAMode addPreviewIndexesItem(QAModePreviewIndex previewIndexesItem) {
    if (this.previewIndexes == null) {
      this.previewIndexes = new ArrayList<>();
    }
    this.previewIndexes.add(previewIndexesItem);
    return this;
  }

  /**
   * List of preview indexes. If present, the list cannot be empty.
   *
   * @return previewIndexes
   */
  public List<QAModePreviewIndex> getPreviewIndexes() {
    return previewIndexes;
  }

  public void setPreviewIndexes(List<QAModePreviewIndex> previewIndexes) {
    this.previewIndexes = previewIndexes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QAMode qaMode = (QAMode) o;
    return Objects.equals(this.token, qaMode.token)
        && Objects.equals(this.listedActivitiesOnly, qaMode.listedActivitiesOnly)
        && Objects.equals(this.evaluateAsTrueAudienceIds, qaMode.evaluateAsTrueAudienceIds)
        && Objects.equals(this.evaluateAsFalseAudienceIds, qaMode.evaluateAsFalseAudienceIds)
        && Objects.equals(this.previewIndexes, qaMode.previewIndexes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        token,
        listedActivitiesOnly,
        evaluateAsTrueAudienceIds,
        evaluateAsFalseAudienceIds,
        previewIndexes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QAMode {\n");
    sb.append("    token: ").append(toIndentedString(token)).append("\n");
    sb.append("    listedActivitiesOnly: ")
        .append(toIndentedString(listedActivitiesOnly))
        .append("\n");
    sb.append("    evaluateAsTrueAudienceIds: ")
        .append(toIndentedString(evaluateAsTrueAudienceIds))
        .append("\n");
    sb.append("    evaluateAsFalseAudienceIds: ")
        .append(toIndentedString(evaluateAsFalseAudienceIds))
        .append("\n");
    sb.append("    previewIndexes: ").append(toIndentedString(previewIndexes)).append("\n");
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
