package com.winning.esb.service.msg.json;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.winning.esb.model.SvcStructureModel;
import com.winning.esb.model.ValueListModel;
import com.winning.esb.model.common.TreeModel;
import com.winning.esb.model.enums.SvcStructureEnum;
import com.winning.esb.model.ext.SvcStructureExtModel;
import com.winning.esb.service.msg.IParser;
import com.winning.esb.service.msg.MsgException;
import com.winning.esb.service.utils.TreeUtils;
import com.winning.esb.utils.JsonUtils;
import com.winning.esb.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author xuehao
 * @date 2017/9/6
 */
@Component("parserJSON")
public class Parser implements IParser {

    @Override
    public SvcStructureExtModel decode(String msg, boolean fillName) throws MsgException {
        try {
            SvcStructureExtModel result;
//            LinkedHashMap<String, Object> jsonObject = JSON.parseObject(msg, LinkedHashMap.class, Feature.OrderedField);
            LinkedHashMap<String, Object> jsonObject = JsonUtils.jsonToLinkedMap(msg);
            if (jsonObject != null && jsonObject.keySet().size() > 0) {
                List<SvcStructureExtModel> list = new ArrayList<>();
                for (Map.Entry<String, Object> item : jsonObject.entrySet()) {
                    list.add(createStructure(item, fillName));
                }
                if (list.size() == 1) {
                    result = list.get(0);
                } else {
                    result = new SvcStructureExtModel();
                    result.setObj(null);
                    result.setChildren(list);
                }
            } else {
                result = null;
            }
            return result;
        } catch (Exception ex) {
            throw new MsgException("解析JSON发生异常错误，JSON可能不符合规范或有异常字符！" + ex.getMessage());
        }
    }

    @Override
    public String decode(String msg){
        return JsonUtils.format(msg);
    }

    @Override
    public String encode(List<TreeModel> treeModels, Integer valueType, List<ValueListModel> valueListModels) throws MsgException {
        if (!SvcStructureEnum.ValueTypeEnum.existCode(valueType)) {
            return null;
        }
        Map<String, Object> map = TreeUtils.treeToMap(treeModels, valueType, valueListModels);
        String result = JsonUtils.mapToJson(map, true);
        return result;
    }

    @Override
    public String getValueByPath(String msg, String path) {
        return JsonUtils.getValueByPath(msg, path);
    }

    /**
     * 根据JSON节点元素生成服务的节点对象结构
     */
    private SvcStructureExtModel createStructure(Map.Entry<String, Object> item, boolean fillName) {
        SvcStructureExtModel extModel = new SvcStructureExtModel();
        SvcStructureModel model = new SvcStructureModel();
        model.setCode(item.getKey());
        Object value = item.getValue();
        if (value instanceof JSONObject || value instanceof JSONArray) {
            JSONObject jsonObject;
            String modelName = fillName ? item.getKey() : null;
            if (value instanceof JSONObject) {
                jsonObject = (JSONObject) value;
            } else {
                model.setIs_loop(SvcStructureEnum.IsLoopEnum.Yes.getCode());
                Object childValue = ((JSONArray) value).get(0);
                if (childValue instanceof JSONObject) {
                    jsonObject = (JSONObject) childValue;
                } else {
                    jsonObject = null;
                    if (!StringUtils.isEmpty(childValue)) {
                        modelName = String.valueOf(childValue);
                    }
                }
            }
            //遍历子元素
            if (jsonObject != null && jsonObject.keySet().size() > 0) {
                List<SvcStructureExtModel> list = new ArrayList<>();
                for (Map.Entry<String, Object> child : jsonObject.entrySet()) {
                    list.add(createStructure(child, fillName));
                }
                extModel.setChildren(list);
            }
            model.setName(modelName);
            model.setData_type(SvcStructureEnum.DataTypeEnum.Complex.getCode());
        } else {
            model.setName((StringUtils.isEmpty(value) && fillName) ? item.getKey() : String.valueOf(value));
            model.setData_type(SvcStructureEnum.DataTypeEnum.Strings.getCode());
        }
        extModel.setObj(model);
        return extModel;
    }

}