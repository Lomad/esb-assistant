package com.winning.esb.service;

import com.winning.esb.model.InspectionDetailModel;
import com.winning.esb.model.InspectionModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.model.ext.InspectionExtModel;

import java.util.List;
import java.util.Map;

public interface IInspectionService {
    String save(InspectionModel obj);

    /**
     * 新增巡检主信息以及明细信息
     */
    String insert(InspectionModel obj, List<InspectionDetailModel> children);

    /**
     * 更新处理结果
     */
    String updateResult(InspectionModel obj);

    CommonObject query(Map map);

    /**
     * 根据巡检主ID，获取巡检主信息以及明细信息
     */
    InspectionExtModel queryByID(Integer id);

    /**
     * 获取结果枚举值列表
     */
    List<SimpleObject> getResultList();
}
