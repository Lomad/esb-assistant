package com.winning.esb.service.impl;

import com.winning.esb.dao.ISimulationTestLogDao;
import com.winning.esb.model.AppInfoModel;
import com.winning.esb.model.SimulationTestLogModel;
import com.winning.esb.model.SimulationTestStepLogModel;
import com.winning.esb.model.SvcInfoModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.model.enums.SimulationTestLogEnum;
import com.winning.esb.service.IAppInfoService;
import com.winning.esb.service.ISimulationTestLogService;
import com.winning.esb.service.ISimulationTestStepLogService;
import com.winning.esb.service.ISvcInfoService;
import com.winning.esb.service.pdf.PdfHelper;
import com.winning.esb.utils.DateUtils;
import com.winning.esb.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 *
 * @author xuehao
 * @date 2017/8/21
 */
@Service
public class SimulationTestLogServiceImpl implements ISimulationTestLogService {
    @Autowired
    private ISimulationTestLogDao dao;
    @Autowired
    private ISimulationTestStepLogService simulationTestStepLogService;
    @Autowired
    private ISvcInfoService svcInfoService;
    @Autowired
    private IAppInfoService appInfoService;

    @Override
    public SimulationTestLogModel getByID(Integer id) {
        return dao.getByID(id);
    }

    @Override
    public List<SimulationTestLogModel> getLatestTest(List<Integer> fidList) {
        return dao.getLatestTest(fidList);
    }

    @Override
    public void createTestLog(SimulationTestLogModel obj) {
        if (obj.getId() == null) {
            obj.setResult(SimulationTestLogEnum.ResultEnum.Testing.getCode());
            obj.setMtime(new Date());
            obj.setCtime(obj.getMtime());
            obj.setBtime(obj.getMtime());
            obj.setId(dao.createTestLog(obj));
        }
    }

    @Override
    public SimulationTestLogModel finishTestLog(SimulationTestLogModel obj) {
        if (obj.getId() != null) {
            SimulationTestLogModel objDB = getByID(obj.getId());
            objDB.setDesp(obj.getDesp());
            objDB.setResult(SimulationTestLogEnum.ResultEnum.Success.getCode());
            objDB.setMtime(new Date());
            objDB.setEtime(objDB.getMtime());
            objDB.setTime_len(DateUtils.diffMilliSecond(objDB.getBtime(), objDB.getEtime()));
            dao.finishTestLog(objDB);

            return objDB;
        }
        return obj;
    }

    @Override
    public List<SimpleObject> getResultEnum() {
        SimulationTestLogEnum.ResultEnum[] items = SimulationTestLogEnum.ResultEnum.values();
        List<SimpleObject> simpleObjects = new ArrayList<>();
        SimpleObject simpleObject;
        for (SimulationTestLogEnum.ResultEnum item : items) {
            simpleObject = new SimpleObject();
            simpleObject.setItem1(String.valueOf(item.getCode()));
            simpleObject.setItem2(item.getValue());
            simpleObjects.add(simpleObject);
        }
        return simpleObjects;
    }

    @Override
    public Integer testResult(Integer aid) {

        return dao.testResult(aid);
    }


}