package com.winning.monitor.data.api.largerScreen;

import com.winning.monitor.data.api.largerScreen.entity.DatabaseInfoVO;

import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 17/07/27.
 * 数据中心首页获取的
 */
public interface IScreenDataCenterService {
    DatabaseInfoVO getOdrInfo();

    /**
     * 福建省立 第三方ODR库
     * @return
     */
    DatabaseInfoVO getODRInfo(String dbname);

    Map GetCDCOverView(String type);

    //CDC当天抽取数据量
    //selecttype：查询结果类型（1、为空值则CDC当天抽取量；2、为‘all’则为CDC分系统抽取情况）
    //selecttype=‘’则字段为 xaxis 时间，nincredata 抽取量
    //selecttype=‘all’则字段为 sourcename系统名称，nincredata 抽取量
    List GetCDCcount(String selecttype);


    //获取数据库IP和类型
    //dbname:ODS CDR
    Map GetDBCONFIG(String dbname);

    //获得数据库抽取量
    //return：nincredata 抽取量，cg 成功数，sb 失败数
    Map GetDBnum(String dbname);

    //获取磁盘信息和数据库空间
    //return:DiskName磁盘名称，total_disk_size_mb 磁盘总空间，free_disk_size_mb 可用空间，free_space_percent 可用空间暂比 ，total_db_size_mb 数据库使用空间
    Map GetDBsize(String dbname);

    //获得LCWD表空间
    //return:name 表名，datanum 数据值
    Map GetLCWDtablesize();

    //概览页面TOP
    Map countByOverView();

    //第二个大屏最后三个指标-作业监控、数据校验、接口校验
    Map queryJobMonitorIndex();

    //接入系统列表
    List GetSysList();

    DatabaseInfoVO getDataBaseInfo(String dbName);

    Map GetCPUSize(String sys);

    Map GetMemorySize(String sys);
}
