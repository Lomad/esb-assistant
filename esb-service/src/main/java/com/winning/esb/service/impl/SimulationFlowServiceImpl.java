package com.winning.esb.service.impl;

import com.winning.esb.dao.ISimulationFlowDao;
import com.winning.esb.model.*;
import com.winning.esb.model.common.ResultObject;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.model.common.TreeModel;
import com.winning.esb.model.common.ZTreeModel;
import com.winning.esb.model.enums.UserEnum;
import com.winning.esb.model.ext.SimulationFlowExtModel;
import com.winning.esb.model.ext.SimulationFlowSvcExtModel;
import com.winning.esb.service.*;
import com.winning.esb.service.pdf.TestLogPdfHelper;
import com.winning.esb.service.utils.EsbReceiverForTestFlow;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.MapUtils;
import com.winning.esb.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author xuehao
 * @date 2017/8/21
 */
@Service
public class SimulationFlowServiceImpl implements ISimulationFlowService {
    @Autowired
    private ISimulationFlowDao dao;
    @Autowired
    private ISimulationFlowSvcService flowSvcService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IUserAppService userAppService;
    @Autowired
    private IOrgInfoService orgInfoService;
    @Autowired
    private IAppInfoService appInfoService;
    @Autowired
    private ISvcInfoService svcInfoService;
    @Autowired
    private ISimulationTestLogService testLogService;
    @Autowired
    private ISimulationTestStepLogService testStepLogService;
    @Autowired
    private TestLogPdfHelper testLogPdfHelper;

    private final String TREE_ID_FLOW = "FLOW";
    private final String TREE_ID_FLOWSVC = "FLOWSVC";

    @Override
    public String save(SimulationFlowModel obj) {
        String err = "";
        obj.setMtime(new Date());
        if (StringUtils.isEmpty(obj.getName())) {
            err += "名称不能为空！";
        }
        if (StringUtils.isEmpty(err)) {
            obj.setMtime(new Date());
            if (obj.getId() == null) {
                obj.setCtime(obj.getMtime());
                dao.insert(obj);
            } else {
                dao.update(obj);
            }
        }
        return err;
    }

    @Override
    public String delete(Integer id) {
        dao.delete(id);
        return null;
    }

    @Override
    public SimulationFlowModel getByID(Integer id) {
        return dao.getByID(id);
    }

    @Override
    public List<SimulationFlowModel> query(Map<String, Object> map) {
        return dao.query(map);
    }

    @Override
    public List<TreeModel> getTree(Map<String, Object> map) throws Exception {
        List<TreeModel> resultList;

        List<SimulationFlowModel> flowList = query(map);
        if (ListUtils.isEmpty(flowList)) {
            resultList = null;
        } else {
            Map<Integer, SimulationFlowModel> flowMap = toFlowMap(flowList);
            //获取最新的测试流程日志
            List<SimulationTestLogModel> testLogModelList = testLogService.getLatestTest(ListUtils.transferToList(flowMap.keySet()));
            Map<Integer, SimulationTestLogModel> logMap = toTestLogMap(testLogModelList);
            //获取最新的明细步骤日志
            Map<Integer, SimulationTestStepLogModel> testStepLogModelMap;
            if (!ListUtils.isEmpty(testLogModelList)) {
                List<Integer> tidList = new ArrayList<>();
                for (SimulationTestLogModel item : testLogModelList) {
                    tidList.add(item.getId());
                }
                List<SimulationTestStepLogModel> testStepLogModels = testStepLogService.getLatestByTID(ListUtils.transferToList(tidList));
                testStepLogModelMap = testStepLogService.mapSidObj(testStepLogModels);
            } else {
                testStepLogModelMap = null;
            }
            //流程步骤
            Map<Integer, List<TreeModel>> flowSvcTreeMap = null;
            //获取服务步骤列表
            Map<Integer, SimulationFlowSvcModel> flowSvcMap = getFlowSvc(flowList);
            //获取流程明细步骤，并创建步骤节点
            if (!MapUtils.isEmpty(flowSvcMap)) {
                //获取服务列表
                Map<Integer, SvcInfoModel> svcInfoModelMap = getSvcModel(flowSvcMap);
                //获取场景步骤涉及的系统ID（消费方系统ID）
                List<Integer> aidList = new ArrayList<>();
                for (Map.Entry<Integer, SimulationFlowSvcModel> item : flowSvcMap.entrySet()) {
                    aidList.add(item.getValue().getAid());
                }
                //获取场景步骤涉及服务的所属系统ID（提供方系统ID）
                for (Map.Entry<Integer, SvcInfoModel> item : svcInfoModelMap.entrySet()) {
                    aidList.add(item.getValue().getAid());
                }
                //获取场景涉及的系统
                Map<Integer, AppInfoModel> appInfoModelMap = null;
                if (!ListUtils.isEmpty(aidList)) {
                    List<AppInfoModel> appInfoModels = appInfoService.getByID(aidList);
                    appInfoModelMap = appInfoService.mapIdObject(appInfoModels);
                }
                //生成流程步骤扩展类实例列表
                List<SimulationFlowSvcExtModel> svcExtModelList = new ArrayList<>();
                SimulationFlowSvcExtModel svcExtModel;
                SvcInfoModel svcInfoModel;
                AppInfoModel consumer, provider;
                SimulationTestStepLogModel log;
                for (Map.Entry<Integer, SimulationFlowSvcModel> item : flowSvcMap.entrySet()) {
                    //获取服务
                    svcInfoModel = svcInfoModelMap.get(item.getValue().getSid());
                    //获取消费方与提供方系统
                    consumer = appInfoModelMap.get(item.getValue().getAid());
                    provider = appInfoModelMap.get(svcInfoModel.getAid());
                    log = MapUtils.isEmpty(testStepLogModelMap) ? null : testStepLogModelMap.get(item.getValue().getSid());
                    //生成服务的扩展对象
                    svcExtModel = new SimulationFlowSvcExtModel(item.getValue(), consumer, provider, svcInfoModel, log);
                    svcExtModelList.add(svcExtModel);
                }
                flowSvcTreeMap = createFlowSvcTree(svcExtModelList);
            }
            //获取用户名
            List<Integer> filterByUsernameFidList = getFlowIdByUsername(map);
            //创建流程节点
            resultList = createFlowTree(flowList, flowSvcTreeMap, logMap, filterByUsernameFidList);
        }
        return resultList;
    }

    /**
     * 创建步骤节点，返回Map：key - 流程ID，value - 包含的服务节点
     */
    private Map<Integer, List<TreeModel>> createFlowSvcTree(List<SimulationFlowSvcExtModel> svcExtModelList) {
        Map<Integer, List<TreeModel>> map;
        if (!ListUtils.isEmpty(svcExtModelList)) {
            map = new HashMap<>();
            Integer id, fid;
            String name, badgeCss, badgeContent, iconSkin = "svc";
            SvcInfoModel svc;
            SimulationFlowSvcModel flowSvcModel;
            for (SimulationFlowSvcExtModel obj : svcExtModelList) {
                svc = obj.getSvc();
                flowSvcModel = obj.getObj();
                id = flowSvcModel.getId();
                fid = flowSvcModel.getFid();
                if (!map.containsKey(fid)) {
                    map.put(fid, new ArrayList<>());
                }
                name = svc.getName();
                if (StringUtils.isEmpty(name)) {
                    name = svc.getCode();
                }
//                //设置测试结果
//                if (!MapUtils.isEmpty(sidTestedMap)) {
//                    if (sidTestedMap.containsKey(id) && sidTestedMap.get(id) != null) {
//                        if (SimulationTestStepLogEnum.ResultEnum.Success.getCode() == sidTestedMap.get(id).intValue()) {
//                            badgeCss = "prject-backcolor-success";
//                            badgeContent = SimulationTestStepLogEnum.ResultEnum.Success.getValue();
//                        } else if (SimulationTestStepLogEnum.ResultEnum.Failure.getCode() == sidTestedMap.get(id).intValue()) {
//                            badgeCss = "prject-backcolor-failure";
//                            badgeContent = SimulationTestStepLogEnum.ResultEnum.Failure.getValue();
//                        } else {
//                            badgeCss = "";
//                            badgeContent = SimulationTestStepLogEnum.ResultEnum.Unknown.getValue();
//                        }
//                        name += "<span class=\"badge badge-tree " + badgeCss + "\">" + badgeContent + "</span>";
//                    }
//                }

                map.get(fid).add(new ZTreeModel(TREE_ID_FLOWSVC + id, name, iconSkin, obj));
            }
        } else {
            map = null;
        }
        return map;
    }

    /**
     * 创建流程MAP（key - 流程ID， value - 流程对象）
     */
    private Map<Integer, SimulationFlowModel> toFlowMap(List<SimulationFlowModel> flowList) {
        Map<Integer, SimulationFlowModel> resultMap;
        if (!ListUtils.isEmpty(flowList)) {
            resultMap = new HashMap<>();
            for (SimulationFlowModel obj : flowList) {
                resultMap.put(obj.getId(), obj);
            }
        } else {
            resultMap = null;
        }
        return resultMap;
    }

    /**
     * 创建流程日志MAP（key - 流程ID， value - 流程日志对象）
     */
    private Map<Integer, SimulationTestLogModel> toTestLogMap(List<SimulationTestLogModel> testLogModelList) {
        Map<Integer, SimulationTestLogModel> resultMap;
        if (!ListUtils.isEmpty(testLogModelList)) {
            resultMap = new HashMap<>();
            for (SimulationTestLogModel obj : testLogModelList) {
                resultMap.put(obj.getFid(), obj);
            }
        } else {
            resultMap = null;
        }
        return resultMap;
    }

    /**
     * 创建场景节点
     *
     * @param filterByUsernameFidList 过滤包含步骤的场景
     */
    private List<TreeModel> createFlowTree(List<SimulationFlowModel> flowList, Map<Integer, List<TreeModel>> flowSvcTreeMap,
                                           Map<Integer, SimulationTestLogModel> logMap, List<Integer> filterByUsernameFidList) {
        List<TreeModel> treeList;
        if (!ListUtils.isEmpty(flowList)) {
            treeList = new ArrayList<>();
            TreeModel treeModel;
            SimulationFlowExtModel flowExtModel;
            SimulationTestLogModel logModel;
            Integer fid;
            for (SimulationFlowModel obj : flowList) {
                fid = obj.getId();
                logModel = MapUtils.isEmpty(logMap) ? null : logMap.get(fid);
                flowExtModel = new SimulationFlowExtModel(obj, logModel);
                treeModel = new ZTreeModel(TREE_ID_FLOW + fid, obj.getName(), "org", flowExtModel);
                if (!MapUtils.isEmpty(flowSvcTreeMap) && flowSvcTreeMap.containsKey(fid)) {
                    treeModel.setChildren(flowSvcTreeMap.get(fid));
                }
                if (!ListUtils.isEmpty(treeModel.getChildren())
                        && filterByUsernameFidList != null && !filterByUsernameFidList.contains(fid)) {
                    continue;
                }
                treeList.add(treeModel);
            }
        } else {
            treeList = null;
        }
        return treeList;
    }

    /**
     * 根据用户名获取流程ID
     *
     * @return 如果返回值为null，不需要过滤场景，如果不为null，则需要过滤场景（只过滤包含步骤的场景）
     */
    private List<Integer> getFlowIdByUsername(Map map) throws Exception {
        String username;
        if (MapUtils.isEmpty(map)) {
            username = null;
        } else {
            Object tempStr = map.get("username");
            username = StringUtils.isEmpty(tempStr) ? null : String.valueOf(tempStr);
        }

        if (!StringUtils.isEmpty(username)) {
            Map<String, Object> mapFilterSvc = new HashMap<>();

            UserModel userModel = userService.queryByUsername(username);
            if (userModel == null) {
                throw new Exception("当前登录用户不存在！");
            }
            if (userModel.getRole() == null || userModel.getRole().intValue() == UserEnum.RoleEnum.Normal.getCode()) {
                //添加业务系统筛选条件
                List<Integer> aidList = userAppService.getAidListByUserid(userModel.getId());
                if (ListUtils.isEmpty(aidList)) {
                    throw new Exception("当前登录用户尚未关联业务系统！");
                } else {
                    mapFilterSvc.put("aidList", aidList);
                }

                //根据业务系统获取所属服务
                if (!ListUtils.isEmpty(aidList)) {
                    List<SvcInfoModel> svcInfoModels = svcInfoService.getByAid(aidList);
                    if (!ListUtils.isEmpty(svcInfoModels)) {
                        List<Integer> sidList = new ArrayList<>();
                        for (SvcInfoModel item : svcInfoModels) {
                            sidList.add(item.getId());
                        }
                        mapFilterSvc.put("sidList", sidList);
                    }
                }

                //获取步骤
                List<SimulationFlowSvcModel> flowSvcList = flowSvcService.query(mapFilterSvc);
                //获取相关的流程ID列表
                List<Integer> resultList = new ArrayList<>();
                if (!ListUtils.isEmpty(flowSvcList)) {
                    resultList = new ArrayList<>();
                    for (SimulationFlowSvcModel obj : flowSvcList) {
                        if (!resultList.contains(obj.getId())) {
                            resultList.add(obj.getFid());
                        }
                    }
                }
                return resultList;
            }
        }
        return null;
    }

    /**
     * 获取流程里的步骤（key - 步骤ID， value - 步骤对象）
     *
     * @param flowList 测试场景列表
     */
    private Map<Integer, SimulationFlowSvcModel> getFlowSvc(List<SimulationFlowModel> flowList) {
        if (!ListUtils.isEmpty(flowList)) {
            Map<String, Object> map = new HashMap<>();

            //获取场景ID
            List<Integer> flowIdList = new ArrayList<>();
            for (SimulationFlowModel flow : flowList) {
                flowIdList.add(flow.getId());
            }
            map.put("fidList", flowIdList);

            //获取服务步骤
            List<SimulationFlowSvcModel> flowSvcList = flowSvcService.query(map);
            Map<Integer, SimulationFlowSvcModel> mapResult = new LinkedHashMap<>();
            for (SimulationFlowSvcModel obj : flowSvcList) {
                mapResult.put(obj.getId(), obj);
            }
            return mapResult;
        }
        return null;
    }

    /**
     * 获取流程里的服务对象（key - 服务ID， value - 服务对象）
     */
    private Map<Integer, SvcInfoModel> getSvcModel(Map<Integer, SimulationFlowSvcModel> flowSvcMap) {
        if (!MapUtils.isEmpty(flowSvcMap)) {
            List<Integer> svcIdList = new ArrayList<>();
            for (Map.Entry<Integer, SimulationFlowSvcModel> obj : flowSvcMap.entrySet()) {
                svcIdList.add(obj.getValue().getSid());
            }
            List<SvcInfoModel> svcInfoModelList = svcInfoService.getByID(svcIdList);
            Map<Integer, SvcInfoModel> mapResult = new HashMap<>();
            for (SvcInfoModel svcInfoModel : svcInfoModelList) {
                svcInfoModel.setRawIn(null);
                svcInfoModel.setRawAck(null);
                mapResult.put(svcInfoModel.getId(), svcInfoModel);
            }
            return mapResult;
        }
        return null;
    }

    @Override
    public void receiveStart(Integer tid, SvcInfoModel svcInfoModel) {
        //重置公共变量
        SimulationTestStepLogModel logModel = new SimulationTestStepLogModel();
        logModel.setTid(tid);
        logModel.setSid(svcInfoModel.getId());
        EsbReceiverForTestFlow.init(logModel, svcInfoModel);
    }

    @Override
    public SimulationTestStepLogModel receive() {
        SimulationTestStepLogModel resultLog = EsbReceiverForTestFlow.getLogModel();
        //记录日志
        if (resultLog != null) {
            if (resultLog.getResult() != null && (!StringUtils.isEmpty(resultLog.getOut_msg())
                    || !StringUtils.isEmpty(resultLog.getAck_msg()))) {
                testStepLogService.save(resultLog);
                EsbReceiverForTestFlow.reset();
            }
            return resultLog;
        }
        return null;
    }

    @Override
    public void receiveStop(SimulationTestLogModel obj) {
        testLogService.finishTestLog(obj);
    }

    @Override
    public ResultObject downLogPdf(Integer fid, Integer tid) {
        //服务测试日志综合信息（服务基本信息、提供方、消费方等）
        List<SimulationFlowSvcExtModel> resultList = null;

        //涉及的所有业务系统
        List<AppInfoModel> appInfoModels = null;
        //获取集成测试的概要信息
        SimulationFlowModel flowModel = getByID(fid);
        //获取日志明细
        List<SimulationTestStepLogModel> stepLogModels = testStepLogService.getByTID(tid);
        Map<Integer, List<SimulationTestStepLogModel>> stepLogMap = testStepLogService.mapSidList(stepLogModels);
        if (!MapUtils.isEmpty(stepLogMap)) {
            //获取所有步骤
            List<SimulationFlowSvcModel> flowSvcModels = flowSvcService.queryByFlowID(fid);
            if (!ListUtils.isEmpty(flowSvcModels)) {
                //获取相关服务
                List<SvcInfoModel> svcInfoModels = svcInfoService.getByID(ListUtils.transferToList(stepLogMap.keySet()));
                Map<Integer, SvcInfoModel> svcInfoModelMap = svcInfoService.mapIdObject(svcInfoModels);
                //获取相关业务系统（服务提供方和消费方）
                List<Integer> aidList = new ArrayList<>();
                for (SvcInfoModel item : svcInfoModels) {
                    aidList.add(item.getAid());
                }
                for (SimulationFlowSvcModel item : flowSvcModels) {
                    aidList.add(item.getAid());
                }
                appInfoModels = appInfoService.getByID(aidList);
                Map<Integer, AppInfoModel> appInfoModelMap = appInfoService.mapIdObject(appInfoModels);

                //设置日志综合信息
                resultList = new ArrayList<>();
                Integer sid;
                SimulationFlowSvcExtModel flowSvcExtModel;
                SvcInfoModel svcInfoModel;
                for (SimulationFlowSvcModel item : flowSvcModels) {
                    sid = item.getSid();
                    if (stepLogMap.containsKey(sid)) {
                        flowSvcExtModel = new SimulationFlowSvcExtModel();
                        svcInfoModel = svcInfoModelMap.get(sid);
                        flowSvcExtModel.setSvc(svcInfoModel);
                        flowSvcExtModel.setLogList(stepLogMap.get(sid));
                        flowSvcExtModel.setProvider(appInfoModelMap.get(svcInfoModel.getAid()));
                        flowSvcExtModel.setConsumer(appInfoModelMap.get(item.getAid()));
                        resultList.add(flowSvcExtModel);
                    }
                }
            }
        }

        //返回下载地址
        ResultObject resultObject = new ResultObject();
        if (ListUtils.isEmpty(resultList)) {
            resultObject.setSuccess(false);
            resultObject.setErrorMsg("暂无日志！");
        } else {
            try {
                //根据业务系统获取所属机构
                List<Integer> orgIds = new ArrayList<>();
                for(AppInfoModel item : appInfoModels) {
                    orgIds.add(item.getOrgId());
                }
                List<OrgInfoModel> orgInfoModels = orgInfoService.getByID(orgIds);
                Map<Integer, OrgInfoModel> orgInfoModelMap = orgInfoService.mapIdObj(orgInfoModels);
                //生成机构与业务系统的对应关系（item1 - 机构名称，item2 - 系统名称）
                List<SimpleObject> orgAppList = new ArrayList<>();
                SimpleObject simpleObject;
                for(AppInfoModel item : appInfoModels) {
                    simpleObject = new SimpleObject();
                    simpleObject.setItem1(orgInfoModelMap.get(item.getOrgId()).getName());
                    simpleObject.setItem2(item.getAppName());
                    orgAppList.add(simpleObject);
                }

                //生成PDF
                String fileName = testLogPdfHelper.main(flowModel, resultList, orgAppList);
                if (StringUtils.isEmpty(fileName)) {
                    resultObject.setSuccess(false);
                    resultObject.setErrorMsg("暂无日志！");
                } else {
                    resultObject.setSuccess(true);
                    resultObject.setObj(fileName);
                }
            } catch (Exception ex) {
                resultObject.setSuccess(false);
                resultObject.setErrorMsg("发生异常错误！" + ex.getMessage());
            }
        }
        return resultObject;
    }

}