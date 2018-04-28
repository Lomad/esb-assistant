package com.winning.esb.dao;

import com.winning.esb.model.InspectionSysModel;
import com.winning.esb.model.common.CommonObject;

import java.util.Map;

public interface IInspectionSysDao {
    /**
     * 新增后返回自增ID
     */
    Integer insert(InspectionSysModel obj);


    /**
     * 更新处理结果
     */
    void update(InspectionSysModel obj);

    CommonObject query(Map map);
}
