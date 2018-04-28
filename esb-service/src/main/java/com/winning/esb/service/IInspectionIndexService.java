package com.winning.esb.service;

import com.winning.esb.model.InspectionIndexModel;

import java.util.List;

public interface IInspectionIndexService {
    /**
     * 获取所有指标
     */
    List<InspectionIndexModel> list();
}
