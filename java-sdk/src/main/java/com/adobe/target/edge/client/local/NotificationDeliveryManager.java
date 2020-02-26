package com.adobe.target.edge.client.local;

import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.service.TargetService;

import java.util.HashMap;
import java.util.Map;

public class NotificationDeliveryManager {

    private static final NotificationDeliveryManager sInstance = new NotificationDeliveryManager();

    private final Map<String, NotificationDeliveryService> serviceMap = new HashMap<>();

    public static NotificationDeliveryManager getInstance() {
        return sInstance;
    }

    public NotificationDeliveryService getService(ClientConfig clientConfig, TargetService targetService) {
        String serviceKey = clientConfig.getClient();
        NotificationDeliveryService service = serviceMap.get(serviceKey);
        if (service != null) {
            return service;
        }
        synchronized (serviceMap) {
            service = serviceMap.get(serviceKey);
            if (service != null) {
                return service;
            }
            service = new NotificationDeliveryService(clientConfig, targetService);
            serviceMap.put(serviceKey, service);
            return service;
        }
    }

}
