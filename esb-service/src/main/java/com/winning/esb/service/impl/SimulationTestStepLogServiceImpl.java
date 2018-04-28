package com.winning.esb.service.impl;

import com.winning.esb.dao.ISimulationTestStepLogDao;
import com.winning.esb.model.SimulationTestStepLogModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.service.ISimulationTestStepLogService;
import com.winning.esb.utils.DateUtils;
import com.winning.esb.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author xuehao
 * @date 2017/8/21
 */
@Service
public class SimulationTestStepLogServiceImpl implements ISimulationTestStepLogService {
    @Autowired
    private ISimulationTestStepLogDao dao;

    @Override
    public SimpleObject save(SimulationTestStepLogModel obj) {
        SimpleObject result = new SimpleObject();
        obj.setCtime(new Date());
        obj.setTime_len(DateUtils.diffMilliSecond(obj.getBtime(), obj.getEtime()));
        Integer id = dao.insert(obj);

        result.setItem2(String.valueOf(id));
        return result;
    }

    @Override
    public SimulationTestStepLogModel getByID(Integer id) {
        List<Integer> idList = new ArrayList<>();
        idList.add(id);
        List<SimulationTestStepLogModel> list = getByID(idList);
        return ListUtils.isEmpty(list) ? null : list.get(0);
    }

    @Override
    public List<SimulationTestStepLogModel> getByID(List<Integer> idList) {
        return dao.getByID(idList);
    }

    @Override
    public List<SimulationTestStepLogModel> getByTID(Integer tid) {
        return dao.getByTID(tid);
    }

    @Override
    public List<SimulationTestStepLogModel> getLatestByTID(Integer tid) {
        return dao.getLatestByTID(tid);
    }

    @Override
    public List<SimulationTestStepLogModel> getLatestByTID(List<Integer> tidList) {
        return dao.getLatestByTID(tidList);
    }

    @Override
    public Integer getLatestSidByTID(Integer tid) {
        return dao.getLatestSidByTID(tid);
    }

    @Override
    public CommonObject query(Map<String, Object> map) {
        return dao.query(map);
    }

    @Override
    public Map<Integer, Integer> queryUnitTestedSidList() {
        return dao.queryUnitTestedSidList();
    }

    @Override
    public Map<Integer, SimulationTestStepLogModel> mapSidObj(List<SimulationTestStepLogModel> list) {
        if(!ListUtils.isEmpty(list)) {
            Map<Integer, SimulationTestStepLogModel> map = new HashMap<>();
            for(SimulationTestStepLogModel item : list) {
                map.put(item.getSid(), item);
            }
            return map;
        }
        return null;
    }

    @Override
    public Map<Integer, List<SimulationTestStepLogModel>> mapSidList(List<SimulationTestStepLogModel> list) {
        if(!ListUtils.isEmpty(list)) {
            Map<Integer, List<SimulationTestStepLogModel>> map = new HashMap<>();
            Integer sid;
            for(SimulationTestStepLogModel item : list) {
                sid = item.getSid();
                if(!map.containsKey(sid)) {
                    map.put(sid, new ArrayList<>());
                }
                map.get(sid).add(item);
            }
            return map;
        }
        return null;
    }

}