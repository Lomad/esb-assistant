package com.winning.esb.service.dataprotocal;

import com.winning.esb.model.biz.EsbDataProtocal;
import com.winning.esb.model.enums.SvcInfoEnum;
import com.winning.esb.model.ext.SvcStructureExtModel;
import com.winning.esb.service.ISvcStructureService;
import com.winning.esb.stable.DataProtocalEnum;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 2017/11/21.
 * 数据协议检测
 */
@Service
public class ProtocalChecker {
    @Autowired
    private ISvcStructureService svcStructureService;

    /**
     * 检测请求消息是否符合数据协议规范
     */
    public String check(SvcStructureExtModel extModel, SvcInfoEnum.DataProtocalEnum dataProtocalEnum, EsbDataProtocal esbDataProtocal) {
        StringBuilder errinfo = new StringBuilder();

        SvcStructureExtModel headExtModel = null;
        SvcStructureExtModel bodyExtModel = null;
        List<SvcStructureExtModel> children = extModel.getChildren();
        if (!ListUtils.isEmpty(children) && children.size() == 2) {
            //判断是否包含Head节点
            if ("Head".equalsIgnoreCase(children.get(0).getObj().getCode())) {
                headExtModel = children.get(0);
            } else if("Head".equalsIgnoreCase(children.get(1).getObj().getCode())) {
                headExtModel = children.get(1);
            } else {
                errinfo.append("Head节点不存在！");
            }
            //判断是否包含Body节点
            if ("Body".equalsIgnoreCase(children.get(0).getObj().getCode())) {
                bodyExtModel = children.get(0);
            } else if("Body".equalsIgnoreCase(children.get(1).getObj().getCode())) {
                bodyExtModel = children.get(1);
            } else {
                errinfo.append("Body节点不存在！");
            }

            //判断Head的子节点
            if(errinfo.length()<1) {
                checkHead(headExtModel, errinfo);
            }
        }

        esbDataProtocal.setHead(headExtModel);
        esbDataProtocal.setBody(bodyExtModel);

        return errinfo.toString();
    }

    /**
     * 检测Head信息
     */
    private void checkHead(SvcStructureExtModel headExtModel, StringBuilder errinfo) {
        if (headExtModel == null || headExtModel.getObj() == null || !"Head".equalsIgnoreCase(headExtModel.getObj().getCode())) {
            errinfo.append("Head节点不存在！");
        } else {
            Map<String, SvcStructureExtModel> map = headExtModel.childrenListToMap();
            Object obj;
            //版本号
            obj = map.get("Version").getObj().getName();
            errinfo.append(checkVersion(obj == null ? "" : obj.toString()));
            //传输类型
            obj = map.get("TransferType").getObj().getName();
            errinfo.append(checkTransferType(obj == null ? "" : obj.toString()));
            //服务代码
            obj = map.get("TranCode").getObj().getName();
            errinfo.append(checkTranCode(obj == null ? "" : obj.toString()));
            //消息类型
            obj = map.get("ContentType").getObj().getName();
            errinfo.append(checkContentType(obj == null ? "" : obj.toString()));
            //消息格式
            obj = map.get("ContentEncoding").getObj().getName();
            errinfo.append(checkContentEncoding(obj == null ? "" : obj.toString()));
            //系统ID
            obj = map.get("AppId").getObj().getName();
            errinfo.append(checkAppId(obj == null ? "" : obj.toString()));
            //消息ID
            obj = map.get("MessageId").getObj().getName();
            errinfo.append(checkMessageId(obj == null ? "" : obj.toString()));
            //客户端类型
            obj = map.get("AppType").getObj().getName();
            errinfo.append(checkAppType(obj == null ? "" : obj.toString()));
            //加密策略
            obj = map.get("SecurityPolicy").getObj().getName();
            errinfo.append(checkSecurityPolicy(obj == null ? "" : obj.toString()));
        }
    }

    /**
     * 检测版本号
     */
    private String checkVersion(String val) {
        String ret = "";
        if (StringUtils.isEmpty(val)) {
            ret = "版本号不能为空(Head/Version结点)！";
        } else if (!DataProtocalEnum.Version.list().contains(val)) {
            ret = "版本号超出范围(Head/Version结点)！";
        }
        return ret;
    }

    /**
     * 检测传输类型
     */
    private String checkTransferType(String val) {
        String ret = "";
        if (!StringUtils.isEmpty(val) && !DataProtocalEnum.TransferType.list().contains(val)) {
            ret = "传输类型超出范围(Head/TransferType结点)！";
        }
        String async = DataProtocalEnum.TransferType.Async.toString();
        if (!StringUtils.isEmpty(val) && async.equals(val)) {
            ret = "传输类型(Head/TransferType结点)为异步注册[" + async + "]时，回调地址不能为空(Head/Callback结点)！";
        }
        return ret;
    }

    /**
     * 检测服务代码
     */
    private String checkTranCode(String val) {
        String ret = "";
        if (StringUtils.isEmpty(val)) {
            ret = "服务代码不能为空(Head/TranCode结点)！";
        }
//        else if (!SvcContainer.existsCode(val))
//            ret = "服务接口[" + val + "(Head/TranCode结点)]未注册！";
//        else if (!SvcContainer.checkPublish(val) && !isTest)
//            ret = "服务接口[" + val + "(Head/TranCode结点)]未发布！";
        return ret;
    }

    /**
     * 检测消息类型
     */
    private String checkContentType(String val) {
        String ret = "";
        if (StringUtils.isEmpty(val)) {
            ret = "消息类型不能为空(Head/ContentType结点)！";
        } else if (!DataProtocalEnum.ContentType.list().contains(val)) {
            ret = "消息类型超出范围(Head/ContentType结点)！";
        }
        return ret;
    }

    /**
     * 检测消息格式
     */
    private String checkContentEncoding(String val) {
        String ret = "";
        if (!StringUtils.isEmpty(val)
                && !DataProtocalEnum.ContentEncoding.list().contains(val)) {
            ret = "消息格式超出范围(Head/ContentEncoding结点)！";
        }
        return ret;
    }

    /**
     * 检测系统ID
     */
    private String checkAppId(String val) {
        String ret = "";
        if (StringUtils.isEmpty(val)) {
            ret = "系统ID不能为空(Head/AppId结点)！";
        }
//        else if (!InfoSystemContainer.existsAppid(null, val) && !testAppid.equals(val))
//            ret = "系统ID[" + val + "(Head/AppId结点值)]未注册！";
        return ret;
    }

    /**
     * 检测消息ID
     */
    private String checkMessageId(String val) {
        String ret = "";
        if (StringUtils.isEmpty(val)) {
            ret = "消息ID不能为空(Head/MessageId结点)！";
        }
        return ret;
    }

    /**
     * 检测客户端类型
     */
    private String checkAppType(String val) {
        String ret = "";
        if (StringUtils.isEmpty(val)) {
            ret = "客户端类型不能为空(Head/AppType结点)！";
        } else if (!DataProtocalEnum.AppType.list().contains(val)) {
            ret = "客户端类型超出范围(Head/AppType结点)！";
        }
        return ret;
    }

    /**
     * 检测加密策略
     */
    private String checkSecurityPolicy(String val) {
        String ret = "";
        if (StringUtils.isEmpty(val)) {
//            ret = "加密策略不能为空(Head/SecurityPolicy结点)！";
        } else if (!DataProtocalEnum.SecurityPolicy.list().contains(val)) {
            ret = "加密策略超出范围(Head/SecurityPolicy结点)！";
        }
        return ret;
    }

}