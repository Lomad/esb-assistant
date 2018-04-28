package com.winning.esb.dao.impl;

import com.winning.esb.dao.IInspectionSysDao;
import com.winning.esb.dao.impl.common.CommonHandle;
import com.winning.esb.model.InspectionSysModel;
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
public class InspectionSysDaoImpl implements IInspectionSysDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private CommonHandle commonHandle;

    private final String TB_NAME = "ESB_InspectionSys";

    @Override
    public Integer insert(InspectionSysModel obj) {
        StringBuffer sql = new StringBuffer();
        sql.append("insert into " + TB_NAME + " (aid, check_type, check_time, check_desp, result_type, result_time, result_desp, result_uid,  ctime, mtime) ");
        sql.append("values(:aid, :check_type, :check_time, :check_desp, :result_type, :result_time, :result_desp, :result_uid,  :ctime, :mtime) ");
        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(obj);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql.toString(), paramSource, keyHolder, new String[]{"id"});
        return keyHolder.getKey().intValue();
    }

    @Override
    public void update(InspectionSysModel obj) {
        StringBuffer sql = new StringBuffer();
        sql.append("update " + TB_NAME + " set result_uid = :result_uid ");
        sql.append(", result_type = :result_type ");
        sql.append(", result_time = :result_time ");
        sql.append(", result_desp = :result_desp ");
        sql.append("where id = :id ");
        Map<String, Object> map = new HashMap<>();
        map.put("result_uid", obj.getResult_uid());
        map.put("result_time", obj.getResult_time());
        map.put("result_desp", obj.getResult_desp());
        map.put("result_type", obj.getResult_type());
        map.put("id", obj.getId());
        namedParameterJdbcTemplate.update(sql.toString(), map);
    }

    @Override
    public CommonObject query(Map map) {
        //生成筛选条件
        StringBuffer sqlWhere = new StringBuffer();
        Object temp = map.get("aid");
        if (temp != null) {
            sqlWhere.append("and aid = :aid ");
        }
        temp = map.get("id");
        if (temp != null) {
            sqlWhere.append("and id = :id ");
        }
        temp = map.get("check_time");
        if (temp != null) {
            sqlWhere.append("and check_time = :check_time ");
        }
        return commonHandle.query(TB_NAME, map, sqlWhere, InspectionSysModel.class);
    }
}
