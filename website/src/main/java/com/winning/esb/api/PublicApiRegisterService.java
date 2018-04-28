package com.winning.esb.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.winning.esb.model.AppInfoModel;
import com.winning.esb.model.SvcInfoModel;
import com.winning.esb.model.SvcStructureModel;
import com.winning.esb.model.SvcUrlModel;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.model.enums.SvcStructureEnum;
import com.winning.esb.model.enums.SvcUrlEnum;
import com.winning.esb.model.ext.SvcStructureExtModel;
import com.winning.esb.service.IAppInfoService;
import com.winning.esb.service.ISvcInfoService;
import com.winning.esb.service.ISvcStructureService;
import com.winning.esb.service.ISvcUrlService;
import com.winning.esb.utils.JsonUtils;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.MapUtils;
import com.winning.esb.utils.StringUtils;
import com.winning.webapp.framework.core.api.response.message.AjaxResponseMessage;
import com.winning.webapp.framework.core.api.response.message.AjaxTableResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by xuehao on 2017/10/26.
 */
@Controller
public class PublicApiRegisterService implements IPublicApiBiz {
    @Autowired
    private ISvcInfoService svcInfoService;
    @Autowired
    private ISvcStructureService svcStructureService;
    @Autowired
    private IAppInfoService appInfoService;
    @Autowired
    private ISvcUrlService svcUrlService;

    @Override
    public AjaxResponseMessage handle(Object obj) {
        String err = "";
        LinkedHashMap<String, Object> map = JsonUtils.jsonToObject(JsonUtils.toJson(obj), LinkedHashMap.class);

        //获取系统代码
        if (MapUtils.isEmptyValue(map, "appId")) {
            err += "系统代码(appId)为空！";
        }
        String appId = map.get("appId").toString();
        AppInfoModel appInfoModel = appInfoService.getByAppId(appId);
        //获取系统ID
        Integer aid = appInfoModel == null ? null : appInfoModel.getId();
        if (aid == null) {
            err += "系统代码(appId)不存在！";
        }

        //获取服务地址
        if (MapUtils.isEmptyValue(map, "url")) {
            err += "服务地址(url)为空！";
        }
        String url = map.get("url").toString();
        Integer urlId = null;
        Integer svcType = Integer.valueOf(MapUtils.getValue(map, "svcType"));
        if (StringUtils.isEmpty(err)) {
            SvcUrlModel svcUrlModel = svcUrlService.getByUrl(url);
            //获取地址ID
            if (svcUrlModel != null) {
                urlId = svcUrlModel.getId();
            }
        }

        //操作服务的基本信息
        Integer sid = null;
        if (StringUtils.isEmpty(err)) {
            String temp;
            //获取服务基本信息
            SvcInfoModel svcInfoModel = new SvcInfoModel();
            svcInfoModel.setCode(MapUtils.getValue(map, "code"));
            svcInfoModel.setName(MapUtils.getValue(map, "name"));
            svcInfoModel.setVersion(MapUtils.getValue(map, "version"));
            svcInfoModel.setAid(aid);
            if (urlId == null) {
                svcInfoModel.setUrl(url);
            } else {
                svcInfoModel.setUrlId(urlId);
            }
            svcInfoModel.setMsgType(MapUtils.getValue(map, "msgType"));
            svcInfoModel.setDataProtocal(Integer.parseInt(MapUtils.getValue(map, "dataProtocal")));
            temp = MapUtils.getValue(map, "otherMark");
            if (!StringUtils.isEmpty(temp)) {
                svcInfoModel.setOtherMark(Integer.parseInt(temp));
            }
            //根据服务代码获取ID
            List<SvcInfoModel> svcInfoModelFromDBList = svcInfoService.getByCode(svcInfoModel.getCode());
            if (!ListUtils.isEmpty(svcInfoModelFromDBList)) {
                for (SvcInfoModel model : svcInfoModelFromDBList) {
                    if (!StringUtils.isEmpty(svcInfoModel.getVersion()) && svcInfoModel.getVersion().equals(model.getVersion())) {
                        sid = model.getId();
                        svcInfoModel.setId(sid);
                        svcInfoModel.setUrlAgentId(model.getUrlAgentId());
                        break;
                    }
                }
                //如果版本号有变更，上一步获取不到ESB代理地址，隐藏需要以下处理
                if (svcInfoModel.getUrlAgentId() == null) {
                    svcInfoModel.setUrlAgentId(svcInfoModelFromDBList.get(0).getUrlAgentId());
                }
            }
            //如果ESB代理地址为空，则从地址表获取可用的ESB代理地址
            if (svcInfoModel.getUrlAgentId() == null) {
                List<SvcUrlModel> svcUrlModels = svcUrlService.queryByEsbAgent(SvcUrlEnum.EsbAgentEnum.Yes.getCode());
                if (!ListUtils.isEmpty(svcUrlModels)) {
                    for (SvcUrlModel svcUrlModel : svcUrlModels) {
                        if (svcType != null && svcUrlModel.getSvcType().intValue() == svcType.intValue()) {
                            svcInfoModel.setUrlAgentId(svcUrlModel.getId());
                        }
                    }
                    //如果没有类型相同的ESB代理地址，则指定第一个
                    if (svcInfoModel.getUrlAgentId() == null) {
                        svcInfoModel.setUrlAgentId(svcUrlModels.get(0).getId());
                    }
                }
            }
            //将服务保存到数据库
            SimpleObject simpleObject = svcInfoService.save(svcInfoModel);
            err = simpleObject.getItem2();
            if (StringUtils.isEmpty(err) && sid == null) {
                sid = Integer.parseInt(simpleObject.getItem1());
            }
        }

        //操作服务的请求参数
        if (StringUtils.isEmpty(err) && sid != null) {
            SvcStructureExtModel svcStructureExtModel;
            //删除旧的结构
            svcStructureService.deleteBySid(sid);

            //获取请求消息参数
            List<SvcStructureExtModel> paramRequestList = readParams(map.get("paramRequest"), SvcStructureEnum.DirectionEnum.In);
            if (!ListUtils.isEmpty(paramRequestList)) {
                //虚拟一个顶级节点
                svcStructureExtModel = SvcStructureExtModel.createVirtualRoot(paramRequestList);
                //导入结构
                svcStructureService.insertForUpload(sid, SvcStructureEnum.DirectionEnum.In.getCode(), svcStructureExtModel);
            }

            //获取应答消息参数
            List<SvcStructureExtModel> paramResponseList = readParams(map.get("paramResponse"), SvcStructureEnum.DirectionEnum.Ack);
            if (!ListUtils.isEmpty(paramResponseList)) {
                //虚拟一个顶级节点
                svcStructureExtModel = SvcStructureExtModel.createVirtualRoot(paramResponseList);
                //导入结构
                svcStructureService.insertForUpload(sid, SvcStructureEnum.DirectionEnum.Ack.getCode(), svcStructureExtModel);
            }
        }

        AjaxResponseMessage responseMessage = new AjaxTableResponseMessage();
        if (StringUtils.isEmpty(err)) {
            responseMessage.setSuccess(true);
        } else {
            responseMessage.setSuccess(false);
            responseMessage.setErrorMsg(err);
        }
        return responseMessage;
    }

    /**
     * 读取参数结构
     */
    private List<SvcStructureExtModel> readParams(Object params, SvcStructureEnum.DirectionEnum directionEnum) {
        List<SvcStructureExtModel> resultList = new ArrayList<>();

        if (params instanceof JSONArray) {
            SvcStructureExtModel svcStructureExtModel;
            SvcStructureModel svcStructureModel;
            List<JSONObject> paramList = (List<JSONObject>) params;
            if (!ListUtils.isEmpty(paramList)) {
                for (int i = 0, len = paramList.size(); i < len; i++) {
                    JSONObject param = paramList.get(i);
                    svcStructureExtModel = new SvcStructureExtModel();
                    if (param.containsKey("children")) {
                        svcStructureExtModel.setChildren(readParams(param.get("children"), directionEnum));
                    }
                    param.remove("children");
                    svcStructureModel = JsonUtils.jsonToObject(JsonUtils.toJson(param), SvcStructureModel.class);
                    if (svcStructureModel.getOrder_num() == null) {
                        svcStructureModel.setOrder_num(i + 1);
                    }
                    svcStructureExtModel.setObj(svcStructureModel);
                    resultList.add(svcStructureExtModel);
                }
            }
        }

        return resultList;
    }
}