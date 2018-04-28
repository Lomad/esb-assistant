package com.winning.esb.service;

import com.winning.esb.model.InspectionDetailModel;
import com.winning.esb.model.InspectionModel;
import com.winning.esb.model.InspectionSysModel;
import com.winning.esb.model.common.CommonObject;

import java.util.List;
import java.util.Map;

public interface IInspectionSysService {
    /**
     * 新增巡检系统信息
     */
    String insert(InspectionSysModel obj);

    /**
     * 更新处理结果
     */
    String update(InspectionSysModel obj);

    CommonObject query(Map map);
}
