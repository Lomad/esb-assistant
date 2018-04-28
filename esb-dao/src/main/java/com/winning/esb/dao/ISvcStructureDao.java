package com.winning.esb.dao;

import com.winning.esb.model.SvcStructureModel;
import com.winning.esb.model.common.CommonObject;

import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 2017/8/9.
 */
public interface ISvcStructureDao {
    Integer insert(SvcStructureModel obj);

    void update(SvcStructureModel obj);

    void updateWhenDrop(List<SvcStructureModel> objs);

    void delete(Integer id);

    void delete(Integer sid, Integer direction);

    CommonObject query(Map<String, Object> map);

    /**
     * 根据服务ID列表删除服务参数
     */
    void deleteBySid(List<Integer> sidList);

    /**
     * 根据服务ID获取其结构明细
     */
    List<SvcStructureModel> queryBySvcID(Integer sid, Integer direction);

    List<SvcStructureModel> queryById(Integer id);

    /**
     * 获取结果标志节点
     */
    SvcStructureModel getResultNode(Integer id, Integer sid);

    /**
     * 根据父级ID获取其子节点的最大排序
     */
    Integer getMaxOrderNumByID(Integer pid);

    /**
     * 检测代码是否存在
     */
    //boolean existCode(String code, Integer pid, Integer id, Integer direction);

    boolean existCode(String code, Integer pid, Integer id, Integer direction, Integer sid);
}
