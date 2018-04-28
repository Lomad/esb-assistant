package com.winning.esb.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 2017/8/15.
 */
public class MapUtils {
    /**
     * 判断Map是否为空
     */
    public static boolean isEmpty(Map map) {
        return (map == null || map.size() < 1) ? true : false;
    }

    /**
     * 根据key判断value是否为空
     */
    public static boolean isEmptyValue(Map map, String key) {
        if(isEmpty(map)) {
            return true;
        } else {
            Object value = map.get(key);
            if (value == null || "".equals(value.toString().trim())) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 根据key获取value
     */
    public static String getValue(Map map, String key) {
        return getValue(map, key, "");
    }

    /**
     * 根据key获取value
     */
    public static String getValue(Map map, String key, String defaultValue) {
        if(!existKey(map,key)) {
            return defaultValue;
        }
        Object value = map.get(key);
        if(value == null || "".equals(value.toString().trim())) {
            return StringUtils.isEmpty(defaultValue) ? "" : defaultValue;
        } else {
            return String.valueOf(value);
        }
    }

    /**
     * 判断key是否存在
     */
    public static boolean existKey(Map map, String key) {
        if(isEmpty(map)) {
            return false;
        } else {
            return map.containsKey(key);
        }
    }

    /**
     * 删除value为空的项
     */
    public static <T> void deleteEmptyNull(Map<T, Object> map) {
        if(!isEmpty(map)) {
            //查询空值项，并记录key
            List<T> toDeleteKeys = new ArrayList<>();
            for(Map.Entry<T, Object> item : map.entrySet()) {
                if(item.getValue()==null || StringUtils.isEmpty(item.getValue())) {
                    toDeleteKeys.add(item.getKey());
                }
            }

            //删除空值的项
            for(T key : toDeleteKeys) {
                map.remove(key);
            }
        }
    }
}