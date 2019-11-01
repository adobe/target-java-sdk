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
package com.adobe.target.edge.client.http;

import kong.unirest.HttpRequestSummary;
import kong.unirest.MetricContext;
import kong.unirest.UniMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class TargetMetrics implements UniMetric {

    private final Consumer<TargetMetricContext> metricContextConsumer;

    public TargetMetrics(Consumer<TargetMetricContext> metricContextConsumer) {
        this.metricContextConsumer = metricContextConsumer;
    }

    @Override
    public MetricContext begin(HttpRequestSummary request) {
        final long millis = System.currentTimeMillis();
        return (responseSummary, exception) -> {

            DefaultTargetMetricContext metricContext = new DefaultTargetMetricContext(request.getUrl(),
                    responseSummary.getStatus(),
                    responseSummary.getStatusText(),
                    (int) (System.currentTimeMillis() - millis));
            this.metricContextConsumer.accept(metricContext);

        };
    }

}
