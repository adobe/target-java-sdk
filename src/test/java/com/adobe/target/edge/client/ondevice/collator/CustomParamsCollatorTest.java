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

import com.adobe.target.delivery.v1.model.ExecuteRequest;
import com.adobe.target.delivery.v1.model.RequestDetails;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.service.VisitorProvider;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class CustomParamsCollatorTest {

  @Test
  public void testCollator() {
    VisitorProvider.init("testOrgId");
    Map<String, String> params =
        new HashMap<String, String>() {
          {
            put("foo", "bar");
            put("BAZ", "BUZ");
          }
        };
    RequestDetails pageLoad = new RequestDetails().parameters(params);
    TargetDeliveryRequest request =
        TargetDeliveryRequest.builder().execute(new ExecuteRequest().pageLoad(pageLoad)).build();
    CustomParamsCollator collator = new CustomParamsCollator();
    Map<String, Object> result = collator.collateParams(request, pageLoad);
    assertEquals("bar", result.get("foo"));
    assertEquals("bar", result.get("foo" + CustomParamsCollator.LOWER_CASE_POSTFIX));
    assertEquals("BUZ", result.get("BAZ"));
    assertEquals("buz", result.get("BAZ" + CustomParamsCollator.LOWER_CASE_POSTFIX));
  }

  @Test
  public void testCustomDotNotation() {
    VisitorProvider.init("testOrgId");
    Map<String, String> params =
        new HashMap<String, String>() {
          {
            put("foo", "bar");
            put("BAZ", "BUZ");
            put("dot.notation", "isConfusing");
            put("first.second.third", "value");
            put("first.second.wonky", "DONKEY");
            put("this..should..be", "ignored");
            put(".something", "aaa");
            put("=cranky .chicken.", "bbb");
          }
        };
    RequestDetails pageLoad = new RequestDetails().parameters(params);
    TargetDeliveryRequest request =
        TargetDeliveryRequest.builder().execute(new ExecuteRequest().pageLoad(pageLoad)).build();
    CustomParamsCollator collator = new CustomParamsCollator();
    Map<String, Object> result = collator.collateParams(request, pageLoad);

    Map<String, Object> dot = (Map<String, Object>) result.get("dot");
    assertEquals("isConfusing", dot.get("notation"));
    assertEquals("isconfusing", dot.get("notation" + CustomParamsCollator.LOWER_CASE_POSTFIX));

    Map<String, Object> first = (Map<String, Object>) result.get("first");
    Map<String, Object> second = (Map<String, Object>) first.get("second");
    assertEquals("value", second.get("third"));
    assertEquals("value", second.get("third" + CustomParamsCollator.LOWER_CASE_POSTFIX));
    assertEquals("DONKEY", second.get("wonky"));
    assertEquals("donkey", second.get("wonky" + CustomParamsCollator.LOWER_CASE_POSTFIX));
    assertEquals("ignored", result.get("this..should..be"));
    assertEquals("aaa", result.get(".something"));
    assertEquals("bbb", result.get("=cranky .chicken."));
  }
}
