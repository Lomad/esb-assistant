package com.winning.monitor.data.transaction;

import com.winning.esb.model.AppInfoModel;
import com.winning.esb.model.SvcInfoModel;
import com.winning.esb.model.enums.AppInfoEnum;
import com.winning.esb.service.IAppInfoService;
import com.winning.esb.service.ISvcInfoService;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.StringUtils;
import com.winning.monitor.agent.logging.message.MessageTree;
import com.winning.monitor.data.api.IErrorOverViewService;
import com.winning.monitor.data.api.enums.DateType;
import com.winning.monitor.data.api.transaction.domain.TransactionMessage;
import com.winning.monitor.data.api.transaction.domain.TransactionMessageList;
import com.winning.monitor.data.storage.api.IErrorOverViewStorage;
import com.winning.monitor.data.storage.api.entity.MessageTreeList;
import com.winning.monitor.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.winning.monitor.data.transaction.utils.DataUtils.toTransactionMessage;

/**
 * Created by nicholasyan on 16/10/20.
 */
@Service
public class ErrorOverViewService implements IErrorOverViewService {
    @Autowired
    private IErrorOverViewStorage dao;

    @Autowired
    private IAppInfoService appInfoService;
    @Autowired
    private ISvcInfoService svcInfoService;

    @Override
    public List<Integer> countErrorProviders(Map<String, Object> map) {
        //设置筛选条件
        handleQueryMap(map);
        //获取系统代码列表
        List<String> appIds = dao.countErrorProviders(map);
        //获取系统列表
        List<AppInfoModel> appInfoModels = appInfoService.getByAppId(appIds);
        //获取系统主键id列表
        List<Integer> idList;
        if (!ListUtils.isEmpty(appInfoModels)) {
            idList = new ArrayList<>();
            for (AppInfoModel item : appInfoModels) {
                if (item.getStatus() == null || AppInfoEnum.StatusEnum.Normal.getCode() == item.getStatus().intValue()) {
                    idList.add(item.getId());
                }
            }
        } else {
            idList = null;
        }
        return idList;
    }

    @Override
    public List<Map<String, Object>> countErrorList(Map<String, Object> map) {
        //设置筛选条件
        handleQueryMap(map);
        //获取初步的统计结果
        List<Map<String, Object>> list = dao.countErrorList(map);
        //设置服务名称
        if (!ListUtils.isEmpty(list)) {
            //获取服务代码列表
            List<String> svcCodeList = new ArrayList<>();
            for (Map<String, Object> item : list) {
                svcCodeList.add(item.get("svcCode").toString());
            }
            //根据服务代码获取代码与名称的映射关系
            Map<String, String> svcCodeNameMap = svcInfoService.mapCodeName(svcCodeList);
            //设置服务名称
            String svcCode;
            for (Map<String, Object> item : list) {
                svcCode = item.get("svcCode").toString();
                item.put("svcName", svcCodeNameMap.get(svcCode));
            }
        }
        return list;
    }


    @Override
    public TransactionMessageList queryTodayErrorMessageList(String serverId, String keyWords, int startIndex, int pageSize) {
        //获取当天时间
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long todayStartTimestamp = calendar.getTimeInMillis();

        MessageTreeList messageList = dao.queryTodayErrorMessageList(serverId, todayStartTimestamp, keyWords, startIndex, pageSize);

        //获取业务系统的系统代码与名称的映射
        Map<String, String> appIdNameConsumerMap;
        Map<String, SvcInfoModel> svcCodeObjMap;
        if (messageList.getTotalSize() > 0) {
            List<String> appIdProviders = new ArrayList<>();
            List<String> appIdConsumers = new ArrayList<>();
            for (MessageTree messageTree : messageList.getMessageTrees()) {
                //获取提供方业务系统信息
                if(!appIdProviders.contains(messageTree.getDomain())) {
                    appIdProviders.add(messageTree.getDomain());
                }
                //获取消费方业务系统信息
                if (messageTree.getCaller() != null && !StringUtils.isEmpty(messageTree.getCaller().getName())
                        && !appIdConsumers.contains(messageTree.getCaller().getName())) {
                    appIdConsumers.add(messageTree.getCaller().getName());
                }
            }
            //获取消费方系统信息
            appIdNameConsumerMap = ListUtils.isEmpty(appIdConsumers) ? null : appInfoService.mapAppIdName(appIdConsumers);

            //获取提供方系统包含的服务信息
            List<SvcInfoModel> svcInfoModels = svcInfoService.getByAppId(appIdProviders);
            svcCodeObjMap = svcInfoService.listToMapCodeObject(svcInfoModels);
        } else {
            appIdNameConsumerMap = null;
            svcCodeObjMap = null;
        }

        //生成返回对象
        TransactionMessageList transactionMessageList = new TransactionMessageList();
        transactionMessageList.setTotalSize(messageList.getTotalSize());

        for (MessageTree messageTree : messageList.getMessageTrees()) {
            TransactionMessage transactionMessage = toTransactionMessage(messageTree, appIdNameConsumerMap, svcCodeObjMap);
            transactionMessageList.addTransactionMessage(transactionMessage);
        }
        return transactionMessageList;
    }

    /**
     * 处理查询的Map（有些需要二次处理后才能提交查询）
     */
    private void handleQueryMap(Map<String, Object> map) {
        //设置开始时间与结束时间
        Long now = DateUtils.getCurrentTime();
        String endTime = DateUtils.toDateString(now);
        Long start;
        int timeType = Integer.parseInt(map.get("timeType").toString());
        if (timeType == DateType.CURRENTWEEK.getKey()) {
            start = DateUtils.getStartTime(now, DateType.WEEK);
        } else if (timeType == DateType.CURRENTMONTH.getKey()) {
            start = DateUtils.getStartTime(now, DateType.MONTH);
        } else {
            start = DateUtils.getStartTime(now, DateType.DAY);
        }
        String startTime = DateUtils.toDateString(start);
        map.remove("timeType");
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        //根据机构代码获取所属的系统代码
        Object orgId = map.get("orgId");
        if (!StringUtils.isEmpty(orgId)) {
            Map<String, Object> mapOrg = new HashMap<>();
            mapOrg.put("orgId", orgId);
            List<String> appIds = appInfoService.listAppId(appInfoService.listActive(mapOrg));
            map.remove("orgId");
            map.put("appIdList", appIds);
        }
    }
}