package com.winning.esb.service.msg;

import com.winning.esb.model.ValueListModel;
import com.winning.esb.model.common.TreeModel;
import com.winning.esb.model.ext.SvcStructureExtModel;

import java.util.List;

/**
 * Created by xuehao on 2017/9/4.
 */
public interface IParser {
    /**
     * 实现类的Bean前缀
     */
    public static String BEAN_PREFIX = "parser";

    /**
     * 解析消息
     * @param fillName  当名称（name）为空时，是否填充【true - 适用于导入参数结构， false - 适用于验证参数内容时】
     */
    SvcStructureExtModel decode(String msg, boolean fillName) throws MsgException;

    /**
     * 解析消息
     *返回格式化后的xml或json
     */
    String decode(String msg) throws MsgException;

    /**
     * 生成消息
     * @param valueType 参考SvcStructureEnum的ValueTypeEnum枚举
     */
    String encode(List<TreeModel> treeModels, Integer valueType, List<ValueListModel> valueListModels) throws MsgException;

    /**
     * 根据字段路径获取值
     * @param path  字段路径，以点号分隔
     */
    String getValueByPath(String msg, String path);
}