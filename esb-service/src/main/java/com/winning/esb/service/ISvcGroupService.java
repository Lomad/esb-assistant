package com.winning.esb.service;

import com.winning.esb.model.SvcGroupModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.model.common.TreeModel;

import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 2017/8/17.
 */
public interface ISvcGroupService {
    String save(SvcGroupModel obj);

    String delete(List<Integer> idList);

    SvcGroupModel getByID(Integer id);

    CommonObject query(Map map);

    /**
     * 获取ID与Name对应的简单队列列表
     */
    List<SimpleObject> listIdName();

    List<TreeModel> createTreeByGroup(Map<String, Object> map);
}