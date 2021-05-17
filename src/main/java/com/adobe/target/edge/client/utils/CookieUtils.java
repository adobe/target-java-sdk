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
package com.adobe.target.edge.client.utils;

import static com.adobe.target.edge.client.utils.StringUtils.isEmpty;
import static com.adobe.target.edge.client.utils.StringUtils.isNotEmpty;
import static com.adobe.target.edge.client.utils.TargetConstants.CLUSTER_COOKIE_NAME;
import static com.adobe.target.edge.client.utils.TargetConstants.COOKIE_NAME;

import com.adobe.target.edge.client.model.TargetCookie;
import com.adobe.target.edge.client.service.VisitorProvider;
import java.util.*;
import java.util.regex.Pattern;

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

    int nowInSeconds = getNowInSeconds();
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
    final String[] cookieTokens =
        cookie.split(Pattern.quote(INTERNAL_COOKIE_SERIALIZATION_SEPARATOR));

    if (cookieTokens != null && cookieTokens.length == 3) {
      final int expires = Integer.parseInt(cookieTokens[2]);
      return new TargetCookie(cookieTokens[0], cookieTokens[1], expires);
    }

    return null;
  }

  public static Optional<TargetCookie> createTargetCookie(String sessionId, String deviceId) {
    final int nowInSeconds = getNowInSeconds();
    final StringBuilder targetCookieValue = new StringBuilder();
    int expires = 0;
    expires = createSessionId(sessionId, nowInSeconds, targetCookieValue, expires);
    expires = createDeviceId(deviceId, nowInSeconds, targetCookieValue, expires);
    TargetCookie targetCookie = null;
    String cookieValue = targetCookieValue.toString();
    int maxAge = expires == 0 ? 0 : expires - nowInSeconds;

    if (isNotEmpty(cookieValue)) {
      targetCookie = new TargetCookie(COOKIE_NAME, cookieValue, maxAge);
    }

    return Optional.ofNullable(targetCookie);
  }

  private static int getNowInSeconds() {
    return (int) (System.currentTimeMillis() / 1000);
  }

  private static int createDeviceId(
      String deviceId, int nowInSeconds, StringBuilder targetCookieValue, int expires) {
    if (isEmpty(deviceId)) {
      return expires;
    }

    int deviceIdExpires = nowInSeconds + DEVICE_ID_COOKIE_MAX_AGE;
    expires = Math.max(expires, deviceIdExpires);
    appendCookieValue(deviceId, targetCookieValue, deviceIdExpires, DEVICE_ID_COOKIE_NAME);

    return expires;
  }

  private static int createSessionId(
      String sessionId, int nowInSeconds, StringBuilder targetCookieValue, int expires) {
    if (isEmpty(sessionId)) {
      return expires;
    }

    int sessionIdExpires = nowInSeconds + SESSION_ID_COOKIE_MAX_AGE;
    expires = sessionIdExpires;
    appendCookieValue(sessionId, targetCookieValue, sessionIdExpires, SESSION_ID_COOKIE_NAME);
    return expires;
  }

  private static void appendCookieValue(
      String id, StringBuilder targetCookieValue, int expires, String cookieName) {
    targetCookieValue
        .append(cookieName)
        .append(INTERNAL_COOKIE_SERIALIZATION_SEPARATOR)
        .append(id)
        .append(INTERNAL_COOKIE_SERIALIZATION_SEPARATOR)
        .append(expires)
        .append(COOKIE_VALUE_SEPARATOR);
  }

  public static Optional<TargetCookie> createClusterCookie(String tntId) {
    if (tntId == null) {
      return Optional.empty();
    }
    TargetCookie targetCookie = null;
    String locationHint = locationHintFromTntId(tntId);
    if (locationHint != null) {
      targetCookie = new TargetCookie(CLUSTER_COOKIE_NAME, locationHint, CLUSTER_LOCATION_HINT_MAX_AGE);
    }
    return Optional.ofNullable(targetCookie);
  }

  public static String locationHintFromTntId(String tntId) {
    String[] parts = tntId.split("\\.");
    if (parts.length == 2) {
      String[] nodeDetails = parts[1].split("_");
      if (nodeDetails.length == 2) {
        return nodeDetails[0];
      }
    }
    return null;
  }

  public static String locationHintToNodeDetails(String locationHint) {
    return String.format("%s_0", locationHint);
  }

  public static Set<String> getTargetCookieNames() {
    if (TARGET_COOKIE_NAMES != null) {
      return TARGET_COOKIE_NAMES;
    }
    TARGET_COOKIE_NAMES =
        Collections.unmodifiableSet(
            new HashSet(
                Arrays.asList(
                    COOKIE_NAME,
                    CLUSTER_COOKIE_NAME,
                    VisitorProvider.getInstance().getVisitorCookieName())));
    return TARGET_COOKIE_NAMES;
  }
}
