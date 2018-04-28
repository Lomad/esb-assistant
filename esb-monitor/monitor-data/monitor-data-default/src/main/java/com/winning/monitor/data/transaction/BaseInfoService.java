package com.winning.monitor.data.transaction;

import com.winning.esb.model.AppInfoModel;
import com.winning.esb.model.OrgInfoModel;
import com.winning.esb.model.SvcInfoModel;
import com.winning.esb.model.enums.AppInfoEnum;
import com.winning.esb.model.enums.OrgInfoEnum;
import com.winning.esb.model.enums.SvcInfoEnum;
import com.winning.esb.service.IAppInfoService;
import com.winning.esb.service.IOrgInfoService;
import com.winning.esb.service.ISvcInfoService;
import com.winning.esb.stable.NormalConst;
import com.winning.esb.utils.DateUtils;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.MapUtils;
import com.winning.esb.utils.StringUtils;
import com.winning.monitor.data.api.IBaseInfoService;
import com.winning.monitor.data.api.enums.TransactionReportsEnum;
import com.winning.monitor.data.storage.api.IBaseInfoStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by xuehao on 17/12/18.
 */
@Service
public class BaseInfoService implements IBaseInfoService {

    @Autowired
    private IBaseInfoStorage storage;
    @Autowired
    private IOrgInfoService orgInfoService;
    @Autowired
    private IAppInfoService appInfoService;
    @Autowired
    private ISvcInfoService svcInfoService;


    @Override
    public void loopAppFromRealtimeReport(int timeType) {
        if (timeType != Calendar.DAY_OF_MONTH && timeType != Calendar.HOUR_OF_DAY) {
            return;
        }

        Date now = new Date();
        Date startTime = DateUtils.addDate(now, timeType, -1, true);
        String strStartTime = DateUtils.toDateString(startTime.getTime());   //开始时间：上一小时或昨天
        String strEndTime = DateUtils.toDateString(now.getTime());           //当前小时

        List<AppInfoModel> appInfoModelsNew = new ArrayList<>();
        List<AppInfoModel> appInfoModelsEdit = new ArrayList<>();

        //获取默认的机构ID
        Integer orgId = getDefaultOrgId();

        //获取所有的业务系统
        List<AppInfoModel> appInfoModels = appInfoService.list();
        Map<String, AppInfoModel> appIdObjMap = appInfoService.mapAppIdObject(appInfoModels);

        //获取提供方
        List<String> appIdProviders = loopProviderFromRealtimeReport(TransactionReportsEnum.GROUP_BI, strStartTime, strEndTime);
        //获取消费方
        List<String> appIdConsumers = loopConsumerFromRealtimeReport(TransactionReportsEnum.GROUP_BI, strStartTime, strEndTime);
        //获取提供方与消费方双重角色的系统AppId
        List<String> appIdProviderConsumers = new ArrayList<>();
        for (String appId : appIdConsumers) {
            if (appIdProviders.contains(appId)) {
                appIdProviderConsumers.add(appId);
            }
        }
        if (!ListUtils.isEmpty(appIdProviderConsumers)) {
            appIdProviders.removeAll(appIdProviderConsumers);
            appIdConsumers.removeAll(appIdProviderConsumers);
        }

        //生成提供方与消费方双重角色的业务系统对象
        createAppModel(appInfoModelsNew, appInfoModelsEdit, appIdObjMap, appIdProviderConsumers, orgId, AppInfoEnum.DirectionEnum.All);
        //生成提供方的业务系统对象
        createAppModel(appInfoModelsNew, appInfoModelsEdit, appIdObjMap, appIdProviders, orgId, AppInfoEnum.DirectionEnum.Provider);
        //生成消费方的业务系统对象
        createAppModel(appInfoModelsNew, appInfoModelsEdit, appIdObjMap, appIdConsumers, orgId, AppInfoEnum.DirectionEnum.Consumer);

        //新增
        if (!ListUtils.isEmpty(appInfoModelsNew)) {
            appInfoService.insert(appInfoModelsNew);
        }
        //修改
        if (!ListUtils.isEmpty(appInfoModelsEdit)) {
            appInfoService.update(appInfoModelsEdit);
        }
    }

    /**
     * 获取默认的机构ID（优先级：卫宁健康 > 医院信息平台 > 医院数据平台）
     */
    private Integer getDefaultOrgId() {
        Integer orgId = null;
        List<OrgInfoModel> orgInfoModels = orgInfoService.getByName(Arrays.asList(
                OrgInfoEnum.NameEnum.Winning.getValue(),
                OrgInfoEnum.NameEnum.ESB.getValue(),
                OrgInfoEnum.NameEnum.Data.getValue()));
        if (!ListUtils.isEmpty(orgInfoModels)) {
            Map<String, Integer> nameIdMap = new HashMap<>();
            for (OrgInfoModel orgInfoModel : orgInfoModels) {
                nameIdMap.put(orgInfoModel.getName(), orgInfoModel.getId());
            }

            if (nameIdMap.containsKey(OrgInfoEnum.NameEnum.Winning.getValue())) {
                //匹配“卫宁健康”
                orgId = nameIdMap.get(OrgInfoEnum.NameEnum.Winning.getValue());
            } else if (nameIdMap.containsKey(OrgInfoEnum.NameEnum.ESB.getValue())) {
                //匹配“医院信息平台”
                orgId = nameIdMap.get(OrgInfoEnum.NameEnum.ESB.getValue());
            } else if (nameIdMap.containsKey(OrgInfoEnum.NameEnum.Data.getValue())) {
                //匹配“医院数据平台”
                orgId = nameIdMap.get(OrgInfoEnum.NameEnum.Data.getValue());
            }
        }
        if (orgId == null) {
            orgId = 0;
        }

        return orgId;
    }

    /**
     * 生成业务系统对象
     */
    private void createAppModel(List<AppInfoModel> appInfoModelsNew, List<AppInfoModel> appInfoModelsEdit,
                                Map<String, AppInfoModel> appIdObjMap, List<String> appIdList, Integer orgId,
                                AppInfoEnum.DirectionEnum directionEnum) {
        //生成对象
        if (!ListUtils.isEmpty(appIdList)) {
            AppInfoModel appInfoModel;
            for (String appId : appIdList) {
                if (!StringUtils.isEmpty(appId)) {
                    if (MapUtils.isEmpty(appIdObjMap) || !appIdObjMap.containsKey(appId)) {
                        appInfoModel = new AppInfoModel();
                        appInfoModel.setAppId(appId);
                        appInfoModel.setAppName(appId);
                        appInfoModel.setOrgId(orgId);
                        appInfoModel.setAppType(AppInfoEnum.AppTypeEnum.Normal.getCode());
                        appInfoModel.setDirection(directionEnum.getCode());
                        appInfoModel.setStatus(AppInfoEnum.StatusEnum.Normal.getCode());
                        appInfoModel.setOrder_num(0);
                        appInfoModel.setDesp(NormalConst.AUTO_SCAN_DESP);
                        appInfoModel.setCtime(new Date());
                        appInfoModel.setMtime(appInfoModel.getCtime());
                        appInfoModelsNew.add(appInfoModel);
                    } else {
                        appInfoModel = appIdObjMap.get(appId);
                        if (!appInfoModel.getDirection().equals(directionEnum.getCode())
                                && !appInfoModel.getDirection().equals(AppInfoEnum.DirectionEnum.All.getCode())
                                && NormalConst.AUTO_SCAN_DESP.equals(appInfoModel.getDesp())) {
                            appInfoModel.setMtime(new Date());
                            appInfoModel.setDirection(AppInfoEnum.DirectionEnum.All.getCode());
                            appInfoModel.setDesp(NormalConst.AUTO_SCAN_DESP);
                            appInfoModelsEdit.add(appInfoModel);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void loopSvcFromRealtimeReport(int timeType) {
        if (timeType != Calendar.DAY_OF_MONTH && timeType != Calendar.HOUR_OF_DAY) {
            return;
        }

        Date now = new Date();
        Date startTime = DateUtils.addDate(now, timeType, -1, true);
        String strStartTime = DateUtils.toDateString(startTime.getTime());   //开始时间：上一小时或昨天
        String strEndTime = DateUtils.toDateString(now.getTime());           //当前小时

        //获取所有服务
        Map<String, List<String>> appIdSvcCodeMap = loopSvcFromRealtimeReport(TransactionReportsEnum.GROUP_BI, null,
                strStartTime, strEndTime);
        if (!MapUtils.isEmpty(appIdSvcCodeMap)) {
            //获取服务列表
            List<String> querySvcCodeList = new ArrayList<>();
            for (Map.Entry<String, List<String>> item : appIdSvcCodeMap.entrySet()) {
                querySvcCodeList.addAll(item.getValue());
            }
            Map<String, SvcInfoModel> mapSvcCodeObj = svcInfoService.mapCodeObj(querySvcCodeList);
            //获取业务系统信息
            Map<String, AppInfoModel> appInfoModelMap = appInfoService.mapAppIdObj(ListUtils.transferToList(appIdSvcCodeMap.keySet()));

            //生成服务对象
            String appId;
            List<String> svcCodeList;
            for (Map.Entry<String, List<String>> item : appIdSvcCodeMap.entrySet()) {
                appId = item.getKey();
                svcCodeList = item.getValue();
                //遍历服务代码，检测在数据库中是否已经存在，如果不存在，则需要新增
                for (String svcCode : svcCodeList) {
                    //如果服务代码在Map中不存在，且所属系统代码存在
                    if (!MapUtils.isEmpty(appInfoModelMap) && appInfoModelMap.containsKey(appId)
                            && !MapUtils.existKey(mapSvcCodeObj, svcCode)) {
                        insertSvc(svcCode, appInfoModelMap.get(appId).getId());
                    }
                }
            }
        }
    }

    /**
     * 新增服务
     */
    private void insertSvc(String svcCode, Integer aid) {
        if (!StringUtils.isEmpty(svcCode)) {
            SvcInfoModel svcInfoModel = new SvcInfoModel();
            svcInfoModel.setCode(svcCode);
            svcInfoModel.setName(svcCode);
            svcInfoModel.setVersion("0");
            svcInfoModel.setAid(aid);
            svcInfoModel.setMsgType("");
            svcInfoModel.setDataProtocal(SvcInfoEnum.DataProtocalEnum.No.getCode());
            svcInfoModel.setOtherMark(SvcInfoEnum.OtherMarkEnum.No.getCode());
            svcInfoModel.setStatus(SvcInfoEnum.StatusEnum.Unpublished.getCode());
            svcInfoModel.setDesp(NormalConst.AUTO_SCAN_DESP);
            svcInfoModel.setCtime(new Date());
            svcInfoModel.setMtime(svcInfoModel.getCtime());
            svcInfoService.insert(svcInfoModel);
        }
    }

    @Override
    public List<String> loopProviderToday() {
        Date now = new Date();
        Date startTime = DateUtils.addDate(now, Calendar.DAY_OF_MONTH, 0, true);
        String strStartTime = DateUtils.toDateString(startTime.getTime());   //开始时间
        String strEndTime = DateUtils.toDateString(now.getTime());           //当前小时
        return loopProviderFromRealtimeReport(TransactionReportsEnum.GROUP_BI, strStartTime, strEndTime);
    }

    @Override
    public List<String> loopProviderFromRealtimeReport(String group, String startTime, String endTime) {
        return storage.loopProviderFromRealtimeReport(group, startTime, endTime);
    }

    @Override
    public List<String> loopConsumerToday() {
        Date now = new Date();
        Date startTime = DateUtils.addDate(now, Calendar.DAY_OF_MONTH, 0, true);
        String strStartTime = DateUtils.toDateString(startTime.getTime());   //开始时间
        String strEndTime = DateUtils.toDateString(now.getTime());           //当前小时
        return loopConsumerFromRealtimeReport(TransactionReportsEnum.GROUP_BI, strStartTime, strEndTime);
    }

    @Override
    public List<String> loopConsumerFromRealtimeReport(String group, String startTime, String endTime) {
        return storage.loopConsumerFromRealtimeReport(group, startTime, endTime);
    }

    @Override
    public List<String> loopSvcToday(List<String> appIds) {
        Date now = new Date();
        Date startTime = DateUtils.addDate(now, Calendar.DAY_OF_MONTH, 0, true);
        String strStartTime = DateUtils.toDateString(startTime.getTime());   //开始时间
        String strEndTime = DateUtils.toDateString(now.getTime());           //当前小时
        List<String> resultList = new ArrayList<>();

        //获取所有服务
        Map<String, List<String>> appIdSvcCodeMap = loopSvcFromRealtimeReport(TransactionReportsEnum.GROUP_BI, appIds,
                strStartTime, strEndTime);

        if (!MapUtils.isEmpty(appIdSvcCodeMap)) {
            //获取服务列表
            List<String> querySvcCodeList = new ArrayList<>();
            for (Map.Entry<String, List<String>> item : appIdSvcCodeMap.entrySet()) {
                querySvcCodeList.addAll(item.getValue());
            }
            Map<String, SvcInfoModel> mapSvcCodeObj = svcInfoService.mapCodeObj(querySvcCodeList);
            //获取业务系统信息
            Map<String, AppInfoModel> appInfoModelMap = appInfoService.mapAppIdObj(ListUtils.transferToList(appIdSvcCodeMap.keySet()));
            Map<Integer, AppInfoModel> mapIdObjApp = MapUtils.isEmpty(appInfoModelMap) ? null
                    : appInfoService.mapIdObject(ListUtils.transferToList(appInfoModelMap.values()));
            //生成服务对象
            String appId;
            List<String> svcCodeList;
            for (Map.Entry<String, List<String>> item : appIdSvcCodeMap.entrySet()) {
                appId = item.getKey();
                svcCodeList = item.getValue();
                //遍历服务代码，检测在数据库中是否已经存在，如果不存在，则需要新增
                for (String svcCode : svcCodeList) {
                    //如果服务代码存在，且所属系统代码存在
                    if (!MapUtils.isEmpty(mapIdObjApp) && MapUtils.existKey(mapSvcCodeObj, svcCode)) {
                        SvcInfoModel svcObj = mapSvcCodeObj.get(svcCode);
                        if (svcObj != null) {
                            Integer aid = svcObj.getAid();
                            if (aid != null) {
                                AppInfoModel appInfoModel = mapIdObjApp.get(aid);
                                if (appInfoModel != null) {
                                    String appId2 = appInfoModel.getAppId();
                                    if (!StringUtils.isEmpty(appId) && appId2.equals(appId)) {
                                        resultList.add(svcCode);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return resultList;
    }

    @Override
    public Map<String, List<String>> loopSvcFromRealtimeReport(String group, Object appId, String startTime, String endTime) {
        return storage.loopSvcFromRealtimeReport(group, appId, startTime, endTime);
    }

}