package com.winning.esb.model.ext;

import com.winning.esb.model.AppInfoModel;
import com.winning.esb.model.GrantModel;
import com.winning.esb.model.SvcInfoModel;

public class GrantExtModel {
    private GrantModel obj;
    private SvcInfoModel svcInfoModel;
    private AppInfoModel appInfoModel;
    /**
     * 授权码的显示名称
     */
    private String approveStateName;
    /**
     * 密钥的显示名称
     */
    private String secretKeyName;

    public GrantModel getObj() {
        return obj;
    }

    public void setObj(GrantModel obj) {
        this.obj = obj;
    }

    public AppInfoModel getAppInfoModel() {
        return appInfoModel;
    }

    public void setAppInfoModel(AppInfoModel appInfoModel) {
        this.appInfoModel = appInfoModel;
    }

    public SvcInfoModel getSvcInfoModel() {
        return svcInfoModel;
    }

    public void setSvcInfoModel(SvcInfoModel svcInfoModel) {
        this.svcInfoModel = svcInfoModel;
    }

    public String getSecretKeyName() {
        return secretKeyName;
    }

    public void setSecretKeyName(String secretKeyName) {
        this.secretKeyName = secretKeyName;
    }

    public String getApproveStateName() {
        return approveStateName;
    }

    public void setApproveStateName(String approveStateName) {
        this.approveStateName = approveStateName;
    }
}
