package com.winning.esb.service.db.impl;

import com.winning.esb.dao.db.IDataAccessDao;
import com.winning.esb.model.ConfigsModel;
import com.winning.esb.model.db.DataSourceModel;
import com.winning.esb.service.IConfigsService;
import com.winning.esb.utils.AppCtxUtils;
import com.winning.esb.utils.DateUtils;
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
    public static Map<String, DataSourceModel> dataSourceModelMap = new HashMap<>();
    /**
     * key - 数据库类型代码，value - 数据源访问接口
     */
    public static Map<String, IDataAccessDao> dataAccessDaoMap = new HashMap<>();

    public static Map<String, DataSourceModel> getDataSourceModelMap() {
        return dataSourceModelMap;
    }

    public static void setDataSourceModelMap(Map<String, DataSourceModel> dataSourceModelMap) {
        CommonDataService.dataSourceModelMap = dataSourceModelMap;
    }

    public static Map<String, IDataAccessDao> getDataAccessDaoMap() {
        return dataAccessDaoMap;
    }

    public static void setDataAccessDaoMap(Map<String, IDataAccessDao> dataAccessDaoMap) {
        CommonDataService.dataAccessDaoMap = dataAccessDaoMap;
    }

    public synchronized static IDataAccessDao getDataAccess(IConfigsService configsService, String configCode) {
        String dbType = null;

        //如果配置参数不存在 或 jdbc为空 或 超过5分钟，需要新创建数据访问层
        if (dataAccessDaoMap==null || dataAccessDaoMap.size()<1
                || dataSourceModelMap==null || dataSourceModelMap.size()<1
                || !dataSourceModelMap.containsKey(configCode)
                || dataSourceModelMap.get(configCode) == null
                || dataSourceModelMap.get(configCode).getDbType() == null
                || dataSourceModelMap.get(configCode).getJdbcTemplate() == null
                || DateUtils.diffMilliSecond(dataSourceModelMap.get(configCode).getCtime()) > 300000) {
            ConfigsModel configsModel = configsService.getByCode(configCode);
            DataSourceModel dataSourceVO = DataSourceUtils.createDataSource(configsModel.getValue());
            dataSourceModelMap.put(configCode, dataSourceVO);

            if (!StringUtils.isEmpty(configsModel.getValue())) {
                String[] connArray = configsModel.getValue().split(",");
                if (connArray != null && connArray.length == 6) {
                    dbType = connArray[0];
                    String beanName = "dataAccessDaoImpl" + dbType;
                    dataAccessDaoMap.put(dbType, AppCtxUtils.getBean(beanName));
                }
            }
        } else {
            dbType = dataSourceModelMap.get(configCode).getDbType();
        }
        return dataAccessDaoMap.get(dbType);
    }
}