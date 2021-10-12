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
package com.adobe.target.edge.client.entities;

import static com.adobe.target.edge.client.utils.TargetTestDeliveryRequestUtils.getContext;
import static com.adobe.target.edge.client.utils.TargetTestDeliveryRequestUtils.getMboxExecuteRequest;
import static com.adobe.target.edge.client.utils.TargetTestDeliveryRequestUtils.getPrefetchViewsRequest;
import static com.adobe.target.edge.client.utils.TargetTestDeliveryRequestUtils.getTestDeliveryResponseFailure;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import com.adobe.target.delivery.v1.model.Context;
import com.adobe.target.delivery.v1.model.DeliveryRequest;
import com.adobe.target.delivery.v1.model.ExecuteRequest;
import com.adobe.target.delivery.v1.model.PrefetchRequest;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.TargetClient;
import com.adobe.target.edge.client.exception.TargetRequestException;
import com.adobe.target.edge.client.http.DefaultTargetHttpClient;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.model.TargetDeliveryResponse;
import com.adobe.target.edge.client.service.DefaultTargetService;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TargetDeliveryExceptionTest {

  static final String TEST_ORG_ID = "0DD934B85278256B0A490D44@AdobeOrg";
  static final String TEST_PROPERTY_TOKEN = "6147bff3-ff76-4793-a185-d2e56380a81a";

  @Mock private DefaultTargetHttpClient defaultTargetHttpClient;

  private DefaultTargetService targetService;

  private TargetClient targetJavaClient;

  @BeforeEach
  void init() throws NoSuchFieldException {

    Mockito.lenient()
        .doReturn(
            getTestDeliveryResponseFailure(
                "ERROR: application server timeout.", "ERROR: application server timeout."))
        .when(defaultTargetHttpClient)
        .execute(any(Map.class), any(String.class), any(DeliveryRequest.class), any(Class.class));

    ClientConfig clientConfig =
        ClientConfig.builder()
            .organizationId(TEST_ORG_ID)
            .defaultPropertyToken(TEST_PROPERTY_TOKEN)
            .build();

    targetService = new DefaultTargetService(clientConfig);

    targetJavaClient = TargetClient.create(clientConfig);

    FieldSetter.setField(
        targetService,
        targetService.getClass().getDeclaredField("targetHttpClient"),
        defaultTargetHttpClient);
    FieldSetter.setField(
        targetJavaClient,
        targetJavaClient.getClass().getDeclaredField("targetService"),
        targetService);
  }

  @Test
  void testTargetDeliveryRequest() {
    Context context = getContext();
    PrefetchRequest prefetchRequest = getPrefetchViewsRequest();
    ExecuteRequest executeRequest = getMboxExecuteRequest();

    TargetDeliveryRequest targetDeliveryRequest =
        TargetDeliveryRequest.builder()
            .context(context)
            .prefetch(prefetchRequest)
            .execute(executeRequest)
            .build();

    TargetRequestException exception =
        assertThrows(
            TargetRequestException.class,
            () -> {
              TargetDeliveryResponse targetDeliveryResponse =
                  targetJavaClient.getOffers(targetDeliveryRequest);
            });

    assertNotNull(exception);

    assertNotNull(exception.getRequest());
    assertNotNull(exception.getRequest().getSessionId());
  }
}
