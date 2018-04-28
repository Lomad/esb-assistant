package com.winning.monitor.data.largerScreen;

import com.winning.esb.model.ConfigsModel;
import com.winning.esb.service.IConfigsService;
import com.winning.monitor.data.api.largerScreen.entity.DataSourceVO;
import com.winning.monitor.data.storage.api.largerScreen.IDataAccessStorage;
import com.winning.monitor.utils.ApplicationContextUtils;
import com.winning.monitor.utils.DataSourceUtils;
import com.winning.monitor.utils.DateUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuehao on 2017/8/12.
 */
public class CommonDataService {
    /**
     * key - 配置参数代码，value - 数据源访问模版
     */
    public static Map<String, DataSourceVO> dataSourceVOMap = new HashMap<>();
    /**
     * key - 数据库类型代码，value - 数据源访问接口
     */
    public static Map<String, IDataAccessStorage> dataAccessStorageMap = new HashMap<>();

    public static Map<String, DataSourceVO> getDataSourceVOMap() {
        return dataSourceVOMap;
    }

    public static void setDataSourceVOMap(Map<String, DataSourceVO> dataSourceVOMap) {
        CommonDataService.dataSourceVOMap = dataSourceVOMap;
    }

    public static Map<String, IDataAccessStorage> getDataAccessStorageMap() {
        return dataAccessStorageMap;
    }

    public static void setDataAccessStorageMap(Map<String, IDataAccessStorage> dataAccessStorageMap) {
        CommonDataService.dataAccessStorageMap = dataAccessStorageMap;
    }

    public synchronized static IDataAccessStorage getDataAccess(IConfigsService configsService, String configCode) {
        String dbType = null;

        //如果配置参数不存在 或 jdbc为空 或 超过5分钟，需要新创建数据访问层
        if (!dataSourceVOMap.containsKey(configCode)
                || dataSourceVOMap.get(configCode) == null
                || dataSourceVOMap.get(configCode).getJdbcTemplate() == null
                || DateUtils.diffMilliSecond(dataSourceVOMap.get(configCode).getCtime()) > 300000) {
            ConfigsModel configsModel = configsService.getByCode(configCode);
            DataSourceVO dataSourceVO = DataSourceUtils.createDataSourceVO(configsModel.getValue());
            dataSourceVOMap.put(configCode, dataSourceVO);

            if (!StringUtils.isEmpty(configsModel.getValue())) {
                String[] connArray = configsModel.getValue().split(",");
                if (connArray != null && connArray.length == 6) {
                    dbType = connArray[0];
                    String beanName = "dataAccessStorage" + dbType;
                    dataAccessStorageMap.put(dbType, ApplicationContextUtils.getBean(beanName, IDataAccessStorage.class));
                }
            }
        } else {
            dbType = dataSourceVOMap.get(configCode).getDbType();
        }
        return dataAccessStorageMap.get(dbType);
    }
}