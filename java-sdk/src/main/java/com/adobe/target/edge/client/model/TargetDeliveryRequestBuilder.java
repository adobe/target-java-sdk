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
package com.adobe.target.edge.client.model;

import com.adobe.experiencecloud.ecid.visitor.AmcvEntry;
import com.adobe.experiencecloud.ecid.visitor.CustomerState;
import com.adobe.experiencecloud.ecid.visitor.Visitor;
import com.adobe.target.delivery.v1.model.*;
import com.adobe.target.edge.client.service.VisitorProvider;
import com.adobe.target.edge.client.utils.CollectionUtils;
import com.adobe.target.edge.client.utils.CookieUtils;

import java.util.*;

import static com.adobe.target.edge.client.utils.StringUtils.isEmpty;
import static com.adobe.target.edge.client.utils.StringUtils.isNotEmpty;
import static com.adobe.target.edge.client.utils.TargetConstants.CLUSTER_COOKIE_NAME;
import static com.adobe.target.edge.client.utils.TargetConstants.COOKIE_NAME;
import static com.adobe.target.edge.client.utils.VisitorConstants.*;

public final class TargetDeliveryRequestBuilder {

    private String sessionId;
    private String locationHint;
    private Visitor visitor;
    private String thirdPartyId;
    private String tntId;
    private String marketingCloudVisitorId;
    private ExperienceCloud experienceCloud;
    private Map<String, CustomerState> visitorCustomerIds;
    private List<CustomerId> targetCustomerIds;
    private String trackingServer;
    private String trackingServerSecure;
    private DecisioningMethod decisioningMethod;
    private Map<String, String> requestCookies = new HashMap<>();
    private DeliveryRequest request;

    TargetDeliveryRequestBuilder() {
        this.request = new DeliveryRequest();
    }

    public TargetDeliveryRequestBuilder requestId(String requestId) {
        this.request.requestId(requestId);
        return this;
    }

    public TargetDeliveryRequestBuilder impressionId(String impressionId) {
        request.impressionId(impressionId);
        return this;
    }

    public TargetDeliveryRequestBuilder environmentId(Long environmentId) {
        request.environmentId(environmentId);
        return this;
    }

    public TargetDeliveryRequestBuilder property(Property property) {
        request.property(property);
        return this;
    }

    public TargetDeliveryRequestBuilder trace(Trace trace) {
        request.trace(trace);
        return this;
    }

    public TargetDeliveryRequestBuilder context(Context context) {
        request.context(context);
        return this;
    }

    public TargetDeliveryRequestBuilder execute(ExecuteRequest execute) {
        request.execute(execute);
        return this;
    }

    public TargetDeliveryRequestBuilder prefetch(PrefetchRequest prefetch) {
        request.prefetch(prefetch);
        return this;
    }

    public TargetDeliveryRequestBuilder notifications(List<Notification> notifications) {
        request.notifications(notifications);
        return this;
    }

    public TargetDeliveryRequestBuilder qaMode(QAMode qaMode) {
        request.qaMode(qaMode);
        return this;
    }

    public TargetDeliveryRequestBuilder sessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public TargetDeliveryRequestBuilder locationHint(String locationHint) {
        this.locationHint = locationHint;
        return this;
    }

    public TargetDeliveryRequestBuilder visitor(Visitor visitor) {
        this.visitor = visitor;
        return this;
    }

    public TargetDeliveryRequestBuilder id(VisitorId id) {
        this.request.id(id);
        return this;
    }

    public TargetDeliveryRequestBuilder experienceCloud(ExperienceCloud experienceCloud) {
        this.experienceCloud = experienceCloud;
        return this;
    }

    public TargetDeliveryRequestBuilder cookies(List<TargetCookie> cookies) {
        if (CollectionUtils.isEmpty(cookies)) {
            return this;
        }
        cookies.stream()
                .filter(Objects::nonNull)
                .forEach(cookie -> this.requestCookies.put(cookie.getName(), cookie.getValue()));
        return this;
    }

    public TargetDeliveryRequestBuilder thirdPartyId(String thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
        return this;
    }

    public TargetDeliveryRequestBuilder tntId(String tntId) {
        this.tntId = tntId;
        return this;
    }

    public TargetDeliveryRequestBuilder marketingCloudVisitorId(String marketingCloudVisitorId) {
        this.marketingCloudVisitorId = marketingCloudVisitorId;
        return this;
    }

    public TargetDeliveryRequestBuilder customerIds(Map<String, CustomerState> customerIds) {
        this.visitorCustomerIds = customerIds;
        return this;
    }

    public TargetDeliveryRequestBuilder trackingServer(String trackingServer) {
        this.trackingServer = trackingServer;
        return this;
    }

    public TargetDeliveryRequestBuilder trackingServerSecure(String trackingServerSecure) {
        this.trackingServerSecure = trackingServerSecure;
        return this;
    }

    public TargetDeliveryRequestBuilder decisioningMethod(DecisioningMethod decisioningMethod) {
        this.decisioningMethod = decisioningMethod;
        return this;
    }

    public TargetDeliveryRequest build() {
        setTargetValues();
        setVisitorValues();
        createVisitorId();
        setExperienceCloudValues();
        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.fromRequest(request);
        targetDeliveryRequest.setLocationHint(locationHint);
        targetDeliveryRequest.setSessionId(sessionId);
        targetDeliveryRequest.setVisitor(visitor);
        targetDeliveryRequest.setDecisioningMethod(decisioningMethod);
        return targetDeliveryRequest;
    }

    private void createVisitorId() {
        if (request.getId() != null) {
            return;
        }

        if (isEmpty(tntId) && isEmpty(marketingCloudVisitorId) && isEmpty(thirdPartyId)) {
            return;
        }

        VisitorId visitorId = new VisitorId()
                .tntId(tntId)
                .marketingCloudVisitorId(marketingCloudVisitorId)
                .thirdPartyId(thirdPartyId)
                .customerIds(targetCustomerIds);
        request.id(visitorId);
    }

    private void setTargetValues() {
        String targetCookie = requestCookies.get(COOKIE_NAME);
        Map<String, String> targetCookies = CookieUtils.parseTargetCookies(targetCookie);
        setSessionId(targetCookies);
        setTntId(targetCookies);
        setCustomerIds();
        setEdgeCluster();
    }

    private void setVisitorValues() {
        String visitorCookie = requestCookies.get(VisitorProvider.getInstance().getVisitorCookieName());

        createAndSetVisitor(visitorCookie);
        Map<String, AmcvEntry> visitorValues = visitor.getVisitorValues();
        AmcvEntry entry = visitorValues.get(MARKETING_CLOUD_VISITOR_ID);
        if (entry != null) {
            marketingCloudVisitorId = entry.getValue();
        }
    }

    private void setExperienceCloudValues() {
        if (visitor == null) {
            return;
        }

        getOrCreateExperienceCloud();
        createAndSetAudienceManager(visitor);
        createAndSetAnalyticsValues(visitor);
        request.experienceCloud(experienceCloud);
    }

    private void createAndSetVisitor(String visitorCookie) {
        if (visitor != null) {
            return;
        }

        visitor = VisitorProvider.getInstance().createVisitor(visitorCookie);
        visitor.setCustomerIds(visitorCustomerIds);
    }

    private void getOrCreateExperienceCloud() {
        if (experienceCloud != null) {
            return;
        }

        experienceCloud = new ExperienceCloud();
    }

    private void createAndSetAudienceManager(Visitor visitor) {
        if (experienceCloud.getAudienceManager() != null) {
            return;
        }

        Map<String, AmcvEntry> visitorValues = visitor.getVisitorValues();
        AmcvEntry locationHintEntry = visitorValues.get(LOCATION_HINT);
        AmcvEntry blobEntry = visitorValues.get(BLOB);
        if (locationHintEntry != null && blobEntry != null) {
            int locationHint = Integer.parseInt(locationHintEntry.getValue());
            String blob = blobEntry.getValue();
            AudienceManager audienceManager = new AudienceManager().blob(blob).locationHint(locationHint);
            experienceCloud.audienceManager(audienceManager);
        }
    }

    private void setSessionId(final Map<String, String> parsedCookies) {
        if (isNotEmpty(sessionId)) {
            return;
        }

        final String cookieValue = parsedCookies.get(CookieUtils.SESSION_ID_COOKIE_NAME);

        if (isNotEmpty(cookieValue)) {
            sessionId = cookieValue;
            return;
        }

        sessionId = UUID.randomUUID().toString();
    }

    private void setTntId(final Map<String, String> parsedCookies) {
        if (isNotEmpty(tntId)) {
            return;
        }

        tntId = parsedCookies.get(CookieUtils.DEVICE_ID_COOKIE_NAME);
    }

    private void setCustomerIds() {
        if (CollectionUtils.isEmpty(visitorCustomerIds)) {
            return;
        }

        List<CustomerId> customerIds = new ArrayList<>();
        for (String integrationKey : visitorCustomerIds.keySet()) {
            CustomerState customerState = visitorCustomerIds.get(integrationKey);
            CustomerId customerId = new CustomerId()
                    .id(customerState.getId())
                    .integrationCode(integrationKey);
            switch (customerState.getAuthState()) {
                case AUTHENTICATED:
                    customerId.setAuthenticatedState(AuthenticatedState.AUTHENTICATED);
                    break;
                case LOGGED_OUT:
                    customerId.setAuthenticatedState(AuthenticatedState.LOGGED_OUT);
                    break;
                default:
                    customerId.setAuthenticatedState(AuthenticatedState.UNKNOWN);
                    break;
            }
            customerIds.add(customerId);
        }
        targetCustomerIds = customerIds;
    }

    private void createAndSetAnalyticsValues(Visitor visitor) {
        if (experienceCloud.getAnalytics() != null) {
            return;
        }

        AnalyticsRequest analyticsRequest = new AnalyticsRequest()
                .trackingServer(trackingServer)
                .trackingServerSecure(trackingServerSecure)
                .logging(LoggingType.SERVER_SIDE)
                .supplementalDataId(visitor.getSupplementalDataId(SDID_CONSUMER_ID));
        experienceCloud.analytics(analyticsRequest);
    }

    private void setEdgeCluster() {
        if (isNotEmpty(locationHint)) {
            return;
        }

        locationHint = requestCookies.get(CLUSTER_COOKIE_NAME);
    }

}
