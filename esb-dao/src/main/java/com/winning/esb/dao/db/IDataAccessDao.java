package com.winning.esb.dao.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 17/07/27.
 */
public interface IDataAccessDao {

    /**
     * 不返回Return_Value
     */
    Map<String, Object> doExecuteStoredProcedure(JdbcTemplate jdbcTemplate, String storedProcedureName);

    Map<String, Object> doExecuteStoredProcedure(JdbcTemplate jdbcTemplate, String storedProcedureName,
                                                 boolean needReturnValue);

    Map<String, Object> doExecuteStoredProcedure(JdbcTemplate jdbcTemplate, String storedProcedureName, Map<String, Object> args);

    Map<String, Object> doExecuteStoredProcedure(JdbcTemplate jdbcTemplate, String storedProcedureName, Map<String, Object> args,
                                                 boolean needReturnValue);

    List<Map<String, Object>> doExecuteSql(NamedParameterJdbcTemplate namedParameterJdbcTemplate, String sql);

    List<Map<String, Object>> doExecuteSql(NamedParameterJdbcTemplate namedParameterJdbcTemplate, String sql, Map<String, Object> args);
}
