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
package com.adobe.target.sample.controller;

import com.adobe.experiencecloud.ecid.visitor.VisitorState;
import com.adobe.target.delivery.v1.model.*;
import com.adobe.target.edge.client.http.ResponseStatus;
import com.adobe.target.edge.client.model.TargetDeliveryResponse;
import com.adobe.target.sample.service.TargetClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * All the end points in this controller assumes we've at.js included on the returned webpage.
 */
@Controller
public class TargetController {

    @Autowired
    private TargetClientService targetClientService;

    /**
     * Mbox - Target Only
     * Make execute as well as a prefetch request in single request.
     * Set the response in serverState variable to be used by at.js
     */
    @GetMapping("/mboxTargetOnly")
    public String mboxTargetOnly(
            @RequestParam(name = "mbox", required = false, defaultValue = "server-side-mbox") String mbox,
            Model model, HttpServletRequest request, HttpServletResponse response) {
        MboxRequest executeMboxRequest = new MboxRequest().name(mbox).index(1);
        TargetDeliveryResponse targetDeliveryResponse =
                targetClientService.getMboxTargetDeliveryResponse(Arrays.asList(executeMboxRequest), request,
                        response);
        model.addAttribute("serverState", targetDeliveryResponse.getServerState());
        return "mboxPrefetchNotification";
    }

    /**
     * Mbox - Notification
     * Make execute as well as a prefetch request in single request.
     * The returned page has a send notification button, which makes an ajax call to /sendNotifications
     * for the prefetched part. i.e server-side-mbox-prefetch
     */
    @GetMapping("/mboxPrefetchNotification")
    public String mboxPrefetchNotification(
            Model model, HttpServletRequest request, HttpServletResponse response) {
        MboxRequest executeMboxRequest = new MboxRequest().name("server-side-mbox").index(1);
        MboxRequest prefetchMboxRequest = new MboxRequest().name("server-side-mbox-prefetch").index(1);
        TargetDeliveryResponse targetDeliveryResponse =
                targetClientService.getMboxTargetDeliveryResponse(Arrays.asList(executeMboxRequest),
                        Arrays.asList(prefetchMboxRequest), request, response);
        model.addAttribute("serverState", targetDeliveryResponse.getServerState());
        return "mboxPrefetchNotification";
    }

    /**
     * Make a batch request. 2 mbox execute requests and one prefetch request.
     * It is recommended to make multiple requests in single call for a user. You can save the
     * prefetched response and notify target once prefetch response is used (converted)
     */
    @GetMapping("/batchMboxTargetOnly")
    public String batchMboxTargetOnly(Model model, HttpServletRequest request, HttpServletResponse response) {
        MboxRequest executeMboxRequest1 = new MboxRequest().name("server-side-mbox").index(1);
        MboxRequest executeMboxRequest2 = new MboxRequest().name("server-side-mbox").index(2);
        MboxRequest prefetchMboxRequest = new MboxRequest().name("server-side-mbox-prefetch").index(1);
        TargetDeliveryResponse targetDeliveryResponse = targetClientService
                .getMboxTargetDeliveryResponse(Arrays.asList(executeMboxRequest1, executeMboxRequest2),
                        Arrays.asList(prefetchMboxRequest), request, response);
        model.addAttribute("serverState", targetDeliveryResponse.getServerState());
        return "mboxTargetOnly";
    }

    /**
     * Make a request for views present on the given URL. This example also demonstrates the power of
     * asynchronous requests. You can check the total page execution time at bottom of the page returned.
     * Next, you can pass the flag async=true in query params to check how much time
     * the total page load in asynchronous mode. It will be max(simulateDelay, targetTime).
     * You can make asynchronous calls for all types of calls mbox, views, execute and prefetch.
     */
    @GetMapping("/viewsTargetOnly")
    public String viewsTargetOnly(@RequestParam(name = "async", required = false, defaultValue = "false") boolean async,
                                  Model model, HttpServletRequest request, HttpServletResponse response) {
        long start = System.currentTimeMillis();
        TargetDeliveryResponse targetDeliveryResponse = async ? getTargetDeliveryResponseAsync(request, response) :
                getTargetDeliveryResponseSync(request, response);
        model.addAttribute("serverState", targetDeliveryResponse.getServerState());
        model.addAttribute("executionTime", System.currentTimeMillis() - start);
        return "viewsTargetOnly";
    }

    /**
     * Sends notification for the provided prefetch response. This method is called when
     * send notification button is clicked on /mboxPrefetchNotification page.
     * This method will send notifications for all tokens and for all pre-fetched content
     */
    @PostMapping(value = "/sendNotifications")
    public @ResponseBody
    ResponseStatus sendNotifications(@RequestBody PrefetchResponse prefetchResponse,
                                     HttpServletRequest request) {
        List<Notification> notifications = new ArrayList<>();
        List<PrefetchMboxResponse> mboxes = prefetchResponse.getMboxes();
        List<View> views = prefetchResponse.getViews();
        //Send notifications for all the mboxes and views.
        getAllMboxNotifications(notifications, mboxes);
        getAllViewNotifications(notifications, views);

        ResponseStatus status = targetClientService.sendNotifications(request, notifications);
        return status;
    }

    /**
     * This page shows the integration between ECID (aka MCID) and Target.
     * This means client who sent this request has visitor.js integration. visitor.js will set a
     * cookie AMCV_* which will be used by Target for integrating with visitor.js.
     * Target response will return a visitorState which needs to be updated in browser so that Target
     * and visitor.js are in sync. You can see the sample in targetMcid.html template.
     * Visitor.getInstance("[[${organizationId}]]", {serverState: [[${visitorState}]]});
     */
    @GetMapping("/targetMcid")
    public String targetMcid(Model model, HttpServletRequest request, HttpServletResponse response) {
        targetVisitorAnalytics(model, request, response);
        return "targetMcid";
    }

    /**
     * This page assumes you've AppMeasurement.js on client side. You don't have to do any special handling
     * in case of analytics because analytics uses visitor.js state which we have already updated
     * in previous state. You might have to add trackingServer for analytics to have proper stiching.
     */
    @GetMapping("/targetAnalytics")
    public String targetAnalytics(Model model, HttpServletRequest request, HttpServletResponse response) {
        targetVisitorAnalytics(model, request, response);
        return "targetAnalytics";
    }

    private void targetVisitorAnalytics(Model model, HttpServletRequest request, HttpServletResponse response) {
        long start = System.currentTimeMillis();
        TargetDeliveryResponse targetDeliveryResponse = targetClientService.prefetchViewsTargetDeliveryResponse(request,
                response);
        model.addAttribute("serverState", targetDeliveryResponse.getServerState());
        Map<String, VisitorState> visitorState = targetDeliveryResponse.getVisitorState();
        if (visitorState != null) {
            model.addAttribute("visitorState", visitorState);
        }
        model.addAttribute("organizationId", "0DD934B85278256B0A490D44@AdobeOrg");
        model.addAttribute("executionTime", System.currentTimeMillis() - start);
    }

    private TargetDeliveryResponse getTargetDeliveryResponseSync(HttpServletRequest request,
                                                                 HttpServletResponse response) {
        TargetDeliveryResponse serverState = targetClientService.prefetchViewsTargetDeliveryResponse(request,
                response);
        simulateDelay();
        return serverState;
    }

    private TargetDeliveryResponse getTargetDeliveryResponseAsync(HttpServletRequest request,
                                                                  HttpServletResponse response) {
        CompletableFuture<TargetDeliveryResponse> serverStateFuture = targetClientService
                .prefetchViewsTargetDeliveryResponseAsync(request, response);
        simulateDelay();
        return serverStateFuture.join();
    }

    private void simulateDelay() {
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getAllViewNotifications(List<Notification> notifications, List<View> views) {

        for (View view : views) {
            List<String> tokens = view.getOptions()
                    .stream()
                    .map(Option::getEventToken)
                    .collect(Collectors.toList());
            Notification notification = new Notification()
                    .id(UUID.randomUUID().toString())
                    .impressionId(UUID.randomUUID().toString())
                    .view(new NotificationView().key(view.getKey()).name(view.getName()).state(view.getState()))
                    .type(MetricType.DISPLAY)
                    .timestamp(System.currentTimeMillis())
                    .tokens(tokens);
            notifications.add(notification);
        }
    }

    private void getAllMboxNotifications(List<Notification> notifications, List<PrefetchMboxResponse> mboxes) {
        for (PrefetchMboxResponse mbox : mboxes) {
            List<String> tokens = mbox.getOptions()
                    .stream()
                    .map(Option::getEventToken)
                    .collect(Collectors.toList());
            Notification notification = new Notification()
                    .id(UUID.randomUUID().toString())
                    .impressionId(UUID.randomUUID().toString())
                    .mbox(new NotificationMbox().name(mbox.getName()).state(mbox.getState()))
                    .type(MetricType.DISPLAY)
                    .timestamp(System.currentTimeMillis())
                    .tokens(tokens);
            notifications.add(notification);
        }
    }

}
