package com.winning.esb.dao;

import com.winning.esb.model.SvcGroupModel;
import com.winning.esb.model.common.CommonObject;

import java.util.Map;

/**
 * Created by xuehao on 2017/8/17.
 */
public interface ISvcGroupDao {
    void insert(SvcGroupModel obj);

    void update(SvcGroupModel obj);

    void delete(Integer id);

    SvcGroupModel getByID(Integer id);

    CommonObject query(Map map);
}