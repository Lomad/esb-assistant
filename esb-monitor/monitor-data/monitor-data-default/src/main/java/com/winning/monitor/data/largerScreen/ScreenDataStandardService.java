package com.winning.monitor.data.largerScreen;

import com.winning.esb.model.enums.ConfigsCodeConst;
import com.winning.esb.service.IConfigsService;
import com.winning.monitor.data.api.enums.DateType;
import com.winning.monitor.data.api.largerScreen.IScreenDataCenterService;
import com.winning.monitor.data.api.largerScreen.IScreenDataStandardService;
import com.winning.monitor.utils.DateUtils;
import com.winning.monitor.utils.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ScreenDataStandardService implements IScreenDataStandardService {
    @Autowired
    private IConfigsService configsService;

    @Autowired
    private IScreenDataCenterService dataCenterIndexService;

    @Override
    public Map countByOverView(String sys,String param) {
        if ("MDM".equalsIgnoreCase(sys)) {
            return GetCountOverViewByMDM(param);
        } else if ("EMPI".equalsIgnoreCase(sys)) {
            return GetCountOverViewByEMPI(param);
        } else if ("WDK".equalsIgnoreCase(sys)) {
            return GetCountOverViewByWDK();
        }
        return null;
    }

    @Override
    public Map getUnRegistryCount() {
        String configCode = ConfigsCodeConst.DB_EMPI;
        String procName = "usp_EmpiRegLostCountFromCDR";

        Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode).doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(), procName);
        if (result != null && result.size() > 0) {
            String resultKey = "#result-set-1";
            if (result.get(resultKey) != null) {
                List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get(resultKey);
                if (resultSet != null && resultSet.size() > 0) {
                    return resultSet.get(0);
                }
            }
        }
        return null;
    }

    @Override
    public List queryPatRegdataByEMPI(Integer type) {
        String configCode = ConfigsCodeConst.DB_EMPI;
        String procName = "usp_StatisticsCountBydate";
        Map paras = new HashMap();
        paras.put("in_from_date", DateUtils.toDateString(DateUtils.getCurrentTime(), new SimpleDateFormat("yyyy-MM-dd")));
        if (DateType.CURRENTWEEK.getKey() == type) {
            type = 1;
        } else if (DateType.CURRENTMONTH.getKey() == type) {
            type = 0;
        } else {
            type = 2;
        }
        paras.put("in_type", type);
        Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode).doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(), procName, paras, true);
        if (result != null && result.size() > 0) {
            String resultKey = "#result-set-1";
            if (result.get(resultKey) != null) {
                List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get(resultKey);
                return resultSet;
            }
        }
        return null;
    }

    @Override
    public List queryAreaDataByEMPI(int type) {
        String configCode = ConfigsCodeConst.DB_EMPI;
        String procName = "usp_StatisticsAreaBydate";
        Map paras = new HashMap();
        paras.put("in_from_date", DateUtils.toDateString(DateUtils.getCurrentTime(), new SimpleDateFormat("yyyy-MM-dd")));
        if (DateType.CURRENTWEEK.getKey() == type) {
            type = 1;
        } else if (DateType.CURRENTMONTH.getKey() == type) {
            type = 0;
        } else {
            type = 2;
        }
        paras.put("in_type", type);
        Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode).doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(), procName, paras, true);
        if (result != null && result.size() > 0) {
            String resultKey = "#result-set-1";
            if (result.get(resultKey) != null) {
                List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get(resultKey);
                return resultSet;
            }
        }
        return null;
    }

    @Override
    public List queryCategoryDatasetByWDK(Integer type) {
        String configCode = ConfigsCodeConst.DB_HLHT;
        String procName = "sp_bzsjj_proportion";
        Map paras = new HashMap();
        paras.put("bl", 0);
        Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode).doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(), procName, paras, true);
        if (result != null && result.size() > 0) {
            String resultKey = "#result-set-1";
            if (result.get(resultKey) != null) {
                List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get(resultKey);
                return resultSet;
            }
        }
        return null;
    }

    @Override
    public List queryRegdataByWDK(Integer type) {
        String configCode = ConfigsCodeConst.DB_CDR;
        String procName = "sp_gxwd_sjfx";
        Map paras = new HashMap();
        String cycletype = "day";
        if (DateType.CURRENTWEEK.getKey() == type) {
            cycletype = "week";
        } else if (DateType.CURRENTMONTH.getKey() == type) {
            cycletype = "month";
        }
        paras.put("cycletype", cycletype);
        paras.put("orgcode", null);
        paras.put("_daytime_s", null);

        Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode).doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(), procName, paras, true);
        if (result != null && result.size() > 0) {
            String resultKey = "#result-set-1";
            if (result.get(resultKey) != null) {
                List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get(resultKey);
                return resultSet;
            }
        }
        return null;
    }

    private Map GetCountOverViewByMDM(String params) {
        String configCode = ConfigsCodeConst.DB_MDM;

        String procName = "USP_MDM_OverView";//"USP_COUNTOVERVIEW";
        Map paras = new HashMap();
        if (StringUtils.hasText(params)) {
            paras.put("now", params);
        }

        Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode).doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(), procName, paras, true);
        if (result != null && result.size() > 0) {
            String resultKey = "#result-set-1";
            if (result.get(resultKey) != null) {
                List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get(resultKey);
                if (resultSet != null && resultSet.size() > 0) {
                    return resultSet.get(0);
                }
            }
        }
        return null;
    }

    @Override
    public List queryMonthCountBeforeMDM(String time) {
        String configCode = ConfigsCodeConst.DB_MDM;

        String procName = "USP_MDM_CountBeforeMonth";//"USP_COUNTOVERVIEW";
        Map paras = new HashMap();
        paras.put("now",time);

        Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode).doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(), procName, paras, true);
        if (result != null && result.size() > 0) {
            String resultKey = "#result-set-1";
            if (result.get(resultKey) != null) {
                List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get(resultKey);
                return resultSet;
            }
        }
        return null;
    }

    @Override
    public List queryPerOfCommunication(String time) {
        String configCode = ConfigsCodeConst.DB_MDM;

        String procName = "USP_MDM_CountSysPie";//"USP_COUNTOVERVIEW";
        Map paras = new HashMap();
        paras.put("now",time);

        Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode).doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(), procName, paras, true);
        if (result != null && result.size() > 0) {
            String resultKey = "#result-set-1";
            if (result.get(resultKey) != null) {
                List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get(resultKey);
                return resultSet;
            }
        }
        return null;
    }

    @Override
    public List queryUpdateCount(String time) {
        String configCode = ConfigsCodeConst.DB_MDM;

        String procName = "USP_MDM_CountItem";
        Map paras = new HashMap();
        paras.put("now",time);

        Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode).doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(), procName, paras, true);
        if (result != null && result.size() > 0) {
            String resultKey = "#result-set-1";
            if (result.get(resultKey) != null) {
                List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get(resultKey);
                return resultSet;
            }
        }
        return null;
    }

    private Map GetCountOverViewByEMPI(String time) {
        String configCode = ConfigsCodeConst.DB_EMPI;

        String procName = "usp_StatisticsTodayResult";
        Map paras = new HashMap();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date current= new Date(System.currentTimeMillis());
        time =format.format(current);
        paras.put("in_from_date", time);
        Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode).doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(), procName, paras, true);
        if (result != null && result.size() > 0) {
            String resultKey = "#result-set-1";
            if (result.get(resultKey) != null) {
                List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get(resultKey);
                if (resultSet != null && resultSet.size() > 0) {
                    return resultSet.get(0);
                }
            }
        }
        return null;
    }

    @Override
    public List queryAddressInfo(String code) {
        String configCode = ConfigsCodeConst.DB_EMPI;

        String procName = "usp_StatisticsAreaAll";
        Map paras = new HashMap();
        paras.put("in_code",code);

        Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode).doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(), procName, paras, true);
        if (result != null && result.size() > 0) {
            String resultKey = "#result-set-1";
            if (result.get(resultKey) != null) {
                List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get(resultKey);
                return resultSet;
            }
        }
        return null;
    }

    private Map GetCountOverViewByWDK() {
        Map map = dataCenterIndexService.GetCDCOverView("gxwd");
        Long gxwdCount = 0L;
        Long datasetCount = 0L;
        Long sysCount = 0L;
        if (map != null) {
            gxwdCount = MathUtils.parseLong(map.get("gxwdcount"));
        }

        String configCode = ConfigsCodeConst.DB_HLHT;
        Map paras = new HashMap();
        String procName = null;
        procName = "sp_bzsjj_count";
        paras.put("clsj", null);
        Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode).doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(), procName, paras, true);
        if (result != null && result.size() > 0) {
            String resultKey = "#result-set-1";
            if (result.get(resultKey) != null) {
                List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get(resultKey);
                if (resultSet != null && resultSet.size() > 0) {
                    map = resultSet.get(0);
                    if (map != null) {
                        datasetCount = MathUtils.parseLong(map.get("sjjcount"));
                    }
                }
            }
        }

        paras.clear();
        configCode = ConfigsCodeConst.DB_CM;
        procName = "sp_hlht_sercercount";
        result = CommonDataService.getDataAccess(configsService, configCode).doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(), procName);
        if (result != null && result.size() > 0) {
            String resultKey = "#result-set-1";
            if (result.get(resultKey) != null) {
                List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get(resultKey);
                if (resultSet != null && resultSet.size() > 0) {
                    map = resultSet.get(0);
                    if (map != null) {
                        sysCount = MathUtils.parseLong(map.get("servercount"));
                    }
                }
            }
        }

        result = new HashMap();
        result.put("gxwdCount", gxwdCount);
        result.put("sjjCount", datasetCount);
        result.put("sysCount", sysCount);
        return result;
    }

/*    @Override
    public List queryTimePeriodStatistic(String key) {
        //暂时将HLHT配置作为第三个大屏的共享文档存储
        String configCode = ConfigsCodeConst.DB_HLHT;//.DB_CDR.getCode();

        String procName = "usp_getdata_jkdp";//"sp_gxwd_sjfx";
        *//*Map paras = new HashMap();
        paras.put("cycletype",cycletype);*//*

        Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode).doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(), procName);
        if (result != null && result.size() > 0) {
            //String resultKey = "#result-set-2";
            if (result.get(key) != null) {
                List<LinkedHashMap<String, Object>> resultSet = (List<LinkedHashMap<String, Object>>) result.get(key);
                return resultSet;
            }
        }
        return null;
    }*/
@Override
public List queryTimePeriodStatistic(String cycletype) {
    String configCode = ConfigsCodeConst.DB_CDR;

    String procName = "sp_gxwd_sjfx";
    Map paras = new HashMap();
    paras.put("cycletype", cycletype);

    Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode).doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(), procName, paras, true);
    if (result != null && result.size() > 0) {
        String resultKey = "#result-set-1";
        if (result.get(resultKey) != null) {
            List<LinkedHashMap<String, Object>> resultSet = (List<LinkedHashMap<String, Object>>) result.get(resultKey);
            return resultSet;
        }
    }
    return null;
}
}
