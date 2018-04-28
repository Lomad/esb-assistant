package com.winning.esb.service.impl;

import com.winning.esb.dao.ISvcInfoDao;
import com.winning.esb.model.AppInfoModel;
import com.winning.esb.model.GrantModel;
import com.winning.esb.model.SvcInfoModel;
import com.winning.esb.model.SvcUrlModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.model.enums.QueryParameterKeys;
import com.winning.esb.model.enums.SvcInfoEnum;
import com.winning.esb.model.enums.SvcStructureEnum;
import com.winning.esb.model.ext.SvcInfoExtModel;
import com.winning.esb.service.*;
import com.winning.esb.service.pdf.PdfHelper;
import com.winning.esb.service.taskmark.SyncToEsbMark;
import com.winning.esb.stable.NormalConst;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.MapUtils;
import com.winning.esb.utils.RegexUtils;
import com.winning.esb.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by xuehao on 2017/8/9.
 */
@Service
public class SvcInfoServiceImpl implements ISvcInfoService {
    @Autowired
    private ISvcInfoDao dao;
    @Autowired
    private ISvcStructureService svcStructureService;
    @Autowired
    private ISvcGroupService svcGroupService;
    @Autowired
    private ISvcUrlService svcUrlService;
    @Autowired
    private IGrantService grantService;
    @Autowired
    private IAppInfoService appInfoService;

    @Override
    public SimpleObject save(SvcInfoModel obj) {
        String err = "";
        String regexStr = "^[\\w\\d_-]*$";
        obj.setMtime(new Date());
        if (StringUtils.isEmpty(obj.getCode())) {
            err += "代码不能为空！";
        } else if (existCodeOrNameWithVersion(obj.getId(), "code", obj.getCode(), obj.getVersion())) {
            err += "代码已存在！";
        } else if (!RegexUtils.match(regexStr, obj.getCode())) {
            err += "代码含有特殊字符，只能是英文字母，数字，下划线和连接符-的组合！";
        }
        if (StringUtils.isEmpty(obj.getName())) {
            err += "名称不能为空！";
        }
        if (StringUtils.isEmpty(obj.getVersion())) {
            err += "版本号不能为空！";
        }
//        else if (existCodeOrNameWithVersion(obj.getId(), "name", obj.getName(), obj.getVersion()))
//            err += "名称已存在！";
        if (obj.getUrlId() == null && StringUtils.isEmpty(obj.getUrl())) {
            err += "服务源地址不能为空！";
        } else if (obj.getUrlId() == null && !StringUtils.isEmpty(obj.getUrl())) {
            err += svcUrlService.checkUrl(obj.getUrl());
        }
        if (obj.getOtherMark() == null) {
            obj.setOtherMark(SvcInfoEnum.OtherMarkEnum.No.getCode());
        }
        if (StringUtils.isEmpty(err)) {
            if (obj.getId() == null) {
                obj.setCtime(obj.getMtime());
                obj.setId(insert(obj));
            } else {
                //清除自动扫描的描述
                if(NormalConst.AUTO_SCAN_DESP.equals(obj.getDesp())) {
                    obj.setDesp("");
                }
                dao.update(obj);
            }

            if (obj.getOtherMark().intValue() == SvcInfoEnum.OtherMarkEnum.WinningCdrApiToken.getCode()) {
                SyncToEsbMark.setSyncToken_Test(true);
            }
        }
        SimpleObject result = new SimpleObject();
        if (obj.getId() != null) {
            result.setItem1(obj.getId().toString());
        }
        result.setItem2(err);
        return result;
    }

    @Override
    public Integer insert(SvcInfoModel obj) {
        obj.setStatus(SvcInfoEnum.StatusEnum.Unpublished.getCode());
        return dao.insert(obj);
    }

    @Override
    public void updateRawContent(Integer id, Integer svcStructureDirection, String rawContent) {
        dao.updateRawContent(id, svcStructureDirection, rawContent);
    }

    @Override
    public String delete(Integer id) {
        List<Integer> idList = new ArrayList<>();
        idList.add(id);
        return delete(idList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String delete(List<Integer> idList) {
        String err = "";
        //判断服务是否已经被授权，如果有，则无法删除
        List<GrantModel> grantModels = grantService.getSvcGranted(idList);
        if (!ListUtils.isEmpty(grantModels)) {
            List<Integer> grantSvcIds = new ArrayList<>();
            for (GrantModel model : grantModels) {
                if (!grantSvcIds.contains(model.getSid())) {
                    grantSvcIds.add(model.getSid());
                }
            }
            //获取服务对象，用于返回的错误提示
            List<SvcInfoModel> objList = getByID(grantSvcIds);
            List<String> names = new ArrayList<>();
            for (int i = 0, len = objList.size(); i < len; i++) {
                names.add(objList.get(i).getName());
                //最多返回三个服务名称
                if (i >= 2) {
                    break;
                }
            }
            String nameInfo = StringUtils.join(names, "、");
            if (names.size() > 2) {
                nameInfo += " …";
            }
            err = "有" + grantSvcIds.size() + "个服务(" + nameInfo + ")已被授权，无法删除！";
        }

        //删除授权申请
        if (StringUtils.isEmpty(err)) {
            err = grantService.deleteBySid(idList);
        }
        //从模拟测试表删除
        if (StringUtils.isEmpty(err)) {
            List<SvcInfoModel> objList = getByID(idList);
        }
        //删除服务
        if (StringUtils.isEmpty(err)) {
            dao.delete(idList);
        }

        //删除服务参数
        if (StringUtils.isEmpty(err)) {
            err = svcStructureService.deleteBySid(idList);
        }

        return err;
    }

    @Override
    public String publish(List<Integer> idList) {
        dao.updateStatus(idList, SvcInfoEnum.StatusEnum.Published.getCode());
        //同步到ESB
        SyncToEsbMark.setSyncSvcApp(true);
        //设置同步ESB的标志
        SyncToEsbMark.setSyncUrl(true);
        return null;
    }

    @Override
    public String rollback(List<Integer> idList) {
        dao.updateStatus(idList, SvcInfoEnum.StatusEnum.Rollbacked.getCode());
        //同步到ESB
        SyncToEsbMark.setSyncSvcApp(true);
        //设置同步ESB的标志
        SyncToEsbMark.setSyncUrl(true);
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String copy(List<Integer> idList, List<Integer> aidList) {
        StringBuilder err = new StringBuilder();
        StringBuilder errItem;
        StringBuilder successInfo = new StringBuilder();
        String error = "";
        List<SvcInfoModel> svcInfoModels = dao.getByID(idList);
        if (ListUtils.isEmpty(svcInfoModels)) {
            return "请选择服务！";
        }
        List<Integer> providerAidList = new ArrayList<>();
        for (SvcInfoModel item : svcInfoModels) {
            providerAidList.add(item.getAid());
        }
        providerAidList.addAll(aidList);
        List<AppInfoModel> appInfoModels = appInfoService.getByID(providerAidList);
        Map<Integer, AppInfoModel> appInfoModelMap = appInfoService.mapIdObject(appInfoModels);
        String appId;
        for (Integer aidTarget : aidList) {
            if (!appInfoModelMap.containsKey(aidTarget)) {
                continue;
            }

            AppInfoModel appInfoModel = appInfoModelMap.get(aidTarget);
            appId = appInfoModel.getAppId();
            errItem = new StringBuilder();
            for (SvcInfoModel obj : svcInfoModels) {
                Integer sid = obj.getId();
                AppInfoModel appObj = appInfoModelMap.get(obj.getAid());
                String oldAppId = appObj.getAppId();
                obj.setId(null);
                String code = obj.getCode();

                //替换服务代码
                String newCode;
                if (code.startsWith(oldAppId)) {
                    newCode = appId + code.substring(oldAppId.length());
                } else {
                    newCode = appId + code;
                }
                List<String> codeList = new ArrayList<>(2);
                codeList.add(newCode);
                List<SvcInfoModel> svcInfoModelList = dao.getByCode(codeList);
                if (ListUtils.isEmpty(svcInfoModelList)) {
                    obj.setCode(newCode);
                    obj.setAid(aidTarget);
                    Integer newSid = dao.insert(obj);
                    //复制请求结构
                    error = svcStructureService.importStruncture(newSid, SvcStructureEnum.DirectionEnum.In.getCode(), sid, SvcStructureEnum.DirectionEnum.In.getCode());
                    if (error != null) {
                        errItem.append(error);
                    }
                    //复制应答结构
                    error = svcStructureService.importStruncture(newSid, SvcStructureEnum.DirectionEnum.Ack.getCode(), sid, SvcStructureEnum.DirectionEnum.Ack.getCode());
                    if (error != null) {
                        errItem.append(error);
                    }
                }
            }
            if (errItem.length() < 1) {
                successInfo.append(appInfoModel.getAppName()).append("复制成功！");
            } else {
                err.append(errItem);
            }
        }
        if (err.length() > 0) {
            err.insert(0, successInfo);
        }
        return err.toString();
    }

    @Override
    public List<Integer> aidListFromSidList(List<Integer> idList) {
        List<Integer> aidList = new ArrayList<>(10);
        List<SvcInfoModel> svcInfoModels = getByID(idList);
        for (SvcInfoModel obj : svcInfoModels) {
            Integer aid = obj.getAid();
            if (!aidList.contains(aid)) {
                aidList.add(aid);
            }
        }
        return aidList;
    }

    @Override
    public SvcInfoModel getByID(Integer id) {
        List<Integer> idList = new ArrayList<>();
        idList.add(id);
        List<SvcInfoModel> list = getByID(idList);
        return ListUtils.isEmpty(list) ? null : list.get(0);
    }

    @Override
    public List<SvcInfoModel> getByID(List<Integer> idList) {
        return dao.getByID(idList);
    }

    @Override
    public List<SvcInfoModel> getByAppId(String appId) {
        List<String> appIdList = new ArrayList<>();
        appIdList.add(appId);
        return getByAppId(appIdList);
    }

    @Override
    public List<SvcInfoModel> getByAppId(List<String> appIdList) {
        return dao.getByAppId(appIdList);
    }

    @Override
    public List<SvcInfoModel> getByCode(String code) {
        List<String> codeList = new ArrayList<>();
        codeList.add(code);
        List<SvcInfoModel> list = getByCode(codeList);
        return list;
    }

    @Override
    public List<SvcInfoModel> getByCode(List<String> codeList) {
        return dao.getByCode(codeList);
    }

    @Override
    public List<String> listCode(Map map) {
        //处理查询的Map
        handleQueryMap(map);
        return dao.listCode(map);
    }

    @Override
    public CommonObject query(Map map) {
        //处理查询的Map
        handleQueryMap(map);
        //查询
        CommonObject commonObject = dao.query(map);
        //是否需要返回原始消息结构字段
        if (!MapUtils.isEmpty(map)) {
            Object temp = map.get("needRaw");
            Boolean needRaw = StringUtils.isEmpty(temp) ? null : (Boolean) temp;
            if (needRaw == null || !needRaw.booleanValue()) {
                List<SvcInfoModel> svcInfoModels = ListUtils.transferToList(commonObject.getDatas());
                if (!ListUtils.isEmpty(svcInfoModels)) {
                    for (SvcInfoModel svcInfoModel : svcInfoModels) {
                        svcInfoModel.setRawIn(null);
                        svcInfoModel.setRawAck(null);
                    }
                }
            }
        }

        return commonObject;
    }

    /**
     * 处理查询的Map（有些需要二次处理后才能提交查询）
     */
    private void handleQueryMap(Map map) {
        if (!MapUtils.isEmpty(map)) {
            //如果包含机构代码，则需要获取机构所属的所有系统ID
            Object temp = map.get("orgId");
            if (!StringUtils.isEmpty(temp)) {
                Map<String, Object> mapOrg = new HashMap<>();
                mapOrg.put("orgId", temp);
                List<AppInfoModel> appInfoModels = appInfoService.listActive(mapOrg);
                List<Integer> aidList = new ArrayList<>();
                for (AppInfoModel model : appInfoModels) {
                    aidList.add(model.getId());
                }
                map.put("aidList", aidList);
            }
        }
    }

    @Override
    public CommonObject queryExt(Map map) {
        CommonObject commonObject = query(map);
        List<SvcInfoModel> svcInfoModels = ListUtils.transferToList(commonObject.getDatas());
        commonObject.setDatas(svcInfoExtModelList(svcInfoModels));
        return commonObject;
    }

    @Override
    public int count(Map map) {
        return dao.count(map);
    }

    @Override
    public List<SvcInfoModel> list(Map map) {
        CommonObject commonObject = query(map);
        return ListUtils.transferToList(commonObject.getDatas());
    }

    @Override
    public List<SvcInfoExtModel> listExt(Map map) {
        CommonObject commonObject = queryExt(map);
        return ListUtils.transferToList(commonObject.getDatas());
    }

    @Override
    public List<Map<String, Object>> listDownload(Integer aid, Integer svcDirection) {
        return dao.listDownload(aid, svcDirection);
    }

    @Override
    public SimpleObject download(Integer aid, List<Integer> sidList) {
        SimpleObject result = new SimpleObject();
        try {
            PdfHelper pdfHelper = new PdfHelper();
            AppInfoModel appInfoModel = appInfoService.getByID(aid);
            Map<String, List<SvcInfoExtModel>> svcInfoExtModelMap = new LinkedHashMap<>();
            if (!ListUtils.isEmpty(sidList)) {
                SvcInfoExtModel svcInfoExtModel;
                String groupName, msgType;
                String paragraphIndex = "5";    //段落索引
                for (int i = 0, len = sidList.size(); i < len; i++) {
                    Integer sid = sidList.get(i);
                    svcInfoExtModel = new SvcInfoExtModel();
                    svcInfoExtModel.setParagraphIndexLevel1(paragraphIndex);
                    svcInfoExtModel.setParagraphIndexLevel2(String.valueOf(i + 1));
                    svcInfoExtModel.setSvcInfo(getByID(sid));
                    msgType = svcInfoExtModel.getSvcInfo().getMsgType();
                    svcInfoExtModel.setInContent(svcStructureService.exportMsg(sid, SvcStructureEnum.DirectionEnum.In.getCode(),
                            msgType, SvcStructureEnum.ValueTypeEnum.Name.getCode()));
                    svcInfoExtModel.setOutContent(svcStructureService.exportMsg(sid, SvcStructureEnum.DirectionEnum.Ack.getCode(),
                            msgType, SvcStructureEnum.ValueTypeEnum.Name.getCode()));
                    //在服务名称前加上段落索引号
                    svcInfoExtModel.getSvcInfo().setName(svcInfoExtModel.getSvcInfo().getName());
                    //获取分组名称
                    groupName = paragraphIndex + ". " + appInfoModel.getAppName() + "参数结构说明";
                    if (!svcInfoExtModelMap.containsKey(groupName)) {
                        svcInfoExtModelMap.put(groupName, new ArrayList<>());
                    }
                    svcInfoExtModelMap.get(groupName).add(svcInfoExtModel);
                }
            }
            //生成pdf文档
            String fileName = pdfHelper.createServiceDespcription(svcInfoExtModelMap);
            //合并到模版文档
            String fileNameNew = PdfHelper.FILE_PATH + fileName;
            String targetFileName = UUID.randomUUID().toString() + ".pdf";
            List<String> files = new ArrayList<>();
            files.add(fileNameNew);
            pdfHelper.mergeToTemplate(files, PdfHelper.FILE_PATH + targetFileName);

            result.setItem2("/download/pdf/" + targetFileName);
        } catch (Exception e) {
            e.printStackTrace();
            result.setItem1(e.getMessage());
        }

        return result;
    }

    @Override
    public List<SimpleObject> listIdName() {
        CommonObject commonObject = dao.query(null);
        List<SvcInfoModel> svcInfoModels = ListUtils.transferToList(commonObject.getDatas());
        return createSimpleObject(svcInfoModels);
    }

    @Override
    public List<SimpleObject> listIdNameByAidNotIn(Integer aidNotIn) {
        List<Integer> aidNotInList;
        if (aidNotIn != null) {
            aidNotInList = new ArrayList<>();
            aidNotInList.add(aidNotIn);
        } else {
            aidNotInList = null;
        }
        return listIdNameByAidNotIn(aidNotInList);
    }

    @Override
    public List<SimpleObject> listIdNameByAidNotIn(List<Integer> aidNotInList) {
        List<SvcInfoModel> svcInfoModels = getByAidNotIn(aidNotInList);
        return createSimpleObject(svcInfoModels);
    }

    private List<SimpleObject> createSimpleObject(List<SvcInfoModel> svcInfoModels) {
        List<SimpleObject> resultList = new ArrayList<>();
        if (ListUtils.isEmpty(svcInfoModels)) {
            resultList = null;
        } else {
            for (SvcInfoModel svcInfoModel : svcInfoModels) {
                resultList.add(new SimpleObject(String.valueOf(svcInfoModel.getId()), svcInfoModel.getName()));
            }
        }
        return resultList;
    }

    @Override
    public Map<Integer, SvcInfoModel> mapIdObject(List<SvcInfoModel> objs) {
        Map<Integer, SvcInfoModel> map;
        if (!ListUtils.isEmpty(objs)) {
            map = new HashMap<>();
            for (SvcInfoModel obj : objs) {
                map.put(obj.getId(), obj);
            }
        } else {
            map = null;
        }
        return map;
    }

    @Override
    public Map<String, String> mapCodeName(List<String> codeList) {
        Map<String, Object> mapQuery = new HashMap<>();
        if (!ListUtils.isEmpty(codeList)) {
            mapQuery.put("codeList", codeList);
        }
        CommonObject commonObject = query(mapQuery);
        List<SvcInfoModel> objs = ListUtils.transferToList(commonObject.getDatas());
        Map<String, String> mapResult = new HashMap<>();
        if (!ListUtils.isEmpty(objs)) {
            for (SvcInfoModel obj : objs) {
                mapResult.put(obj.getCode(), obj.getName());
            }
        }
        return mapResult;
    }

    @Override
    public Map<String, SvcInfoModel> mapCodeObj(List<String> codeList) {
        Map<String, Object> mapQuery = new HashMap<>();
        if (!ListUtils.isEmpty(codeList)) {
            mapQuery.put("codeList", codeList);
        }
        CommonObject commonObject = query(mapQuery);
        List<SvcInfoModel> objs = ListUtils.transferToList(commonObject.getDatas());
        Map<String, SvcInfoModel> mapResult = new HashMap<>();
        if (!ListUtils.isEmpty(objs)) {
            for (SvcInfoModel obj : objs) {
                mapResult.put(obj.getCode(), obj);
            }
        }
        return mapResult;
    }

    @Override
    public List<SvcInfoModel> getByAid(List<Integer> aidList) {
        return getByAid(aidList, null);
    }

    @Override
    public List<SvcInfoModel> getByAidNotIn(List<Integer> aidNotInList) {
        return getByAid(null, aidNotInList);
    }

    @Override
    public List<SvcInfoModel> getByAid(List<Integer> aidList, List<Integer> aidNotInList) {
        return getByAid(aidList, aidNotInList, null, null, null);
    }

    @Override
    public List<SvcInfoModel> getByAid(List<Integer> aidList, List<Integer> aidNotInList, List<Integer> idNotInList, String queryWord, Integer svcStatus) {
        return dao.getByAid(aidList, aidNotInList, idNotInList, queryWord, svcStatus);
    }

    @Override
    public List<SvcInfoExtModel> getExtByAid(List<Integer> aidList) {
        List<SvcInfoModel> svcInfoModels = getByAid(aidList, null);
        return svcInfoExtModelList(svcInfoModels);
    }

    @Override
    public List<SvcInfoExtModel> getExtByAidNotIn(List<Integer> aidNotInList) {
        List<SvcInfoModel> svcInfoModels = getByAid(null, aidNotInList);
        return svcInfoExtModelList(svcInfoModels);
    }

    @Override
    public List<SvcInfoExtModel> getExtByAid(List<Integer> aidList, List<Integer> aidNotInList) {
        List<SvcInfoModel> svcInfoModels = getByAid(aidList, aidNotInList);
        return svcInfoExtModelList(svcInfoModels);
    }

    @Override
    public List<SvcInfoModel> getByGroupId(List<Integer> groupIdList) {
        return dao.getByGroupId(groupIdList);
    }

    @Override
    public Map<String, SvcInfoModel> listToMapCodeObject(List<SvcInfoModel> svcInfoModelList) {
        if (ListUtils.isEmpty(svcInfoModelList)) {
            return null;
        } else {
            Map<String, SvcInfoModel> result = new HashMap<>();
            for (SvcInfoModel svcInfoModel : svcInfoModelList) {
                result.put(svcInfoModel.getCode(), svcInfoModel);
            }
            return result;
        }
    }

    @Override
    public Map<Integer, List<SvcInfoModel>> listToMapAidSvc(List<SvcInfoModel> svcInfoModelList) {
        if (ListUtils.isEmpty(svcInfoModelList)) {
            return null;
        } else {
            Map<Integer, List<SvcInfoModel>> result = new HashMap<>();
            Integer aid;
            for (SvcInfoModel svcInfoModel : svcInfoModelList) {
                aid = svcInfoModel.getAid();
                if (!result.containsKey(aid)) {
                    result.put(aid, new ArrayList<>());
                }
                result.get(aid).add(svcInfoModel);
            }
            return result;
        }
    }

    @Override
    public List<String> listSvcCode(List<SvcInfoModel> svcInfoModelList) {
        if (ListUtils.isEmpty(svcInfoModelList)) {
            return null;
        } else {
            List<String> result = new ArrayList<>();
            for (SvcInfoModel svcInfoModel : svcInfoModelList) {
                result.add(svcInfoModel.getCode());
            }
            return result;
        }
    }

    public boolean existCodeOrNameWithVersion(Integer id, String columnName, String columnValue, String version) {
        Map map = new HashMap();
        map.put(QueryParameterKeys.STARTINDEX.getKey(), 0);
        map.put(QueryParameterKeys.PAGESIZE.getKey(), 2);
        map.put(columnName, columnValue);
        map.put("version", version);
        CommonObject commonObject = dao.query(map);
        if (commonObject == null) {
            return false;
        } else if (commonObject.getTotalSize() > 1) {
            return true;
        } else if (commonObject.getTotalSize() == 1) {
            SvcInfoModel svcInfo = (SvcInfoModel) commonObject.getDatas().iterator().next();
            if (svcInfo.getId() != null && id != null && svcInfo.getId().intValue() == id.intValue()) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private List<SvcInfoExtModel> svcInfoExtModelList(List<SvcInfoModel> svcInfoModels) {
        List<SvcInfoExtModel> resultList;
        if (!ListUtils.isEmpty(svcInfoModels)) {
            //获取服务地址ID
            List<Integer> urlIdList = new ArrayList<>();
            for (SvcInfoModel svc : svcInfoModels) {
                if (!urlIdList.contains(svc.getUrlId())) {
                    urlIdList.add(svc.getUrlId());
                }
            }
            //根据地址ID获取服务地址对象
            List<SvcUrlModel> svcUrlModels = svcUrlService.getByID(urlIdList);
            Map<Integer, SvcUrlModel> urlMap = new HashMap<>();
            if (!ListUtils.isEmpty(svcInfoModels)) {
                for (SvcUrlModel url : svcUrlModels) {
                    urlMap.put(url.getId(), url);
                }
            }

            //生成服务的扩展对象
            resultList = new ArrayList<>();
            SvcInfoExtModel svcInfoExtModel;
            for (SvcInfoModel svc : svcInfoModels) {
                svcInfoExtModel = new SvcInfoExtModel();
                svcInfoExtModel.setSvcInfo(svc);
                if (urlMap.containsKey(svc.getUrlId())) {
                    svcInfoExtModel.setStatus(urlMap.get(svc.getUrlId()).getStatus());
                }
                resultList.add(svcInfoExtModel);
            }
        } else {
            resultList = null;
        }
        return resultList;
    }

}