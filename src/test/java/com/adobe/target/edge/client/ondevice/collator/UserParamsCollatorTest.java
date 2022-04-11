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
 */
package com.adobe.target.edge.client.ondevice.collator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.adobe.target.delivery.v1.model.ClientHints;
import com.adobe.target.delivery.v1.model.Context;
import com.adobe.target.delivery.v1.model.ExecuteRequest;
import com.adobe.target.delivery.v1.model.RequestDetails;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.service.VisitorProvider;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserParamsCollatorTest {

  private final RequestDetails pageLoad = new RequestDetails();
  private final UserParamsCollator collator = new UserParamsCollator();

  @BeforeEach
  void init() {
    VisitorProvider.init("testOrgId");
  }

  @Test
  public void testFirefoxWindows() {
    String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0";
    TargetDeliveryRequest request = requestWithUserAgent(userAgent);
    Map<String, Object> result = collator.collateParams(request, pageLoad);
    assertEquals("firefox", result.get(UserParamsCollator.USER_BROWSER_TYPE));
    assertEquals("54", result.get(UserParamsCollator.USER_BROWSER_VERSION));
    assertEquals("windows", result.get(UserParamsCollator.USER_PLATFORM));
  }

  @Test
  public void testSafariIphone() {
    String userAgent =
        "Mozilla/5.0 (iPhone; CPU iPhone OS 13_1_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.1 Mobile/15E148 Safari/604.1";
    TargetDeliveryRequest request = requestWithUserAgent(userAgent);
    Map<String, Object> result = collator.collateParams(request, pageLoad);
    assertEquals("iphone", result.get(UserParamsCollator.USER_BROWSER_TYPE));
    assertEquals("13", result.get(UserParamsCollator.USER_BROWSER_VERSION));
  }

  @Test
  public void testSafariMac() {
    String userAgent =
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.1 Safari/605.1.15";
    TargetDeliveryRequest request = requestWithUserAgent(userAgent);
    Map<String, Object> result = collator.collateParams(request, pageLoad);
    assertEquals("safari", result.get(UserParamsCollator.USER_BROWSER_TYPE));
    assertEquals("13", result.get(UserParamsCollator.USER_BROWSER_VERSION));
    assertEquals("mac", result.get(UserParamsCollator.USER_PLATFORM));
  }

  @Test
  public void testChromeLinux() {
    String userAgent =
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.157 Safari/537.36";
    TargetDeliveryRequest request = requestWithUserAgent(userAgent);
    Map<String, Object> result = collator.collateParams(request, pageLoad);
    assertEquals("chrome", result.get(UserParamsCollator.USER_BROWSER_TYPE));
    assertEquals("44", result.get(UserParamsCollator.USER_BROWSER_VERSION));
    assertEquals("linux", result.get(UserParamsCollator.USER_PLATFORM));
  }

  @Test
  public void testClientHintsBrowserUAFullVersionInChrome() {
    ClientHints clientHints = new ClientHints();
    clientHints.setMobile(true);
    clientHints.setPlatform("macOS");
    clientHints.setArchitecture("x86");
    clientHints.setPlatformVersion("11.3.1");
    clientHints.setBrowserUAWithFullVersion(
        "\" Not A;Brand\";v=\"99.0.0.0\", \"Chromium\";v=\"99.0.4844.83\", \"Google Chrome\";v=\"99.0.4844.83\"");
    String userAgent =
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.109 Safari/537.36";

    TargetDeliveryRequest request = requestWithClientHintsAndUA(clientHints, userAgent);
    Map<String, Object> result = collator.collateParams(request, pageLoad);
    assertEquals("chrome", result.get(UserParamsCollator.USER_BROWSER_TYPE));
    assertEquals("mac", result.get(UserParamsCollator.USER_PLATFORM));
    assertEquals("99", result.get(UserParamsCollator.USER_BROWSER_VERSION));
  }

  @Test
  public void testClientHintsBrowserUAMajorVersionInChrome() {
    ClientHints clientHints = new ClientHints();
    clientHints.setMobile(true);
    clientHints.setPlatform("macOS");
    clientHints.setArchitecture("x86");
    clientHints.setPlatformVersion("11.3.1");
    clientHints.setBrowserUAWithMajorVersion(
        "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"99\", \"Google Chrome\";v=\"99\"");
    String userAgent =
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.109 Safari/537.36";

    TargetDeliveryRequest request = requestWithClientHintsAndUA(clientHints, userAgent);
    Map<String, Object> result = collator.collateParams(request, pageLoad);
    assertEquals("chrome", result.get(UserParamsCollator.USER_BROWSER_TYPE));
    assertEquals("mac", result.get(UserParamsCollator.USER_PLATFORM));
    assertEquals("99", result.get(UserParamsCollator.USER_BROWSER_VERSION));
  }

  @Test
  public void testClientHintsInEdge() {
    ClientHints clientHints = new ClientHints();
    clientHints.setMobile(true);
    clientHints.setPlatform("Windows");
    clientHints.setArchitecture("x86");
    clientHints.setPlatformVersion("11.3.1");
    clientHints.setBrowserUAWithFullVersion(
        "\" Not A;Brand\";v=\"99.0.0.0\", \"Chromium\";v=\"99.0.1150.46\", \"Microsoft Edge\";v=\"102.0.1150.46\"");
    String userAgent = "";

    TargetDeliveryRequest request = requestWithClientHintsAndUA(clientHints, userAgent);
    Map<String, Object> result = collator.collateParams(request, pageLoad);
    assertEquals("edge", result.get(UserParamsCollator.USER_BROWSER_TYPE));
    assertEquals("windows", result.get(UserParamsCollator.USER_PLATFORM));
    assertEquals("102", result.get(UserParamsCollator.USER_BROWSER_VERSION));
  }

  @Test
  public void testBrowserVersionFromWhenClientHintAbsent() {
    ClientHints clientHints = new ClientHints();
    clientHints.setMobile(true);
    clientHints.setPlatform("macOS");
    clientHints.setArchitecture("x86");
    clientHints.setPlatformVersion("11.3.1");
    String userAgent =
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.109 Safari/537.36";

    TargetDeliveryRequest request = requestWithClientHintsAndUA(clientHints, userAgent);
    Map<String, Object> result = collator.collateParams(request, pageLoad);
    assertEquals("chrome", result.get(UserParamsCollator.USER_BROWSER_TYPE));
    assertEquals("mac", result.get(UserParamsCollator.USER_PLATFORM));
    assertEquals("98", result.get(UserParamsCollator.USER_BROWSER_VERSION));
  }

  private TargetDeliveryRequest requestWithUserAgent(String userAgent) {
    return TargetDeliveryRequest.builder()
        .execute(new ExecuteRequest().pageLoad(pageLoad))
        .context(new Context().userAgent(userAgent))
        .build();
  }

  private TargetDeliveryRequest requestWithClientHintsAndUA(
      ClientHints clientHints, String userAgent) {
    return TargetDeliveryRequest.builder()
        .execute(new ExecuteRequest().pageLoad(pageLoad))
        .context(new Context().clientHints(clientHints).userAgent(userAgent))
        .build();
  }
}
