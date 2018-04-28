package com.winning.esb.dao;

import com.winning.esb.model.InspectionDetailModel;

import java.util.List;

public interface IInspectionDetailDao {
    void insert(List<InspectionDetailModel> list);

    /**
     * 根据巡检主ID列表，获取明细中最大ID对应的记录（优先返回错误信息）
     */
    @Deprecated
    List<InspectionDetailModel> queryMaxByInsIDList(List<Integer> insIDList);

    /**
     * 根据巡检主ID，获取明细
     */
    List<InspectionDetailModel> queryByInsID(Integer insID);
}
