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
package com.adobe.target.edge.client.entities;

import com.adobe.experiencecloud.ecid.visitor.CustomerState;
import com.adobe.experiencecloud.ecid.visitor.VisitorState;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.TargetClient;
import com.adobe.target.edge.client.http.DefaultTargetHttpClient;
import com.adobe.target.edge.client.model.TargetCookie;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.model.TargetDeliveryResponse;
import com.adobe.target.edge.client.service.DefaultTargetService;
import com.adobe.target.delivery.v1.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static com.adobe.target.edge.client.utils.TargetConstants.CLUSTER_COOKIE_NAME;
import static com.adobe.target.edge.client.utils.TargetConstants.COOKIE_NAME;
import static com.adobe.target.edge.client.entities.TargetTestDeliveryRequestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TargetDeliveryRequestTest {

    static final String TEST_SESSION_ID = "430a140336d545daacde53af9636eef5";
    static final String TEST_THIRD_PARTY_ID = "12345678";
    static final String TEST_TNT_ID = "20250794242226839061607285880759069379.22_33";
    static final String TEST_MCID = "20250794242226839061607285880759069379";
    static final String TEST_TRACKING_SERVER = "jimsbrims.sc.omtrds.net";
    static final String TEST_ORG_ID = "0DD934B85278256B0A490D44@AdobeOrg";
    static final String TEST_PROPERTY_TOKEN = "6147bff3-ff76-4793-a185-d2e56380a81a";

    @Mock
    private DefaultTargetHttpClient defaultTargetHttpClient;

    private DefaultTargetService targetService;

    private TargetClient targetJavaClient;

    @BeforeEach
    void init() throws NoSuchFieldException {

        Mockito.lenient().doReturn(getTestDeliveryResponse())
                .when(defaultTargetHttpClient).execute(any(Map.class), any(String.class), any(DeliveryRequest.class),
                any(Class.class));

        ClientConfig clientConfig = ClientConfig.builder()
                .client("emeaprod4")
                .organizationId(TEST_ORG_ID)
                .defaultPropertyToken(TEST_PROPERTY_TOKEN)
                .build();

        targetService = new DefaultTargetService(clientConfig);

        targetJavaClient = TargetClient.create(clientConfig);

        FieldSetter.setField(targetService, targetService.getClass()
                .getDeclaredField("targetHttpClient"), defaultTargetHttpClient);
        FieldSetter.setField(targetJavaClient, targetJavaClient.getClass()
                .getDeclaredField("targetService"), targetService);

    }

    @Test
    void testTargetDeliveryRequestWithCookies() {
        Context context = getContext();
        PrefetchRequest prefetchRequest = getPrefetchViewsRequest();
        ExecuteRequest executeRequest = getMboxExecuteRequest();
        Map<String, CustomerState> customerIds = getCustomerIds();
        List<Notification> mboxNotifications = getMboxNotifications();
        List<TargetCookie> testCookies = getTestCookies();


        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                .context(context)
                .prefetch(prefetchRequest)
                .execute(executeRequest)
                .customerIds(customerIds)
                .notifications(mboxNotifications)
                .trackingServer(TEST_TRACKING_SERVER)
                .cookies(testCookies)
                .thirdPartyId(TEST_THIRD_PARTY_ID)
                .build();

        assertEquals(TEST_SESSION_ID, targetDeliveryRequest.getSessionId());
        assertEquals(prefetchRequest, targetDeliveryRequest.getDeliveryRequest().getPrefetch());
        assertEquals(executeRequest, targetDeliveryRequest.getDeliveryRequest().getExecute());
        assertEquals(context, targetDeliveryRequest.getDeliveryRequest().getContext());
        assertEquals(mboxNotifications, targetDeliveryRequest.getDeliveryRequest().getNotifications());

        verifyId(customerIds, targetDeliveryRequest);
        verifyVisitorValues(targetDeliveryRequest);
        verifyAnalyticsValues(targetDeliveryRequest);

        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        verifyServerStateAndNewCookie(targetDeliveryResponse, TEST_SESSION_ID);
        verifyVisitorState(targetDeliveryResponse, customerIds);
    }

    private void verifyId(Map<String, CustomerState> customerIds, TargetDeliveryRequest targetDeliveryRequest) {
        VisitorId id = targetDeliveryRequest.getDeliveryRequest().getId();
        assertEquals(TEST_TNT_ID, id.getTntId());
        assertEquals(TEST_THIRD_PARTY_ID, id.getThirdPartyId());
        assertEquals(TEST_MCID, id.getMarketingCloudVisitorId());
        validateCustomerId(customerIds, id.getCustomerIds());
    }

    private void verifyVisitorState(TargetDeliveryResponse targetDeliveryResponse,
                                    Map<String, CustomerState> customerIdsExpected) {
        assertNotNull(targetDeliveryResponse.getVisitorState());
        assertEquals(1, targetDeliveryResponse.getVisitorState().size());
        VisitorState visitorState = targetDeliveryResponse.getVisitorState().get(TEST_ORG_ID);
        Map<String, CustomerState> customerIdsActual = visitorState.getCustomerIDs();
        assertEquals(customerIdsExpected, customerIdsActual);
    }

    private void verifyAnalyticsValues(TargetDeliveryRequest targetDeliveryRequest) {
        AnalyticsRequest analytics = targetDeliveryRequest.getDeliveryRequest().getExperienceCloud().getAnalytics();
        assertEquals(LoggingType.SERVER_SIDE, analytics.getLogging());
        assertEquals(TEST_TRACKING_SERVER, analytics.getTrackingServer());
        assertNotNull(analytics.getSupplementalDataId());
    }

    private void verifyVisitorValues(TargetDeliveryRequest targetDeliveryRequest) {
        AudienceManager audienceManager =
                targetDeliveryRequest.getDeliveryRequest().getExperienceCloud().getAudienceManager();
        assertNotNull(audienceManager.getBlob());
        assertNotNull(audienceManager.getLocationHint());
    }

    private void validateCustomerId(Map<String, CustomerState> customerIdMap, List<CustomerId> customerIds) {
        assertEquals(customerIdMap.size(), customerIds.size());
        customerIds.forEach(customerId -> {
            CustomerState customerState = customerIdMap.get(customerId.getIntegrationCode());
            assertNotNull(customerState);
            assertEquals(customerState.getId(), customerId.getId());
            assertEquals(customerState.getAuthState().ordinal(), customerId.getAuthenticatedState().ordinal());
        });
    }

    private void verifyServerStateAndNewCookie(TargetDeliveryResponse targetDeliveryResponse, String newSessionId) {
        List<TargetCookie> targetCookies = targetDeliveryResponse.getCookies();
        targetCookies.sort(Comparator.comparing(TargetCookie::getName));
        assertEquals(2, targetCookies.size());
        assertEquals(COOKIE_NAME, targetCookies.get(0).getName());
        assertTrue(targetCookies.get(0).getValue().startsWith("session#" + newSessionId));
        assertEquals(CLUSTER_COOKIE_NAME, targetCookies.get(1).getName());
        assertEquals(targetCookies.get(1).getValue(), String.valueOf(22));
        assertNotNull(targetDeliveryResponse.getRequest());
        assertNotNull(targetDeliveryResponse.getResponse());
    }

    @Test
    void testTargetDeliveryRequestWithoutCookies() {
        Context context = getContext();
        PrefetchRequest prefetchRequest = getPrefetchViewsRequest();
        ExecuteRequest executeRequest = getMboxExecuteRequest();
        Map<String, CustomerState> customerIds = getCustomerIds();
        List<Notification> mboxNotifications = getMboxNotifications();

        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                .context(context)
                .prefetch(prefetchRequest)
                .execute(executeRequest)
                .customerIds(customerIds)
                .notifications(mboxNotifications)
                .trackingServer(TEST_TRACKING_SERVER)
                .thirdPartyId(TEST_THIRD_PARTY_ID)
                .build();

        String newSessionId = targetDeliveryRequest.getSessionId();
        assertNotNull(newSessionId);
        VisitorId id = targetDeliveryRequest.getDeliveryRequest().getId();
        assertNull(id.getTntId());
        assertEquals(TEST_THIRD_PARTY_ID, id.getThirdPartyId());
        assertNull(id.getMarketingCloudVisitorId());
        validateCustomerId(customerIds, id.getCustomerIds());

        assertEquals(prefetchRequest, targetDeliveryRequest.getDeliveryRequest().getPrefetch());
        assertEquals(executeRequest, targetDeliveryRequest.getDeliveryRequest().getExecute());
        assertEquals(context, targetDeliveryRequest.getDeliveryRequest().getContext());
        assertEquals(mboxNotifications, targetDeliveryRequest.getDeliveryRequest().getNotifications());

        verifyAnalyticsValues(targetDeliveryRequest);

        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        verifyServerStateAndNewCookie(targetDeliveryResponse, newSessionId);
        verifyVisitorState(targetDeliveryResponse, customerIds);
    }

    @Test
    void testTargetDeliveryRequestWithExpiredCookies() {
        Context context = getContext();
        PrefetchRequest prefetchRequest = getPrefetchViewsRequest();
        ExecuteRequest executeRequest = getMboxExecuteRequest();
        Map<String, CustomerState> customerIds = getCustomerIds();
        List<Notification> mboxNotifications = getMboxNotifications();
        List<TargetCookie> expiredSessionCookie = getExpiredSessionCookie();


        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                .context(context)
                .prefetch(prefetchRequest)
                .execute(executeRequest)
                .customerIds(customerIds)
                .notifications(mboxNotifications)
                .trackingServer(TEST_TRACKING_SERVER)
                .cookies(expiredSessionCookie)
                .thirdPartyId(TEST_THIRD_PARTY_ID)
                .build();

        String newSessionId = targetDeliveryRequest.getSessionId();
        assertNotEquals(TEST_SESSION_ID, newSessionId);
        assertEquals(TEST_TNT_ID, targetDeliveryRequest.getDeliveryRequest().getId().getTntId());
        assertEquals(prefetchRequest, targetDeliveryRequest.getDeliveryRequest().getPrefetch());
        assertEquals(executeRequest, targetDeliveryRequest.getDeliveryRequest().getExecute());
        assertEquals(context, targetDeliveryRequest.getDeliveryRequest().getContext());
        assertEquals(mboxNotifications, targetDeliveryRequest.getDeliveryRequest().getNotifications());

        assertEquals(prefetchRequest, targetDeliveryRequest.getDeliveryRequest().getPrefetch());
        assertEquals(executeRequest, targetDeliveryRequest.getDeliveryRequest().getExecute());
        assertEquals(context, targetDeliveryRequest.getDeliveryRequest().getContext());
        assertEquals(mboxNotifications, targetDeliveryRequest.getDeliveryRequest().getNotifications());

        verifyAnalyticsValues(targetDeliveryRequest);

        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        verifyServerStateAndNewCookie(targetDeliveryResponse, newSessionId);
        verifyVisitorState(targetDeliveryResponse, customerIds);
    }

    @Test
    void testTargetDeliveryRequestWithDefaultProperty() {
        Context context = getContext();
        PrefetchRequest prefetchRequest = getPrefetchViewsRequest();
        ExecuteRequest executeRequest = getMboxExecuteRequest();

        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                .context(context)
                .prefetch(prefetchRequest)
                .execute(executeRequest)
                .build();

        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        DeliveryRequest finalRequest = targetDeliveryResponse.getRequest();
        assertEquals(TEST_PROPERTY_TOKEN, finalRequest.getProperty().getToken());
    }

    @Test
    void testTargetDeliveryRequestWithNonDefaultProperty() {
        Context context = getContext();
        PrefetchRequest prefetchRequest = getPrefetchViewsRequest();
        ExecuteRequest executeRequest = getMboxExecuteRequest();
        String nonDefaultToken = "non-default-token";

        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                .context(context)
                .prefetch(prefetchRequest)
                .execute(executeRequest)
                .property(new Property().token(nonDefaultToken))
                .build();

        TargetDeliveryResponse targetDeliveryResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        DeliveryRequest finalRequest = targetDeliveryResponse.getRequest();
        assertEquals(nonDefaultToken, finalRequest.getProperty().getToken());
    }

}
