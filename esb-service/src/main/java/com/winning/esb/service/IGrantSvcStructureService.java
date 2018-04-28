package com.winning.esb.service;

import com.winning.esb.model.GrantSvcStructureModel;

import java.util.List;

public interface IGrantSvcStructureService {
    String insert(List<GrantSvcStructureModel> list);

    String delete(List<Integer> idList);

    /**
     * 根据授权ID删除字段
     */
    String deleteByGid(Integer gid);

    List<GrantSvcStructureModel> queryByGid(Integer gid);

    /**
     * 根据授权ID获取已授权的结构ID列表
     */
    List<Integer> listSsidByGid(Integer gid);

}