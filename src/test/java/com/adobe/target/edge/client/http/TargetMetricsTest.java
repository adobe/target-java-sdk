package com.adobe.target.edge.client.http;

import kong.unirest.HttpRequestSummary;
import kong.unirest.HttpResponseSummary;
import kong.unirest.MetricContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.function.Consumer;

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
