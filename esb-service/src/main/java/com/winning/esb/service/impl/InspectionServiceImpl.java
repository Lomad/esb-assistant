package com.winning.esb.service.impl;

import com.winning.esb.dao.IInspectionDao;
import com.winning.esb.model.InspectionDetailModel;
import com.winning.esb.model.InspectionModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.model.enums.InspectionEnum;
import com.winning.esb.model.ext.InspectionExtModel;
import com.winning.esb.service.IInspectionDetailService;
import com.winning.esb.service.IInspectionService;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class InspectionServiceImpl implements IInspectionService {
    @Autowired
    private IInspectionDao dao;
    @Autowired
    private IInspectionDetailService detailService;

    @Override
    public String save(InspectionModel obj) {
        obj.setCtime(new Date());
        obj.setCheck_uid(0);
        dao.insert(obj);
        return null;
    }

    /**
     * 该操作加入了事务管理
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String insert(InspectionModel obj, List<InspectionDetailModel> children) {
        String err = null;
        //保存主信息
        if(!StringUtils.isEmpty(obj.getResult_desp())) {
            obj.setResult_time(new Date());
        }
        obj.setCtime(new Date());
        obj.setCheck_uid(0);    //这是测试代码，应该在前端获取用户名
        Integer id = dao.insert(obj);
        if (id == null) {
            err = "保存巡检主信息失败或获取ID失败！";
        }
        //保存明细信息
        if (StringUtils.isEmpty(err) && !ListUtils.isEmpty(children)) {
            for (InspectionDetailModel child : children) {
                child.setId(null);
                child.setIns_id(id);
                child.setCtime(new Date());
            }
            err = detailService.save(children);
        }
        return err;
    }

    @Override
    public String updateResult(InspectionModel obj) {
        String err = null;
        if(StringUtils.isEmpty(obj.getResult_desp())) {
            err = "处理描述不能为空！";
        }
        if(StringUtils.isEmpty(err)) {
            dao.updateResult(obj);
        }
        return err;
    }

    @Override
    public CommonObject query(Map map) {
        //获取巡检主信息
        CommonObject commonObject = dao.query(map);
//        List<InspectionExtModel> insExtModelList = new ArrayList<>();
//        Map<Integer, InspectionExtModel> insIDMap = new HashMap<>();
//        InspectionExtModel insExtModel;
//        //设置巡检主信息
//        for (Object obj : commonObject.getDatas()) {
//            insExtModel = new InspectionExtModel();
//            insExtModel.setObj((InspectionModel) obj);
//            insIDMap.put(insExtModel.getObj().getId(), insExtModel);
//            insExtModelList.add(insExtModel);
//        }
//        //获取巡检最大ID对应的子信息（错误信息优先），并加入到返回结果
//        List<InspectionDetailModel> maxChildren = detailService.queryMaxByInsIDList(ListUtils.transferToList(insIDMap.keySet()));
//        if (!ListUtils.isEmpty(maxChildren)) {
//            Integer insID;
//            for (InspectionDetailModel child : maxChildren) {
//                insID = child.getIns_id();
//                if (insIDMap.containsKey(insID)) {
//                    insIDMap.get(insID).setChildren(new ArrayList<>());
//                    insIDMap.get(insID).getChildren().add(child);
//                }
//            }
//        }
//        //设置返回结果
//        commonObject.setDatas(insExtModelList);
        return commonObject;
    }

    @Override
    public InspectionExtModel queryByID(Integer id) {
        InspectionExtModel result = new InspectionExtModel();
        //获取巡检主信息
        Map map = new HashMap();
        map.put("id", id);
        CommonObject commonObject = dao.query(map);
        result.setObj((InspectionModel) commonObject.getDatas().iterator().next());
        result.setChildren(detailService.queryByInsID(id));
        return result;
    }

    @Override
    public List<SimpleObject> getResultList() {
        InspectionEnum.ResultEnum[] items = InspectionEnum.ResultEnum.values();
        List<SimpleObject> simpleObjects = new ArrayList<>();
        SimpleObject simpleObject;
        for (InspectionEnum.ResultEnum item : items) {
            if (item.getCode() == InspectionEnum.ResultEnum.Unknown.getCode()) {
                continue;
            }

            simpleObject = new SimpleObject();
            simpleObject.setItem1(String.valueOf(item.getCode()));
            simpleObject.setItem2(item.getValue());
            simpleObjects.add(simpleObject);
        }
        return simpleObjects;
    }
}
