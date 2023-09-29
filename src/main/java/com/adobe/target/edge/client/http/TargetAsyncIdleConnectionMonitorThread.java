/*
 * Copyright 2023 Adobe. All rights reserved.
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

import java.util.concurrent.TimeUnit;

import com.adobe.target.edge.client.ClientConfig;
import kong.unirest.apache.AsyncIdleConnectionMonitorThread;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;

public class TargetAsyncIdleConnectionMonitorThread extends AsyncIdleConnectionMonitorThread {

  public static final long CONNECTION_CHECK_INTERVAL_MS = 1000;
  private final PoolingNHttpClientConnectionManager connectionManager;
  private final long evictIdleConnectionsAfterSecs;

  public TargetAsyncIdleConnectionMonitorThread(PoolingNHttpClientConnectionManager connMgr, ClientConfig clientConfig) {
    super(connMgr);
    super.setDaemon(true);
    this.connectionManager = connMgr;
    this.evictIdleConnectionsAfterSecs = clientConfig.getEvictIdleConnectionsAfterSecs();
  }

  @Override
  public void run() {
    try {
      while (!Thread.currentThread().isInterrupted()) {
        synchronized (this) {
          wait(CONNECTION_CHECK_INTERVAL_MS);
          connectionManager.closeExpiredConnections();
          connectionManager.closeIdleConnections(evictIdleConnectionsAfterSecs, TimeUnit.SECONDS);
        }
      }
    } catch (InterruptedException ex) {
      // terminate
    }
  }
}
