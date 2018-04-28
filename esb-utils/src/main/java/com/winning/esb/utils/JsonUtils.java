package com.winning.esb.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.dom4j.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 2017/8/15.
 */
public class JsonUtils {
    /**
     * json缩进
     */
    private static final String indent = "    ";

    public static <T> String toJson(T t) {
        return JSON.toJSONString(t, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty);
    }

    public static Map jsonToMap(String json) {
        return (json != null) ? JSON.parseObject(json, Map.class) : null;
    }

    public static LinkedHashMap jsonToLinkedMap(String json) {
        return JSON.parseObject(json, LinkedHashMap.class, Feature.OrderedField);
    }

    public static <T> List<T> jsonToList(Object json, Class<T> clazz) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        return jsonToList(String.valueOf(json), clazz);
    }

    public static <T> List<T> jsonToList(String json, Class<T> clazz) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        return JSONArray.parseArray(json, clazz);
    }

    public static <T> T jsonToObject(String json, Class<T> clazz) {
        return JSONObject.parseObject(json, clazz);
    }

    public static String mapToJson(Map map) {
        return mapToJson(map, false);
    }

    /**
     * 将Map转为Json字符串
     *
     * @param isFormat true - 格式化形式，false - 紧凑形式
     */
    public static String mapToJson(Map map, boolean isFormat) {
        String json = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty);
        if (isFormat) {
            json = format(json);
        }
        return json;
    }

    /**
     * 得到格式化json数据
     */
    public static String format(String jsonStr) {
        String result;
//        if(jsonStr.substring(0, 1) == "{") {
//            //普通json
//            JSONObject jsonObject = JSON.parseObject(jsonStr);
//            result = JSON.toJSONString(jsonObject, SerializerFeature.PrettyFormat);
//        } else {
//            //json数组
//            JSONArray jsonArray = JSON.parseArray(jsonStr);
//            result = JSON.toJSONString(jsonArray, SerializerFeature.PrettyFormat);
//        }

        Object obj = JSON.parse(jsonStr);
        result = JSON.toJSONString(obj, SerializerFeature.PrettyFormat);

        return result;
    }

    /**
     * 根据字段路径获取值
     */
    public static String getValueByPath(String jsonStr, String path) {
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        Object result = JSONPath.eval(jsonObject, "$." + path);
        return result == null ? null : String.valueOf(result);
    }

    /**
     * 将xml转json
     */
    public static JSONObject xml2Json(String xmlStr) throws DocumentException {
        Document doc = DocumentHelper.parseText(xmlStr);
        JSONObject json = new JSONObject();
        dom4j2Json(doc.getRootElement(), json);
        return json;
    }

    /**
     * xml转json
     *
     * @param element
     * @param json
     */
    public static void dom4j2Json(Element element, JSONObject json) {
        //如果是属性
        for (Object o : element.attributes()) {
            Attribute attr = (Attribute) o;
            if (!StringUtils.isEmpty(attr.getValue())) {
                json.put("@" + attr.getName(), attr.getValue());
            }
        }
        List<Element> chdEl = element.elements();
        if (chdEl.isEmpty() && !StringUtils.isEmpty(element.getText())) {//如果没有子元素,只有一个值
            json.put(element.getName(), element.getText());
        }

        for (Element e : chdEl) {//有子元素
            if (!e.elements().isEmpty()) {//子元素也有子元素
                JSONObject chdjson = new JSONObject();
                dom4j2Json(e, chdjson);
                Object o = json.get(e.getName());
                if (o != null) {
                    JSONArray jsona = null;
                    if (o instanceof JSONObject) {//如果此元素已存在,则转为jsonArray
                        JSONObject jsono = (JSONObject) o;
                        json.remove(e.getName());
                        jsona = new JSONArray();
                        jsona.add(jsono);
                        jsona.add(chdjson);
                    }
                    if (o instanceof JSONArray) {
                        jsona = (JSONArray) o;
                        jsona.add(chdjson);
                    }
                    json.put(e.getName(), jsona);
                } else {
                    if (!chdjson.isEmpty()) {
                        json.put(e.getName(), chdjson);
                    }
                }

            } else {//子元素没有子元素
                for (Object o : element.attributes()) {
                    Attribute attr = (Attribute) o;
                    if (!StringUtils.isEmpty(attr.getValue())) {
                        json.put("@" + attr.getName(), attr.getValue());
                    }
                }
                if (!e.getText().isEmpty()) {
                    json.put(e.getName(), e.getText());
                }
            }
        }
    }

}
