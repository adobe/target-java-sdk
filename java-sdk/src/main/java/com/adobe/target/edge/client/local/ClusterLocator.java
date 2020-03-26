package com.adobe.target.edge.client.local;

import com.adobe.target.delivery.v1.model.ChannelType;
import com.adobe.target.delivery.v1.model.Context;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.model.TargetDeliveryResponse;
import com.adobe.target.edge.client.service.TargetService;
import com.adobe.target.edge.client.utils.CookieUtils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

import static org.apache.http.HttpStatus.SC_OK;

public class ClusterLocator {

    private static final int MAX_RETRIES = 10;

    private boolean running;
    private int retries;
    private String locationHint;
    private CompletableFuture<TargetDeliveryResponse> future = null;
    private Timer timer = null;

    public void start(final ClientConfig clientConfig, final TargetService targetService) {
        if (clientConfig.getLocalEnvironment() == null) {
            return;
        }
        if (this.running) {
            return;
        }
        this.running = true;
        this.retries = 0;
        this.timer = new Timer(this.getClass().getCanonicalName());
        executeRequest(targetService);
    }

    public void stop() {
        if (!this.running) {
            return;
        }
        this.running = false;
        this.timer.cancel();
        if (this.future != null) {
            this.future.cancel(true);
        }
    }

    public String getLocationHint() {
        return this.locationHint;
    }

    private void executeRequest(final TargetService targetService) {
        TargetDeliveryRequest request = TargetDeliveryRequest.builder()
                .context(new Context().channel(ChannelType.WEB))
                .build();
        future = targetService.executeRequestAsync(request);
        future.thenAcceptAsync(response -> {
            if (!this.running) { return; }
            if (response != null &&
                    response.getStatus() == SC_OK &&
                    response.getResponse() != null &&
                    response.getResponse().getId() != null &&
                    response.getResponse().getId().getTntId() != null) {
                String tntId = response.getResponse().getId().getTntId();
                this.locationHint = CookieUtils.locationHintFromTntId(tntId);
            }
            else if (retries++ < MAX_RETRIES) {
                this.timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        executeRequest(targetService);
                    }
                }, retries * 10000);
            }
        });
    }

}
