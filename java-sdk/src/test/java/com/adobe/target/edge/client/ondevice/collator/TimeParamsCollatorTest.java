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

import com.adobe.target.delivery.v1.model.ExecuteRequest;
import com.adobe.target.delivery.v1.model.RequestDetails;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.service.VisitorProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;

public class TimeParamsCollatorTest {

    @Test
    public void testCollator() {
        VisitorProvider.init("testOrgId");
        TimeParamsCollator collator = mock(TimeParamsCollator.class, CALLS_REAL_METHODS);
        long now = 1592433971000L;
        Mockito.doReturn(now)
                .when(collator).currentTimestamp();
        RequestDetails pageLoad = new RequestDetails();
        TargetDeliveryRequest request = TargetDeliveryRequest.builder()
                .execute(new ExecuteRequest().pageLoad(pageLoad))
                .build();
        Map<String, Object> result = collator.collateParams(request, pageLoad);
        assertEquals(now, result.get(TimeParamsCollator.CURRENT_TIMESTAMP));
        assertEquals("3", result.get(TimeParamsCollator.CURRENT_DAY));
        assertEquals("2246", result.get(TimeParamsCollator.CURRENT_TIME));
    }
}
