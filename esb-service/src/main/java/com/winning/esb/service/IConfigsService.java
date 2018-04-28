package com.winning.esb.service;

import com.winning.esb.model.ConfigsModel;
import com.winning.esb.model.common.CommonObject;

import java.util.List;
import java.util.Map;

/**
 *
 * @author xuehao
 * @date 17/07/27
 */
public interface IConfigsService {

    CommonObject query(Map map);

    void insert(ConfigsModel obj);

    String editValue(ConfigsModel obj);

    /**
     * 根据参数代码获取配置对象
     */
    ConfigsModel getByCode(String code);

    /**
     * 根据参数代码获取配置对象
     */
    List<ConfigsModel> getByCode(List<String> codeList);

    /**
     * 根据参数代码获取配置值
     */
    String getValueByCode(String code);

    /**
     * 根据参数代码获取配置值(key - 代码， value - 配置值)
     */
    Map<String, String> getValueByCode(List<String> codeList);

    void delete(String code);



    //以下函数获取特定配置值

    /**
     * 获取【监控概览显示的系统数量最大值】
     */
    int getMonitorOverviewShowSysUpper();
    /**
     * 获取【监控概览显示无数据的系统】
     */
    int getMonitorOverviewShowSysNoData();
    /**
     * 获取ESB测试的地址
     */
    String getEsbTestUrl();

    /**
     * 保存ESB配置的参数
     */
    String save(Map map);

    /**
     * 获取ESB配置参数
     */
    String getMiddlewareInfo();
}