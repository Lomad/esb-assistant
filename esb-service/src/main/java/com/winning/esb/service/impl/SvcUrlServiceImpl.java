package com.winning.esb.service.impl;

import com.winning.esb.dao.ISvcUrlDao;
import com.winning.esb.model.AppInfoModel;
import com.winning.esb.model.InspectionSysModel;
import com.winning.esb.model.SvcInfoModel;
import com.winning.esb.model.SvcUrlModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.model.enums.QueryParameterKeys;
import com.winning.esb.model.enums.SvcUrlEnum;
import com.winning.esb.model.ext.SvcInfoExtModel;
import com.winning.esb.service.IAppInfoService;
import com.winning.esb.service.IInspectionSysService;
import com.winning.esb.service.ISvcInfoService;
import com.winning.esb.service.ISvcUrlService;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.NetUtils;
import com.winning.esb.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author xuehao
 */
@Service
public class SvcUrlServiceImpl implements ISvcUrlService {
    @Autowired
    private ISvcUrlDao dao;
    @Autowired
    private ISvcInfoService svcInfoService;
    @Autowired
    private IAppInfoService appInfoService;
    @Autowired
    private IInspectionSysService inspectionSysService;

    @Override
    public String save(SvcUrlModel obj) {
        String err = "";

        String url = obj.getUrl();
        err += checkUrl(obj.getSvcType(), url);
        if (existUrlOrName(obj.getId(), "name", obj.getName())) {
            err += "地址名称已存在。 ";
        }
        if (existUrlOrName(obj.getId(), "url", url)) {
            err += "服务地址已存在！";
        }

        //设置ESB代理的默认标志
        if (obj.getEsbAgent() == null) {
            obj.setEsbAgent(SvcUrlEnum.EsbAgentEnum.No.getCode());
        }

        //保存
        if (StringUtils.isEmpty(err)) {
            if (obj.getId() == null) {
                dao.insert(obj);
            } else {
                dao.update(obj);
            }
        }
        return err;
    }

    @Override
    public String delete(List<Integer> idList) {
        Map<String, Object> map = new HashMap<>();
        String err = "";
        for (Integer id : idList) {
            map.put("urlId", id);
            List<SvcInfoExtModel> svcInfoExtModels = svcInfoService.listExt(map);
            if (svcInfoExtModels == null || svcInfoExtModels.size() == 0) {
                dao.delete(id);
            }
            else {
                err = "服务地址被引用，不能删除！";
            }
        }
        return err;
    }

    @Override
    public CommonObject query(Map map) {
        return dao.query(map);
    }

    @Override
    public List<SvcUrlModel> queryByEsbAgent(Integer esbAgent) {
        Map map = new HashMap();
        map.put("esbAgent", esbAgent);
        CommonObject commonObject = query(map);
        return ListUtils.transferToList(commonObject.getDatas());
    }

    @Override
    public List<SimpleObject> listIdName() {
        return listIdName(null);
    }

    @Override
    public List<SimpleObject> listIdName(Integer esbAgent) {
        List<SimpleObject> resultList;
        Map map = new HashMap();
        map.put("esbAgent", esbAgent);
        CommonObject commonObject = query(map);
        if (commonObject.getDatas() != null && commonObject.getDatas().size() > 0) {
            SvcUrlModel svcUrlModel;
            resultList = new ArrayList<>();
            String showText;
            for (Object obj : commonObject.getDatas()) {
                svcUrlModel = (SvcUrlModel) obj;
                showText = svcUrlModel.getName();
                if (StringUtils.isEmpty(showText)) {
                    showText = svcUrlModel.getUrl();
                }
                resultList.add(new SimpleObject(String.valueOf(svcUrlModel.getId()), showText));
            }
        } else {
            resultList = null;
        }
        return resultList;
    }

    @Override
    public SvcUrlModel getByID(Integer id) {
        List<Integer> idList = new ArrayList<>();
        idList.add(id);
        List<SvcUrlModel> list = getByID(idList);
        return ListUtils.isEmpty(list) ? null : list.get(0);
    }

    @Override
    public List<SvcUrlModel> getByID(List<Integer> idList) {
        return dao.getByID(idList);
    }

    @Override
    public SvcUrlModel getByUrl(String url) {
        return dao.getByUrl(url);
    }

    @Override
    public String linkTest(SvcUrlModel obj) {
        String err = "";
        String url = obj.getUrl();
        if (obj.getSvcType() != null && SvcUrlEnum.SvcTypeEnum.Socket.getCode() == obj.getSvcType().intValue()) {
            if (!NetUtils.isIpPort(url)) {
                err += "服务地址不符合Socket配置要求！";
            }
        } else {
            if (!NetUtils.isHttp(url)) {
                err += "服务地址不符合Http配置要求！";
            }
        }
        if (obj.getSvcType() != null && SvcUrlEnum.SvcTypeEnum.Socket.getCode() == obj.getSvcType().intValue()) {
            if (!NetUtils.checkSocket(url)) {
                err += "连接失败";
            }
        } else {
            if (!NetUtils.checkHttp(url)) {
                err += "连接失败";
            }
        }
        return err;
    }

    @Override
    public Map<Integer, SvcUrlModel> map() {
        Map<Integer, SvcUrlModel> resultMap = new HashMap<>();
        CommonObject commonObject = query(null);
        List<SvcUrlModel> serviceUrlVOList = ListUtils.transferToList(commonObject.getDatas());
        return map(serviceUrlVOList);
    }

    @Override
    public Map<Integer, SvcUrlModel> map(List<SvcUrlModel> list) {
        Map<Integer, SvcUrlModel> resultMap = new HashMap<>();
        if (!ListUtils.isEmpty(list)) {
            for (SvcUrlModel serviceUrlVO : list) {
                resultMap.put(serviceUrlVO.getId(), serviceUrlVO);
            }
        }
        return resultMap;
    }

    @Override
    public List<SimpleObject> getSvcTypeList() {
        SvcUrlEnum.SvcTypeEnum[] svcTypes = SvcUrlEnum.SvcTypeEnum.values();
        List<SimpleObject> simpleObjects = new ArrayList<>();
        SimpleObject simpleObject;
        for (SvcUrlEnum.SvcTypeEnum svcTypeEnum : svcTypes) {
            simpleObject = new SimpleObject();
            simpleObject.setItem1(String.valueOf(svcTypeEnum.getCode()));
            simpleObject.setItem2(svcTypeEnum.getValue());
            simpleObjects.add(simpleObject);
        }
        return simpleObjects;
    }

    @Override
    public Integer queryIdMax() {
        return dao.getMaxId();
    }

    /**
     * 判断名称是否存在
     */
    private boolean existUrlOrName(Integer id, String columnName, String columnValue) {
        Map map = new HashMap();
        map.put(QueryParameterKeys.STARTINDEX.getKey(), 0);
        map.put(QueryParameterKeys.PAGESIZE.getKey(), 2);
        map.put(columnName, columnValue);
        if (StringUtils.isEmpty(columnValue)) {
            return false;
        } else {
            CommonObject commonObject = query(map);
            if (commonObject.getTotalSize() > 1) {
                return true;
            } else if (commonObject.getTotalSize() == 1) {
                SvcUrlModel svcUrlModel = (SvcUrlModel) commonObject.getDatas().iterator().next();
                if (svcUrlModel.getId() != null && svcUrlModel.getId().equals(id)) {
                    return false;
                }
                else {
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    /**
     * 扫描地址是否可用
     */
    @Override
    public void scanUrl() throws ParseException {
        //设置服务状态
        CommonObject commonObject = dao.query(null);
        SvcUrlModel obj;
        for (Object item : commonObject.getDatas()) {
            obj = (SvcUrlModel) item;
            obj.setStatus(checkUrlStatus(obj.getSvcType(), obj.getUrl()));
            //保存服务
            dao.update(obj);
        }
        //把服务地址的状态写入到系统巡检表里
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<AppInfoModel> appInfoModels = appInfoService.list();
        for(AppInfoModel appInfoModel : appInfoModels) {
            String check_desp = " ";
            Integer aid = appInfoModel.getId();
            Map map = new HashMap();
            map.put("aid",aid);
            List<SvcInfoModel> svcInfoModels = svcInfoService.list(map);
            for (SvcInfoModel svcInfoModel : svcInfoModels) {
                Integer urlId = svcInfoModel.getUrlId();
                Integer status = 1;
                if (urlId != null) {
                    List<Integer> idList = new ArrayList<>();
                    idList.add(urlId);
                    List<SvcUrlModel> urlList = dao.getByID(idList);
                    if (!ListUtils.isEmpty(urlList)) {
                        status = urlList.get(0).getStatus();
                    }
                } else {
                    String url = svcInfoModel.getUrl();
                    status = checkUrlStatus(url);
                }
                if (status.intValue() == SvcUrlEnum.StatusEnum.Stop.getCode()) {
                    String svcName = svcInfoModel.getName();
                    check_desp += svcName + "地址异常! ";
                }
            }
            String date = sdf.format(new Date());
            Map<String, Object> smap = new HashMap<>();
            smap.put("check_time",date);
            smap.put("aid",aid);
            smap.put("check_type",0);
            CommonObject commonObject1 = inspectionSysService.query(map);
            if(commonObject1.getTotalSize() == 0) {
                InspectionSysModel inspectionSysModel = new InspectionSysModel();
                inspectionSysModel.setCheck_desp(check_desp);
                inspectionSysModel.setAid(aid);
                inspectionSysModel.setCheck_type(1);
                inspectionSysModel.setCheck_time(sdf.parse(date));
                inspectionSysModel.setResult_type(0);
                inspectionSysService.insert(inspectionSysModel);
            }
        }
    }

    @Override
    public String checkUrl(String url) {
        Integer svcType = getUrlType(url);
        return checkUrl(svcType, url);
    }

    @Override
    public String checkUrl(Integer svcType, String url) {
        String err = "";
        if(StringUtils.isEmpty(url)) {
            err += "地址不能为空！";
        } else {
            if (svcType != null) {
                if (SvcUrlEnum.SvcTypeEnum.Socket.getCode() == svcType.intValue()) {
                    if (!NetUtils.isIpPort(url)) {
                        err += "服务地址不符合Socket配置要求（正确格式示例：127.0.0.1:8080）！";
                    }
                } else if (SvcUrlEnum.SvcTypeEnum.Ws.getCode() == svcType.intValue()) {
                    if (!NetUtils.isHttp(url)) {
                        err += "服务地址不符合Http配置要求（必须“http://”或“https://”开头）！";
                    } else {
                        if (!url.toLowerCase().endsWith("?wsdl")) {
                            err += "服务地址不符合Web服务配置要求（必须以“?wsdl”结尾）！";
                        }
                    }
                } else {
                    if (!NetUtils.isHttp(url)) {
                        err += "服务地址不符合Http配置要求（必须“http://”或“https://”开头）！";
                    }
                }
            } else {
                err += "服务地址不符合规范！<br>Socket（正确格式示例：127.0.0.1:8080）<br>Restful（必须“http://”或“https://”开头）<br>Webservice（必须以“?wsdl”结尾）";
            }
        }
        return err;
    }

    @Override
    public int checkUrlStatus(String url) {
        Integer svcType = getUrlType(url);
        if (svcType == null) {
            return SvcUrlEnum.StatusEnum.Stop.getCode();
        }
        return checkUrlStatus(svcType, url);
    }

    /**
     * 检测服务地址状态
     */
    @Override
    public int checkUrlStatus(Integer svcType, String url) {
        SvcUrlEnum.StatusEnum result;
        if (svcType != null && SvcUrlEnum.SvcTypeEnum.Socket.getCode() == svcType.intValue()) {
            if (NetUtils.checkSocket(url)) {
                result = SvcUrlEnum.StatusEnum.Start;
            }
            else {
                result = SvcUrlEnum.StatusEnum.Stop;
            }
        } else {
            if (NetUtils.checkHttp(url)) {
                result = SvcUrlEnum.StatusEnum.Start;
            }
            else {
                result = SvcUrlEnum.StatusEnum.Stop;
            }
        }
        return result.getCode();
    }

    @Override
    public Integer getUrlType(String url) {
        Integer svcType;
        if (!StringUtils.isEmpty(url) && url.length() > 0) {
            url = url.toLowerCase();
            if (url.endsWith("?wsdl")) {
                svcType = SvcUrlEnum.SvcTypeEnum.Ws.getCode();
            } else if (NetUtils.isHttp(url)) {
                svcType = SvcUrlEnum.SvcTypeEnum.Rest.getCode();
            } else if(NetUtils.isIpPort(url)) {
                svcType = SvcUrlEnum.SvcTypeEnum.Socket.getCode();
            } else {
                svcType = null;
            }
        } else {
            svcType = null;
        }
        return svcType;
    }
}