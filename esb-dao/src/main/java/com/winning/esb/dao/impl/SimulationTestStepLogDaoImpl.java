package com.winning.esb.dao.impl;

import com.winning.esb.dao.ISimulationTestStepLogDao;
import com.winning.esb.dao.impl.common.CommonHandle;
import com.winning.esb.model.SimulationTestStepLogModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.stable.DatabaseConst;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author xuehao
 * @date 2017/8/21
 */
@Repository
public class SimulationTestStepLogDaoImpl implements ISimulationTestStepLogDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private CommonHandle commonHandle;

    public static final String TB_NAME = "ESB_SimulationTestStepLog";

    @Override
    public Integer insert(SimulationTestStepLogModel obj) {
        String sql = "insert into " + TB_NAME + " (tid, sid, result, out_msg, ack_msg, user_id, time_len, btime, etime, ctime) " +
                "values(:tid, :sid, :result, :out_msg, :ack_msg, :user_id, :time_len, :btime, :etime, :ctime)";
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(obj);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    @Override
    public List<SimulationTestStepLogModel> getByID(List<Integer> idList) {
        return commonHandle.listByKey(TB_NAME, idList, SimulationTestStepLogModel.class);
    }

    @Override
    public List<SimulationTestStepLogModel> getByTID(Integer tid) {
        return commonHandle.listByColumn(TB_NAME, "tid", tid, SimulationTestStepLogModel.class);
    }

    @Override
    public List<SimulationTestStepLogModel> getLatestByTID(Object tid) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select * from ").append(TB_NAME).append("(nolock) where id in ");
        sql.append("(select max(id) as id from ").append(TB_NAME).append("(nolock) where tid ");
        if (tid instanceof List) {
            sql.append(" in (:tid) ");
        } else {
            sql.append(" = :tid ");
        }
        sql.append(" GROUP BY tid, sid) ");
        Map<String, Object> map = new HashMap<>();
        map.put("tid", tid);
        return namedParameterJdbcTemplate.query(sql.toString(), map, new BeanPropertyRowMapper<>(SimulationTestStepLogModel.class));
    }

    @Override
    public Integer getLatestSidByTID(Integer tid) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT TOP 1 sid FROM ESB_SimulationTestStepLog(NOLOCK) WHERE tid = :tid ORDER BY id DESC ");
        Map<String, Object> map = new HashMap<>();
        map.put("tid", tid);
        List<Integer> result = namedParameterJdbcTemplate.queryForList(sql.toString(), map, Integer.class);
        return ListUtils.isEmpty(result) ? null : result.get(0);
    }

    @Override
    public List<Map<String, Object>> countByTID(Integer tid) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT a.testCount, b.* FROM ( ");
        sql.append("SELECT in_b.sid, COUNT(*) AS testCount, MAX(in_b.id) AS maxID ");
        sql.append("FROM ESB_SimulationTestLog in_a(NOLOCK) JOIN ESB_SimulationTestStepLog in_b(NOLOCK) ");
        sql.append("ON in_a.id=in_b.tid AND in_b.tid = :tid GROUP BY in_b.sid) a ");
        sql.append("LEFT JOIN ESB_SimulationTestStepLog b(NOLOCK) ON a.maxID = b.id ");
        Map<String, Object> map = new HashMap<>();
        map.put("tid", tid);
        return namedParameterJdbcTemplate.queryForList(sql.toString(), map);
    }

    @Override
    public CommonObject query(Map<String, Object> map) {
        StringBuffer sqlWhere = new StringBuffer();
        if (map != null) {
            Object temp1 = map.get("sid");
            if (!StringUtils.isEmpty(temp1)) {
                sqlWhere.append("and sid = :sid ");
            }
            temp1 = map.get("tid");
            if (!StringUtils.isEmpty(temp1)) {
                sqlWhere.append("and tid = :tid ");
            }
        }
        LinkedHashMap<String, String> orderColumns = new LinkedHashMap<>();
        orderColumns.put("id", DatabaseConst.ORDER_DESC);
        CommonObject commonObject = commonHandle.query(TB_NAME, map, sqlWhere, orderColumns, SimulationTestStepLogModel.class);
        return commonObject;
    }

    @Override
    public Map<Integer, Integer> queryUnitTestedSidList() {
        String sql = " SELECT sid, result FROM ESB_SimulationTestStepLog(NOLOCK) WHERE id IN ( " +
                " SELECT MAX(id) FROM ESB_SimulationTestStepLog(NOLOCK) WHERE tid = 0 GROUP BY sid) ";
        Map<String, Object> paramMap = null;
        List<Map<String, Object>> resultRawList = namedParameterJdbcTemplate.queryForList(sql, paramMap);
        Map<Integer, Integer> resultMap;
        if (!ListUtils.isEmpty(resultRawList)) {
            resultMap = new HashMap<>();
            for (Map item : resultRawList) {
                resultMap.put(Integer.parseInt(item.get("sid").toString()), Integer.parseInt(item.get("result").toString()));
            }
        } else {
            resultMap = null;
        }
        return resultMap;
    }

}