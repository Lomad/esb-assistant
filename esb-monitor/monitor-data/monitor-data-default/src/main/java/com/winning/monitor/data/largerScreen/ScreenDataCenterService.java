package com.winning.monitor.data.largerScreen;

import com.winning.esb.model.enums.ConfigsCodeConst;
import com.winning.esb.service.IConfigsService;
import com.winning.monitor.data.api.largerScreen.IScreenDataCenterService;
import com.winning.monitor.data.api.largerScreen.entity.DatabaseInfoVO;
import com.winning.monitor.utils.DataSourceUtils;
import com.winning.monitor.utils.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.winning.monitor.utils.ProcedureResultUtils.*;

/**
 * Created by xuehao on 17/08/03.
 */
@Service
public class ScreenDataCenterService implements IScreenDataCenterService {

    @Autowired
    private IConfigsService configsService;

    @Override
    public DatabaseInfoVO getDataBaseInfo(String dbName) {
        DatabaseInfoVO databaseInfoVO = new DatabaseInfoVO();
        Map<String, Object> map = ("WDK".equals(dbName)) ?
                this.GetDBCONFIG("CDR") : this.GetDBCONFIG(dbName);
        if (map != null) {
            databaseInfoVO.setIp(String.valueOf(map.get("serveraddress")));
            databaseInfoVO.setDbType(DataSourceUtils.transferToSqlserver(String.valueOf(map.get("dbtype"))));
        }

        // TODO: 2018/3/16 若备份库换成医院其他业务系统库，置零功能需要去掉，且保证相应的存储返回结果格式
        //备份库没有抽取量，直接置为0
        if (dbName.equals("BU")){
            databaseInfoVO.setDataNumber("0");
            databaseInfoVO.setDataNumberSuccess("0");
            databaseInfoVO.setDataNumberFailure("0");
        }else {
            map = this.GetDBnum(dbName);
            dealExtractDBNumberMapResult(map, databaseInfoVO);
        }

        map = ("WDK".equals(dbName)) ? this.GetDBsize("CDR") : this.GetDBsize(dbName);
        dealDBSizeMapResult(map, databaseInfoVO);

        map = ("WDK".equals(dbName)) ? this.GetCPUSize("CDR") : this.GetCPUSize(dbName);
        dealCPUMapResult(map, databaseInfoVO);

        map = ("WDK".equals(dbName)) ? this.GetMemorySize("CDR") : this.GetMemorySize(dbName);
        dealMemoryMapResult(map, databaseInfoVO);

        databaseInfoVO.setDbName(dbName);
        return databaseInfoVO;
    }

    @Override
    public Map countByOverView() {
        Map<String, Object> result = new HashMap<>();
        //CDR年限、共享文档数、临床文档数
        Long cdrYear = null, gxwdCount = null, lcwdCount = null;
        //CDR接入系统
        List cdrSyss = null;

        Map map = this.GetCDCOverView("year");
        if (map != null) {
            cdrYear = MathUtils.parseLong(map.get("yearstr"));
        }
        map = this.GetCDCOverView("lcwd");
        if (map != null) {
            lcwdCount = MathUtils.parseLong(map.get("lcwdcount"));
        }
        map = this.GetCDCOverView("gxwd");
        if (map != null) {
            gxwdCount = MathUtils.parseLong(map.get("gxwdcount"));
        }

        List list = this.GetSysList();
        if (list != null) {
            cdrSyss = list;
        }

        result.put("cdrYear", cdrYear);
        result.put("cdrSyss", cdrSyss);
        result.put("lcwdCount", lcwdCount);
        result.put("gxwdCount", gxwdCount);
        return result;
    }

    /**
     * 查询大屏作业监控指标
     *
     * @return map
     */
    @Override
    public Map queryJobMonitorIndex() {
        Map<String, Object> primary = new HashMap<>();
        try {
            String configCode = ConfigsCodeConst.DB_CM;

            String procName = "sp_CMCockpit";
            Map<String, Object> paras = new HashMap<>();
            paras.put("datetype", "today");

            Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode).doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(), procName, paras, true);
            primary = dealProcedureForMap(result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (primary != null) {
            int jobMonitorAll = (int) primary.get("zyjk_alldata");
            int errorJobMonitor = (int) primary.get("zyjk_errordata");
            String jobPercent = getPercentByString(jobMonitorAll, errorJobMonitor);
            primary.put("jobMonitor", jobPercent);

            int dataCheckAll = (int) primary.get("sjyz_alldata");
            int errorDataCheck = (int) primary.get("sjyz_errordata");
            String dataCheckPercent = getPercentByString(dataCheckAll, errorDataCheck);
            primary.put("dataCheck", dataCheckPercent);

            int interfaceCheckAll = (int) primary.get("jkyz_alldata");
            int errorInterfaceCheck = (int) primary.get("jkyz_errordata");
            String interfacePercent = getPercentByString(interfaceCheckAll, errorInterfaceCheck);
            primary.put("interfaceCheck", interfacePercent);
        }
        return primary;
    }

    private String getPercentByString(int all, int error) {
        Double percent = (all - error) * 1.0 / all * 100.0;
        return String.valueOf(percent.intValue()) + "%";
    }

    @Override
    public List GetSysList() {
        try {
            String configCode = ConfigsCodeConst.DB_CM;

            String procName = "sp_get_into_server";
            Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode).doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(), procName);
            return dealProcedureForList(result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    @Override
    public DatabaseInfoVO getOdrInfo() {
        DatabaseInfoVO databaseInfoVO = new DatabaseInfoVO();
        String configCode = ConfigsCodeConst.DB_ODR;

        String procName = "usp_odr_dbsize";
        Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode).doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(), procName);
        if (result != null && result.size() > 0) {
            String resultKey = "#result-set-1";
            if (result.get(resultKey) != null) {
                List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get(resultKey);
                if (resultSet != null && resultSet.size() > 0) {
                    Map map = resultSet.get(0);
                    if (map != null) {
                        databaseInfoVO.setIp(String.valueOf(map.get("local_net_address")));
                        databaseInfoVO.setDbType(String.valueOf(map.get("OS")));
                        databaseInfoVO.setDataNumber(String.valueOf(map.get("zbjl")));
                        databaseInfoVO.setCpuPercent(String.valueOf(map.get("cpu")));
                        Double totalMemorySize = MathUtils.parseDouble(map.get("memorySize"));
                        Double usedMemorySize = MathUtils.parseDouble(map.get("memorySizeUsed"));
                        if (usedMemorySize != null) {
                            Double memoryPercent = MathUtils.round(usedMemorySize / totalMemorySize * 100, 0);
                            databaseInfoVO.setMemoryPercent(memoryPercent.toString());
                        }

                        Double diskSize = MathUtils.parseDouble(map.get("total_disk_size_mb"));
                        Double diskFreeSize = MathUtils.parseDouble(map.get("free_disk_size_mb"));
                        Double dbSize = MathUtils.parseDouble(map.get("total_db_size_mb"));
                        Double diskUsed = diskSize - diskFreeSize;
                        databaseInfoVO.setDiskSize(diskSize.toString());
                        databaseInfoVO.setDiskUsed(diskUsed.toString());

                        if (diskSize != null) {
                            if (diskUsed != null) {
                                Double diskSizePercent = MathUtils.round(diskUsed / diskSize * 100, 0);
                                databaseInfoVO.setDiskSizePercent(diskSizePercent.toString());
                            }
                            if (dbSize != null) {
                                databaseInfoVO.setDbSize(dbSize.toString());
                                Double dbSizePercent = MathUtils.round(dbSize / diskSize * 100, 0);
                                databaseInfoVO.setDbSizePercent(dbSizePercent.toString());
                            }
                        }
                        if (map.containsKey("zs")) {
                            int allCount = (int) map.get("zs");
                            databaseInfoVO.setDataNumber(String.valueOf(allCount));
                        }
                        if (map.containsKey("cg")) {
                            int successCount = (int) map.get("cg");
                            databaseInfoVO.setDataNumberSuccess(String.valueOf(successCount));
                        }
                        if (map.containsKey("sb")) {
                            int failCount = (int) map.get("sb");
                            databaseInfoVO.setDataNumberFailure(String.valueOf(failCount));
                        }
                    }
                }
            }
        }
        return databaseInfoVO;
    }

    @Override
    public DatabaseInfoVO getODRInfo(String dbName) {
        DatabaseInfoVO databaseInfoVO = new DatabaseInfoVO();
        String configCode = ConfigsCodeConst.DB_ODR;

        Map<String, Object> paras = new HashMap<>();
        paras.put("dbname", dbName);

        Map<String, Object> map = this.GetDBCONFIG(dbName);
        if (map != null) {
            databaseInfoVO.setIp(String.valueOf(map.get("serveraddress")));
            databaseInfoVO.setDbType(DataSourceUtils.transferToSqlserver(String.valueOf(map.get("dbtype"))));
        }

        String procName = "sp_get_db_etlcount";
        Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode).doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(), procName, paras, true);
        map = dealProcedureForMap(result);
        dealExtractDBNumberMapResult(map, databaseInfoVO);

        String procForDisk = "sp_get_disk_size";
        Map<String, Object> resultOfDisk = CommonDataService.getDataAccess(configsService, configCode).doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(), procForDisk, paras, true);
        map = dealProcedureForMap(resultOfDisk);
        dealDBSizeMapResult(map, databaseInfoVO);

        String procForCPU = "sp_get_cpu";
        Map<String, Object> resultOfCPU = CommonDataService.getDataAccess(configsService, configCode)
                .doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(),
                        procForCPU, paras, true);
        map = dealProcedureForMap(resultOfCPU);
        dealCPUMapResult(map, databaseInfoVO);

        String procForMemory = "sp_get_memory";
        Map<String, Object> resultOfMemory = CommonDataService.getDataAccess(configsService, configCode).doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(), procForMemory, paras, true);
        map = dealProcedureForMap(resultOfMemory);
        dealMemoryMapResult(map, databaseInfoVO);

        return databaseInfoVO;
    }

    @Override
    public Map GetCDCOverView(String type) {
        try {
            String configCode = ConfigsCodeConst.DB_CDR;

            String procName = "sp_get_cdr_data";
            Map<String, Object> paras = new HashMap<>();
            paras.put("type", type);
            Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode)
                    .doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(),
                            procName, paras, true);

            return dealProcedureForMap(result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public List GetCDCcount(String selecttype) {
        String configCode = ConfigsCodeConst.DB_CM;

        String procName = "sp_cm_cdc_count";
        Map<String, Object> paras = new HashMap<>();
        paras.put("orgcode", "");
        paras.put("selecttype", selecttype);
        Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode)
                .doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(),
                        procName, paras, true);

        return dealProcedureForList(result);
    }

    @Override
    public Map<String, Object> GetDBCONFIG(String dbname) {
        String configCode = ConfigsCodeConst.DB_CM;

        String procName = "sp_get_dbconfig";
        Map<String, Object> paras = new HashMap<>();
        paras.put("dbname", dbname);
        Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode)
                .doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(),
                        procName, paras, true);

        return dealProcedureForMap(result);
    }

    @Override
    public Map<String, Object> GetDBnum(String dbname) {
        String configCode = ConfigsCodeConst.DB_CM;
        if ("LCWD".equalsIgnoreCase(dbname)) {
            configCode = ConfigsCodeConst.DB_CDR;
        }

        String procName = "sp_get_db_etlcount";
        Map<String, Object> paras = new HashMap<>();
        if ("WDK".equalsIgnoreCase(dbname)) {
            paras.put("dbname", "LCWD");
        } else {
            paras.put("dbname", dbname);
        }
        Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode)
                .doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(),
                        procName, paras, true);

        return dealProcedureForMap(result);
    }

    @Override
    public Map<String, Object> GetDBsize(String dbname) {
        String configCode = ConfigsCodeConst.DB_DW;
        if ("CDR".equalsIgnoreCase(dbname)) {
            configCode = ConfigsCodeConst.DB_CDR;
        } else if ("BU".equalsIgnoreCase(dbname)) {
            configCode = ConfigsCodeConst.DB_BU;
        }

        String procName = "sp_get_disk_size";
        Map<String, Object> paras = new HashMap<>();
        paras.put("dbname", dbname);
        Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode)
                .doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(),
                        procName, paras, true);

        return dealProcedureForMap(result);
    }

    @Override
    public Map<String, Object> GetLCWDtablesize() {
        String configCode = ConfigsCodeConst.DB_CDR;

        String procName = "sp_spaceused_lcwd";
        Map<String, Object> paras = new HashMap<>();
        paras.put("type", "U");
        paras.put("updateusage", "false");
        Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode)
                .doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(),
                        procName, paras, true);

        return dealProcedureForMap(result);
    }

    @Override
    public Map<String, Object> GetCPUSize(String dbname) {
        String configCode = ConfigsCodeConst.DB_DW;
        if ("CDR".equalsIgnoreCase(dbname)) {
            configCode = ConfigsCodeConst.DB_CDR;
        } else if ("BU".equalsIgnoreCase(dbname)) {
            configCode = ConfigsCodeConst.DB_BU;
        }
        String procName = "sp_get_cpu";
        Map<String, Object> paras = new HashMap<>();
        paras.put("dbname", dbname);
        Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode)
                .doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(),
                        procName, paras, true);

        return dealProcedureForMap(result);
    }

    @Override
    public Map<String, Object> GetMemorySize(String dbname) {
        String configCode = ConfigsCodeConst.DB_DW;
        if ("CDR".equalsIgnoreCase(dbname)) {
            configCode = ConfigsCodeConst.DB_CDR;
        } else if ("BU".equalsIgnoreCase(dbname)) {
            configCode = ConfigsCodeConst.DB_BU;
        }
        String procName = "sp_get_memory";
        Map<String, Object> paras = new HashMap<>();
        paras.put("dbname", dbname);
        Map<String, Object> result = CommonDataService.getDataAccess(configsService, configCode)
                .doExecuteStoredProcedure(CommonDataService.getDataSourceVOMap().get(configCode).getJdbcTemplate(),
                        procName, paras, true);

        return dealProcedureForMap(result);
    }
}