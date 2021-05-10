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
import java.util.Objects;

/** Browser object may be specified only when the Channel is Web. */
@ApiModel(description = "Browser object may be specified only when the Channel is Web.")
@JsonPropertyOrder({
  Browser.JSON_PROPERTY_HOST,
  Browser.JSON_PROPERTY_LANGUAGE,
  Browser.JSON_PROPERTY_WEB_G_L_RENDERER
})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-10T11:24:27.013-07:00[America/Los_Angeles]")
public class Browser {
  public static final String JSON_PROPERTY_HOST = "host";
  private String host;

  public static final String JSON_PROPERTY_LANGUAGE = "language";
  private String language;

  public static final String JSON_PROPERTY_WEB_G_L_RENDERER = "webGLRenderer";
  private String webGLRenderer;

  public Browser host(String host) {

    this.host = host;
    return this;
  }

  /**
   * Current web page host
   *
   * @return host
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Current web page host")
  @JsonProperty(JSON_PROPERTY_HOST)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public Browser language(String language) {

    this.language = language;
    return this;
  }

  /**
   * Language in Accept-Language header format, see RFC 7231 sec. 5.3.5
   *
   * @return language
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Language in Accept-Language header format, see RFC 7231 sec. 5.3.5")
  @JsonProperty(JSON_PROPERTY_LANGUAGE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public Browser webGLRenderer(String webGLRenderer) {

    this.webGLRenderer = webGLRenderer;
    return this;
  }

  /**
   * This is an optional field, added to help with device detection using device atlas
   *
   * @return webGLRenderer
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value = "This is an optional field, added to help with device detection using device atlas ")
  @JsonProperty(JSON_PROPERTY_WEB_G_L_RENDERER)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getWebGLRenderer() {
    return webGLRenderer;
  }

  public void setWebGLRenderer(String webGLRenderer) {
    this.webGLRenderer = webGLRenderer;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Browser browser = (Browser) o;
    return Objects.equals(this.host, browser.host)
        && Objects.equals(this.language, browser.language)
        && Objects.equals(this.webGLRenderer, browser.webGLRenderer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(host, language, webGLRenderer);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Browser {\n");
    sb.append("    host: ").append(toIndentedString(host)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    webGLRenderer: ").append(toIndentedString(webGLRenderer)).append("\n");
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
