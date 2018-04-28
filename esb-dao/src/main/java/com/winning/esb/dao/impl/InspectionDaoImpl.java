package com.winning.esb.dao.impl;

import com.winning.esb.dao.IInspectionDao;
import com.winning.esb.dao.impl.common.CommonHandle;
import com.winning.esb.model.InspectionModel;
import com.winning.esb.model.common.CommonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InspectionDaoImpl implements IInspectionDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private CommonHandle commonHandle;

    private final String TB_NAME = "ESB_Inspection";

    @Override
    public Integer insert(InspectionModel obj) {
        String sql = "insert into " + TB_NAME + " (result, check_uid, check_desp, time_len, btime, etime, " +
                "result_uid, result_time, result_desp, ctime) " +
                "values(:result, :check_uid, :check_desp, :time_len, :btime, :etime, " +
                ":result_uid, :result_time, :result_desp, :ctime)";
//        Map<String, Object> map = new HashMap<>();
//        map.put("result", obj.getResult());
//        map.put("check_uid", obj.getCheck_uid());
//        map.put("check_desp", obj.getCheck_desp());
//        map.put("time_len", obj.getTime_len());
//        map.put("btime", obj.getBtime());
//        map.put("etime", obj.getEtime());
//        map.put("result_uid", obj.getResult_uid());
//        map.put("result_time", obj.getResult_time());
//        map.put("result_desp", obj.getResult_desp());
//        map.put("ctime", obj.getCtime());

        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(obj);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    @Override
    public void updateResult(InspectionModel obj) {
        StringBuffer sql = new StringBuffer();
        sql.append("update " + TB_NAME + " set result_uid = :result_uid ");
        sql.append(", result_time = :result_time ");
        sql.append(", result_desp = :result_desp ");
        sql.append("where id = :id ");
        Map<String, Object> map = new HashMap<>();
        map.put("result_uid", obj.getResult_uid());
        map.put("result_time", obj.getResult_time());
        map.put("result_desp", obj.getResult_desp());
        map.put("id", obj.getId());
        namedParameterJdbcTemplate.update(sql.toString(), map);
    }

    @Override
    public CommonObject query(Map map) {
        //生成筛选条件
        StringBuffer sqlWhere = new StringBuffer();
        Object temp = map.get("user_id");
        if (temp != null) {
            sqlWhere.append("and user_id = :user_id ");
        }
        temp = map.get("id");
        if (temp != null) {
            sqlWhere.append("and id = :id ");
        }
        return commonHandle.query(TB_NAME, map, sqlWhere, InspectionModel.class);
    }
}