package com.winning.esb.service;

import com.winning.esb.model.ValueListModel;
import com.winning.esb.model.ext.ValueListExtModel;

import java.util.List;

public interface IValueListService {
    String insertAfterDelete(Integer ssid, Integer resultMark, String strValueList, String strValueListFailure);

    /**
     * 新增列表
     */
    String insertAfterDelete(Integer ssid, List<ValueListModel> list);

    /**
     * 新增列表
     *
     * @param deleteBeforeInsert true - 新增之前，根据ssid删除旧的记录，false - 不删除
     */
    String insert(Integer ssid, List<ValueListModel> list, boolean deleteBeforeInsert);

    String delete(Integer ssid);

    /**
     * 根据服务ID获取结果字段的成功与失败字段值
     */
    List<ValueListModel> queryResultNodeValuesBySid(Integer sid);

    /**
     * 根据服务ID获取结果字段的成功字段值
     */
    List<ValueListModel> queryResultNodeValuesSuccessBySid(Integer sid);

    /**
     * 根据对象提取取值列表
     */
    List<String> listValueByModel(List<ValueListModel> list);

    /**
     * 根据服务ID获取所有的候选值
     */
    List<ValueListModel> queryBySid(Integer sid, Integer direction);

    /**
     * 根据结构参数ID获取其候选值
     */
    List<ValueListModel> queryBySsid(Integer ssid);

    /**
     * 根据结构参数ID和类型type获取其候选值
     */
    List<ValueListModel> queryBySsid(Integer ssid, Integer type);

    ValueListExtModel getExtBySsid(Integer ssid);
}