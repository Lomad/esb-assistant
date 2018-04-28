package com.winning.esb.dao;

import com.winning.esb.model.SvcUrlModel;
import com.winning.esb.model.common.CommonObject;

import java.util.List;
import java.util.Map;

public interface ISvcUrlDao {
    void insert(SvcUrlModel obj);

    void update(SvcUrlModel obj);

    void delete(Integer id);

    CommonObject query(Map map);

    List<SvcUrlModel> getByID(List<Integer> idList);

    SvcUrlModel getByUrl(String url);

    Integer getMaxId();
}
