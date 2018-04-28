package com.winning.esb.dao;

import com.winning.esb.model.ValueListModel;
import com.winning.esb.model.common.CommonObject;

import java.util.List;
import java.util.Map;

/**
 * @author xuehao
 */
public interface IValueListDao {
    /**
     * 新增列表
     * @param deleteBeforeInsert    true - 新增之前，根据ssid删除旧的记录，false - 不删除
     */
    void insert(Integer ssid, List<ValueListModel> list, boolean deleteBeforeInsert);

    void delete(Integer ssid);

    List<ValueListModel> queryBySid(Integer sid, Integer direction);

    List<ValueListModel> queryBySsid(Integer ssid, Integer type);
}