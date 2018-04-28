package com.winning.esb.dao;

import com.winning.esb.model.InspectionIndexModel;

import java.util.List;

public interface IInspectionIndexDao {
    /**
     * 获取所有指标
     */
    List<InspectionIndexModel> list();
}
