package com.winning.esb.service.utils;

import com.winning.esb.model.SvcStructureModel;
import com.winning.esb.model.ValueListModel;
import com.winning.esb.model.common.TreeModel;
import com.winning.esb.model.enums.SvcStructureEnum;
import com.winning.esb.utils.DateUtils;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.StringUtils;
import com.winning.esb.utils.XmlUtils;
import org.apache.commons.collections.map.LinkedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 2017/9/13.
 */
public class TreeUtils {


    /**
     * 将tree转为map
     */
    public static Map<String, Object> treeToMap(List<TreeModel> treeModels, SvcStructureEnum.ValueTypeEnum valueType, List<ValueListModel> valueListModels) {
        return treeToMap(treeModels, valueType.getCode(), valueListModels);
    }

    /**
     * 将tree转为map
     */
    public static Map<String, Object> treeToMap(List<TreeModel> treeModels, Integer valueType, List<ValueListModel> valueListModels) {
        Map<String, Object> map;
        if (!ListUtils.isEmpty(treeModels)) {
            SvcStructureModel svcStructure;
            map = new LinkedMap();
            Object obj;
            String key;
            String firstEnum;
            for (TreeModel treeModel : treeModels) {
                svcStructure = (SvcStructureModel) treeModel.getMyData();
                Integer ssid = svcStructure.getId();
                firstEnum = null;
                for (ValueListModel valueModel : valueListModels) {
                    if (ssid.intValue() == valueModel.getSsid().intValue()) {
                        String value = valueModel.getValue();
                        if (!StringUtils.isEmpty(value)) {
                            firstEnum = value;
                            break;
                        }
                    }
                }
                key = svcStructure.getCode();
                if (!ListUtils.isEmpty(treeModel.getChildren())) {
                    Map<String, Object> val = treeToMap(treeModel.getChildren(), valueType, valueListModels);
                    if (svcStructure.getIs_loop() != null
                            && SvcStructureEnum.IsLoopEnum.Yes.getCode() == svcStructure.getIs_loop().intValue()) {
                        List<Map<String, Object>> objects = new ArrayList<>();
                        objects.add(val);
                        obj = objects;
                    } else {
                        obj = val;
                    }
//                    map.put(key, obj);
                } else {
                    String val;
                    //生成模拟值
                    if (valueType.intValue() == SvcStructureEnum.ValueTypeEnum.VistualValue.getCode()) {
                        if (!StringUtils.isEmpty(svcStructure.getValue_default())) {
                            val = svcStructure.getValue_default();
                        } else if (!StringUtils.isEmpty(firstEnum)) {
                            val = firstEnum;
                        } else if (svcStructure.getData_type() != null
                                && svcStructure.getData_type().intValue() == SvcStructureEnum.DataTypeEnum.Number.getCode()) {
                            val = "1";
                        } else if (svcStructure.getData_type() != null
                                && svcStructure.getData_type().intValue() == SvcStructureEnum.DataTypeEnum.Date.getCode()) {
                            val = DateUtils.getCurrentDateString();
                        } else if (svcStructure.getData_type() != null
                                && svcStructure.getData_type().intValue() == SvcStructureEnum.DataTypeEnum.Time.getCode()) {
                            val = DateUtils.getCurrentTimeString();
                        } else if (svcStructure.getData_type() != null
                                && svcStructure.getData_type().intValue() == SvcStructureEnum.DataTypeEnum.Datetime.getCode()) {
                            val = DateUtils.getCurrentDatetimeString();
                        } else if (svcStructure.getData_type() != null
                                && svcStructure.getData_type().intValue() == SvcStructureEnum.DataTypeEnum.HL7Datetime.getCode()) {
                            val = "20171019105100";
                        } else {
                            val = svcStructure.getName();
                            if (!StringUtils.isEmpty(svcStructure.getDesp())) {
                                val += "（" + svcStructure.getDesp() + "）";
                            }
                        }
                        if (svcStructure.getIs_loop() != null
                                && SvcStructureEnum.IsLoopEnum.Yes.getCode() == svcStructure.getIs_loop().intValue()) {
                            obj = ListUtils.transferToList(val);
                        } else {
                            obj = val;
                        }
                    }
                    //生成空值
                    else if (valueType.intValue() == SvcStructureEnum.ValueTypeEnum.Empty.getCode()) {
                        if (svcStructure.getIs_loop() != null
                                && SvcStructureEnum.IsLoopEnum.Yes.getCode() == svcStructure.getIs_loop().intValue()) {
                            obj = ListUtils.transferToList("");
                        } else {
                            obj = "";
                        }
                    }
                    //使用名称填充
                    else {
                        val = svcStructure.getName();
                        if (!StringUtils.isEmpty(svcStructure.getDesp())) {
                            val += "（" + svcStructure.getDesp() + "）";
                        }
                        if (svcStructure.getIs_loop() != null
                                && SvcStructureEnum.IsLoopEnum.Yes.getCode() == svcStructure.getIs_loop().intValue()) {
                            obj = ListUtils.transferToList(val);
                        } else {
                            obj = val;
                        }
                    }
//                    map.put(key, obj);
                }
                //如果是属性，则在key前加上前缀
                if (SvcStructureEnum.IsAttrEnum.Yes.getCode() == svcStructure.getIs_attr().intValue()) {
                    key = XmlUtils.ATTR_PREFIX + key;
                }
                map.put(key, obj);
            }
        } else {
            map = null;
        }
        return map;
    }

}