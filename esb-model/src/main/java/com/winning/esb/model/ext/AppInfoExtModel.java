package com.winning.esb.model.ext;

import com.winning.esb.model.AppInfoModel;
import com.winning.esb.model.InspectionSysModel;

import java.util.List;

/**
 * Created by xuehao on 2017/8/29.
 */
public class AppInfoExtModel {
    private AppInfoModel obj;
    /**
     * 提供的服务总数
     */
    private int serviceCount;
    /**
     * 提供的服务发生错误的总数
     */
    private int serviceFailCount;
    /**
     * 巡检对象
     */
    private List<InspectionSysModel> inspectionSysModels;

    public AppInfoExtModel() {
    }
    public AppInfoExtModel(AppInfoModel obj) {
        this.obj = obj;
    }

    public AppInfoModel getObj() {
        return obj;
    }

    public void setObj(AppInfoModel obj) {
        this.obj = obj;
    }

    public List<InspectionSysModel> getInspectionSysModels() {
        return inspectionSysModels;
    }

    public void setInspectionSysModels(List<InspectionSysModel> inspectionSysModels) {
        this.inspectionSysModels = inspectionSysModels;
    }

    public int getServiceCount() {
        return serviceCount;
    }

    public void setServiceCount(int serviceCount) {
        this.serviceCount = serviceCount;
    }

    public int getServiceFailCount() {
        return serviceFailCount;
    }

    public void setServiceFailCount(int serviceFailCount) {
        this.serviceFailCount = serviceFailCount;
    }
}