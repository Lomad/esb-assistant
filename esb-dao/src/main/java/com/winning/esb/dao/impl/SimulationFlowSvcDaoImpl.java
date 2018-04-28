package com.winning.esb.dao.impl;

import com.winning.esb.dao.ISimulationFlowSvcDao;
import com.winning.esb.dao.impl.common.CommonHandle;
import com.winning.esb.model.SimulationFlowSvcModel;
import com.winning.esb.stable.DatabaseConst;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuehao
 * @date 2017/8/21
 */
@Repository
public class SimulationFlowSvcDaoImpl implements ISimulationFlowSvcDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private CommonHandle commonHandle;

    public static final String TB_NAME = "ESB_SimulationFlowSvc";

    @Override
    public void insert(List<SimulationFlowSvcModel> objList) {
        if (!ListUtils.isEmpty(objList)) {
            StringBuffer sql = new StringBuffer();
            Map<String, Object> map = new HashMap<>();
            SimulationFlowSvcModel obj;
            for (int i = 0, len = objList.size(); i < len; i++) {
                sql.append("insert into ").append(TB_NAME).append("(fid, aid, sid, order_num) ");
                sql.append("values(:fid").append(i)
                        .append(", :aid").append(i)
                        .append(", :sid").append(i)
                        .append(", :order_num").append(i)
                        .append(") ");
                obj = objList.get(i);
                map.put("aid" + i, obj.getAid());
                map.put("fid" + i, obj.getFid());
                map.put("sid" + i, obj.getSid());
                map.put("order_num" + i, obj.getOrder_num());
            }
            namedParameterJdbcTemplate.update(sql.toString(), map);
        }
    }

    @Override
    public void updateOrder(List<SimulationFlowSvcModel> flowSvcModelList) {
        if (!ListUtils.isEmpty(flowSvcModelList)) {
            StringBuffer sql = new StringBuffer();
            Map<String, Object> map = new HashMap<>();
            SimulationFlowSvcModel obj;
            for (int i = 0, len = flowSvcModelList.size(); i < len; i++) {
                sql.append(" update ").append(TB_NAME)
                        .append(" set order_num = :order_num_").append(i)
                        .append(" where id = :id_").append(i).append(" ");
                obj = flowSvcModelList.get(i);
                map.put("order_num_" + i, obj.getOrder_num());
                map.put("id_" + i, obj.getId());
            }
            namedParameterJdbcTemplate.update(sql.toString(), map);
        }
    }

    @Override
    public void delete(List<Integer> idList) {
        commonHandle.delete(TB_NAME, idList);
    }

    @Override
    public List<SimulationFlowSvcModel> query(Map<String, Object> map) {
        StringBuffer sqlWhere = new StringBuffer();
        if (map != null) {
            Object temp = map.get("fid");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and fid = :fid ");
            }
            temp = map.get("fidList");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and fid in (:fidList) ");
            }
            temp = map.get("aid");
            if (!StringUtils.isEmpty(temp)) {
                sqlWhere.append("and aid = :aid ");
            }

            //aidList与sidList筛选时采用“或”的关系
            Object tempAidList = map.get("aidList");
            Object tempSidList = map.get("sidList");
            if (!StringUtils.isEmpty(tempAidList)) {
                if(StringUtils.isEmpty(tempSidList)) {
                    sqlWhere.append("and aid in (:aidList) ");
                } else {
                    sqlWhere.append("and (aid in (:aidList) or sid in (:sidList)) ");
                }
            }
        }
        LinkedHashMap<String, String> orderColumns = new LinkedHashMap<>();
        orderColumns.put("fid", DatabaseConst.ORDER_ASC);
        orderColumns.put("order_num", DatabaseConst.ORDER_ASC);
        return commonHandle.queryList(TB_NAME, map, sqlWhere, orderColumns, SimulationFlowSvcModel.class);
    }
}