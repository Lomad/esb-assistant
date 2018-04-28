package com.winning.esb.controller.svc;

import com.winning.esb.controller.common.CommonOperation;
import com.winning.esb.model.SvcStructureModel;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.model.common.SimpleObjectMap;
import com.winning.esb.model.common.TreeModel;
import com.winning.esb.model.enums.SvcStructureEnum;
import com.winning.esb.service.ISvcStructureService;
import com.winning.esb.utils.JsonUtils;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import com.winning.webapp.framework.core.api.response.message.AjaxTableResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 2017/8/9.
 */
@Controller
@RequestMapping(value = {"/view/serviceManage/svcInfo__structure", "/view/serviceManage/svcInfo__structure/",
        "/ajax/serviceManage/svcStructure/"})
public class SvcStructureController {
    @Autowired
    private ISvcStructureService svcStructureService;

    @RequestMapping(value = {""})
    public ModelAndView svcStructure() {
        return new ModelAndView("/serviceManage/svcStructure");
    }

    /**
     * 获取基础信息
     */
    @RequestMapping(value = {"getBaseInfo"})
    @ResponseBody
    public SimpleObjectMap getBaseInfo() {
        SimpleObjectMap simpleObjectMap = new SimpleObjectMap();
        Map<String, List<SimpleObject>> map = new HashMap<>();
        map.put("required", SvcStructureEnum.RequiredEnum.getSimpleList());
        map.put("is_loop", SvcStructureEnum.IsLoopEnum.getSimpleList());
        map.put("data_type", SvcStructureEnum.DataTypeEnum.getSimpleList());
        map.put("result_mark", SvcStructureEnum.ResultMarkEnum.getSimpleList());
        map.put("is_attr", SvcStructureEnum.IsAttrEnum.getSimpleList());
        map.put("direction", SvcStructureEnum.DirectionEnum.getSimpleList());
        simpleObjectMap.setMap(map);
        return simpleObjectMap;
    }

    /**
     * 获取服务结构树（适用于ztree.js）
     *
     * @param sid     服务ID
     * @param grantID 授权ID，如果不为空，则获取授权的字段ID列表，以便设置选中状态
     */
    @RequestMapping(value = {"getZTree"})
    @ResponseBody
    public AjaxTableResponseMessage getZTree(Integer sid, Integer direction, Integer grantID) {
        AjaxTableResponseMessage responseMessage = new AjaxTableResponseMessage();
        try {
            List<TreeModel> list = svcStructureService.createZTree(sid, direction, grantID);
            responseMessage.setDatas(list);
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"save"})
    @ResponseBody
    public AjaxDataResponseMessage save(SvcStructureModel obj) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            String err = svcStructureService.save(obj);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"delete"})
    @ResponseBody
    public AjaxDataResponseMessage delete(Integer id) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            String err = svcStructureService.delete(id);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"updateWhenDrop"})
    @ResponseBody
    public AjaxDataResponseMessage updateWhenDrop(String objs) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            List<SvcStructureModel> svcStructureModels = JsonUtils.jsonToList(objs, SvcStructureModel.class);
            String err = svcStructureService.updateWhenDrop(svcStructureModels);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"existCode"})
    @ResponseBody
    public AjaxDataResponseMessage existCode(String code, Integer pid, Integer id, Integer direction, Integer sid) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            boolean result = svcStructureService.existCode(code, pid, id, direction, sid);
            responseMessage.setSuccess(!result);
            if (result) {
                responseMessage.setErrorMsg("该代码已存在（同级节点中的代码不能重复）！");
            }
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"importStruncture"})
    @ResponseBody
    public AjaxDataResponseMessage importStruncture(Integer sid, Integer direction, Integer sidFrom, Integer directionFrom) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            String err = svcStructureService.importStruncture(sid, direction, sidFrom, directionFrom);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"upload"})
    @ResponseBody
    public AjaxDataResponseMessage upload(Integer sid, String msgType, Integer direction, String rawContent) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            String err = svcStructureService.upload(sid, msgType, direction, rawContent);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    /**
     * 导出消息
     *
     * @param returnType 返回类型：0或空 - 下载地址， 1 - 返回消息
     * @param valueType  填充值类型
     * @param wrapperDataProtocal  是否使用数据协议规范封装
     */
    @RequestMapping(value = {"export"})
    @ResponseBody
    public AjaxDataResponseMessage export(Integer sid, Integer direction, String msgType, Integer returnType, Integer valueType,
            Boolean wrapperDataProtocal) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            if (valueType == null) {
                valueType = SvcStructureEnum.ValueTypeEnum.VistualValue.getCode();
            }
            String url = svcStructureService.export(sid, direction, msgType, returnType, valueType.intValue(), wrapperDataProtocal);
            responseMessage.setData(url);
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"getDataProtocalTemplate"})
    @ResponseBody
    private AjaxDataResponseMessage getDataProtocalTemplate(Integer protocalCode, String msgType) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            Map<String, Object> map = svcStructureService.getDataProtocalTemplate(protocalCode, msgType);
            responseMessage.setData(map);
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

}