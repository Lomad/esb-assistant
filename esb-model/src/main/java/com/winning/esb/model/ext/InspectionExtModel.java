package com.winning.esb.model.ext;

import com.winning.esb.model.InspectionDetailModel;
import com.winning.esb.model.InspectionModel;

import java.util.List;

/**
 * Created by xuehao on 2017/8/25.
 */
public class InspectionExtModel {
    private InspectionModel obj;
    private List<InspectionDetailModel> children;

    public InspectionModel getObj() {
        return obj;
    }

    public void setObj(InspectionModel obj) {
        this.obj = obj;
    }

    public List<InspectionDetailModel> getChildren() {
        return children;
    }

    public void setChildren(List<InspectionDetailModel> children) {
        this.children = children;
    }
}