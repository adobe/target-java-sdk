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
package com.adobe.target.edge.client.http;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.function.Consumer;
import kong.unirest.HttpRequestSummary;
import kong.unirest.HttpResponseSummary;
import kong.unirest.MetricContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TargetMetricsTest {

  private Consumer<TargetMetricContext> consumer;
  private HttpRequestSummary request;
  private HttpResponseSummary response;

  @BeforeEach
  void init() {
    consumer = mock(Consumer.class);
    request = mock(HttpRequestSummary.class);
    response = mock(HttpResponseSummary.class);
  }

  @Test
  public void testMetricsAreAcceptedWhenNoException() {
    TargetMetrics targetMetrics = new TargetMetrics(consumer);
    MetricContext metricContext = targetMetrics.begin(request);

    metricContext.complete(response, null);

    verify(consumer, times(1)).accept(any(TargetMetricContext.class));
  }

  @Test
  public void testMetricsAreNotAcceptedWhenException() {
    TargetMetrics targetMetrics = new TargetMetrics(consumer);
    MetricContext metricContext = targetMetrics.begin(request);

    metricContext.complete(null, new Exception());

    verify(consumer, times(0)).accept(any(TargetMetricContext.class));
  }
}
