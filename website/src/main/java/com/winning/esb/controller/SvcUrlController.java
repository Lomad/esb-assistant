package com.winning.esb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.winning.esb.controller.common.CommonOperation;
import com.winning.esb.model.SvcUrlModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.model.common.SimpleObjectMap;
import com.winning.esb.model.enums.SvcUrlEnum;
import com.winning.esb.service.IConfigsService;
import com.winning.esb.service.ISvcUrlService;
import com.winning.esb.utils.JsonUtils;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.NetUtils;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import com.winning.webapp.framework.core.api.response.message.AjaxTableResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = {"/view/baseManage/svcUrl", "/view/baseManage/svcUrl/", "/ajax/baseManage/svcUrl/"})
public class SvcUrlController {
    @Autowired
    private ISvcUrlService service;
    @Autowired
    private IConfigsService configsService;

    @RequestMapping(value = {""})
    public ModelAndView urlmanagement() {
        return new ModelAndView("baseManage/svcUrl");
    }

    /**
     * 获取基础信息
     */
    @RequestMapping(value = {"getBaseInfo"})
    @ResponseBody
    public SimpleObjectMap getBaseInfo() {
        SimpleObjectMap simpleObjectMap = new SimpleObjectMap();
        Map<String, List<SimpleObject>> map = new HashMap<>();
        map.put("svcType", SvcUrlEnum.SvcTypeEnum.getSimpleList());
        map.put("esbAgent", SvcUrlEnum.EsbAgentEnum.getSimpleList());
        simpleObjectMap.setMap(map);
        return simpleObjectMap;
    }

    @RequestMapping(value = {"query"})
    @ResponseBody
    public AjaxTableResponseMessage query(String datas) {
        AjaxTableResponseMessage responseMessage = new AjaxTableResponseMessage();
        try {
            Map<String, Object> map = JsonUtils.jsonToMap(datas);
            CommonObject commonObject = service.query(map);
            CommonOperation.resultCommonObject(responseMessage, commonObject);
        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setErrorMsg(e.getMessage());
        }
        return responseMessage;
    }

    @RequestMapping(value = {"save"})
    @ResponseBody
    public AjaxDataResponseMessage save(SvcUrlModel obj) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            if (StringUtils.isEmpty(obj.getId())) {
                obj.setId(null);
            }
            String err = service.save(obj);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"linkTest"})
    @ResponseBody
    public AjaxDataResponseMessage linkTest(SvcUrlModel obj) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            String err = service.linkTest(obj);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"delete"})
    @ResponseBody
    public AjaxDataResponseMessage delete(String strIdList) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            List<Integer> idList = JsonUtils.jsonToList(strIdList, Integer.class);
            String err = service.delete(idList);
            CommonOperation.resultError(responseMessage, err);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    @RequestMapping(value = {"urlIdMax"})
    @ResponseBody
    public AjaxDataResponseMessage urlIdMax() {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            Integer MaxId = service.queryIdMax();
            responseMessage.setData(MaxId);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    /**
     * 获取服务源地址、代理地址以及通信类型
     */
    @RequestMapping("getUrlInfo")
    @ResponseBody
    public AjaxDataResponseMessage getUrlInfo(Integer urlAgentId, Integer urlId, String url) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            Integer urlStatus = null, urlAgentStatus = null;
            String urlAgent = null;

            List<Integer> idList = new ArrayList<>();
            idList.add(urlAgentId);
            if (urlId != null) {
                idList.add(urlId);
            }
            List<SvcUrlModel> objList = service.getByID(idList);
            if (!ListUtils.isEmpty(objList)) {
                SvcUrlModel urlModelAgent = null, urlModel = null;
                //判断第一个元素
                if (objList.get(0).getId().equals(urlAgentId)) {
                    urlModelAgent = objList.get(0);
                } else {
                    urlModel = objList.get(0);
                }
                //判断第二个元素
                if (objList.size() > 1) {
                    if (objList.get(1).getId().equals(urlAgentId)) {
                        urlModelAgent = objList.get(1);
                    } else {
                        urlModel = objList.get(1);
                    }
                }
                //获取代理地址与状态
                if (urlModelAgent != null) {
                    urlAgentStatus = urlModelAgent.getStatus();
                    urlAgent = urlModelAgent.getUrl();
                }
                //获取源地址与状态
                if (urlModel != null) {
                    urlStatus = urlModel.getStatus();
                    url = urlModel.getUrl();
                }
            }

            //获取管理平台与ESB的内容交互地址
            String urlEsbTest = configsService.getEsbTestUrl();

            //生成返回对象
            Map<String, Object> map = new HashMap<>();
            //通信类型
            map.put("url", url);
            map.put("urlType", SvcUrlEnum.SvcTypeEnum.getValueByCode(service.getUrlType(url)));
            map.put("urlStatus", urlStatus);
            map.put("urlAgent", urlAgent);
            map.put("urlAgentType", SvcUrlEnum.SvcTypeEnum.getValueByCode(service.getUrlType(urlAgent)));
            map.put("urlAgentStatus", urlAgentStatus);
            map.put("urlEsbTest", urlEsbTest);
            responseMessage.setData(map);
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }

    /**
     * 检测服务地址状态
     */
    @RequestMapping("checkUrlStatus")
    @ResponseBody
    public AjaxDataResponseMessage checkUrlStatus(Integer urlAgentId, Integer urlId, String url) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
//            Integer urlStatus = null, urlAgentStatus = null;
            String urlAgent = null;

            List<Integer> idList = new ArrayList<>();
            idList.add(urlAgentId);
            if (urlId != null) {
                idList.add(urlId);
            }
            List<SvcUrlModel> objList = service.getByID(idList);
            if (!ListUtils.isEmpty(objList)) {
                SvcUrlModel urlModelAgent = null, urlModel = null;
                //判断第一个元素
                if (objList.get(0).getId().equals(urlAgentId)) {
                    urlModelAgent = objList.get(0);
                } else {
                    urlModel = objList.get(0);
                }
                //判断第二个元素
                if (objList.size() > 1) {
                    if (objList.get(1).getId().equals(urlAgentId)) {
                        urlModelAgent = objList.get(1);
                    } else {
                        urlModel = objList.get(1);
                    }
                }
                //获取代理地址与状态
                if (urlModelAgent != null) {
//                    urlAgentStatus = urlModelAgent.getStatus();
                    urlAgent = urlModelAgent.getUrl();
                }
                //获取源地址与状态
                if (urlModel != null) {
//                    urlStatus = urlModel.getStatus();
                    url = urlModel.getUrl();
                }
            }
//            //如果源地址是界面直接输入的，则需要检测状态
//            if (urlId == null) {
//                urlStatus = service.checkUrlStatus(url);
//            }

            //获取管理平台与ESB的内容交互地址
            String urlEsbTest = configsService.getEsbTestUrl();
//            Integer urlEsbTestStatus = service.checkUrlStatus(urlEsbTest);

            //生成返回对象
            Map<String, Object> map = new HashMap<>();
            //通信类型
            map.put("url", url);
            map.put("urlType", SvcUrlEnum.SvcTypeEnum.getValueByCode(service.getUrlType(url)));
//            map.put("urlStatus", urlStatus);
            map.put("urlAgent", urlAgent);
            map.put("urlAgentType", SvcUrlEnum.SvcTypeEnum.getValueByCode(service.getUrlType(urlAgent)));
//            map.put("urlAgentStatus", urlAgentStatus);
            map.put("urlEsbTest", urlEsbTest);
//            map.put("urlEsbTestStatus", urlEsbTestStatus);
            responseMessage.setData(map);
            responseMessage.setSuccess(true);
        } catch (Exception e) {
            CommonOperation.resultException(responseMessage, e);
        }
        return responseMessage;
    }
}