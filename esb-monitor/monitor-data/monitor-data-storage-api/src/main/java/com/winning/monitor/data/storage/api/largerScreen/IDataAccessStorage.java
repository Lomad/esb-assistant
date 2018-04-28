package com.winning.monitor.data.storage.api.largerScreen;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

/**
 * Created by xuehao on 17/07/27.
 */
public interface IDataAccessStorage {

    /**
     * 不返回Return_Value
     */
    Map<String, Object> doExecuteStoredProcedure(JdbcTemplate jdbcTemplate, String storedProcedureName);

    Map<String, Object> doExecuteStoredProcedure(JdbcTemplate jdbcTemplate, String storedProcedureName,
                                                 boolean needReturnValue);

    Map<String, Object> doExecuteStoredProcedure(JdbcTemplate jdbcTemplate, String storedProcedureName, Map<String, Object> args,
                                                 boolean needReturnValue);

//    <T> T doExecuteStoredProcedure(JdbcTemplate jdbcTemplate, String storedProcedureName, Class<T> clazz);
//
//    <T> T doExecuteStoredProcedure(JdbcTemplate jdbcTemplate, String storedProcedureName, Map<String, Object> args, Class<T> clazz);

}
