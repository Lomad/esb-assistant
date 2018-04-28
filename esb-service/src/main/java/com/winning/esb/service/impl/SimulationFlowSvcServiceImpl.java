package com.winning.esb.service.impl;

import com.winning.esb.dao.ISimulationFlowSvcDao;
import com.winning.esb.model.SimulationFlowSvcModel;
import com.winning.esb.service.ISimulationFlowSvcService;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuehao
 * @date 2017/8/21
 */
@Service
public class SimulationFlowSvcServiceImpl implements ISimulationFlowSvcService {
    @Autowired
    private ISimulationFlowSvcDao dao;

    @Override
    public String insert(Integer fid, Integer aid, List<Integer> sidList) {
        String err = "";
        if (fid == null) {
            err += "请选择流程名称！";
        }
        if (ListUtils.isEmpty(sidList)) {
            err += "请选择流程步骤（即服务）！";
        }
        if (StringUtils.isEmpty(err)) {
            //获取已有的步骤中的最大排序
            List<SimulationFlowSvcModel> objDbList = queryByFlowID(fid);
            int maxOrder = 0;
            if (!ListUtils.isEmpty(objDbList)) {
                for (SimulationFlowSvcModel obj : objDbList) {
                    if (obj.getOrder_num() != null && obj.getOrder_num().intValue() > maxOrder) {
                        maxOrder = obj.getOrder_num().intValue();
                    }
                    if (obj.getAid().equals(aid) && sidList.contains(obj.getSid())) {
                        sidList.remove(obj.getSid());
                    }
                }
            }
            //生成对象列表，新增到数据库中
            List<SimulationFlowSvcModel> objList = new ArrayList<>();
            SimulationFlowSvcModel obj;
            for (Integer sid : sidList) {
                obj = new SimulationFlowSvcModel();
                obj.setFid(fid);
                obj.setSid(sid);
                obj.setAid(aid);
                obj.setOrder_num(++maxOrder);
                objList.add(obj);
            }
            err = insert(objList);
        }
        return err;
    }

    @Override
    public String insert(List<SimulationFlowSvcModel> objList) {
        dao.insert(objList);
        return null;
    }

    @Override
    public String updateOrder(List<SimulationFlowSvcModel> flowSvcModelList) {
        dao.updateOrder(flowSvcModelList);
        return null;
    }

    @Override
    public String delete(Integer id) {
        List<Integer> idList = new ArrayList<>();
        idList.add(id);
        delete(idList);
        return null;
    }

    @Override
    public String delete(List<Integer> idList) {
        dao.delete(idList);
        return null;
    }

    @Override
    public List<SimulationFlowSvcModel> query(Map<String, Object> map) {
        return dao.query(map);
    }

    @Override
    public List<SimulationFlowSvcModel> queryByFlowID(Integer fid) {
        Map<String, Object> map = new HashMap<>();
        map.put("fid", fid);
        return query(map);
    }

    @Override
    public List<Integer> querySidByFidAid(Integer fid, Integer aid) {
        Map<String, Object> map = new HashMap<>();
        map.put("fid", fid);
        map.put("aid", aid);
        List<SimulationFlowSvcModel> list = query(map);
        List<Integer> stepIdList;
        if(ListUtils.isEmpty(list)) {
            stepIdList = null;
        } else {
            stepIdList = new ArrayList<>();
            for(SimulationFlowSvcModel obj : list) {
                stepIdList.add(obj.getSid());
            }
        }
        return stepIdList;
    }

}