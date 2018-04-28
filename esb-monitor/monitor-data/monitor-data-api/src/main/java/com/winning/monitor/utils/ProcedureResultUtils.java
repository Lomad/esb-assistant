package com.winning.monitor.utils;

import com.winning.monitor.data.api.largerScreen.entity.DatabaseInfoVO;

import java.util.List;
import java.util.Map;

/**
 * @Author Lemod
 * @Version 2018/1/19
 */
public class ProcedureResultUtils {

    private static final String RESULT_KEY = "#result-set-1";

    /**
     * 处理存储过程返回结果，获取对应的Map
     *
     * @param result 存储过程返回结果
     * @return map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> dealProcedureForMap(Map<String, Object> result) {
        if (result != null && result.size() > 0) {
            if (result.get(RESULT_KEY) != null) {
                List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get(RESULT_KEY);
                if (resultSet != null && resultSet.size() > 0) {
                    return resultSet.get(0);
                }
            }
        }
        return null;
    }

    /**
     * 处理存储过程返回结果，获取对应的List of Map
     *
     * @param result 存储过程返回结果
     * @return map
     */
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> dealProcedureForList(Map<String, Object> result) {
        if (result != null && result.size() > 0) {
            if (result.get(RESULT_KEY) != null) {
                return (List<Map<String, Object>>) result.get(RESULT_KEY);
            }
        }
        return null;
    }

    /**
     * 处理CPU指标
     * @param map 存储返回值
     * @param databaseInfoVO 各类装载对象
     */
    public static void dealCPUMapResult(Map<String, Object> map, DatabaseInfoVO databaseInfoVO) {
        if (map != null) {
            Double SQLProcessUtilization = MathUtils.parseDouble(map.get("SQLProcessUtilization"));
            Double OtherProcessUtilization = MathUtils.parseDouble(map.get("OtherProcessUtilization"));
            Double cpuPercent = MathUtils.round(MathUtils.add(SQLProcessUtilization, OtherProcessUtilization), 0);
            databaseInfoVO.setCpuPercent(cpuPercent.toString());
        }
    }

    /**
     * 处理数据抽取量指标
     * @param map 存储返回值
     * @param databaseInfoVO 各类指标装载对象
     */
    public static void dealExtractDBNumberMapResult(Map<String, Object> map, DatabaseInfoVO databaseInfoVO){
        if (map != null) {
            Double nincredata = MathUtils.parseDouble(map.get("nincredata"));
            Double cg = MathUtils.parseDouble(map.get("cg"));
            Double sb = MathUtils.parseDouble(map.get("sb"));
            if (nincredata != null) {
                databaseInfoVO.setDataNumber(nincredata.toString());
            }
            if (cg != null) {
                databaseInfoVO.setDataNumberSuccess(cg.toString());
            }
            if (sb != null) {
                databaseInfoVO.setDataNumberFailure(sb.toString());
            }
        }
    }

    /**
     * 处理数据库磁盘指标
     * @param map 存储返回结果
     * @param databaseInfoVO 装载对象
     */
    public static void dealDBSizeMapResult(Map<String, Object> map, DatabaseInfoVO databaseInfoVO){
        if (map != null) {
            Double diskSize = MathUtils.parseDouble(map.get("total_disk_size_mb"));
            Double diskFreeSize = MathUtils.parseDouble(map.get("free_disk_size_mb"));
            Double dbSize = MathUtils.parseDouble(map.get("total_db_size_mb"));
            Double diskUsed = null;

            if (diskSize != null && diskFreeSize != null) {
                diskUsed = diskSize - diskFreeSize;
                databaseInfoVO.setDiskSize(diskSize.toString());
                databaseInfoVO.setDiskUsed(diskUsed.toString());

                if (dbSize != null) {
                    databaseInfoVO.setDbSize(dbSize.toString());
                    Double dbSizePercent = MathUtils.round(dbSize / diskSize * 100, 0);
                    databaseInfoVO.setDbSizePercent(dbSizePercent.toString());
                }
            }else {
                databaseInfoVO.setDiskSize("0");
                databaseInfoVO.setDiskUsed("0");
            }

            if (diskUsed != null) {
                Double diskSizePercent = MathUtils.round(diskUsed / diskSize * 100, 0);
                databaseInfoVO.setDiskSizePercent(diskSizePercent.toString());
            }
        }
    }

    /**
     * 处理内存指标
     * @param map 存储返回值
     * @param databaseInfoVO 装载对象
     */
    public static void dealMemoryMapResult(Map<String, Object> map, DatabaseInfoVO databaseInfoVO) {
        if (map != null) {
            Double totalMemorySize = MathUtils.parseDouble(map.get("Physical Memory_MB"));
            Double freeMemorySize = MathUtils.parseDouble(map.get("Available_Memory_MB"));

            if (totalMemorySize != null && freeMemorySize != null) {
                Double usedMemorySize = MathUtils.sub(totalMemorySize, freeMemorySize);

                Double memoryPercent = MathUtils.round(usedMemorySize / totalMemorySize * 100, 0);
                databaseInfoVO.setMemoryPercent(memoryPercent.toString());
            }
        }
    }

}
