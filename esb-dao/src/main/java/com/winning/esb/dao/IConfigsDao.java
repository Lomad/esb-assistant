package com.winning.esb.dao;

import com.winning.esb.model.ConfigsModel;
import com.winning.esb.model.common.CommonObject;

import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 17/07/27.
 */
public interface IConfigsDao {

    CommonObject query(Map map);

    void insert(ConfigsModel obj);

    String editValue(ConfigsModel obj);

    /**
     * 根据参数代码获取配置对象
     * @param code  支持两种类型：String和List<String>
     */
    List<ConfigsModel> getByCode(Object code);

    void delete(String code);
}
