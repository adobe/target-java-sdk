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

import java.util.Arrays;

public class ResponseStatus {
    private final int status;
    private final String message;
    private String[] remoteMboxes;

    public ResponseStatus(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String[] getRemoteMboxes() { return remoteMboxes; }

    public void setRemoteMboxes(String[] mboxes) { remoteMboxes = mboxes; }

    @Override
    public String toString() {
        return "ResponseStatus{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", remoteMboxes=" + Arrays.toString(remoteMboxes) +
                '}';
    }
}
