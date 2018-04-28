package com.winning.esb.service.impl;

import com.winning.esb.dao.IInspectionIndexDao;
import com.winning.esb.model.InspectionIndexModel;
import com.winning.esb.service.IInspectionIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InspectionIndexServiceImpl implements IInspectionIndexService {
    @Autowired
    private IInspectionIndexDao dao;

    @Override
    public List<InspectionIndexModel> list() {
        return dao.list();
    }
}
