package com.winning.esb.controller.monitor.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @Author Lemod
 * @Version 2017/10/13
 */
public class SpecialConvertUtil {
    private static final Gson GSON = new Gson();

    public static JsonObject convertObjectToTree(Object o) {
        try {
            JsonElement element = GSON.toJsonTree(o);

            return element.getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T formatJsonString(String jsonString, T t) {
        return (T) GSON.fromJson(jsonString, t.getClass());
    }

}
