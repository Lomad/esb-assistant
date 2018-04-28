package com.winning.esb.service.impl;

import com.winning.esb.dao.ISvcStructureDao;
import com.winning.esb.model.SvcInfoModel;
import com.winning.esb.model.SvcStructureModel;
import com.winning.esb.model.ValueListModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.TreeModel;
import com.winning.esb.model.common.ZTreeModel;
import com.winning.esb.model.enums.AppInfoEnum;
import com.winning.esb.model.enums.SvcInfoEnum;
import com.winning.esb.model.enums.SvcStructureEnum;
import com.winning.esb.model.ext.SvcStructureExtModel;
import com.winning.esb.service.IGrantSvcStructureService;
import com.winning.esb.service.ISvcInfoService;
import com.winning.esb.service.ISvcStructureService;
import com.winning.esb.service.IValueListService;
import com.winning.esb.service.msg.IParser;
import com.winning.esb.service.msg.MsgException;
import com.winning.esb.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by xuehao on 2017/8/9.
 */
@Service
public class SvcStructureServiceImpl implements ISvcStructureService {
    @Autowired
    private ISvcStructureDao dao;
    @Autowired
    private ISvcInfoService svcInfoService;
    @Autowired
    private IGrantSvcStructureService grantSvcStructureService;
    @Autowired
    private IValueListService valueListService;

    /**
     * 数据协议的内容占位符
     */
    private final String DATA_PROTOCAL_CONTENT = "[TEMPLATE_CONTENT]";
    /**
     * 数据协议的内容占位符
     */
    private final String DATA_PROTOCAL_TRANCODE = "[TranCode]";
    /**
     * 机构代码的内容占位符
     */
    private final String DATA_PROTOCAL_ORGID = "[OrgId]";
    /**
     * 业务系统代码的内容占位符
     */
    private final String DATA_PROTOCAL_APPID = "[AppId]";
    /**
     * 数据协议的内容占位符
     */
    private final String DATA_PROTOCAL_MESSAGEID = "[MessageId]";
    /**
     * 数据协议的内容占位符
     */
    private final String DATA_PROTOCAL_TIMESTAMP = "[Timestamp]";

    private Map<String, IParser> parserMap;

    @PostConstruct
    private void init() {
        parserMap = AppCtxUtils.getBeansOfType(IParser.class);
    }

    //事务控制
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String importStruncture(Integer sid, Integer direction, Integer sidFrom, Integer directionFrom) {
        String err;
        try {
            //获取节点树
            List<TreeModel> treeModels = createZTree(sidFrom, directionFrom, null);
            //获取候选值列表
            List<ValueListModel> valueListModels = valueListService.queryBySid(sidFrom, directionFrom);
            Map<Integer, List<ValueListModel>> valueListMap = new HashMap<>();  //key - 参数ID， value - 参数对应的候选值列表
            if (!ListUtils.isEmpty(valueListModels)) {
                Integer ssid;
                for (ValueListModel valueListModel : valueListModels) {
                    ssid = valueListModel.getSsid();
                    if (!valueListMap.containsKey(ssid)) {
                        valueListMap.put(ssid, new ArrayList<ValueListModel>());
                    }
                    valueListMap.get(ssid).add(valueListModel);
                }
            }
            //提取树中的对象
            List<SvcStructureExtModel> svcStructureExtModels = retriveObjectFromTree(treeModels, valueListMap);
            if (!ListUtils.isEmpty(svcStructureExtModels)) {
                //虚拟一个顶级节点
                SvcStructureExtModel svcStructureExtModel = SvcStructureExtModel.createVirtualRoot(svcStructureExtModels);
                //删除旧的结构
                dao.delete(sid, direction);
                //导入结构
                insertForUpload(sid, direction, svcStructureExtModel);
            }

            err = null;
        } catch (Exception ex) {
            err = ex.getMessage();
        }
        return err;
    }

    /**
     * 提取树中的对象
     */
    private List<SvcStructureExtModel> retriveObjectFromTree(List<TreeModel> treeModels, Map<Integer, List<ValueListModel>> valueListMap) {
        List<SvcStructureExtModel> children = new ArrayList<>();
        SvcStructureExtModel svcStructureExtModel;
        for (TreeModel model : treeModels) {
            svcStructureExtModel = new SvcStructureExtModel();
            svcStructureExtModel.setObj((SvcStructureModel) model.getMyData());
            svcStructureExtModel.setChildren(retriveObjectFromTree(model.getChildren(), valueListMap));
            svcStructureExtModel.setValueList(valueListMap.get(svcStructureExtModel.getObj().getId()));
            children.add(svcStructureExtModel);
        }
        return children;
    }

    //事务控制
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String upload(Integer sid, String msgType, Integer direction, String rawContent) {
        String err;
        try {
            if (StringUtils.isEmpty(msgType)) {
                return "消息格式不能为空！";
            }
            SvcInfoModel svcInfoModel = svcInfoService.getByID(sid);
            //将原始信息保存到服务信息表
            svcInfoService.updateRawContent(sid, direction, rawContent);
            //解析上传的原始消息结构
            IParser parser = parserMap.get(IParser.BEAN_PREFIX + msgType.toUpperCase());
            SvcStructureExtModel extModel = parser.decode(rawContent, true);
            //判断是否数据平台1.0、1.1规范，如果是，则只需导入body内容即可
            if (svcInfoModel.getDataProtocal() != null
                    && (SvcInfoEnum.DataProtocalEnum.ESB_1_0.getCode() == svcInfoModel.getDataProtocal().intValue()
                    || SvcInfoEnum.DataProtocalEnum.ESB_1_1.getCode() == svcInfoModel.getDataProtocal().intValue())) {
                setEsbDataProtocalBody(extModel);
            }
            //HL7默认值
            if ("hl7".equalsIgnoreCase(msgType)) {
                checkHL7DataProtocal(extModel);
            }
            //将上传的消息结构保存到数据库
            if (extModel != null) {
                //删除旧的结构
                dao.delete(sid, direction);
                //上传结构
                insertForUpload(sid, direction, extModel);
            }

            err = null;
        } catch (Exception ex) {
            err = ex.getMessage();
        }
        return err;
    }

    //设置数据平台1.0、1.1规范的Body内容
    private void setEsbDataProtocalBody(SvcStructureExtModel extModel) {
        List<SvcStructureExtModel> children = extModel.getChildren();
        if (!ListUtils.isEmpty(children) && children.size() == 2) {
            SvcStructureExtModel bodyModel;
            if ("Body".equalsIgnoreCase(children.get(1).getObj().getCode())) {
                bodyModel = children.get(1);
            } else if ("Body".equalsIgnoreCase(children.get(0).getObj().getCode())) {
                bodyModel = children.get(0);
            } else {
                bodyModel = null;
            }
            if (bodyModel != null) {
                extModel.setObj(null);
                extModel.setValueList(null);
                extModel.setChildren(bodyModel.getChildren());
            }
        }
    }

    //判断是否HL7规范
    private void checkHL7DataProtocal(SvcStructureExtModel extModel) {
        List<SvcStructureExtModel> objList = new ArrayList<>();
        List<SvcStructureExtModel> svcStructureExtModelList = extModel.getChildren();
        for (SvcStructureExtModel svcStructureExtModelList1 : svcStructureExtModelList) {
            List<SvcStructureExtModel> svcStructureExtModelList2 = svcStructureExtModelList1.getChildren();
            for (SvcStructureExtModel svcStructureExtModel : svcStructureExtModelList2) {
                if (svcStructureExtModel.getChildren() == null) {
                    objList.add(svcStructureExtModel);
                } else {
                    objList.addAll(svcStructureExtModel.getChildren());
                }
            }
        }
        for (SvcStructureExtModel objExt : objList) {
            SvcStructureModel obj = objExt.getObj();
            SvcStructureModel pObj = queryById(obj.getPid());
            if (obj != null && "MSH.1".equalsIgnoreCase(obj.getCode())) {
                obj.setValue_default("|");
            }
            if (obj != null && "MSH.2".equalsIgnoreCase(obj.getCode())) {
                obj.setValue_default("^~\\&");
            }
            if (pObj != null && "MSH.11".equalsIgnoreCase(pObj.getCode())) {
                pObj.setValue_default("P");
            }
            if (obj != null && "MSH.17".equalsIgnoreCase(obj.getCode())) {
                obj.setValue_default("CHN");
            }
        }
    }

    //将上传的消息结构保存到数据库
    @Override
    public void insertForUpload(Integer sid, Integer direction, SvcStructureExtModel extModel) {
        SvcStructureModel model = extModel.getObj();
        Integer id;
        if (model == null) {
            id = 0;
        } else {
            model.setSid(sid);
            model.setDirection(direction);
            //检测
            checkSvcStructure(model);
            //新增到数据库
            id = dao.insert(model);

            //将返回的参数ID赋值到ssid字段
            if (!ListUtils.isEmpty(extModel.getValueList())) {
                for (ValueListModel valueListModel : extModel.getValueList()) {
                    valueListModel.setSsid(id);
                }
                valueListService.insertAfterDelete(id, extModel.getValueList());
            }
        }
        if (!ListUtils.isEmpty(extModel.getChildren())) {
            for (SvcStructureExtModel child : extModel.getChildren()) {
                child.getObj().setPid(id);
                insertForUpload(sid, direction, child);
            }
        }
    }

    /**
     * 检测服务结构的字段长度等
     */
    private void checkSvcStructure(SvcStructureModel svcStructureModel) {
        String temp = svcStructureModel.getCode();
        int maxLen = 100;
        //判断代码长度，如果超过，则截取
        if (!StringUtils.isEmpty(temp) && temp.length() > maxLen) {
            svcStructureModel.setCode(temp.substring(0, maxLen));
            temp = svcStructureModel.getCode();
            if (StringUtils.getLen(temp) > maxLen) {
                svcStructureModel.setCode(temp.substring(0, maxLen / 2));
            }
        }
        //判断名称长度，如果超过，则截取
        temp = svcStructureModel.getName();
        maxLen = 100;
        if (!StringUtils.isEmpty(temp) && temp.length() > maxLen) {
            svcStructureModel.setName(temp.substring(0, maxLen));
        }
    }

    @Override
    public String save(SvcStructureModel obj) {
        String errInfo = "";
        if (StringUtils.isEmpty(obj.getCode())) {
            errInfo += "代码不能为空！ ";
        } else if (StringUtils.isEmpty(obj.getName())) {
            errInfo += "名称不能为空！ ";
        } else if (existCode(obj.getCode(), obj.getPid(), obj.getId(), obj.getDirection(), obj.getSid())) {
            errInfo += "该代码已存在（同级节点中的代码不能重复）！";
        }
        if (obj.getResult_mark() != null && obj.getResult_mark().intValue() == SvcStructureEnum.ResultMarkEnum.Yes.getCode()
                && StringUtils.isEmpty(errInfo)) {
            SvcStructureModel resultNode = getResultNode(obj.getId(), obj.getSid());
            if (resultNode != null) {
                errInfo += "已存在结果标志节点（" + resultNode.getCode() + " - " + resultNode.getName() + "）！";
            }
        }
        if (!StringUtils.isEmpty(obj.getValue_default())) {
            Integer data_type = obj.getData_type();
            String regex = SvcStructureEnum.DataTypeEnum.getRegex(data_type);
            if (!RegexUtils.match(regex, obj.getValue_default())) {
                errInfo += "默认值不符合数据类型！ ";
            }
        }
        if (StringUtils.isEmpty(errInfo)) {
            obj.setMtime(new Date());
            if (obj.getId() == null) {
                Integer maxOrderNum = getMaxOrderNumByID(obj.getPid());
                if (maxOrderNum == null) {
                    maxOrderNum = 0;
                }
                maxOrderNum++;
                obj.setOrder_num(maxOrderNum);
                obj.setCtime(obj.getMtime());
                dao.insert(obj);
            } else {
                dao.update(obj);
                //删除候选值
                if (obj.getResult_mark() != null && obj.getResult_mark().intValue() == SvcStructureEnum.ResultMarkEnum.No.getCode()) {
                    valueListService.delete(obj.getId());
                }
            }
        }
        return errInfo;
    }

    @Override
    public String updateWhenDrop(List<SvcStructureModel> objs) {
        if (!ListUtils.isEmpty(objs)) {
            for (SvcStructureModel obj : objs) {
                obj.setMtime(new Date());
            }
        }
        dao.updateWhenDrop(objs);
        return null;
    }

    @Override
    public String delete(Integer id) {
        dao.delete(id);
        return null;
    }

    @Override
    public String deleteBySid(Integer sid) {
        List<Integer> sidList = new ArrayList<>();
        sidList.add(sid);
        dao.deleteBySid(sidList);
        return null;
    }

    @Override
    public String deleteBySid(List<Integer> sidList) {
        dao.deleteBySid(sidList);
        return null;
    }

    @Override
    public List<SvcStructureModel> queryBySvcID(Integer sid, Integer direction) {
        return dao.queryBySvcID(sid, direction);
    }

    @Override
    public SvcStructureModel getResultNode(Integer sid) {
        return getResultNode(null, sid);
    }

    @Override
    public SvcStructureModel getResultNode(Integer id, Integer sid) {
        return dao.getResultNode(id, sid);
    }

    @Override
    public String getResultNodePath(Integer sid) {
        String result = "";
        List<SvcStructureModel> list = queryBySvcID(sid, SvcStructureEnum.DirectionEnum.Ack.getCode());
        if (!ListUtils.isEmpty(list)) {
            SvcStructureModel resultNode = null;
            for (SvcStructureModel model : list) {
                if (model.getResult_mark() != null && model.getResult_mark().intValue() == SvcStructureEnum.ResultMarkEnum.Yes.getCode()) {
                    resultNode = model;
                    break;
                }
            }
            if (resultNode != null) {
                Map<Integer, SvcStructureModel> map = listToMap(list);
                result = retrieveNodePath(resultNode, map, null);
            }
        }
        return result;
    }

    @Override
    public Integer getMaxOrderNumByID(Integer pid) {
        return dao.getMaxOrderNumByID(pid);
    }

    @Override
    public List<TreeModel> createZTree(Integer sid, Integer direction, Integer grantID) {
        List<SvcStructureModel> svcStructures = queryBySvcID(sid, direction);
        List<Integer> ssidList = grantSvcStructureService.listSsidByGid(grantID);
        List<TreeModel> resultList = createZTreeChildren(null, svcStructures, ssidList);
        return resultList;
    }

    /**
     * 创建树的子节点（ztree）
     */
    private List<TreeModel> createZTreeChildren(TreeModel parentTree, List<SvcStructureModel> svcStructures, List<Integer> ssidList) {
        List<TreeModel> resultList = new ArrayList<>();
        if (!ListUtils.isEmpty(svcStructures)) {
            SvcStructureModel parentObj;
            if (parentTree != null) {
                parentObj = (SvcStructureModel) parentTree.getMyData();
            } else {
                parentObj = null;
            }
            TreeModel treeModel;
            for (SvcStructureModel svcStructure : svcStructures) {
                if ((parentObj == null && svcStructure.getPid().intValue() != 0)
                        || (parentObj != null && parentObj.getId().intValue() != svcStructure.getPid().intValue())) {
                    continue;
                }

                treeModel = createZTreeNode(svcStructure, ssidList);
                if (parentTree != null) {
                    if (parentTree.getChildren() == null) {
                        parentTree.setChildren(new ArrayList<>());
                    }
                    parentTree.getChildren().add(treeModel);
                }
                //递归调用创建子节点
                createZTreeChildren(treeModel, svcStructures, ssidList);
                resultList.add(treeModel);
            }
        }
        return resultList;
    }

    /**
     * 创建树的节点
     */
    private TreeModel createZTreeNode(SvcStructureModel svcStructure, List<Integer> ssidList) {
        String showText, code, name, iconSkin = "param";
        code = svcStructure.getCode();
        name = svcStructure.getName();
//        boolean checked = !ListUtils.isEmpty(ssidList) && ssidList.contains(svcStructure.getId());
        showText = "";
        if (svcStructure.getRequired() != null && SvcStructureEnum.RequiredEnum.Yes.getCode() == svcStructure.getRequired().intValue()) {
//            showText = "<b style='font-size: inherit;'>" + code + "</b>";
            iconSkin = "param_require";
        }
        if (svcStructure.getIs_loop() != null && SvcStructureEnum.IsLoopEnum.Yes.getCode() == svcStructure.getIs_loop().intValue()) {
//            showText = "<i style='font-size: inherit;'>" + code + "</i>";
            iconSkin = "param_repeat";
        }
        if (svcStructure.getIs_loop() != null && SvcStructureEnum.IsAttrEnum.Yes.getCode() == svcStructure.getIs_attr().intValue()) {
//            showText = "<i style='font-size: inherit;'>" + code + "</i>";
            iconSkin = "param_attr";
        }
        if (StringUtils.isEmpty(showText)) {
            showText = code;
        }
        showText += code.equals(name) ? "" : " " + svcStructure.getName();
//        boolean nocheck;
//        if (svcStructure.getCan_edit() != null && svcStructure.getCan_edit().intValue() == SvcStructureEnum.CanEditEnum.No.getCode()) {
//            nocheck = true;
//        } else {
//            nocheck = false;
//        }
        TreeModel treeModel = new ZTreeModel(String.valueOf(svcStructure.getId()), showText,
                iconSkin, svcStructure, true);
        return treeModel;
    }

    @Override
    public String export(Integer sid, Integer direction, String msgType, Integer returnType, int valueType,
                         Boolean wrapperDataProtocal) throws MsgException {
        String result = "";
        SvcInfoModel svcInfoModel = svcInfoService.getByID(sid);
        msgType = svcInfoModel.getMsgType();
        if (!StringUtils.isEmpty(msgType)) {
            String path = FileUtils.getRootPath() + "/download";
            FileUtils.createPath(path);
            String fileName = "Service_" + sid;
            String msg;
            String filePathName = path + "/" + fileName;
            String appendName = "." + msgType.toLowerCase();
            filePathName += appendName;
            msg = exportMsg(sid, direction, msgType, valueType);
            //使用数据协议封装
            if (!StringUtils.isEmpty(msg) && wrapperDataProtocal != null && wrapperDataProtocal.booleanValue()) {
                msg = wrapperDataProtocal(msg, svcInfoModel, direction);
            }
            //返回下载地址
            if (returnType == null || SvcStructureEnum.ReturnTypeEnum.URL.getCode() == returnType.intValue()) {
                FileUtils.writeFile(filePathName, msg);
                result = "/download/" + fileName + appendName;
            }
            //返回消息数据
            else {
                result = msg;
            }
        }
        return result;
    }

    /**
     * 使用数据协议1.1封装
     */
    private String wrapperDataProtocal(String msg, SvcInfoModel svcInfoModel, Integer direction) {
        String result = null;

        String msgType = svcInfoModel.getMsgType();
        SvcStructureEnum.DirectionEnum directionEnum = SvcStructureEnum.DirectionEnum.getByCode(direction);
        String dataProtocalTemplate = getDataProtocalTemplate(SvcInfoEnum.DataProtocalEnum.ESB_1_1.getCode(),
                msgType, directionEnum.getCode());
        if (!StringUtils.isEmpty(dataProtocalTemplate)) {
            //替换内容占位符
            dataProtocalTemplate = dataProtocalTemplate.replace(DATA_PROTOCAL_CONTENT, msg);
            //替换服务代码（交易代码）
            dataProtocalTemplate = dataProtocalTemplate.replace(DATA_PROTOCAL_TRANCODE, svcInfoModel.getCode());
            //替换机构代码（暂时设为与业务系统代码相同）
            dataProtocalTemplate = dataProtocalTemplate.replace(DATA_PROTOCAL_ORGID, AppInfoEnum.ReservedCodeEnum.EsbTestUnit.getValue());
            //替换业务系统代码
            dataProtocalTemplate = dataProtocalTemplate.replace(DATA_PROTOCAL_APPID, AppInfoEnum.ReservedCodeEnum.EsbTestUnit.getValue());
            //替换消息ID
            dataProtocalTemplate = dataProtocalTemplate.replace(DATA_PROTOCAL_MESSAGEID, UUID.randomUUID().toString());
            //替换时间戳
            dataProtocalTemplate = dataProtocalTemplate.replace(DATA_PROTOCAL_TIMESTAMP, DateUtils.getCurrentDatetimeMiliSecondString());
            //格式化消息
            if ("json".equalsIgnoreCase(msgType)) {
                result = JsonUtils.format(dataProtocalTemplate);
            } else if ("xml".equalsIgnoreCase(msgType)) {
                result = dataProtocalTemplate;
            }
        } else {
            result = msg;
        }

        return result;
    }

    @Override
    public String exportMsg(Integer sid, Integer direction, String msgType, int valueType) throws MsgException {
        List<TreeModel> treeModels = createZTree(sid, direction, null);
        List<SvcStructureModel> structureModels = dao.queryBySvcID(sid, direction);
        List<ValueListModel> valueListModels = new ArrayList<>();
        List<ValueListModel> list;
        for (SvcStructureModel structureModel : structureModels) {
            Integer ssid = structureModel.getId();
            list = new ArrayList<>();
            if (ssid != null) {
                list = valueListService.queryBySsid(ssid);
            }
            if (!ListUtils.isEmpty(list)) {
                valueListModels.addAll(list);
            }
        }
        if (!ListUtils.isEmpty(treeModels)) {
            IParser parser = parserMap.get(IParser.BEAN_PREFIX + msgType.toUpperCase());
            //由于HL7节点要求必须符合相应的数据类型，因此，使用虚拟值填充更合适
            valueType = SvcInfoEnum.MsgTypeEnum.HL7.getCode().equalsIgnoreCase(msgType)
                    ? SvcStructureEnum.ValueTypeEnum.VistualValue.getCode()
                    : SvcStructureEnum.ValueTypeEnum.Name.getCode();
            String result = parser.encode(treeModels, valueType, valueListModels);
            return result;
        }
        return null;
    }

    @Override
    public boolean existCode(String code, Integer pid, Integer id, Integer direction, Integer sid) {
        return dao.existCode(code, pid, id, direction, sid);
    }

    @Override
    public SvcStructureModel queryById(Integer id) {
        List<SvcStructureModel> svcStructureModels = dao.queryById(id);
        if (svcStructureModels.size() == 0) {
            return null;
        } else {
            return svcStructureModels.get(0);
        }
    }

    @Override
    public Map<String, Object> getDataProtocalTemplate(Integer protocalCode, String msgType) {
        if (StringUtils.isEmpty(protocalCode) || StringUtils.isEmpty(msgType) || "hl7".equalsIgnoreCase(msgType)) {
            return null;
        }

        String filePathName = SvcInfoEnum.DataProtocalEnum.getPath(protocalCode);
        if (StringUtils.isEmpty(filePathName)) {
            return null;
        }
        Map<String, Object> result = new HashMap<>();
        if ("json".equalsIgnoreCase(msgType)) {
            result.put("protocalReq", getDataProtocalTemplate(protocalCode, msgType, SvcStructureEnum.DirectionEnum.In.getCode()));
            result.put("protocalAck", getDataProtocalTemplate(protocalCode, msgType, SvcStructureEnum.DirectionEnum.Ack.getCode()));
        }
        return result;
    }

    /**
     * 获取数据协议模版
     */
    private String getDataProtocalTemplate(Integer protocalCode, String msgType, int direction) {
        if (StringUtils.isEmpty(protocalCode) || StringUtils.isEmpty(msgType) || "hl7".equalsIgnoreCase(msgType)) {
            return null;
        }

        String filePathName = SvcInfoEnum.DataProtocalEnum.getPath(protocalCode);
        if (StringUtils.isEmpty(filePathName)) {
            return null;
        }
        String directionName;
        if (direction == SvcStructureEnum.DirectionEnum.In.getCode()) {
            directionName = "req";
        } else {
            directionName = "ack";
        }
        String result = FileUtils.readFileNeedRootPath(filePathName + "_" + directionName + "." + msgType.toLowerCase());
        return result;
    }

    /**
     * 将list转为map
     */
    @Override
    public Map<Integer, SvcStructureModel> listToMap(List<SvcStructureModel> list) {
        Map<Integer, SvcStructureModel> map;
        if (!ListUtils.isEmpty(list)) {
            map = new HashMap<>();
            for (SvcStructureModel model : list) {
                map.put(model.getId(), model);
            }
        } else {
            map = null;
        }
        return map;
    }

    /**
     * 获取节点路径
     */
    @Override
    public String retrieveNodePath(SvcStructureModel currentNode, Map<Integer, SvcStructureModel> map, String path) {
        String result;
        if (currentNode != null && currentNode.getPid() != null && currentNode.getPid().intValue() > 0 && !MapUtils.isEmpty(map)) {
            if (StringUtils.isEmpty(path)) {
                path = currentNode.getCode();
            }
            SvcStructureModel parentNode = map.get(currentNode.getPid());
            result = parentNode.getCode() + "." + path;
            if (parentNode != null && parentNode.getPid() != null && parentNode.getPid().intValue() > 0) {
                result = retrieveNodePath(parentNode, map, result);
            }
        } else if (currentNode != null && currentNode.getPid() != null && currentNode.getPid().intValue() == 0 && !MapUtils.isEmpty(map)) {
            if (StringUtils.isEmpty(path)) {
                result = currentNode.getCode();
            } else {
                result = currentNode.getCode() + "." + path;
            }
        } else {
            result = null;
        }
        return result;
    }

    /**
     * 获取节点路径
     */
    @Override
    public String retrieveNodeXmlPath(SvcStructureModel currentNode, Map<Integer, SvcStructureModel> map, String path) {
        String result;
        if (currentNode != null && currentNode.getPid() != null && currentNode.getPid().intValue() > 0 && !MapUtils.isEmpty(map)) {
            if (StringUtils.isEmpty(path)) {
                path = currentNode.getCode();
            }
            SvcStructureModel parentNode = map.get(currentNode.getPid());
            result = parentNode.getCode() + "/" + path;
            if (parentNode != null && parentNode.getPid() != null && parentNode.getPid().intValue() > 0) {
                result = retrieveNodeXmlPath(parentNode, map, result);
            }
        } else if (currentNode != null && currentNode.getPid() != null && currentNode.getPid().intValue() == 0 && !MapUtils.isEmpty(map)) {
            if (StringUtils.isEmpty(path)) {
                result = currentNode.getCode();
            } else {
                result = currentNode.getCode() + "/" + path;
            }
        } else {
            result = null;
        }
        return result;
    }

    @Override
    public SvcInfoEnum.MsgTypeEnum checkMsgType(String msg) {
        SvcInfoEnum.MsgTypeEnum msgTypeEnum;
        int dataFormat = StringUtils.checkDataFormat(msg);
        if (dataFormat == 0) {
            msgTypeEnum = SvcInfoEnum.MsgTypeEnum.XML;
        } else if (dataFormat == 1) {
            msgTypeEnum = SvcInfoEnum.MsgTypeEnum.JSON;
        } else if (dataFormat == 2) {
            msgTypeEnum = SvcInfoEnum.MsgTypeEnum.HL7;
        } else {
            msgTypeEnum = null;
        }
        return msgTypeEnum;
    }

    @Override
    public SvcInfoEnum.DataProtocalEnum checkEsbDataProtocal(SvcStructureExtModel extModel) throws Exception {
        SvcInfoEnum.DataProtocalEnum result = null;

        List<SvcStructureExtModel> children = extModel.getChildren();
        if (!ListUtils.isEmpty(children) && children.size() == 2) {
            SvcStructureExtModel headExtModel = null;
            //判断是否包含Head与Body节点
            List<String> childrenCode = new ArrayList<>();
            childrenCode.add("Head");
            childrenCode.remove(children.get(0).getObj().getCode());
            if (!childrenCode.contains("Head")) {
                headExtModel = children.get(0);
            }
            childrenCode.remove(children.get(1).getObj().getCode());
            if (childrenCode.contains("Head")) {
                headExtModel = children.get(1);
            }

            //判断Head的子节点
            Map<String, SvcStructureExtModel> map;
            if (headExtModel != null && ListUtils.isEmpty(headExtModel.getChildren())) {
                //判断Head中是否存在Version与TranCode节点
                map = headExtModel.childrenListToMap();

                //判断是否存在Version
                Object version = map.get("Version");
                if (version != null) {
                    if (map.keySet().contains("LicKey")) {
                        result = SvcInfoEnum.DataProtocalEnum.ESB_1_1;
                    } else {
                        result = SvcInfoEnum.DataProtocalEnum.ESB_1_0;
                    }
                }

            }
        }

        return result;
    }

    @Override
    public SvcInfoEnum.DataProtocalEnum checkEsbHL7(SvcStructureExtModel extModel) {
        return null;
    }

    @Override
    public CommonObject query(Map map) {
        return dao.query(map);
    }

}