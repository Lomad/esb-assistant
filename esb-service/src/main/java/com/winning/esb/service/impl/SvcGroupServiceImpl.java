package com.winning.esb.service.impl;

import com.winning.esb.dao.IOrgInfoDao;
import com.winning.esb.dao.ISvcGroupDao;
import com.winning.esb.model.*;
import com.winning.esb.model.common.*;
import com.winning.esb.model.enums.UserEnum;
import com.winning.esb.service.*;
import com.winning.esb.utils.JsonUtils;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.MapUtils;
import com.winning.esb.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by xuehao on 2017/8/9.
 */
@Service
public class SvcGroupServiceImpl implements ISvcGroupService {
    @Autowired
    private ISvcGroupDao dao;
    @Autowired
    private ISvcInfoService svcInfoService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IUserAppService userAppService;
    @Autowired
    private IGrantService grantService;
    @Autowired
    private ISimulationTestStepLogService stepLogService;


    private final String TREE_TYPE_GROUP = "9000";
    private final String TREE_TYPE_SVC = "6000";
    private final String TREE_ID_GROUP = "GROUP";
    private final String TREE_ID_SVC = "SVC";

    @Override
    public String save(SvcGroupModel obj) {
        if (obj.getId() == null) {
            dao.insert(obj);
        } else {
            dao.update(obj);
        }
        return null;
    }

    @Override
    public String delete(List<Integer> idList) {
        String err = "";
        for(Integer id : idList) {
            List<SvcInfoModel> svcInfoModels = svcInfoService.getByGroupId(idList);
            if(svcInfoModels.size() ==0) {
                dao.delete(id);
            } else {
                err += "服务分组被使用，不能删除";
            }
        }
        return err;
    }

    @Override
    public SvcGroupModel getByID(Integer id) {
        return dao.getByID(id);
    }

    @Override
    public CommonObject query(Map map) {
        return dao.query(map);
    }

    @Override
    public List<SimpleObject> listIdName() {
        List<SimpleObject> resultList;
        CommonObject commonObject = query(null);
        if(commonObject.getDatas()!=null && commonObject.getDatas().size()>0) {
            SvcGroupModel svcGroupModel;
            resultList = new ArrayList<>();
            for(Object obj : commonObject.getDatas()) {
                svcGroupModel = (SvcGroupModel) obj;
                resultList.add(new SimpleObject(String.valueOf(svcGroupModel.getId()), svcGroupModel.getName()));
            }
        } else {
            resultList = null;
        }
        return resultList;
    }

    @Override
    public List<TreeModel> createTreeByGroup(Map<String, Object> map){
        List<TreeModel> resultList;
        List<Integer> aidList;
        List<Integer> grantAidList;
        List<SvcInfoModel> svcInfoModels = null;
        Map<Integer, List<TreeModel>> svcMap = null;
        Set<Integer> svcGroupIdSet = new HashSet<>();
        Object tempStr;

        //登录的用户名，根据用户名筛选已经授权的业务系统ID列表
        tempStr = map.get("username");
        String username = StringUtils.isEmpty(tempStr) ? null : String.valueOf(tempStr);
        if (!StringUtils.isEmpty(username)) {
            UserModel userModel = userService.queryByUsername(username);
            grantAidList = userAppService.getAidListByUserid(userModel.getId());
            if (userModel != null && userModel.getRole() != null && userModel.getRole().intValue() == UserEnum.RoleEnum.Normal.getCode()
                    && !ListUtils.isEmpty(grantAidList)) {
                aidList = new ArrayList<>();
                aidList.addAll(grantAidList);
                svcInfoModels = svcInfoService.getByAid(grantAidList);
                
            }else if(userModel != null && userModel.getRole() != null 
                    && userModel.getRole().intValue() == UserEnum.RoleEnum.Admin.getCode()){
                Map<String, Object> serviceMap = new HashMap<>();
                svcInfoModels = svcInfoService.list(serviceMap);
            }
            svcMap = createSvcZTree(svcInfoModels);
            for(SvcInfoModel obj : svcInfoModels){
                Integer groupId = obj.getGroupId();
                svcGroupIdSet.add(groupId);
            }
        }

        for(SvcInfoModel svcInfoModel : svcInfoModels){
            Integer sid = svcInfoModel.getId();
            List<GrantModel> grantModels = grantService.getSvcGranted(sid);
            for(GrantModel grantModel : grantModels){
                Integer grantStatus = grantModel.getApprove_state();
                if(grantStatus.intValue() != 1){
                    svcInfoModels.remove(svcInfoModel);
                }
            }
        }

        List<SvcGroupModel> svcGroupModelList = new ArrayList<>();
        for(Integer grpid : svcGroupIdSet){
            SvcGroupModel model = getByID(grpid);
            svcGroupModelList.add(model);
        }

        //服务组数
        resultList = createGroupZTree(svcGroupModelList, svcMap);

        return resultList;
    }

    /**
     * 创建服务节点，返回Map：key - 服务组ID，value - 包含的服务节点
     */
    private Map<Integer, List<TreeModel>> createSvcZTree(List<SvcInfoModel> svcInfoList) {
        Map<Integer, List<TreeModel>> map;
        if (!ListUtils.isEmpty(svcInfoList)) {
            map = new HashMap<>();
            Integer gid;
            String name;
            String bageCss = null;
            for (SvcInfoModel svc : svcInfoList) {
                Integer count = 0;
                gid = svc.getGroupId();
                Map<String , Object> sidMap = new HashMap<>();
                sidMap.put("sid", svc.getId());
                CommonObject commonObject = stepLogService.query(sidMap);
                List<SimulationTestStepLogModel> stepLogList = ListUtils.transferToList(commonObject.getDatas());
                Collections.sort(stepLogList, new Comparator<SimulationTestStepLogModel>() {

                    @Override
                    public int compare(SimulationTestStepLogModel o1, SimulationTestStepLogModel o2) {
                        int i = o1.getId() - o2.getId();
                        return i;
                    }

                });
                SimulationTestStepLogModel stepLogModel;
                if(stepLogList.size() != 0) {
                    count = stepLogList.size();
                    stepLogModel = stepLogList.get(stepLogList.size() - 1);
                    Integer result = stepLogModel.getResult();
                    if(result.intValue() == 1) {
                        bageCss = "badge badgeSuccess";
                    } else {
                        bageCss = "badge badgeFailure";
                    }
                }
                if (!map.containsKey(gid)) {
                    map.put(gid, new ArrayList<>());
                }
                if(count.intValue() != 0 && bageCss != null) {
                    name = svc.getName() + "<span id = sp" + svc.getId() + " class = " + bageCss + ">" + count + "</span>";
                } else {
                    name = svc.getName();
                }
                if (StringUtils.isEmpty(name)) {
                    name = svc.getCode();
                }
                map.get(gid).add(new ZTreeModel(TREE_ID_SVC + svc.getId(), name,
                        "svc", svc));
            }
        } else {
            map = null;
        }
        return map;
    }

    /**
     * 创建机构节点，返回Map：key - 机构ID，value - 包含的业务系统节点
     */
    private List<TreeModel> createGroupZTree(List<SvcGroupModel> svcGroupList, Map<Integer, List<TreeModel>> svcMap) {
        List<TreeModel> treeList;
        if (!ListUtils.isEmpty(svcGroupList)) {
            treeList = new ArrayList<>();
            TreeModel treeModel;
            for (SvcGroupModel svcGroupModel : svcGroupList) {
                if (!MapUtils.isEmpty(svcMap) && svcMap.containsKey(svcGroupModel.getId())) {
                    treeModel = new ZTreeModel(TREE_ID_GROUP + svcGroupModel.getId(), svcGroupModel.getName(),
                            "org", svcGroupModel);
                    treeModel.setChildren(svcMap.get(svcGroupModel.getId()));
                    treeList.add(treeModel);
                }
            }
        } else {
            treeList = null;
        }
        return treeList;
    }
}