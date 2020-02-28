package com.adobe.target.edge.client.local;

import com.adobe.target.delivery.v1.model.RequestDetails;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;

import java.text.SimpleDateFormat;
import java.util.*;

public class TimeParamsCollator implements ParamsCollator {

    public Map<String, Object> collateParams(TargetDeliveryRequest deliveryRequest,
                                             RequestDetails requestDetails, Map<String, Object> meta) {
        Map<String, Object> time = new HashMap<>();
        long now = System.currentTimeMillis();
        Date nowDate = new Date(now);
        time.put("current_timestamp", now);
        SimpleDateFormat dayFormat = new SimpleDateFormat("u");
        dayFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        time.put("current_day", dayFormat.format(nowDate));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        time.put("current_time", timeFormat.format(nowDate));
        return time;
    }

}
