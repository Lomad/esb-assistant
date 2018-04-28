package com.winning.esb.service;

import com.winning.esb.model.SvcUrlModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @author xuehao
 */
public interface ISvcUrlService {
    String save(SvcUrlModel obj);
    String delete(List<Integer> idList);

    CommonObject query(Map map);
    /**
     * 根据ESB代理标志获取地址列表
     */
    List<SvcUrlModel> queryByEsbAgent(Integer esbAgent);

    /**
     * 获取地址ID与URL(或别名)的列表
     */
    List<SimpleObject> listIdName();
    /**
     * 根据ESB代理标志获取地址ID与URL(或别名)的列表
     */
    List<SimpleObject> listIdName(Integer esbAgent);

    Map<Integer, SvcUrlModel> map();

    Map<Integer, SvcUrlModel> map(List<SvcUrlModel> list);

    SvcUrlModel getByID(Integer id);

    List<SvcUrlModel> getByID(List<Integer> idList);

    SvcUrlModel getByUrl(String url);

    String linkTest(SvcUrlModel obj);

    List<SimpleObject> getSvcTypeList();

    Integer queryIdMax();

    /**
     * 扫描地址是否可用
     */
    void scanUrl() throws ParseException;

    /**
     * 检测服务地址是否有错误
     */
    String checkUrl(String url);

    /**
     * 检测服务地址是否有错误
     */
    String checkUrl(Integer svcType, String url);

    /**
     * 检测服务地址状态
     */
    int checkUrlStatus(String url);
    /**
     * 检测服务地址状态
     */
    int checkUrlStatus(Integer svcType, String url);

    /**
     * 获取URL地址类型
     */
    Integer getUrlType(String url);
}