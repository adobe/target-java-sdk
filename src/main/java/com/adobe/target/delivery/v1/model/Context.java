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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Specifies the context for the request, IE if it a web request then is should include user agent
 * etc.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Context {
  @JsonProperty("channel")
  private ChannelType channel = null;

  @JsonProperty("mobilePlatform")
  private MobilePlatform mobilePlatform = null;

  @JsonProperty("application")
  private Application application = null;

  @JsonProperty("screen")
  private Screen screen = null;

  @JsonProperty("window")
  private Window window = null;

  @JsonProperty("browser")
  private Browser browser = null;

  @JsonProperty("address")
  private Address address = null;

  @JsonProperty("geo")
  private Geo geo = null;

  @JsonProperty("timeOffsetInMinutes")
  private Double timeOffsetInMinutes;

  @JsonProperty("userAgent")
  private String userAgent;

  @JsonProperty("beacon")
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
  public Boolean getBeacon() {
    return beacon;
  }

  public void setBeacon(Boolean beacon) {
    this.beacon = beacon;
  }

  @Override
  public boolean equals(Object o) {
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
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
