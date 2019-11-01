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
package com.adobe.target.edge.client.service;

import com.adobe.experiencecloud.ecid.visitor.Visitor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class VisitorProvider {

    private static VisitorProvider INSTANCE = null;

    private final String VISITOR_COOKIE_PREFIX = "AMCV_";
    private String visitorCookieName;
    private String orgId;

    private VisitorProvider(String orgId) throws UnsupportedEncodingException {
        this.orgId = orgId;
        this.visitorCookieName = VISITOR_COOKIE_PREFIX + URLEncoder.encode(orgId, "UTF-8");
    }

    public Visitor createVisitor(String visitorCookie) {
        return new Visitor(orgId, visitorCookie);
    }

    public String getVisitorCookieName() {
        return visitorCookieName;
    }

    public static VisitorProvider getInstance() {
        if (INSTANCE == null) {
            throw new TargetRequestException("VisitorProvider instance is not initialized");
        }

        return INSTANCE;
    }

    public static VisitorProvider init(String orgId) {
        try {
            INSTANCE = new VisitorProvider(orgId);
        } catch (UnsupportedEncodingException e) {
            throw new TargetClientException("Error occurred while initializing VisitorProvider", e);
        }
        return INSTANCE;
    }

}
