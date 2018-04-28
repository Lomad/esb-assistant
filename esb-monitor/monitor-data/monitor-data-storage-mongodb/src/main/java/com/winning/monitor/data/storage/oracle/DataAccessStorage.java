package com.winning.monitor.data.storage.oracle;

import com.winning.monitor.data.storage.api.largerScreen.IDataAccessStorage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("dataAccessStorageOracle")
public class DataAccessStorage implements IDataAccessStorage {

    @Override
    public Map<String, Object> doExecuteStoredProcedure(JdbcTemplate jdbcTemplate, String storedProcedureName) {
        return doExecuteStoredProcedure(jdbcTemplate, storedProcedureName, false);
    }

    @Override
    public Map<String, Object> doExecuteStoredProcedure(JdbcTemplate jdbcTemplate, String storedProcedureName, boolean needReturnValue) {
        Map<String, Object> args = null;
        return doExecuteStoredProcedure(jdbcTemplate, storedProcedureName, args, needReturnValue);
    }

    @Override
    public Map<String, Object> doExecuteStoredProcedure(JdbcTemplate jdbcTemplate, String storedProcedureName, Map<String, Object> args,
                                                        boolean needReturnValue) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate);
        simpleJdbcCall.withProcedureName(storedProcedureName);
        Map<String, Object> resultMap;
        if (args == null) {
            resultMap = simpleJdbcCall.execute();
        } else {
            resultMap = simpleJdbcCall.execute(args);
        }
        return resultMap;
    }

}
