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

import com.adobe.target.delivery.v1.model.ExecuteRequest;
import com.adobe.target.delivery.v1.model.Trace;
import com.adobe.target.edge.client.TargetClient;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.model.TargetDeliveryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.concurrent.CompletableFuture;

import static com.adobe.target.sample.util.TargetRequestUtils.*;

@RestController
@RequestMapping("/rest")
public class TargetRestController {

    @Autowired
    private TargetClient targetJavaClient;

    @GetMapping("/mboxTargetOnly")
    public TargetDeliveryResponse mboxTargetOnly(
            @RequestParam(name = "mbox", defaultValue = "server-side-mbox") String mbox,
            HttpServletRequest request, HttpServletResponse response) {
        ExecuteRequest executeRequest = new ExecuteRequest()
                .mboxes(getMboxRequests(mbox));

        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                .context(getContext(request))
                .execute(executeRequest)
                .cookies(getTargetCookies(request.getCookies()))
                .build();
        TargetDeliveryResponse targetResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        setCookies(targetResponse.getCookies(), response);
        return targetResponse;
    }

    @GetMapping("/mboxTargetOnlyAsync")
    public TargetDeliveryResponse mboxTargetOnlyAsync(
            @RequestParam(name = "mbox", defaultValue = "server-side-mbox") String mbox,
            HttpServletRequest request, HttpServletResponse response) {
        ExecuteRequest executeRequest = new ExecuteRequest()
                .mboxes(getMboxRequests(mbox));

        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                .context(getContext(request))
                .execute(executeRequest)
                .cookies(getTargetCookies(request.getCookies()))
                .build();
        CompletableFuture<TargetDeliveryResponse> targetResponseAsync =
                targetJavaClient.getOffersAsync(targetDeliveryRequest);
        targetResponseAsync.thenAccept(tr -> setCookies(tr.getCookies(), response));
        simulateIO();
        TargetDeliveryResponse targetResponse = targetResponseAsync.join();
        return targetResponse;
    }

    /**
     * Function for simulating network calls like other microservices and databases
     */
    private void simulateIO() {
        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/targetTraces")
    public TargetDeliveryResponse targetTraces(
            @RequestParam(name = "mbox", defaultValue = "server-side-mbox") String mbox,
            HttpServletRequest request, HttpServletResponse response) {
        ExecuteRequest executeRequest = new ExecuteRequest()
                .mboxes(getMboxRequests(mbox));

        TargetDeliveryRequest targetDeliveryRequest = TargetDeliveryRequest.builder()
                .context(getContext(request))
                .execute(executeRequest)
                .cookies(getTargetCookies(request.getCookies()))
                .trace(new Trace().authorizationToken("dummyAuthToken"))
                .build();

        TargetDeliveryResponse targetResponse = targetJavaClient.getOffers(targetDeliveryRequest);
        setCookies(targetResponse.getCookies(), response);
        return targetResponse;
    }


}
