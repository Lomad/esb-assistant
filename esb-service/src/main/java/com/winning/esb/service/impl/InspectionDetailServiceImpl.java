package com.winning.esb.service.impl;

import com.winning.esb.dao.IInspectionDetailDao;
import com.winning.esb.model.InspectionDetailModel;
import com.winning.esb.service.IInspectionDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class InspectionDetailServiceImpl implements IInspectionDetailService {
    @Autowired
    private IInspectionDetailDao dao;

    @Override
    public String save(InspectionDetailModel obj) {
        List<InspectionDetailModel> list = new ArrayList<>();
        list.add(obj);
        return save(list);
    }

    @Override
    public String save(List<InspectionDetailModel> list) {
        for(InspectionDetailModel obj : list) {
            obj.setCtime(new Date());
        }
        dao.insert(list);
        return null;
    }

    @Deprecated
    @Override
    public List<InspectionDetailModel> queryMaxByInsIDList(List<Integer> insIDList) {
        return dao.queryMaxByInsIDList(insIDList);
    }

    @Override
    public List<InspectionDetailModel> queryByInsID(Integer insID) {
        return dao.queryByInsID(insID);
    }
}
