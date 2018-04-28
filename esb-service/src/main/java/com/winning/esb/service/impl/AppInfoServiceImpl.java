package com.winning.esb.service.impl;

import com.winning.esb.dao.IAppInfoDao;
import com.winning.esb.model.*;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.model.common.TreeModel;
import com.winning.esb.model.common.ZTreeModel;
import com.winning.esb.model.enums.*;
import com.winning.esb.model.ext.AppInfoExtModel;
import com.winning.esb.model.ext.SvcInfoExtModel;
import com.winning.esb.service.*;
import com.winning.esb.stable.NormalConst;
import com.winning.esb.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by xuehao on 2017/8/22.
 */
@Service
public class AppInfoServiceImpl implements IAppInfoService {
    private static final Logger logger = LoggerFactory.getLogger(AppInfoServiceImpl.class);
    @Autowired
    private IAppInfoDao dao;
    @Autowired
    private IOrgInfoService orgInfoService;
    @Autowired
    private ISvcInfoService svcInfoService;
    @Autowired
    private IGrantService grantService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IUserAppService userAppService;
    @Autowired
    private ISimulationTestStepLogService simulationTestStepLogService;
    @Autowired
    private IInspectionSysService inspectionSysService;

    private final String TREE_ID_ORG = "ORG";
    private final String TREE_ID_APP = "APP";
    private final String TREE_ID_SVC = "SVC";

    @Override
    public String save(AppInfoModel obj) {
        String err = "";
        String regexStr = "^[\\w\\d_-]*$";
        if (StringUtils.isEmpty(obj.getAppId())) {
            err += "代码不能为空！";
        } else {
            String reservedCodeCheck = AppInfoEnum.ReservedCodeEnum.check(obj.getAppId());
            if(!StringUtils.isEmpty(reservedCodeCheck)) {
                err += reservedCodeCheck;
            } else if (existCodeOrName(obj.getId(), "appId", obj.getAppId())) {
                err += "代码已存在！";
            }
        }
        if (StringUtils.isEmpty(obj.getAppName())) {
            err += "名称不能为空！";
        } else if (existCodeOrName(obj.getId(), "appName", obj.getAppName())) {
            err += "名称已存在！";
        } else if (!RegexUtils.match(regexStr, obj.getAppId())) {
            err += "代码不符合要求,必须英文字符、数字、下划线和连接符“-”的组合！";
        }
        if (obj.getOrder_num() == null) {
            obj.setOrder_num(0);
        }

        //清除自动扫描的描述
        if(NormalConst.AUTO_SCAN_DESP.equals(obj.getDesp())) {
            obj.setDesp("");
        }

        if (StringUtils.isEmpty(err)) {
            obj.setMtime(new Date());
            if (obj.getId() == null) {
                obj.setCtime(obj.getMtime());
                obj.setStatus(AppInfoEnum.StatusEnum.Normal.getCode());
                insert(obj);
            } else {
                update(obj);
            }
        }
        return err;
    }

    @Override
    public void insert(AppInfoModel obj) {
        List<AppInfoModel> objs = new ArrayList<>();
        objs.add(obj);
        insert(objs);
    }

    @Override
    public void insert(List<AppInfoModel> objs) {
        dao.insert(objs);
    }

    @Override
    public void update(AppInfoModel obj) {
        //获取修改前的信息
        AppInfoModel appInfoModelOld = getByID(obj.getId());
        //修改业务系统
        List<AppInfoModel> objs = new ArrayList<>();
        objs.add(obj);
        update(objs);
        //修改服务代码前缀

    }

    @Override
    public void update(List<AppInfoModel> objs) {
        dao.update(objs);
    }

    @Override
    public String updateStatus(List<Integer> ids, Integer status) {
        String err = null;

        dao.updateStatus(ids, status);

        return err;
    }

    @Override
    public String delete(List<Integer> idList) {
        String err = null;
        //查询是否有服务已关联系统
        List<SvcInfoModel> svcInfoModels = svcInfoService.getByAid(idList);
        if (ListUtils.isEmpty(svcInfoModels)) {
            dao.delete(idList);
        } else {
            //获取已关联的系统id
            List<Integer> idUsedList = new ArrayList<>();
            for (SvcInfoModel svcInfoModel : svcInfoModels) {
                idUsedList.add(svcInfoModel.getAid());
            }
            List<AppInfoModel> appInfoModels = getByID(idUsedList);
            List<String> nameUsedList = new ArrayList<>();
            for (AppInfoModel appInfoModel : appInfoModels) {
                nameUsedList.add(appInfoModel.getAppName());
            }
            err = "【" + StringUtils.join(nameUsedList, ",") + "】已关联服务，无法删除！";
        }
        return err;
    }

    @Override
    public AppInfoModel getByID(Integer id) {
        List<Integer> idList = new ArrayList<>();
        idList.add(id);
        List<AppInfoModel> list = getByID(idList);
        return ListUtils.isEmpty(list) ? null : list.get(0);
    }

    @Override
    public List<AppInfoModel> getByID(List<Integer> idList) {
        return dao.getByID(idList);
    }

    @Override
    public CommonObject query(Map map) {
        return dao.query(map);
    }

    @Override
    public List<AppInfoModel> list() {
        return list(null);
    }

    @Override
    public List<AppInfoModel> list(Map map) {
        CommonObject commonObject = query(map);
        return ListUtils.transferToList(commonObject.getDatas());
    }

    @Override
    public List<AppInfoModel> listActive(Map map) {
        if (map == null) {
            map = new HashMap();
        }
        map.put("status", AppInfoEnum.StatusEnum.Normal.getCode());
        return list(map);
    }

    @Override
    public List<AppInfoModel> listActiveWithoutEsb() {
        return listActiveWithoutEsb(null);
    }

    @Override
    public List<AppInfoModel> listActiveWithoutEsb(Map map) {
        if (map == null) {
            map = new HashMap();
        }
        map.put("needESB", false);
        return listActive(map);
    }

    @Override
    public List<AppInfoModel> listEsb() {
        return listEsb(null);
    }

    @Override
    public List<AppInfoModel> listEsb(Map map) {
        if (map == null) {
            map = new HashMap();
        }
        map.put("appType", AppInfoEnum.AppTypeEnum.ESB.getCode());
        CommonObject commonObject = query(map);
        return ListUtils.transferToList(commonObject.getDatas());
    }

    @Override
    public List<AppInfoExtModel> listActiveWithStatistic(Map datas) {
        List<AppInfoExtModel> resultList;
        List<AppInfoModel> appInfoModels = listActive(datas);
        if (!ListUtils.isEmpty(appInfoModels)) {
            Map<Integer, AppInfoExtModel> appExtMap = new HashMap<>();
            AppInfoExtModel appExt;
            //获取业务系统ID
            List<Integer> idList = new ArrayList<>();
            for (AppInfoModel app : appInfoModels) {
                idList.add(app.getId());
                appExtMap.put(app.getId(), new AppInfoExtModel(app));
            }
            //获取服务扩展对象
            List<SvcInfoExtModel> svcInfoExtModels = svcInfoService.getExtByAid(idList);
            if (!ListUtils.isEmpty(svcInfoExtModels)) {
                //统计服务总数与错误总数
                Integer aid;
                for (SvcInfoExtModel svc : svcInfoExtModels) {
                    aid = svc.getSvcInfo().getAid();
                    if (appExtMap.containsKey(aid)) {
                        Map map = new HashMap();
                        map.put("aid", aid);
                        map.put("checck_time", DateUtils.toDateStringYmd((new Date()).getTime()));
                        CommonObject commonObject = inspectionSysService.query(map);
                        appExt = appExtMap.get(aid);
                        appExt.setInspectionSysModels(ListUtils.transferToList(commonObject.getDatas()));
                        appExt.setServiceCount(appExt.getServiceCount() + 1);
                        if (svc.getStatus() != null && SvcUrlEnum.StatusEnum.Stop.getCode() == svc.getStatus().intValue()) {
                            appExt.setServiceFailCount(appExt.getServiceFailCount() + 1);
                        }
                    }
                }
            }


            //设置返回的业务系统对象
            resultList = ListUtils.transferToList(appExtMap.values());
        } else {
            resultList = null;
        }
        return resultList;
    }

    @Override
    public AppInfoModel getByAppId(String appId) {
        List<AppInfoModel> list = dao.getByAppId(appId);
        return ListUtils.isEmpty(list) ? null : list.get(0);
    }

    @Override
    public List<AppInfoModel> getByAppId(List<String> appIds) {
        return dao.getByAppId(appIds);
    }

    @Override
    public boolean existCodeOrName(Integer id, String columnName, String columnValue) {
        Map map = new HashMap();
        map.put(QueryParameterKeys.STARTINDEX.getKey(), 0);
        map.put(QueryParameterKeys.PAGESIZE.getKey(), 2);
        map.put(columnName, columnValue);
        CommonObject commonObject = query(map);
        if (commonObject.getTotalSize() > 1) {
            return true;
        } else if (commonObject.getTotalSize() == 1) {
            AppInfoModel appInfo = (AppInfoModel) commonObject.getDatas().iterator().next();
            if (appInfo.getId() != null && id != null && appInfo.getId().intValue() == id.intValue()) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public List<Integer> listId(List<AppInfoModel> objs) {
        List<Integer> result;
        if (!ListUtils.isEmpty(objs)) {
            result = new ArrayList<>();
            for (AppInfoModel obj : objs) {
                result.add(obj.getId());
            }
        } else {
            result = null;
        }
        return result;
    }

    @Override
    public List<String> listAppId(List<AppInfoModel> objs) {
        List<String> result;
        if (!ListUtils.isEmpty(objs)) {
            result = new ArrayList<>();
            for (AppInfoModel obj : objs) {
                result.add(obj.getAppId());
            }
        } else {
            result = null;
        }
        return result;
    }

    @Override
    public List<SimpleObject> listIdName() {
        List<SimpleObject> resultList;
        CommonObject commonObject = query(null);
        if (commonObject.getDatas() != null && commonObject.getDatas().size() > 0) {
            AppInfoModel appInfoModel;
            resultList = new ArrayList<>();
            for (Object obj : commonObject.getDatas()) {
                appInfoModel = (AppInfoModel) obj;
                resultList.add(new SimpleObject(String.valueOf(appInfoModel.getId()), appInfoModel.getAppName()));
            }
        } else {
            resultList = null;
        }
        return resultList;
    }

    @Override
    public List<SimpleObject> listIdAppId() {
        List<SimpleObject> resultList;
        CommonObject commonObject = query(null);
        if (commonObject.getDatas() != null && commonObject.getDatas().size() > 0) {
            AppInfoModel appInfoModel;
            resultList = new ArrayList<>();
            for (Object obj : commonObject.getDatas()) {
                appInfoModel = (AppInfoModel) obj;
                resultList.add(new SimpleObject(String.valueOf(appInfoModel.getId()), appInfoModel.getAppId()));
            }
        } else {
            resultList = null;
        }
        return resultList;
    }

    @Override
    public Map<String, String> mapAppIdName() {
        List<String> appIdList = null;
        return mapAppIdName(appIdList);
    }

    @Override
    public Map<String, String> mapAppIdName(String... appIds) {
        List<String> appIdList = ListUtils.transferToList(appIds);
        return mapAppIdName(appIdList);
    }

    @Override
    public Map<String, String> mapAppIdName(List<String> appIdList) {
        Map<String, String> resultMap;
        Map<String, Object> queryMap = new HashMap<>();
        if (!ListUtils.isEmpty(appIdList)) {
            queryMap.put("appIdList", appIdList);
        }
        CommonObject commonObject = query(queryMap);
        if (commonObject.getDatas() != null && commonObject.getDatas().size() > 0) {
            AppInfoModel appInfoModel;
            resultMap = new LinkedHashMap<>();
            String appId;
            for (Object obj : commonObject.getDatas()) {
                appInfoModel = (AppInfoModel) obj;
                appId = appInfoModel.getAppId();
                resultMap.put(appId, appInfoModel.getAppName());
            }
        } else {
            resultMap = null;
        }
        return resultMap;
    }

    @Override
    public Map<String, AppInfoModel> mapAppIdObj(List<String> appIdList) {
        List<AppInfoModel> appInfoModels = getByAppId(appIdList);
        Map<String, AppInfoModel> resultMap;
        if (!ListUtils.isEmpty(appInfoModels)) {
            resultMap = new HashMap<>();
            for (AppInfoModel obj : appInfoModels) {
                resultMap.put(obj.getAppId(), obj);
            }
        } else {
            resultMap = null;
        }
        return resultMap;
    }

    @Override
    public Map<Integer, AppInfoModel> mapIdObject(List<AppInfoModel> objs) {
        Map<Integer, AppInfoModel> map;
        if (!ListUtils.isEmpty(objs)) {
            map = new HashMap<>();
            for (AppInfoModel obj : objs) {
                map.put(obj.getId(), obj);
            }
        } else {
            map = null;
        }
        return map;
    }

    @Override
    public Map<String, AppInfoModel> mapAppIdObject(List<AppInfoModel> objs) {
        Map<String, AppInfoModel> map;
        if (!ListUtils.isEmpty(objs)) {
            map = new HashMap<>();
            for (AppInfoModel obj : objs) {
                map.put(obj.getAppId(), obj);
            }
        } else {
            map = null;
        }
        return map;
    }

    @Override
    public List<TreeModel> createZTree(Map<String, Object> map) throws Exception {
        List<TreeModel> resultList = new ArrayList<>();
        //logger.info("接收请求时间：" + df.format(new Date()));

        //====================输入参数begin====================
        Object tempStr;
        //如果为空，则返回所有的提供方与消费方，否则按照输入值筛选
        tempStr = map.get("sysDirection");
        Integer sysDirection = StringUtils.isEmpty(tempStr) ? null : (Integer) tempStr;
        //false-不需要返回ESB，true-需要返回ESB
        tempStr = map.get("needESB");
        Boolean needESB = StringUtils.isEmpty(tempStr) ? null : (Boolean) tempStr;
        //false-不需要返回停用的系统，true-需要返回停用的系统
        tempStr = map.get("needStop");
        Boolean needStop = StringUtils.isEmpty(tempStr) ? null : (Boolean) tempStr;
        //false-不需要返回服务，true-需要返回服务
        tempStr = map.get("needSvc");
        Boolean needSvc = StringUtils.isEmpty(tempStr) ? null : (Boolean) tempStr;
        //业务系统ID
        tempStr = map.get("strIdList");
        List<Integer> idList = StringUtils.isEmpty(tempStr) ? null : JsonUtils.jsonToList(String.valueOf(tempStr), Integer.class);
        //排除的业务系统ID
        tempStr = map.get("strIdNotInList");
        List<Integer> idNotInList = StringUtils.isEmpty(tempStr) ? null : JsonUtils.jsonToList(String.valueOf(tempStr), Integer.class);
        //排除的服务ID
        tempStr = map.get("strSidNotInList");
        List<Integer> sidNotInList = StringUtils.isEmpty(tempStr) ? null : JsonUtils.jsonToList(String.valueOf(tempStr), Integer.class);
        //排除已经授权的服务(此时根据strIdNotInList中的业务系统ID获取已经授权的服务ID)
        tempStr = map.get("exceptGrantSvc");
        Boolean exceptGrantSvc = StringUtils.isEmpty(tempStr) ? null : (Boolean) tempStr;
        if (exceptGrantSvc != null && exceptGrantSvc.booleanValue() && !ListUtils.isEmpty(idNotInList)) {
            for (Integer id : idNotInList) {
                List<GrantModel> models = grantService.queryByAid(id);
                if (!ListUtils.isEmpty(models)) {
                    if (sidNotInList == null) {
                        sidNotInList = new ArrayList<>();
                    }
                    for (GrantModel model : models) {
                        sidNotInList.add(model.getSid());
                    }
                }
            }
        }
        //排除已经授权全部服务的业务系统
        tempStr = map.get("exceptAppGrantAllSvc");
        Boolean exceptAppGrantAllSvc = StringUtils.isEmpty(tempStr) ? null : (Boolean) tempStr;
        if (exceptAppGrantAllSvc != null && exceptAppGrantAllSvc.booleanValue() && ListUtils.isEmpty(idNotInList)) {
            List<Integer> aidGrantAllSvc = grantService.getAidGrantAllSvc();
            if (!ListUtils.isEmpty(aidGrantAllSvc)) {
                if (ListUtils.isEmpty(idNotInList)) {
                    idNotInList = new ArrayList<>();
                }
                idNotInList.addAll(aidGrantAllSvc);
            }
        }
        //登录的用户名，根据用户名筛选已经授权的业务系统ID列表
        tempStr = map.get("username");
        String username = StringUtils.isEmpty(tempStr) ? null : String.valueOf(tempStr);
        if (!StringUtils.isEmpty(username)) {
            UserModel userModel = userService.queryByUsername(username);
            if (userModel == null) {
                throw new Exception("当前登录用户不存在！");
            }
            List<Integer> grantAidList = userAppService.getAidListByUserid(userModel.getId());
            if (userModel != null && userModel.getRole() != null && userModel.getRole().intValue() == UserEnum.RoleEnum.Normal.getCode()
                    && !ListUtils.isEmpty(grantAidList)) {
                if (ListUtils.isEmpty(idList)) {
                    idList = new ArrayList<>();
                    idList.addAll(grantAidList);
                } else {
                    List<Integer> removeList = new ArrayList<>();
                    for (Integer id : idList) {
                        if (!grantAidList.contains(id)) {
                            removeList.add(id);
                        }
                    }
                    idList.removeAll(removeList);
                    idList.addAll(grantAidList);
                }
            }
        }
        //查询关键字
        tempStr = map.get("queryWord");
        String queryWord = StringUtils.isEmpty(tempStr) ? null : String.valueOf(tempStr);
        //服务状态
        tempStr = map.get("svcStatus");
        Integer svcStatus = StringUtils.isEmpty(tempStr) ? null : Integer.parseInt(String.valueOf(tempStr));
        //是否对服务单元测试筛选
        tempStr = map.get("isTestUnit");
        Boolean isTestUnit = StringUtils.isEmpty(tempStr) ? null : (Boolean) tempStr;
        //单元测试标志（空值 - 获取全部（默认）， 1 - 获取已测试， 2 - 获取未测试）
        tempStr = map.get("testUnitFlag");
        Integer testUnitFlag = StringUtils.isEmpty(tempStr) ? null : (Integer) tempStr;
        //====================输入参数end====================
        //logger.info("获取完参数时间：" + df.format(new Date()));
        //获取机构列表
        List<OrgInfoModel> orgInfoList = orgInfoService.list();
        if (ListUtils.isEmpty(orgInfoList)) {
            return resultList;
        }

        //获取业务系统列表
        Map<String, Object> mapAppFilter = new HashMap<>();
        if (!StringUtils.isEmpty(sysDirection)) {
            mapAppFilter.put("direction", sysDirection);
        }
        if (needESB != null) {
            mapAppFilter.put("needESB", needESB);
        }
        if (needStop != null) {
            mapAppFilter.put("needStop", needStop);
        }
        if (idList != null) {
            mapAppFilter.put("idList", idList);
        }
        if (idNotInList != null) {
            mapAppFilter.put("idNotInList", idNotInList);
        }
        if (!StringUtils.isEmpty(queryWord) && (needSvc == null || !needSvc.booleanValue())) {
            mapAppFilter.put(NormalConst.QUERY_WORD_NAME, queryWord);
        }
        List<AppInfoModel> appInfoList = ListUtils.transferToList(query(mapAppFilter).getDatas());
        //logger.info("获取完系统列表时间：" + df.format(new Date()));
        //获取业务系统ID列表，用于获取服务列表
        List<Integer> aidList;
        if (ListUtils.isEmpty(appInfoList)) {
            aidList = null;
        } else {
            aidList = new ArrayList<>();
            for (AppInfoModel app : appInfoList) {
                aidList.add(app.getId());
            }
        }

        //获取服务列表，并生成树的节点
        Map<Integer, List<TreeModel>> svcMap;
        if (!ListUtils.isEmpty(aidList) && needSvc != null && needSvc.booleanValue()) {
            List<SvcInfoModel> svcInfoList;
            List<Integer> sidTestedList;
            Map<Integer, Integer> sidTestedMap;
            Map<String, Object> mapFilter = new HashMap<>();
            mapFilter.put("queryWord", queryWord);
            mapFilter.put("status", svcStatus);
            mapFilter.put("aidList", aidList);
            mapFilter.put("idNotInList", sidNotInList);
            if (isTestUnit != null && isTestUnit.booleanValue()) {
                sidTestedMap = simulationTestStepLogService.queryUnitTestedSidList();
                if (testUnitFlag != null) {
                    sidTestedList = MapUtils.isEmpty(sidTestedMap) ? Arrays.asList(Integer.MIN_VALUE) : ListUtils.transferToList(sidTestedMap.keySet());
                    if (testUnitFlag.intValue() == 1) {
                        //获取已测试
                        mapFilter.put("idList", sidTestedList);
                    } else {
                        //获取未测试
                        if (ListUtils.isEmpty(sidNotInList)) {
                            sidNotInList = sidTestedList;
                        } else {
                            sidNotInList.addAll(sidTestedList);
                        }
                        mapFilter.put("idNotInList", sidNotInList);
                    }
                }
            } else {
                sidTestedMap = null;
            }
            svcInfoList = svcInfoService.list(mapFilter);
            svcMap = createSvcZTree(svcInfoList, isTestUnit, sidTestedMap);
        } else {
            svcMap = null;
        }
        //logger.info("获取完服务列表时间：" + df.format(new Date()));
        //生成业务系统的树节点
        Map<Integer, List<TreeModel>> appMap = createAppZTree(appInfoList, needSvc, svcMap);
        //logger.info("生成业务系统树节点的时间：" + df.format(new Date()));
        //生成机构
        resultList = createOrgZTree(orgInfoList, appMap);
        //logger.info("生成机构时间：" + df.format(new Date()));
        return resultList;
    }

    /**
     * 创建服务节点，返回Map：key - 业务系统ID，value - 包含的服务节点
     *
     * @param isTestUnit   是否单元测试
     * @param sidTestedMap 如果isTestUnit为true，则需要标识测试结果【key - 服务id， value - 测试结果】
     */
    private Map<Integer, List<TreeModel>> createSvcZTree(List<SvcInfoModel> svcInfoList, Boolean isTestUnit,
                                                         Map<Integer, Integer> sidTestedMap) {
        Map<Integer, List<TreeModel>> map;
        if (!ListUtils.isEmpty(svcInfoList)) {
            map = new HashMap<>();
            Integer id, pid;
            String name, badgeCss, badgeContent;
            for (SvcInfoModel svc : svcInfoList) {
                id = svc.getId();
                pid = svc.getAid();
                if (!map.containsKey(pid)) {
                    map.put(pid, new ArrayList<>());
                }
                name = svc.getName();
                if (StringUtils.isEmpty(name)) {
                    name = svc.getCode();
                }
                //如果是单元测试，则需要设置测试结果
                if (isTestUnit != null && isTestUnit.booleanValue() && !MapUtils.isEmpty(sidTestedMap)) {
                    if (sidTestedMap.containsKey(id) && sidTestedMap.get(id) != null) {
                        if (SimulationTestStepLogEnum.ResultEnum.Success.getCode() == sidTestedMap.get(id).intValue()) {
                            badgeCss = "prject-backcolor-success";
                            badgeContent = SimulationTestStepLogEnum.ResultEnum.Success.getValue();
                        } else if (SimulationTestStepLogEnum.ResultEnum.Failure.getCode() == sidTestedMap.get(id).intValue()) {
                            badgeCss = "prject-backcolor-failure";
                            badgeContent = SimulationTestStepLogEnum.ResultEnum.Failure.getValue();
                        } else {
                            badgeCss = "";
                            badgeContent = SimulationTestStepLogEnum.ResultEnum.Unknown.getValue();
                        }
                        name += "<span class=\"badge badge-tree " + badgeCss + "\">" + badgeContent + "</span>";
                    }
                }
                map.get(pid).add(new ZTreeModel(TREE_ID_SVC + svc.getId(), name, "svc", svc));
            }
        } else {
            map = null;
        }
        return map;
    }

    /**
     * 创建业务系统节点，返回Map：key - 机构ID，value - 包含的业务系统节点
     */
    private Map<Integer, List<TreeModel>> createAppZTree(List<AppInfoModel> appInfoList, Boolean needSvc,
                                                         Map<Integer, List<TreeModel>> svcMap) {
        Map<Integer, List<TreeModel>> map;
        if (!ListUtils.isEmpty(appInfoList)) {
            map = new HashMap<>();
            Integer id, pid;
            String name;
            TreeModel treeModel;
            for (AppInfoModel app : appInfoList) {
                id = app.getId();
                //如果需要返回服务，则服务为空时，不需要加载业务系统
                if (needSvc == null || needSvc.booleanValue() == false
                        || needSvc != null && needSvc.booleanValue() == true && !MapUtils.isEmpty(svcMap) && svcMap.containsKey(id)) {
                    pid = app.getOrgId();
                    if (!map.containsKey(pid)) {
                        map.put(pid, new ArrayList<>());
                    }
                    name = app.getAppName();
                    if (StringUtils.isEmpty(name)) {
                        name = app.getAppId();
                    }
                    treeModel = new ZTreeModel(TREE_ID_APP + id, name, "app", app);
                    if (!MapUtils.isEmpty(svcMap) && svcMap.containsKey(id)) {
                        treeModel.setChildren(svcMap.get(id));
                    }
                    map.get(pid).add(treeModel);
                }
            }
        } else {
            map = null;
        }
        return map;
    }

    /**
     * 创建机构节点
     */
    private List<TreeModel> createOrgZTree(List<OrgInfoModel> orgInfoList, Map<Integer, List<TreeModel>> appMap) {
        List<TreeModel> treeOrgList;
        if (!ListUtils.isEmpty(orgInfoList)) {
            treeOrgList = new ArrayList<>();
            TreeModel treeModel;
            for (OrgInfoModel org : orgInfoList) {
                if (!MapUtils.isEmpty(appMap) && appMap.containsKey(org.getId())) {
                    treeModel = new ZTreeModel(TREE_ID_ORG + org.getId(), org.getName(),
                            "org", org);
                    treeModel.setChildren(appMap.get(org.getId()));
                    treeOrgList.add(treeModel);
                }
            }
        } else {
            treeOrgList = null;
        }
        return treeOrgList;
    }

}