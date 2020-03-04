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
package com.adobe.target.edge.client.utils;

import com.adobe.target.edge.client.model.TargetCookie;
import com.adobe.target.edge.client.service.VisitorProvider;

import java.util.*;
import java.util.regex.Pattern;

import static com.adobe.target.edge.client.utils.TargetConstants.CLUSTER_COOKIE_NAME;
import static com.adobe.target.edge.client.utils.TargetConstants.COOKIE_NAME;
import static com.adobe.target.edge.client.utils.StringUtils.isEmpty;
import static com.adobe.target.edge.client.utils.StringUtils.isNotEmpty;

public class CookieUtils {

    private static final String COOKIE_VALUE_SEPARATOR = "|";
    private static final String INTERNAL_COOKIE_SERIALIZATION_SEPARATOR = "#";
    public static final String SESSION_ID_COOKIE_NAME = "session";
    public static final String DEVICE_ID_COOKIE_NAME = "PC";
    private static final int SESSION_ID_COOKIE_MAX_AGE = 1860;
    private static final int DEVICE_ID_COOKIE_MAX_AGE = 63244800;
    private static final int CLUSTER_LOCATION_HINT_MAX_AGE = 1860;
    private static Set<String> TARGET_COOKIE_NAMES;

    public static String getDeviceId(final Map<String, TargetCookie> parseCookies) {
        String returnValue = null;
        final TargetCookie cookie = parseCookies.get(DEVICE_ID_COOKIE_NAME);
        if (cookie != null) {
            returnValue = cookie.getValue();
        }
        return returnValue;
    }

    public static String getSessionId(final Map<String, TargetCookie> parseCookies) {
        final TargetCookie cookie = parseCookies.get(SESSION_ID_COOKIE_NAME);
        String sessionId;

        if (cookie != null && !isEmpty(cookie.getValue())) {
            sessionId = cookie.getValue();
        } else {
            sessionId = UUID.randomUUID().toString().replaceAll("-", "");
        }

        return sessionId;
    }

    public static Map<String, String> parseTargetCookies(String targetCookie) {

        if (isEmpty(targetCookie)) {
            return Collections.EMPTY_MAP;
        }

        int nowInSeconds = (int) (System.currentTimeMillis() / 1000);
        Map<String, String> internalTargetCookies = new HashMap<>();
        final String[] rawInternalCookies = targetCookie.split(Pattern.quote(COOKIE_VALUE_SEPARATOR));
        for (final String rawInternalCookie : rawInternalCookies) {

            if (isEmpty(rawInternalCookie)) {
                break;
            }

            final TargetCookie internalCookie = deserializeInternalCookie(rawInternalCookie);

            if (internalCookie != null && internalCookie.getMaxAge() > nowInSeconds) {
                internalTargetCookies.put(internalCookie.getName(), internalCookie.getValue());
            }

        }
        return internalTargetCookies;
    }

    private static TargetCookie deserializeInternalCookie(String cookie) {
        final String[] cookieTokens = cookie.split(Pattern.quote(INTERNAL_COOKIE_SERIALIZATION_SEPARATOR));

        if (cookieTokens != null && cookieTokens.length == 3) {
            final int expires = Integer.parseInt(cookieTokens[2]);
            return new TargetCookie(cookieTokens[0], cookieTokens[1], expires);
        }

        return null;
    }

    public static Optional<TargetCookie> createTargetCookie(String sessionId, String deviceId) {
        final long nowInSeconds = System.currentTimeMillis() / 1000;
        final StringBuilder targetCookieValue = new StringBuilder();
        long maxAge = 0;
        maxAge = createSessionId(sessionId, nowInSeconds, targetCookieValue, maxAge);
        maxAge = createDeviceId(deviceId, nowInSeconds, targetCookieValue, maxAge);
        TargetCookie targetCookie = null;
        String cookieValue = targetCookieValue.toString();

        if (isNotEmpty(cookieValue)) {
            targetCookie = new TargetCookie(COOKIE_NAME, cookieValue, (int) (maxAge / 1000));
        }

        return Optional.ofNullable(targetCookie);
    }

    private static long createDeviceId(String deviceId, long nowInSeconds, StringBuilder targetCookieValue,
                                       long maxAge) {
        if (isEmpty(deviceId)) {
            return maxAge;
        }

        long deviceIdMaxAge = nowInSeconds + DEVICE_ID_COOKIE_MAX_AGE;
        maxAge = Math.max(maxAge, deviceIdMaxAge);
        appendCookieValue(deviceId, targetCookieValue, deviceIdMaxAge, DEVICE_ID_COOKIE_NAME);

        return maxAge;
    }

    private static long createSessionId(String sessionId, long nowInSeconds, StringBuilder targetCookieValue,
                                        long maxAge) {
        if (isEmpty(sessionId)) {
            return maxAge;
        }

        long sessionIdMaxAge = nowInSeconds + SESSION_ID_COOKIE_MAX_AGE;
        maxAge = sessionIdMaxAge;
        appendCookieValue(sessionId, targetCookieValue, sessionIdMaxAge, SESSION_ID_COOKIE_NAME);
        return maxAge;
    }

    private static void appendCookieValue(String id, StringBuilder targetCookieValue, long maxAge, String cookieName) {
        targetCookieValue.append(cookieName)
                .append(INTERNAL_COOKIE_SERIALIZATION_SEPARATOR)
                .append(id)
                .append(INTERNAL_COOKIE_SERIALIZATION_SEPARATOR)
                .append(maxAge)
                .append(COOKIE_VALUE_SEPARATOR);
    }

    public static Optional<TargetCookie> createClusterCookie(String tntId) {
        if (tntId == null) {
            return Optional.ofNullable(null);
        }
        TargetCookie targetCookie = null;
        String[] parts = tntId.split("\\.");
        if (parts.length == 2) {
            String[] nodeDetails = parts[1].split("_");
            if (nodeDetails.length == 2) {
                long maxAge = System.currentTimeMillis() / 1000 + CLUSTER_LOCATION_HINT_MAX_AGE;
                targetCookie = new TargetCookie(CLUSTER_COOKIE_NAME, nodeDetails[0], (int) (maxAge / 1000));
            }
        }
        return Optional.ofNullable(targetCookie);
    }

    public static Set<String> getTargetCookieNames() {
        if (TARGET_COOKIE_NAMES != null) {
            return TARGET_COOKIE_NAMES;
        }
        TARGET_COOKIE_NAMES = Collections.unmodifiableSet(
                new HashSet(Arrays.asList(COOKIE_NAME, CLUSTER_COOKIE_NAME,
                        VisitorProvider.getInstance().getVisitorCookieName())));
        return TARGET_COOKIE_NAMES;
    }
}
