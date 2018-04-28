package com.winning.esb.dao.impl;

import com.winning.esb.dao.IInspectionIndexDao;
import com.winning.esb.dao.impl.common.CommonHandle;
import com.winning.esb.model.InspectionIndexModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InspectionIndexDaoImpl implements IInspectionIndexDao {
    @Autowired
    private CommonHandle commonHandle;

    private final String DB_NAME = "ESB_InspectionIndex";

    @Override
    public List<InspectionIndexModel> list() {
        return commonHandle.listByColumn(DB_NAME, InspectionIndexModel.class);
    }
}