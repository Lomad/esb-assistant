package com.winning.esb.service.impl;

import com.winning.esb.dao.IInspectionSysDao;
import com.winning.esb.model.InspectionSysModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.service.IInspectionSysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class InspectionSysServiceImpl implements IInspectionSysService{
    @Autowired
    private IInspectionSysDao dao;

    @Override
    public String insert(InspectionSysModel obj) {
        obj.setCtime(new Date());
        obj.setMtime(obj.getCtime());
        dao.insert(obj);
        return null;
    }

    @Override
    public String update(InspectionSysModel obj) {
        obj.setMtime(new Date());
        dao.update(obj);
        return null;
    }

    @Override
    public CommonObject query(Map map) {
        CommonObject commonObject = dao.query(map);
        return commonObject;
    }
}
