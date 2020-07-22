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
 */
package com.adobe.target.edge.client.ondevice.collator;

import com.adobe.target.delivery.v1.model.Address;
import com.adobe.target.delivery.v1.model.Context;
import com.adobe.target.delivery.v1.model.ExecuteRequest;
import com.adobe.target.delivery.v1.model.RequestDetails;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.service.VisitorProvider;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PageParamsCollatorTest {

    @Test
    public void testCollator() {
        VisitorProvider.init("testOrgId");
        String url = "http://WWW.TARGET.ADOBE.COM/ABOUT/?foo=bar&name=JimmyG#Part1";

        RequestDetails pageLoad = new RequestDetails();
        TargetDeliveryRequest request = TargetDeliveryRequest.builder()
                .execute(new ExecuteRequest().pageLoad(pageLoad))
                .context(new Context().address(new Address().url(url)))
                .build();
        PageParamsCollator collator = new PageParamsCollator();
        Map<String, Object> result = collator.collateParams(request, pageLoad);

        assertEquals(url, result.get(PageParamsCollator.PAGE_URL));
        assertEquals(url.toLowerCase(), result.get(PageParamsCollator.PAGE_URL_LOWER));
        assertEquals("/ABOUT/", result.get(PageParamsCollator.PAGE_PATH));
        assertEquals("/about/", result.get(PageParamsCollator.PAGE_PATH_LOWER));
        assertEquals("WWW.TARGET.ADOBE.COM", result.get(PageParamsCollator.PAGE_DOMAIN));
        assertEquals("www.target.adobe.com", result.get(PageParamsCollator.PAGE_DOMAIN_LOWER));
        assertEquals("TARGET", result.get(PageParamsCollator.PAGE_SUBDOMAIN));
        assertEquals("target", result.get(PageParamsCollator.PAGE_SUBDOMAIN_LOWER));
        assertEquals("COM", result.get(PageParamsCollator.PAGE_TOP_LEVEL_DOMAIN));
        assertEquals("com", result.get(PageParamsCollator.PAGE_TOP_LEVEL_DOMAIN_LOWER));
        assertEquals("foo=bar&name=JimmyG", result.get(PageParamsCollator.PAGE_QUERY));
        assertEquals("foo=bar&name=jimmyg", result.get(PageParamsCollator.PAGE_QUERY_LOWER));
        assertEquals("Part1", result.get(PageParamsCollator.PAGE_FRAGMENT));
        assertEquals("part1", result.get(PageParamsCollator.PAGE_FRAGMENT_LOWER));
    }

}
