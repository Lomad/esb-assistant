package com.winning.esb.dao.db.impl;

import com.winning.esb.dao.db.IDataAccessDao;
import com.winning.esb.model.ConfigsModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("dataAccessDaoImplSqlServer")
public class DataAccessDaoImplSqlServer implements IDataAccessDao {

    @Override
    public Map<String, Object> doExecuteStoredProcedure(JdbcTemplate jdbcTemplate, String storedProcedureName) {
        return doExecuteStoredProcedure(jdbcTemplate, storedProcedureName, false);
    }

    @Override
    public Map<String, Object> doExecuteStoredProcedure(JdbcTemplate jdbcTemplate, String storedProcedureName,
                                                        boolean needReturnValue) {
        Map<String, Object> args = null;
        return doExecuteStoredProcedure(jdbcTemplate, storedProcedureName, args, needReturnValue);
    }

    @Override
    public Map<String, Object> doExecuteStoredProcedure(JdbcTemplate jdbcTemplate, String storedProcedureName, Map<String, Object> args) {
        return doExecuteStoredProcedure(jdbcTemplate, storedProcedureName, args, false);
    }

    @Override
    public Map<String, Object> doExecuteStoredProcedure(JdbcTemplate jdbcTemplate, String storedProcedureName, Map<String, Object> args,
                                                        boolean needReturnValue) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate);
        simpleJdbcCall.withProcedureName(storedProcedureName);
        simpleJdbcCall.setReturnValueRequired(needReturnValue);
        Map<String, Object> resultMap;
        if (args == null) {
            resultMap = simpleJdbcCall.execute();
        } else {
            resultMap = simpleJdbcCall.execute(args);
        }
        return resultMap;
    }

    @Override
    public List<Map<String,Object>> doExecuteSql(NamedParameterJdbcTemplate namedParameterJdbcTemplate, String sql) {
        return doExecuteSql(namedParameterJdbcTemplate, sql, null);
    }

    @Override
    public List<Map<String,Object>> doExecuteSql(NamedParameterJdbcTemplate namedParameterJdbcTemplate, String sql, Map<String, Object> args) {
        return namedParameterJdbcTemplate.queryForList(sql, args);
    }

}
