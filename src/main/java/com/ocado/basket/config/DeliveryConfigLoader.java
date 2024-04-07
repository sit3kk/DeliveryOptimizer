package com.ocado.basket.config;

import com.ocado.basket.utils.JsonUtils;

import java.util.List;
import java.util.Map;

public class DeliveryConfigLoader {

    private Map<String, List<String>> deliveryOptions;

    public DeliveryConfigLoader(String absolutePathToConfigFile) {
        this.deliveryOptions = JsonUtils.readJsonFromFile(absolutePathToConfigFile, Map.class);
    }

    public Map<String, List<String>> getDeliveryOptions() {
        return deliveryOptions;
    }
}
