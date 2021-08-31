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

/** Action */
public class Action {
  @JsonProperty("type")
  private String type;

  @JsonProperty("selector")
  private String selector;

  @JsonProperty("cssSelector")
  private String cssSelector;

  @JsonProperty("content")
  private Object content;

  public Action type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   *
   * @return type
   */
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Action selector(String selector) {
    this.selector = selector;
    return this;
  }

  /**
   * Get selector
   *
   * @return selector
   */
  public String getSelector() {
    return selector;
  }

  public void setSelector(String selector) {
    this.selector = selector;
  }

  public Action cssSelector(String cssSelector) {
    this.cssSelector = cssSelector;
    return this;
  }

  /**
   * Get cssSelector
   *
   * @return cssSelector
   */
  public String getCssSelector() {
    return cssSelector;
  }

  public void setCssSelector(String cssSelector) {
    this.cssSelector = cssSelector;
  }

  public Action content(Object content) {
    this.content = content;
    return this;
  }

  /**
   * Get content
   *
   * @return content
   */
  public Object getContent() {
    return content;
  }

  public void setContent(Object content) {
    this.content = content;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Action action = (Action) o;
    return Objects.equals(this.type, action.type)
        && Objects.equals(this.selector, action.selector)
        && Objects.equals(this.cssSelector, action.cssSelector)
        && Objects.equals(this.content, action.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, selector, cssSelector, content);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Action {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    selector: ").append(toIndentedString(selector)).append("\n");
    sb.append("    cssSelector: ").append(toIndentedString(cssSelector)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
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
