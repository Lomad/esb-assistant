package com.winning.esb.dao;

import com.winning.esb.model.InspectionDetailModel;
import com.winning.esb.model.InspectionModel;
import com.winning.esb.model.common.CommonObject;

import java.util.List;
import java.util.Map;

public interface IInspectionDao {
    /**
     * 新增后返回自增ID
     */
    Integer insert(InspectionModel obj);

//    /**
//     * 新增巡检主信息以及明细信息
//     */
//    Integer insert(InspectionModel obj, List<InspectionDetailModel> children);

    /**
     * 更新处理结果
     */
    void updateResult(InspectionModel obj);

    CommonObject query(Map map);
}
