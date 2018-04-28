package com.winning.esb.service.impl;

import com.winning.esb.dao.IGrantDao;
import com.winning.esb.model.*;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.enums.GrantEnum;
import com.winning.esb.model.enums.QueryParameterKeys;
import com.winning.esb.model.enums.UserEnum;
import com.winning.esb.model.ext.GrantExtModel;
import com.winning.esb.service.*;
import com.winning.esb.service.middleware.IMiddlewareService;
import com.winning.esb.service.taskmark.SyncToEsbMark;
import com.winning.esb.utils.JsonUtils;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author xuehao
 */
@Service
public class GrantServiceImpl implements IGrantService {
    @Autowired
    private IGrantDao grantDao;
    @Autowired
    private IConfigsService configsService;
    @Autowired
    private ISvcInfoService svcInfoService;
    @Autowired
    private IGrantSvcStructureService grantSvcStructureService;
    @Autowired
    private IAppInfoService appInfoService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IUserAppService userAppService;

    private Map<String, IMiddlewareService> esbMap;

    /*@PostConstruct
    private void init() {
        esbMap = AppCtxUtils.getBeansOfType(IMiddlewareService.class);
    }*/

    @Override
    public Integer insert(GrantModel obj) {
        return grantDao.insert(obj);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String save(String strModel, String strSsidList) {
        String err = "";
        GrantModel grantModel = transferToModel(strModel);
        List<Integer> ssidList = JsonUtils.jsonToObject(strSsidList, List.class);

        //保存授权主信息
        if (grantModel.getId() == null) {
            grantModel.setId(grantDao.insert(grantModel));
        } else {
            grantDao.update(grantModel);
        }

        //保存授权字段信息
        if (StringUtils.isEmpty(err) && !ListUtils.isEmpty(ssidList)) {
            List<GrantSvcStructureModel> children = new ArrayList<>();
            GrantSvcStructureModel child;
            for (Integer ssid : ssidList) {
                child = new GrantSvcStructureModel();
                child.setGid(grantModel.getId());
                child.setSsid(ssid);
                children.add(child);
            }
            err = grantSvcStructureService.insert(children);
        }

        //同步到中间件
        SyncToEsbMark.setSyncGrant(true);
        SyncToEsbMark.setSyncSecret(true);
        SyncToEsbMark.setSyncToken(true);
        return err;
    }

    /**
     * 将原始信息转为对象
     */
    private GrantModel transferToModel(String strModel) {
        GrantModel grantModel = JsonUtils.jsonToObject(strModel, GrantModel.class);
        grantModel.setMtime(new Date());
        if (grantModel.getId() == null) {
            grantModel.setCtime(grantModel.getMtime());
            grantModel.setApply_time(grantModel.getMtime());
        }
        GrantEnum.ApproveStateEnum approveStateEnum = GrantEnum.ApproveStateEnum.getByCode(grantModel.getApprove_state());
        if (approveStateEnum != null && (GrantEnum.ApproveStateEnum.Approved.equals(approveStateEnum)
                || GrantEnum.ApproveStateEnum.Refused.equals(approveStateEnum))) {
            grantModel.setApprove_time(grantModel.getMtime());
        }
        return grantModel;
    }

    @Override
    public String delete(Integer id) {
        List<Integer> idList = new ArrayList<>();
        idList.add(id);
        delete(idList);
        return null;
    }

    @Override
    public String delete(List<Integer> idList) {
        grantDao.delete(idList);
        return null;
    }

    @Override
    public String deleteBySid(Integer sid) {
        List<Integer> sidList = new ArrayList<>();
        sidList.add(sid);
        return deleteBySid(sidList);
    }

    @Override
    public String deleteBySid(List<Integer> sidList) {
        grantDao.deleteBySid(sidList);
        return null;
    }

    @Override
    public String apply(String userid, String strModel, String strSidList) {
        List<GrantModel> list = transferToModel(userid, strModel, strSidList, GrantEnum.ApproveStateEnum.Apply);
        grantDao.insert(list);
        return null;
    }

    @Override
    public List<GrantModel> queryByAid(Integer aid) {
        List<Integer> aidList = new ArrayList<>();
        aidList.add(aid);
        return queryByAid(aidList);
    }

    @Override
    public List<GrantModel> queryByAid(List<Integer> aidList) {
        List<GrantModel> list = grantDao.queryByAid(aidList);
        return list;
    }

    @Override
    public List<GrantExtModel> getGrantExtList(Integer aid) {
        List<GrantModel> list = queryByAid(aid);
        return getGrantExtList(list);
    }

    private List<GrantExtModel> getGrantExtList(List<GrantModel> list) {
        List<GrantExtModel> resultList;
        if (!ListUtils.isEmpty(list)) {
            //获取服务id
            List<Integer> sidList = new ArrayList<>();
            //获取申请的业务系统
            List<Integer> consumerAidList = new ArrayList<>();
            for (GrantModel model : list) {
                sidList.add(model.getSid());
                consumerAidList.add(model.getAid());
            }
            Map<Integer, SvcInfoModel> svcInfoModelMap = svcInfoService.mapIdObject(svcInfoService.getByID(sidList));
            Map<Integer, AppInfoModel> appInfoModelMap = appInfoService.mapIdObject(appInfoService.getByID(consumerAidList));

            //设置扩展对象的
            resultList = new ArrayList<>();
            GrantExtModel grantExtModel;
            for (GrantModel model : list) {
                if(svcInfoModelMap.containsKey(model.getSid())) {
                    grantExtModel = new GrantExtModel();
                    grantExtModel.setObj(model);
                    grantExtModel.setSvcInfoModel(svcInfoModelMap.get(model.getSid()).clone());
                    grantExtModel.setAppInfoModel(appInfoModelMap.get(model.getAid()).clone());
                    grantExtModel.setSecretKeyName(StringUtils.isEmpty(model.getSecret_key()) ? "否" : "是");
                    grantExtModel.setApproveStateName(GrantEnum.ApproveStateEnum.getValueByCode(model.getApprove_state()));
                    resultList.add(grantExtModel);
                }
            }
        } else {
            resultList = null;
        }
        return resultList;
    }

    @Override
    public CommonObject query(Map map) {
        Object temp = map.get("username");
        if (!StringUtils.isEmpty(temp)) {
            UserModel userModel = userService.queryByUsername(String.valueOf(temp));
            if (userModel != null && userModel.getRole() != null && userModel.getRole().intValue() == UserEnum.RoleEnum.Normal.getCode()) {
                List<Integer> grantAidList = userAppService.getAidListByUserid(userModel.getId());
                map.put("aidList", grantAidList);
            }
        }
        temp = map.get("queryWord");
        if (!StringUtils.isEmpty(temp)) {
            Map<String, Object> mapSvc = new HashMap<>();
            mapSvc.put("queryWord", temp);
            List<SvcInfoModel> svcInfoModels = svcInfoService.list(mapSvc);
            temp = map.get("sidList");
            List<Integer> sidList = StringUtils.isEmpty(temp) ? new ArrayList<>() : JsonUtils.jsonToList(temp, Integer.class);
            for (SvcInfoModel model : svcInfoModels) {
                sidList.add(model.getId());
            }
            map.put("sidList", sidList);
        }
        CommonObject commonObject = grantDao.query(map);
        return commonObject;
    }

    @Override
    public CommonObject queryExt(Map map) {
        CommonObject commonObject = query(map);
        commonObject.setDatas(getGrantExtList(ListUtils.transferToList(commonObject.getDatas())));
        return commonObject;
    }


    @Override
    public String approveState(GrantModel obj) {
        obj.setApprove_time(new Date());
        obj.setMtime(obj.getApprove_time());
        grantDao.approveState(obj);
        //同步到中间件
        SyncToEsbMark.setSyncGrant(true);
        SyncToEsbMark.setSyncSecret(true);
        SyncToEsbMark.setSyncToken(true);
        return null;
    }

    @Override
    public List<Integer> getAidGrantAllSvc() {
        return grantDao.getAidGrantAllSvc();
    }

    @Override
    public List<GrantModel> getSvcGranted(Integer sid) {
        List<Integer> sidList = new ArrayList<>();
        sidList.add(sid);
        return getSvcGranted(sidList);
    }

    @Override
    public List<GrantModel> getSvcGranted(List<Integer> sidList) {
        if (ListUtils.isEmpty(sidList)) {
            return null;
        }

        Map<String, Object> map = new HashMap<>();
        map.put(QueryParameterKeys.STARTINDEX.getKey(), 0);
        map.put(QueryParameterKeys.PAGESIZE.getKey(), 1);
        map.put("approve_state", GrantEnum.ApproveStateEnum.Approved.getCode());
        map.put("sidList", sidList);
        CommonObject commonObject = query(map);
        return ListUtils.transferToList(commonObject.getDatas());
    }


    /**
     * 将原始信息转为对象
     */
    private List<GrantModel> transferToModel(String userid, String strModel, String strSidList,
                                             GrantEnum.ApproveStateEnum approveStateEnum) {
        List<Integer> aidList = userAppService.getAidListByUsername(userid);
        GrantModel grantModel = JsonUtils.jsonToObject(strModel, GrantModel.class);
        grantModel.setMtime(new Date());
        grantModel.setCtime(grantModel.getMtime());
        grantModel.setApply_time(grantModel.getMtime());
        grantModel.setApprove_state(approveStateEnum.getCode());
        if (GrantEnum.ApproveStateEnum.Approved.equals(approveStateEnum)
                || GrantEnum.ApproveStateEnum.Refused.equals(approveStateEnum)) {
            grantModel.setApprove_time(grantModel.getMtime());
        }
        List<Integer> sidList = JsonUtils.jsonToObject(strSidList, List.class);
        List<GrantModel> grantModels = new ArrayList<>();
        if (!ListUtils.isEmpty(aidList) && !ListUtils.isEmpty(sidList)) {
            GrantModel tempModel;
            for (Integer aid : aidList) {
                grantModel.setAid(aid);
                for (Integer sid : sidList) {
                    tempModel = grantModel.clone();
                    tempModel.setSid(sid);
                    grantModels.add(tempModel);
                }
            }
        }
        return grantModels;
    }
}