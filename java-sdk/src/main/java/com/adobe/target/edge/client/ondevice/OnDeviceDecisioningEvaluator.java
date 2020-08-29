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
package com.adobe.target.edge.client.ondevice;

import com.adobe.target.delivery.v1.model.ExecuteRequest;
import com.adobe.target.delivery.v1.model.MboxRequest;
import com.adobe.target.delivery.v1.model.PrefetchRequest;
import com.adobe.target.delivery.v1.model.ViewRequest;
import com.adobe.target.edge.client.model.ondevice.OnDeviceDecisioningRuleSet;
import com.adobe.target.edge.client.model.ondevice.OnDeviceDecisioningEvaluation;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OnDeviceDecisioningEvaluator {

    private final RuleLoader ruleLoader;

    public OnDeviceDecisioningEvaluator(RuleLoader ruleLoader) {
        this.ruleLoader = ruleLoader;
    }

    /**
     * Use to determine if the given request can be fully executed locally or not and why.
     *
     * @param deliveryRequest request to examine
     * @return LocalExecutionResult
     */
    public OnDeviceDecisioningEvaluation evaluateLocalExecution(TargetDeliveryRequest deliveryRequest) {
        if (deliveryRequest == null) {
            return new OnDeviceDecisioningEvaluation(false,
                    "Given request cannot be null", null, null, null);
        }

        OnDeviceDecisioningRuleSet ruleSet = this.ruleLoader.getLatestRules();
        if (ruleSet == null) {
            return new OnDeviceDecisioningEvaluation(false,
                    "Local-decisioning rule set not yet available",
                    null, null, null);
        }

        List<String> remoteMboxes = computeRemoteMboxes(deliveryRequest, ruleSet);
        List<String> remoteViews = computeRemoteViews(deliveryRequest, ruleSet);

        if (!remoteMboxes.isEmpty() || !remoteViews.isEmpty()) {
            StringBuilder reason = new StringBuilder("remote activities in: ");
            boolean haveRemoteMboxes = !remoteMboxes.isEmpty();
            if (haveRemoteMboxes) {
                reason.append(String.format("mboxes %s", remoteMboxes));
            }
            if (!remoteViews.isEmpty()) {
                if (haveRemoteMboxes) {
                    reason.append(", ");
                }
                reason.append(String.format("views %s", remoteViews));
            }
            return new OnDeviceDecisioningEvaluation(false,
                    reason.toString(), ruleSet.getGlobalMbox(),
                    remoteMboxes.isEmpty() ? null : new ArrayList<>(remoteMboxes),
                    remoteViews.isEmpty() ? null : new ArrayList<>(remoteViews));
        }

        return new OnDeviceDecisioningEvaluation(true, null, ruleSet.getGlobalMbox(),
                null, null);
    }

    private List<String> computeRemoteMboxes(TargetDeliveryRequest deliveryRequest, OnDeviceDecisioningRuleSet ruleSet) {
        List<String> requestMboxNames = allMboxNames(deliveryRequest, ruleSet);
        if (requestMboxNames.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> remoteMboxes = new HashSet<>();
        Set<String> localMboxSet = new HashSet<>(ruleSet.getLocalMboxes());
        Set<String> bothMboxSet = new HashSet<>(ruleSet.getRemoteMboxes());

        for (String mboxName : requestMboxNames) {
            if (!localMboxSet.contains(mboxName) ||
                    bothMboxSet.contains(mboxName)) {
                remoteMboxes.add(mboxName);
            }
        }
        return new ArrayList<>(remoteMboxes);
    }

    private List<String> computeRemoteViews(TargetDeliveryRequest deliveryRequest, OnDeviceDecisioningRuleSet ruleSet) {
        List<String> requestViews = allViewNames(deliveryRequest);
        if (requestViews.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> bothViewSet = new HashSet<>(ruleSet.getRemoteViews());
        if (allViews(requestViews)) {
            return new ArrayList<>(bothViewSet);
        }

        Set<String> remoteViews = new HashSet<>();
        Set<String> localViewSet = new HashSet<>(ruleSet.getLocalViews());
        for (String viewName : requestViews) {
            if (!localViewSet.contains(viewName) ||
                    bothViewSet.contains(viewName)) {
                remoteViews.add(viewName);
            }
        }
        return new ArrayList<>(remoteViews);
    }

    private List<String> allMboxNames(TargetDeliveryRequest request, OnDeviceDecisioningRuleSet ruleSet) {
        if (request == null || ruleSet == null) {
            return Collections.emptyList();
        }
        List<String> mboxNames = new ArrayList<>();
        String globalMbox = ruleSet.getGlobalMbox();
        PrefetchRequest prefetch = request.getDeliveryRequest().getPrefetch();
        if (prefetch != null) {
            if (prefetch.getPageLoad() != null) {
                mboxNames.add(globalMbox);
            }
            mboxNames.addAll(prefetch.getMboxes().stream().map(MboxRequest::getName).collect(Collectors.toList()));
        }
        ExecuteRequest execute = request.getDeliveryRequest().getExecute();
        if (execute != null) {
            if (execute.getPageLoad() != null) {
                mboxNames.add(globalMbox);
            }
            mboxNames.addAll(execute.getMboxes().stream().map(MboxRequest::getName).collect(Collectors.toList()));
        }
        return mboxNames;
    }

    private List<String> allViewNames(TargetDeliveryRequest request) {
        if (request == null) {
            return Collections.emptyList();
        }
        List<String> viewNames = new ArrayList<>();
        PrefetchRequest prefetch = request.getDeliveryRequest().getPrefetch();
        if (prefetch != null) {
            List<ViewRequest> views = prefetch.getViews();
            if (views != null) {
                for (ViewRequest viewRequest : views) {
                    viewNames.add(viewRequest.getName());
                }
            }
        }
        return viewNames;
    }

    private boolean allViews(List<String> viewNames) {
        return viewNames.size() == 1 && viewNames.get(0) == null;
    }

}
