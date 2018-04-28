package com.winning.esb.service;

import com.winning.esb.model.SvcStructureModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.TreeModel;
import com.winning.esb.model.enums.SvcInfoEnum;
import com.winning.esb.model.ext.SvcStructureExtModel;
import com.winning.esb.service.msg.MsgException;
import com.winning.esb.utils.ListUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 2017/8/9.
 */
public interface ISvcStructureService {
    /**
     * 导入其他服务的结构
     */
    String importStruncture(Integer sid, Integer direction, Integer sidFrom, Integer directionFrom);

    /**
     * 上传服务结构
     */
    String upload(Integer sid, String msgType, Integer direction, String rawContent);

    //将上传的消息结构保存到数据库
    void insertForUpload(Integer sid, Integer direction, SvcStructureExtModel extModel);

    String save(SvcStructureModel obj);

    String updateWhenDrop(List<SvcStructureModel> objs);

    String delete(Integer id);

    /**
     * 根据服务ID列表删除服务参数
     */
    String deleteBySid(Integer sid);

    /**
     * 根据服务ID列表删除服务参数
     */
    String deleteBySid(List<Integer> sidList);

    /**
     * 根据服务ID获取其结构明细
     */
    List<SvcStructureModel> queryBySvcID(Integer sid, Integer direction);

    /**
     * 获取结果标志节点
     */
    SvcStructureModel getResultNode(Integer sid);

    /**
     * 获取结果标志节点
     */
    SvcStructureModel getResultNode(Integer id, Integer sid);

    /**
     * 获取结果标志节点路径，使用点号分隔
     */
    String getResultNodePath(Integer sid);

    /**
     * 根据父级ID获取其子节点的最大排序
     */
    Integer getMaxOrderNumByID(Integer pid);

//    /**
//     * 创建树
//     */
//    List<TreeModel> createJsTree(Integer sid);

    /**
     * 创建树(适用于ztree.js)
     */
    List<TreeModel> createZTree(Integer sid, Integer direction, Integer grantID);

    /**
     * 导出服务结构，并返回下载地址
     *
     * @param returnType          0 - 返回下载地址， 1 - 返回下载内容
     * @param wrapperDataProtocal 是否使用数据协议规范封装(对HL7消息无效)
     */
    String export(Integer sid, Integer direction, String msgType, Integer returnType, int valueType,
                  Boolean wrapperDataProtocal) throws MsgException;

    /**
     * 导出服务结构
     */
    String exportMsg(Integer sid, Integer direction, String msgType, int valueType) throws MsgException;

    /**
     * 检测代码是否存在
     */
    boolean existCode(String code, Integer pid, Integer id, Integer direction, Integer sid);

    SvcStructureModel queryById(Integer id);

    /**
     * 获取数据协议模版
     */
    Map<String, Object> getDataProtocalTemplate(Integer protocalCode, String msgType);

    /**
     * 将list转为map
     */
    Map<Integer, SvcStructureModel> listToMap(List<SvcStructureModel> list);

    String retrieveNodePath(SvcStructureModel currentNode, Map<Integer, SvcStructureModel> map, String path);

    String retrieveNodeXmlPath(SvcStructureModel currentNode, Map<Integer, SvcStructureModel> map, String path);

    /**
     * 检测消息类型（JSON、XML、HL7）
     */
    SvcInfoEnum.MsgTypeEnum checkMsgType(String msg);

    /**
     * 判断数据平台协议规范（支持XML和JSON格式）
     */
    SvcInfoEnum.DataProtocalEnum checkEsbDataProtocal(SvcStructureExtModel extModel) throws Exception;

    /**
     * 判断HL7协议规范
     */
    SvcInfoEnum.DataProtocalEnum checkEsbHL7(SvcStructureExtModel extModel);

    CommonObject query(Map map);

}