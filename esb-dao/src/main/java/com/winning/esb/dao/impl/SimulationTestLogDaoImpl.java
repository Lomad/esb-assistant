package com.winning.esb.dao.impl;

import com.winning.esb.dao.ISimulationTestLogDao;
import com.winning.esb.dao.impl.common.CommonHandle;
import com.winning.esb.model.SimulationTestLogModel;
import com.winning.esb.stable.DatabaseConst;
import com.winning.esb.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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
public class SimulationTestLogDaoImpl implements ISimulationTestLogDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private CommonHandle commonHandle;

    private final String TB_NAME = "ESB_SimulationTestLog";

    @Override
    public SimulationTestLogModel getByID(Integer id) {
        return commonHandle.getByKey(TB_NAME, id, SimulationTestLogModel.class);
    }

    @Override
    public List<SimulationTestLogModel> getLatestTest(List<Integer> fidList) {
        String nolock = DatabaseConst.NOLOCK_BRACKETS;
        Map<String, Object> map = new HashMap<>();
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT * FROM ").append(TB_NAME).append(nolock);
        sql.append(" WHERE id in (SELECT MAX(id) FROM ").append(TB_NAME).append(nolock);
        if (!ListUtils.isEmpty(fidList)) {
            if (fidList.size() == 1) {
                sql.append(" WHERE fid = :fid ");
                map.put("fid", fidList.get(0));
            } else {
                sql.append(" WHERE fid in (:fid) ");
                map.put("fid", fidList);
            }
        }
        sql.append(" GROUP BY fid) ");
        return namedParameterJdbcTemplate.query(sql.toString(), map, new BeanPropertyRowMapper<>(SimulationTestLogModel.class));
    }

    @Override
    public Integer createTestLog(SimulationTestLogModel obj) {
        String sql = "insert into " + TB_NAME + " (fid, result, btime, ctime, mtime) " +
                "values(:fid, :result, :btime, :ctime, :mtime)";
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(obj);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    @Override
    public void finishTestLog(SimulationTestLogModel obj) {
        StringBuffer sql = new StringBuffer();
        sql.append("update " + TB_NAME + " set result = :result ");
        sql.append(", desp = :desp ");
        sql.append(", user_id = :user_id ");
        sql.append(", time_len = :time_len ");
        sql.append(", etime = :etime ");
        sql.append(", mtime = :mtime ");
        sql.append("where id = :id ");
        Map<String, Object> map = new HashMap<>();
        map.put("result", obj.getResult());
        map.put("desp", obj.getDesp());
        map.put("user_id", obj.getUser_id());
        map.put("time_len", obj.getTime_len());
        map.put("etime", obj.getEtime());
        map.put("mtime", obj.getMtime());
        map.put("id", obj.getId());
        namedParameterJdbcTemplate.update(sql.toString(), map);
    }

    @Override
    public Integer testResult(Integer aid) {
        StringBuffer sql = new StringBuffer();
        sql.append("select top 1 result from ").append(TB_NAME).append(" where aid = :aid order by id desc ");
        Map<String, Object> map = new HashMap<>();
        map.put("aid", aid);
        return namedParameterJdbcTemplate.queryForObject(sql.toString(), map, Integer.class);
    }
}