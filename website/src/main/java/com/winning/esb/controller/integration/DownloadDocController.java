package com.winning.esb.controller.integration;

import com.winning.esb.controller.common.CommonOperation;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.service.ISvcInfoService;
import com.winning.esb.utils.ListUtils;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import com.winning.webapp.framework.core.api.response.message.AjaxTableResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 2017/8/15.
 */
@Controller
@RequestMapping(value = {"/view/integrationManage/downloadDoc/", "/view/integrationManage/downloadDoc",
        "/ajax/integrationManage/downloadDoc/"})
public class DownloadDocController {
    @Autowired
    private ISvcInfoService svcInfoService;

    @RequestMapping(value = {""})
    public ModelAndView downloadDoc() {
        return new ModelAndView("/integrationManage/downloadDoc");
    }

    /**
     * 获取待下载的服务列表
     *
     * @param aid          业务系统ID
     * @param svcDirection null - 获取全部，1-获取提供的服务，2-获取订阅的服务
     */
    @RequestMapping(value = {"listDownload"})
    @ResponseBody
    public AjaxTableResponseMessage listDownload(Integer aid, Integer svcDirection) {
        AjaxTableResponseMessage responseMessage = new AjaxTableResponseMessage();
        try {
            if (svcDirection.intValue() == 0) {
                svcDirection = null;
            }
            List<Map<String, Object>> svcInfoModels = svcInfoService.listDownload(aid, svcDirection);
            if (ListUtils.isEmpty(svcInfoModels)) {
                CommonOperation.resultError(responseMessage, "该系统尚未提供或授权服务！");
            } else {
                responseMessage.setDatas(svcInfoModels);
                responseMessage.setSuccess(true);
                responseMessage.setTotalSize(svcInfoModels.size());
            }
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    /**
     * 下载服务结构
     *
     * @param sid 服务ID（以英文逗号分割）
     */
    @RequestMapping(value = {"download"})
    @ResponseBody
    public AjaxDataResponseMessage download(Integer aid, String sid) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            List<String> sidStringList = Arrays.asList(sid.split(","));
            List<Integer> sidList = new ArrayList<>();
            if (!ListUtils.isEmpty(sidStringList)) {
                for (String sidString : sidStringList) {
                    sidList.add(Integer.valueOf(sidString));
                }
            }
            SimpleObject ret = svcInfoService.download(aid, sidList);
            CommonOperation.resultError(responseMessage, ret.getItem1());
            responseMessage.setData(ret.getItem2());
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

}