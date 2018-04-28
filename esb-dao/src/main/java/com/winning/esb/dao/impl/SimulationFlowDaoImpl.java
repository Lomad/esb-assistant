package com.winning.esb.dao.impl;

import com.winning.esb.dao.ISimulationFlowDao;
import com.winning.esb.dao.impl.common.CommonHandle;
import com.winning.esb.model.SimulationFlowModel;
import com.winning.esb.utils.MapUtils;
import com.winning.esb.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuehao
 * @date 2017/8/21
 */
@Repository
public class SimulationFlowDaoImpl implements ISimulationFlowDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private CommonHandle commonHandle;

    private final String TB_NAME = "ESB_SimulationFlow";

    @Override
    public Integer insert(SimulationFlowModel obj) {
        StringBuffer sql = new StringBuffer();
        sql.append("insert into ").append(TB_NAME).append("(name, desp, ctime, mtime) ");
        sql.append("values(:name, :desp, :ctime, :mtime)");
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(obj);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql.toString(), paramSource, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public void update(SimulationFlowModel obj) {
        StringBuffer sql = new StringBuffer();
        sql.append("update ").append(TB_NAME).append(" set name = :name ");
        sql.append(", desp = :desp ");
        sql.append(", mtime = :mtime ");
        sql.append("where id = :id ");
        Map<String, Object> map = new HashMap<>();
        map.put("name", obj.getName());
        map.put("desp", obj.getDesp());
        map.put("mtime", obj.getMtime());
        map.put("id", obj.getId());
        namedParameterJdbcTemplate.update(sql.toString(), map);
    }

    @Override
    public void delete(Integer id) {
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM ").append(SimulationFlowSvcDaoImpl.TB_NAME).append(" where fid = :id ");
        sql.append("DELETE FROM ").append(TB_NAME).append(" where id = :id ");
        sql.append("DELETE FROM ESB_SimulationTestStepLog ");
        sql.append("WHERE tid IN (SELECT id FROM ESB_SimulationTestLog(NOLOCK) WHERE fid = :id) ");
        sql.append("DELETE FROM ESB_SimulationTestLog WHERE fid = :id ");
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        namedParameterJdbcTemplate.update(sql.toString(), map);
    }

    @Override
    public SimulationFlowModel getByID(Integer id) {
        return commonHandle.getByKey(TB_NAME, id, SimulationFlowModel.class);
    }

    @Override
    public List<SimulationFlowModel> query(Map<String, Object> map) {
        StringBuffer sqlWhere = new StringBuffer();
        if (!MapUtils.isEmpty(map)) {
            Object temp = map.get("queryWord");
            if (!StringUtils.isEmpty(temp)) {
                map.put("queryWord", "%" + temp + "%");
                sqlWhere.append("and name like :queryWord ");
            }
        }
        return commonHandle.queryList(TB_NAME, map, sqlWhere, SimulationFlowModel.class);
    }
}