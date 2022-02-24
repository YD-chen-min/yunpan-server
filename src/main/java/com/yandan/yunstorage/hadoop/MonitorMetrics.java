package com.yandan.yunstorage.hadoop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Create by yandan
 * 2022/2/24  10:59
 */
public class MonitorMetrics {
    // beans为通过jmx所返回的json串中最起始的key
    // 结构为{"beans":[{"":"","":"",...}]}
    List<Map<String, Object>> beans = new ArrayList<>();

    public List<Map<String, Object>> getBeans() {
        return beans;
    }

    public void setBeans(List<Map<String, Object>> beans) {
        this.beans = beans;
    }

    public Object getMetricsValue(String name) {
        if (beans.isEmpty()) {
            return null;
        }
        return beans.get(0).getOrDefault(name, null);
    }

}
