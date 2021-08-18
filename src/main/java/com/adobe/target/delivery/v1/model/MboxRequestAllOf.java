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
import java.util.Objects;

/** MboxRequestAllOf */
public class MboxRequestAllOf {
  @JsonProperty("index")
  private Integer index;

  @JsonProperty("name")
  private String name;

  public MboxRequestAllOf index(Integer index) {
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
  public Integer getIndex() {
    return index;
  }

  public void setIndex(Integer index) {
    this.index = index;
  }

  public MboxRequestAllOf name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The name of the regional mbox to be evaluated.
   *
   * @return name
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MboxRequestAllOf mboxRequestAllOf = (MboxRequestAllOf) o;
    return Objects.equals(this.index, mboxRequestAllOf.index)
        && Objects.equals(this.name, mboxRequestAllOf.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(index, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MboxRequestAllOf {\n");
    sb.append("    index: ").append(toIndentedString(index)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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
