package com.winning.esb.dao;

import com.winning.esb.model.GrantSvcStructureModel;

import java.util.List;

public interface IGrantSvcStructureDao {
    void insert(List<GrantSvcStructureModel> list);

    void delete(List<Integer> idList);

    /**
     * 根据授权ID删除字段
     */
    void deleteByGid(Integer gid);

    List<GrantSvcStructureModel> queryByGid(Integer gid);

}