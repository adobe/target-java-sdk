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

/**
 * Specifies the context for the request, IE if it a web request then is should include user agent
 * etc.
 */
@ApiModel(
    description =
        "Specifies the context for the request, IE if it a web request then is should include user agent etc.")
@JsonPropertyOrder({
  Context.JSON_PROPERTY_CHANNEL,
  Context.JSON_PROPERTY_MOBILE_PLATFORM,
  Context.JSON_PROPERTY_APPLICATION,
  Context.JSON_PROPERTY_SCREEN,
  Context.JSON_PROPERTY_WINDOW,
  Context.JSON_PROPERTY_BROWSER,
  Context.JSON_PROPERTY_ADDRESS,
  Context.JSON_PROPERTY_GEO,
  Context.JSON_PROPERTY_TIME_OFFSET_IN_MINUTES,
  Context.JSON_PROPERTY_USER_AGENT,
  Context.JSON_PROPERTY_BEACON
})
@javax.annotation.Generated(
    value = "org.openapitools.codegen.languages.JavaClientCodegen",
    date = "2021-05-10T11:24:27.013-07:00[America/Los_Angeles]")
public class Context {
  public static final String JSON_PROPERTY_CHANNEL = "channel";
  private ChannelType channel;

  public static final String JSON_PROPERTY_MOBILE_PLATFORM = "mobilePlatform";
  private MobilePlatform mobilePlatform;

  public static final String JSON_PROPERTY_APPLICATION = "application";
  private Application application;

  public static final String JSON_PROPERTY_SCREEN = "screen";
  private Screen screen;

  public static final String JSON_PROPERTY_WINDOW = "window";
  private Window window;

  public static final String JSON_PROPERTY_BROWSER = "browser";
  private Browser browser;

  public static final String JSON_PROPERTY_ADDRESS = "address";
  private Address address;

  public static final String JSON_PROPERTY_GEO = "geo";
  private Geo geo;

  public static final String JSON_PROPERTY_TIME_OFFSET_IN_MINUTES = "timeOffsetInMinutes";
  private Double timeOffsetInMinutes;

  public static final String JSON_PROPERTY_USER_AGENT = "userAgent";
  private String userAgent;

  public static final String JSON_PROPERTY_BEACON = "beacon";
  private Boolean beacon = false;

  public Context channel(ChannelType channel) {

    this.channel = channel;
    return this;
  }

  /**
   * Get channel
   *
   * @return channel
   */
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_CHANNEL)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public ChannelType getChannel() {
    return channel;
  }

  public void setChannel(ChannelType channel) {
    this.channel = channel;
  }

  public Context mobilePlatform(MobilePlatform mobilePlatform) {

    this.mobilePlatform = mobilePlatform;
    return this;
  }

  /**
   * Get mobilePlatform
   *
   * @return mobilePlatform
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_MOBILE_PLATFORM)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public MobilePlatform getMobilePlatform() {
    return mobilePlatform;
  }

  public void setMobilePlatform(MobilePlatform mobilePlatform) {
    this.mobilePlatform = mobilePlatform;
  }

  public Context application(Application application) {

    this.application = application;
    return this;
  }

  /**
   * Get application
   *
   * @return application
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_APPLICATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Application getApplication() {
    return application;
  }

  public void setApplication(Application application) {
    this.application = application;
  }

  public Context screen(Screen screen) {

    this.screen = screen;
    return this;
  }

  /**
   * Get screen
   *
   * @return screen
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_SCREEN)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Screen getScreen() {
    return screen;
  }

  public void setScreen(Screen screen) {
    this.screen = screen;
  }

  public Context window(Window window) {

    this.window = window;
    return this;
  }

  /**
   * Get window
   *
   * @return window
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_WINDOW)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Window getWindow() {
    return window;
  }

  public void setWindow(Window window) {
    this.window = window;
  }

  public Context browser(Browser browser) {

    this.browser = browser;
    return this;
  }

  /**
   * Get browser
   *
   * @return browser
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_BROWSER)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Browser getBrowser() {
    return browser;
  }

  public void setBrowser(Browser browser) {
    this.browser = browser;
  }

  public Context address(Address address) {

    this.address = address;
    return this;
  }

  /**
   * Get address
   *
   * @return address
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_ADDRESS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public Context geo(Geo geo) {

    this.geo = geo;
    return this;
  }

  /**
   * Get geo
   *
   * @return geo
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_GEO)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Geo getGeo() {
    return geo;
  }

  public void setGeo(Geo geo) {
    this.geo = geo;
  }

  public Context timeOffsetInMinutes(Double timeOffsetInMinutes) {

    this.timeOffsetInMinutes = timeOffsetInMinutes;
    return this;
  }

  /**
   * Specifies minutes from UTC for specific client
   *
   * @return timeOffsetInMinutes
   */
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Specifies minutes from UTC for specific client")
  @JsonProperty(JSON_PROPERTY_TIME_OFFSET_IN_MINUTES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Double getTimeOffsetInMinutes() {
    return timeOffsetInMinutes;
  }

  public void setTimeOffsetInMinutes(Double timeOffsetInMinutes) {
    this.timeOffsetInMinutes = timeOffsetInMinutes;
  }

  public Context userAgent(String userAgent) {

    this.userAgent = userAgent;
    return this;
  }

  /**
   * User-Agent should be sent only via this property. HTTP header User-Agent is ignored.
   *
   * @return userAgent
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "User-Agent should be sent only via this property. HTTP header User-Agent is ignored.")
  @JsonProperty(JSON_PROPERTY_USER_AGENT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public Context beacon(Boolean beacon) {

    this.beacon = beacon;
    return this;
  }

  /**
   * In case beacon &#x3D; true is provided in the request, the server will return a 204 No Content
   * response with no response body.
   *
   * @return beacon
   */
  @javax.annotation.Nullable
  @ApiModelProperty(
      value =
          "In case beacon = true is provided in the request, the server will return a 204 No Content response with no response body. ")
  @JsonProperty(JSON_PROPERTY_BEACON)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Boolean getBeacon() {
    return beacon;
  }

  public void setBeacon(Boolean beacon) {
    this.beacon = beacon;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Context context = (Context) o;
    return Objects.equals(this.channel, context.channel)
        && Objects.equals(this.mobilePlatform, context.mobilePlatform)
        && Objects.equals(this.application, context.application)
        && Objects.equals(this.screen, context.screen)
        && Objects.equals(this.window, context.window)
        && Objects.equals(this.browser, context.browser)
        && Objects.equals(this.address, context.address)
        && Objects.equals(this.geo, context.geo)
        && Objects.equals(this.timeOffsetInMinutes, context.timeOffsetInMinutes)
        && Objects.equals(this.userAgent, context.userAgent)
        && Objects.equals(this.beacon, context.beacon);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        channel,
        mobilePlatform,
        application,
        screen,
        window,
        browser,
        address,
        geo,
        timeOffsetInMinutes,
        userAgent,
        beacon);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Context {\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    mobilePlatform: ").append(toIndentedString(mobilePlatform)).append("\n");
    sb.append("    application: ").append(toIndentedString(application)).append("\n");
    sb.append("    screen: ").append(toIndentedString(screen)).append("\n");
    sb.append("    window: ").append(toIndentedString(window)).append("\n");
    sb.append("    browser: ").append(toIndentedString(browser)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    geo: ").append(toIndentedString(geo)).append("\n");
    sb.append("    timeOffsetInMinutes: ")
        .append(toIndentedString(timeOffsetInMinutes))
        .append("\n");
    sb.append("    userAgent: ").append(toIndentedString(userAgent)).append("\n");
    sb.append("    beacon: ").append(toIndentedString(beacon)).append("\n");
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
