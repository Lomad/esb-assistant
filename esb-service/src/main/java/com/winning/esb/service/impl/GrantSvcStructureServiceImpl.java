package com.winning.esb.service.impl;

import com.winning.esb.dao.IGrantSvcStructureDao;
import com.winning.esb.model.GrantSvcStructureModel;
import com.winning.esb.service.IGrantSvcStructureService;
import com.winning.esb.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuehao on 17/09/07.
 */
@Service
public class GrantSvcStructureServiceImpl implements IGrantSvcStructureService {
    @Autowired
    private IGrantSvcStructureDao dao;

    @Override
    public String insert(List<GrantSvcStructureModel> list) {
        Integer gid = list.get(0).getGid();
        deleteByGid(gid);
        dao.insert(list);
        return null;
    }

    @Override
    public String delete(List<Integer> idList) {
        dao.delete(idList);
        return null;
    }

    @Override
    public String deleteByGid(Integer gid) {
        dao.deleteByGid(gid);
        return null;
    }

    @Override
    public List<GrantSvcStructureModel> queryByGid(Integer gid) {
        List<GrantSvcStructureModel> list;
        if (gid == null) {
            list = null;
        } else {
            list = dao.queryByGid(gid);
        }
        return list;
    }

    @Override
    public List<Integer> listSsidByGid(Integer gid) {
        List<Integer> resultList;
        List<GrantSvcStructureModel> list = queryByGid(gid);
        if (!ListUtils.isEmpty(list)) {
            resultList = new ArrayList<>();
            for (GrantSvcStructureModel item : list) {
                resultList.add(item.getSsid());
            }
        } else {
            resultList = null;
        }
        return resultList;
    }
}