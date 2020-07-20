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
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.local.OnDeviceDecisioningService;
import com.adobe.target.edge.client.local.OnDeviceDecisioningEvaluator;
import com.adobe.target.edge.client.local.collator.ParamsCollator;
import com.adobe.target.edge.client.local.RuleLoader;
import com.adobe.target.edge.client.model.ondevice.OnDeviceDecisioningRuleSet;
import com.adobe.target.edge.client.model.TargetCookie;
import com.adobe.target.edge.client.utils.CookieUtils;
import com.adobe.target.delivery.v1.model.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.*;
import org.apache.http.HttpStatus;
import org.mockito.internal.util.reflection.FieldSetter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.adobe.target.edge.client.entities.TargetDeliveryRequestTest.*;
import static com.adobe.target.edge.client.utils.TargetConstants.COOKIE_NAME;

public class TargetTestDeliveryRequestUtils {

    public static final String TEST_RESOURCES_DIR = "src/test/resources";
    static final int SESSION_ID_COOKIE_MAX_AGE = 1860;
    static final int DEVICE_ID_COOKIE_MAX_AGE = 63244800;

    static PrefetchRequest getPrefetchViewsRequest() {
        PrefetchRequest prefetchRequest = new PrefetchRequest();
        ViewRequest requestDetails = new ViewRequest();
        prefetchRequest.setViews(Arrays.asList(requestDetails));
        return prefetchRequest;
    }

    static ExecuteRequest getMboxExecuteRequest() {
        List<MboxRequest> mboxRequests = new ArrayList() {{
            add(new MboxRequest().name("server-side-mbox").index(1));
            add(new MboxRequest().name("server-side-mbox").index(2));
            add(new MboxRequest().name("server-side-mbox-prefetch").index(1));
        }};
        ExecuteRequest executeRequest = new ExecuteRequest();
        executeRequest.setMboxes(mboxRequests);
        return executeRequest;
    }

    static Map<String, CustomerState> getCustomerIds() {
        Map<String, CustomerState> customerIds = new HashMap<>();
        customerIds.put("userid", CustomerState.authenticated("67312378756723456"));
        customerIds.put("puuid", CustomerState.unknown("550e8400-e29b-41d4-a716-446655440000"));
        return customerIds;
    }

    static Context getContext() {
        Context context = new Context();
        context.setChannel(ChannelType.WEB);
        context.setTimeOffsetInMinutes(330.0);
        context.setAddress(getAddress());
        return context;
    }

    static Address getAddress() {
        Address address = new Address();
        address.setUrl("http://localhost:8080");
        return address;
    }

    static List<TargetCookie> getTestCookies() {
        int timeNow = (int) (System.currentTimeMillis() / 1000);
        Optional<TargetCookie> targetCookie = CookieUtils.createTargetCookie(TEST_SESSION_ID, TEST_TNT_ID);
        String visitorValue = "-1330315163%7CMCIDTS%7C18145%7CMCMID%7C" + TargetDeliveryRequestTest.TEST_MCID +
                "%7CMCAAMLH-1567731426%7C9%7CMCAAMB-1568280923%7CRKhpRz8krg2tLO6pguXWp5olkAcUniQYPHaMWWgdJ3xz" +
                "PWQmdj0y%7CMCOPTOUT-1567683323s%7CNONE%7CMCAID%7CNONE%7CMCCIDH%7C1806392961";
        TargetCookie visitorCookie = null;
        try {
            visitorCookie = new TargetCookie("AMCV_" + URLEncoder.encode(TEST_ORG_ID, "UTF-8"),
                    visitorValue, timeNow + SESSION_ID_COOKIE_MAX_AGE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        List<TargetCookie> cookies = new ArrayList<>();
        Collections.addAll(cookies, targetCookie.get(), visitorCookie);
        return cookies;
    }

    static List<TargetCookie> getExpiredSessionCookie() {
        int timeNow = (int) (System.currentTimeMillis() / 1000);
        int sessionExpirationTime = timeNow - 1;
        int tntIdExpirationTime = timeNow + DEVICE_ID_COOKIE_MAX_AGE;
        String cookieValue = "session#430a140336d545daacde53af9636eef5#" +
                sessionExpirationTime + "|PC#" + TEST_TNT_ID + "#" + tntIdExpirationTime;
        TargetCookie targetCookie = new TargetCookie(COOKIE_NAME, cookieValue, timeNow);
        List<TargetCookie> cookies = new ArrayList<>();
        cookies.add(targetCookie);
        return cookies;
    }

    static List<Notification> getMboxNotifications() {
        List<Notification> notifications = new ArrayList<>();
        List<PrefetchMboxResponse> dummyMboxPrefetchResponse = getDummyMboxPrefetchResponse();
        for (PrefetchMboxResponse mbox : dummyMboxPrefetchResponse) {
            NotificationMbox notificationMbox = new NotificationMbox()
                    .name(mbox.getName())
                    .state(mbox.getState());
            Notification notification = new Notification()
                    .id(UUID.randomUUID().toString())
                    .impressionId(UUID.randomUUID().toString())
                    .mbox(notificationMbox);
            setNotificationOptions(notifications, notification, mbox.getOptions());
        }
        return notifications;
    }

    static void setNotificationOptions(List<Notification> notifications, Notification notification,
                                       List<Option> options) {
        notification.type(MetricType.DISPLAY).timestamp(System.currentTimeMillis());
        List<String> tokens = new ArrayList<>();
        for (Option option : options) {
            tokens.add(option.getEventToken());
        }
        notification.tokens(tokens);
        notifications.add(notification);
    }

    static List<PrefetchMboxResponse> getDummyMboxPrefetchResponse() {
        Option option = new Option().type(OptionType.HTML)
                .content("<b>Test option content</b>")
                .eventToken("DQ5I8XE7vs6wVIBc5m8");
        PrefetchMboxResponse prefetchMboxResponse = new PrefetchMboxResponse();
        prefetchMboxResponse.name("server-side-mbox");
        prefetchMboxResponse.state("R3GqCTBoZfbf6JuhJihve+");
        prefetchMboxResponse.options(Arrays.asList(option));
        return Arrays.asList(prefetchMboxResponse);
    }

    static HttpResponse<DeliveryResponse> getTestDeliveryResponse() {
        DeliveryResponse deliveryResponse = new DeliveryResponse() {

            @Override
            public Integer getStatus() {
                return HttpStatus.SC_OK;
            }

            @Override
            public VisitorId getId() {
                return new VisitorId().tntId(TEST_TNT_ID);
            }

        };
        RawResponse rawResponse = new RawResponse() {
            @Override
            public int getStatus() {
                return HttpStatus.SC_OK;
            }

            @Override
            public String getStatusText() {
                return null;
            }

            @Override
            public Headers getHeaders() {
                return new Headers();
            }

            @Override
            public InputStream getContent() {
                return null;
            }

            @Override
            public byte[] getContentAsBytes() {
                return new byte[0];
            }

            @Override
            public String getContentAsString() {
                return null;
            }

            @Override
            public String getContentAsString(String charset) {
                return null;
            }

            @Override
            public InputStreamReader getContentReader() {
                return null;
            }

            @Override
            public boolean hasContent() {
                return false;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public String getEncoding() {
                return null;
            }

            @Override
            public Config getConfig() {
                return null;
            }

            @Override
            public HttpResponseSummary toSummary() {
                return null;
            }
        };
        HttpResponse<DeliveryResponse> basicResponse = new BasicResponse(rawResponse, deliveryResponse);
        return basicResponse;
    }

    public static RuleLoader getTestRuleLoaderFromFile(final String fileName) throws IOException {
        File inputFile = new File(TEST_RESOURCES_DIR, fileName);
        InputStream is = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] fileBytes = new byte[4096];
        int read;
        try {
            is = new BufferedInputStream(new FileInputStream(inputFile));
            while ((read = is.read(fileBytes)) >= 0) {
                os.write(fileBytes, 0, read);
            }
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
        return getTestRuleLoader(os.toString(StandardCharsets.UTF_8.name()));
    }

    public static RuleLoader getTestRuleLoader(final String ruleSet) {
        return new RuleLoader() {
            @Override
            public void start(ClientConfig clientConfig) {
            }

            @Override
            public void stop() {
            }

            @Override
            public void refresh() {
            }

            @Override
            public OnDeviceDecisioningRuleSet getLatestRules() {
                if (ruleSet == null) {
                    return null;
                }
                ObjectMapper mapper = new ObjectMapper();
                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
                try {
                    return mapper.readValue(ruleSet, new TypeReference<OnDeviceDecisioningRuleSet>() {});
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public long getPollingInterval() {
                return 0;
            }

            @Override
            public int getNumFetches() {
                return 0;
            }

            @Override
            public Date getLastFetch() {
                return null;
            }

            @Override
            public String getLocation() {
                return null;
            }
        };
    }

    static PrefetchRequest getMboxPrefetchLocalRequest(String mbox) {
        List<MboxRequest> mboxRequests = new ArrayList() {{
            add(new MboxRequest().name(mbox).index(1).parameters(getLocalParameters()));
        }};
        PrefetchRequest prefetchRequest = new PrefetchRequest();
        prefetchRequest.setMboxes(mboxRequests);
        return prefetchRequest;
    }

    static ExecuteRequest getMboxExecuteLocalRequest(String mbox) {
        List<MboxRequest> mboxRequests = new ArrayList() {{
            add(new MboxRequest().name(mbox).index(1).parameters(getLocalParameters()));
        }};
        ExecuteRequest executeRequest = new ExecuteRequest();
        executeRequest.setMboxes(mboxRequests);
        return executeRequest;
    }

    static Context getLocalContext() {
        Context context = new Context();
        context.setChannel(ChannelType.WEB);
        context.setAddress(getLocalAddress());
        context.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:73.0) Gecko/20100101 Firefox/73.0");
        return context;
    }

    static Address getLocalAddress() {
        Address address = new Address();
        address.setUrl("http://localhost:8080/foo.jpg?foo=1#ref1");
        return address;
    }

    static Map<String, String> getLocalParameters() {
        return new HashMap<String, String>() {{
            put("foo", "bar");
            put("baz", "buzbuz");
        }};
    }

    static ParamsCollator getSpecificTimeCollator(final long now) {
        return (deliveryRequest, requestDetails) -> {
            Map<String, Object> time = new HashMap<>();
            time.put("current_timestamp", now);
            Date nowDate = new Date(now);
            SimpleDateFormat dayFormat = new SimpleDateFormat("u");
            dayFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            time.put("current_day", dayFormat.format(nowDate));
            SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm");
            timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            time.put("current_time", timeFormat.format(nowDate));
            return time;
        };
    }

    public static void fileRuleLoader(String fileName, OnDeviceDecisioningService localService) throws IOException, NoSuchFieldException {
        RuleLoader testRuleLoader = TargetTestDeliveryRequestUtils.getTestRuleLoaderFromFile(fileName);
        OnDeviceDecisioningEvaluator evaluator = new OnDeviceDecisioningEvaluator(testRuleLoader);
        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("ruleLoader"), testRuleLoader);
        FieldSetter.setField(localService, localService.getClass()
                .getDeclaredField("onDeviceDecisioningEvaluator"), evaluator);
    }
}
