package com.winning.esb.service.middleware.odin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.winning.esb.model.*;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.enums.GrantEnum;
import com.winning.esb.model.enums.SvcInfoEnum;
import com.winning.esb.model.ext.MonitorEntity;
import com.winning.esb.model.ext.SvcInfoExtModel;
import com.winning.esb.model.middleware.Items;
import com.winning.esb.model.middleware.OdinGrantEntity;
import com.winning.esb.model.url.EsbUrl;
import com.winning.esb.service.*;
import com.winning.esb.service.middleware.IMiddlewareService;
import com.winning.esb.utils.HttpRequestUtils;
import com.winning.esb.utils.NetUtils;
import com.winning.esb.utils.StringUtils;
import com.winning.esb.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("Odin")
public class OdinServiceImpl implements IMiddlewareService {
    private static final Logger logger = LoggerFactory.getLogger(OdinServiceImpl.class);
    @Autowired
    private IGrantService grantService;
    @Autowired
    private ISvcInfoService svcInfoService;
    @Autowired
    private IAppInfoService appInfoService;
    @Autowired
    private ISvcUrlService svcUrlService;
    @Autowired
    private IGrantSvcStructureService grantSvcStructureService;
    @Autowired
    private ISvcStructureService svcStructureService;

    public EsbUrl parseUrl(String url) {
        String[] urlParm = url.split(",");
        EsbUrl esbUrl = new EsbUrl();
        esbUrl.setIp(urlParm[0]);
        esbUrl.setPort(urlParm[1]);
        esbUrl.setUsername(urlParm[2]);
        esbUrl.setPassword(urlParm[3]);
        return esbUrl;
    }

    @Override
    public List<String> checkEndpoint(String url) {
        //终端是否运行
        EsbUrl esbUrl = parseUrl(url);
        String restfulUrl = "http://" + esbUrl.getIp() + ":" + esbUrl.getPort() + "/api/v1/endpoint";
        JSONArray responseJson = HttpRequestUtils.httpGet(restfulUrl, esbUrl.getUsername(), esbUrl.getPassword());
        List<String> stopIdList = new ArrayList<>();
        for (int i = 0; i < responseJson.size(); i++) {
            String stat = responseJson.getJSONObject(i).getString("state");
            if ("STOPPED".equals(stat)) {
                String id = responseJson.getJSONObject(i).getString("id");
                stopIdList.add(id);
            }
        }

        //终端等待处理消息队列
        String restfulUrl2 = "http://" + esbUrl.getIp() + ":" + esbUrl.getPort() + "/api/v1/queue/lengths";
        String responseJson2 = HttpRequestUtils.httpGet(restfulUrl2, esbUrl.getUsername(), esbUrl.getPassword(), null);
        JSONObject jsonObject = JSON.parseObject(responseJson2);
        JSONObject endpoints = jsonObject.getJSONObject("endpoints");
        Set<String> keySet = endpoints.keySet();
        for (String key : keySet) {
            JSONObject singleEndpoint = endpoints.getJSONObject(key);
            Integer queneLength = singleEndpoint.getInteger("length");
            if (queneLength > 0) {
                stopIdList.add(key);
            }
        }
        return stopIdList;
    }

    @Override
    public List<String> checkRoute(String url) {
        EsbUrl esbUrl = parseUrl(url);
        List<String> list_key = new ArrayList<>();
        List<String> stopIdList = new ArrayList<>();
        String restfulUrl = "http://" + esbUrl.getIp() + ":" + esbUrl.getPort() + "/api/v1/project";
        JSONArray responseJson = HttpRequestUtils.httpGet(restfulUrl, esbUrl.getUsername(), esbUrl.getPassword());
        for (int i = 0; i < responseJson.size(); i++) {
            Set<String> stat = responseJson.getJSONObject(i).getJSONObject("routeStates").keySet();
            for (String str : stat) {
                list_key.add(str);
                String value = responseJson.getJSONObject(i).getJSONObject("routeStates").getString(str);
                if ("STOPPED".equals(value)) {
                    stopIdList.add(str);
                }
            }

        }
        return stopIdList;
    }

    @Override
    public Double checkCpu(String url) {
        EsbUrl esbUrl = parseUrl(url);
        Double result = 0.0;
        String restfulUrl = "http://" + esbUrl.getIp() + ":" + esbUrl.getPort() + "/jolokia/";
        //cpu
        MonitorEntity monitorEntity1 = new MonitorEntity();
        monitorEntity1.setType("read");
        monitorEntity1.setAttribute("SystemCpuLoad");
        monitorEntity1.setMbean("java.lang:type=OperatingSystem");
        monitorEntity1.setGroup("cpu");
        MonitorEntity monitorEntity2 = new MonitorEntity();
        monitorEntity2.setType("read");
        monitorEntity2.setAttribute("ProcessCpuLoad");
        monitorEntity2.setMbean("java.lang:type=OperatingSystem");
        monitorEntity2.setGroup("cpu");
        List<MonitorEntity> monitorEntityList = new ArrayList<MonitorEntity>();
        monitorEntityList.add(monitorEntity1);
        monitorEntityList.add(monitorEntity2);
        String jsonString = JSON.toJSONString(monitorEntityList);
        JSONArray responseJson = HttpRequestUtils.httpPost(restfulUrl, esbUrl.getUsername(), esbUrl.getPassword(), jsonString);
        for (int i = 0; i < responseJson.size(); i++) {
            String attribute = responseJson.getJSONObject(i).getJSONObject("request").getString("attribute");
            if ("SystemCpuLoad".equals(attribute)) {
                result = responseJson.getJSONObject(i).getDouble("value");

            }
        }
        return result;
    }

    @Override
    public Double checkMemory(String url) {
        Double result2 = 0.0;
        Double memoryTotal = 1.0;
        Double memoryUnused = 0.0;
        EsbUrl esbUrl = parseUrl(url);
        String restfulUrl = "http://" + esbUrl.getIp() + ":" + esbUrl.getPort() + "/jolokia/";
        MonitorEntity monitorEntity3 = new MonitorEntity();
        monitorEntity3.setType("read");
        monitorEntity3.setAttribute("HeapMemoryUsage");
        monitorEntity3.setMbean("java.lang:type=Memory");
        monitorEntity3.setGroup("langMemory");
        monitorEntity3.setPath("committed");
        MonitorEntity monitorEntity4 = new MonitorEntity();
        monitorEntity4.setType("read");
        monitorEntity4.setAttribute("NonHeapMemoryUsage");
        monitorEntity4.setMbean("java.lang:type=Memory");
        monitorEntity4.setGroup("langMemory");
        monitorEntity4.setPath("committed");
        List<MonitorEntity> monitorEntityList2 = new ArrayList<MonitorEntity>();
        monitorEntityList2.add(monitorEntity3);
        monitorEntityList2.add(monitorEntity4);
        String jsonString2 = JSON.toJSONString(monitorEntityList2);
        JSONArray responseJson2 = HttpRequestUtils.httpPost(restfulUrl, esbUrl.getUsername(), esbUrl.getPassword(), jsonString2);
        for (int i = 0; i < responseJson2.size(); i++) {
            String attribute = responseJson2.getJSONObject(i).getJSONObject("request").getString("attribute");
            if ("HeapMemoryUsage".equals(attribute)) {
                memoryTotal = responseJson2.getJSONObject(i).getDouble("value");

            }
            if ("NonHeapMemoryUsage".equals(attribute)) {
                memoryUnused = responseJson2.getJSONObject(i).getDouble("value");

            }
        }
        Double memoryUsed = memoryTotal - memoryUnused;
        result2 = memoryUsed / memoryTotal;
        return result2;
    }

    @Override
    public Double checkDisk(String url) {
        Double result3 = 0.0;
        EsbUrl esbUrl = parseUrl(url);
        String restfulUrl = "http://" + esbUrl.getIp() + ":" + esbUrl.getPort() + "/jolokia/";
        MonitorEntity monitorEntity5 = new MonitorEntity();
        monitorEntity5.setType("read");
        monitorEntity5.setAttribute("UsePercent");
        monitorEntity5.setMbean("sigar:type=FileSystem");
        monitorEntity5.setGroup("disk");
        MonitorEntity monitorEntity6 = new MonitorEntity();
        monitorEntity6.setType("read");
        monitorEntity6.setAttribute("Used");
        monitorEntity6.setMbean("sigar:type=FileSystem");
        monitorEntity6.setGroup("disk");
        List<MonitorEntity> monitorEntityList3 = new ArrayList<MonitorEntity>();
        monitorEntityList3.add(monitorEntity5);
        monitorEntityList3.add(monitorEntity6);
        String jsonString3 = JSON.toJSONString(monitorEntityList3);
        JSONArray responseJson3 = HttpRequestUtils.httpPost(restfulUrl, esbUrl.getUsername(), esbUrl.getPassword(), jsonString3);
        for (int i = 0; i < responseJson3.size(); i++) {
            String attribute = responseJson3.getJSONObject(i).getJSONObject("request").getString("attribute");
            if ("UsePercent".equals(attribute)) {
                result3 = responseJson3.getJSONObject(i).getDouble("value");

            }
        }
        return result3;
    }

    @Override
    public Integer checkErrorList(String url) {
        EsbUrl esbUrl = parseUrl(url);
        String restfulUrl = "http://" + esbUrl.getIp() + ":" + esbUrl.getPort() + "/api/v1/deadletter/count";
        JSONArray jsonArray = HttpRequestUtils.httpGet(url, esbUrl.getUsername(), esbUrl.getPassword());
        Integer errNum = jsonArray.getJSONObject(0).getInteger("count");
        return errNum;
    }

    @Override
    public void releaseGrant(String url) {
        releaseCommon(url, GrantEnum.ReleaseTypeEnum.LicKey.getCode());
    }

    @Override
    public void releaseApiUrl(String url) {
        String path = "";
        String hostIP = NetUtils.getHostIP();
        //logger.error("ip:"+hostIP);
        Integer port = NetUtils.getTomcatPortFromConfigXml();
        //logger.error("port:"+port);
        String webappName = NetUtils.getTomcatWebappName();
        //logger.error("webappName:"+webappName);
        if (!StringUtils.isEmpty(hostIP) && port != null && !StringUtils.isEmpty(webappName)) {
            path = hostIP + ":" + port + "/" + webappName + "/api";
            //logger.error("path:"+path);
        }
        EsbUrl esbUrl = parseUrl(url);
        String odinIp = esbUrl.getIp();
        String odinPort = esbUrl.getPort();
        String userName = esbUrl.getUsername();
        String password = esbUrl.getPassword();
        String restfulUrl = "http://" + odinIp + ":" + odinPort + "/api/v1/codemapping/source";

        JSONArray responseJson = HttpRequestUtils.httpGet(restfulUrl, userName, password);
        List<String> nameList = new ArrayList<>();
        String tokenMapId = "";
        String tokenMapUrl = restfulUrl;
        for (int i = 0; i < responseJson.size(); i++) {
            String name = responseJson.getJSONObject(i).getString("name");
            nameList.add(name);
            if ("ESB_ApiUrl".equals(name)) {
                tokenMapId = responseJson.getJSONObject(i).getString("id");
                tokenMapUrl = "http://" + odinIp + ":" + odinPort + "/api/v1/codemapping/source/" + tokenMapId;
            }
        }
        OdinGrantEntity grantEntity = new OdinGrantEntity();
        Items items = new Items();
        grantEntity.setName("ESB_ApiUrl");
        grantEntity.setType("MANUAL");
        items.setKey("ApiUrl");
        items.setValue(path);
        grantEntity.getItems().add(items);
        if (nameList.contains("ESB_ApiUrl")) {
            grantEntity.setId(tokenMapId);
            String jsonString1 = JSON.toJSONString(grantEntity);
            HttpRequestUtils.httpPut(tokenMapUrl, userName, password, jsonString1);
        } else {
            String jsonString = JSON.toJSONString(grantEntity);
            HttpRequestUtils.httpPost(tokenMapUrl, userName, password, jsonString, false);
        }

    }


    @Override
    public void releaseToken(String url) {
        EsbUrl esbUrl = parseUrl(url);
        String odinIp = esbUrl.getIp();
        String odinPort = esbUrl.getPort();
        String userName = esbUrl.getUsername();
        String password = esbUrl.getPassword();
        String restfulUrl = "http://" + odinIp + ":" + odinPort + "/api/v1/codemapping/source";

        JSONArray responseJson = HttpRequestUtils.httpGet(restfulUrl, userName, password);
        List<String> nameList = new ArrayList<>();
        String tokenMapId = "";
        String tokenMapUrl = restfulUrl;
        for (int i = 0; i < responseJson.size(); i++) {
            String name = responseJson.getJSONObject(i).getString("name");
            nameList.add(name);
            if (GrantEnum.ReleaseTypeEnum.Token.getCode().equals(name)) {
                tokenMapId = responseJson.getJSONObject(i).getString("id");
                tokenMapUrl = "http://" + odinIp + ":" + odinPort + "/api/v1/codemapping/source/" + tokenMapId;
            }
        }

        Map<String, Object> map = new HashMap<>();
        CommonObject commonObject = grantService.query(map);
        Collection<GrantModel> collection = commonObject.getDatas();
        //同步Token
        OdinGrantEntity grantEntity1 = new OdinGrantEntity();
        LinkedHashSet<Integer> aidSet = new LinkedHashSet<>();
        for (GrantModel grantModel2 : collection) {
            grantEntity1.setName(GrantEnum.ReleaseTypeEnum.Token.getCode());
            grantEntity1.setType("MANUAL");
            Integer aid = grantModel2.getAid();
            aidSet.add(aid);
        }
        for (Integer aid : aidSet) {
            List<GrantModel> grantModelList = grantService.queryByAid(aid);
            String appId;
            String urlNOIpAndPort = "";
            String authLastStr = "";

            SvcInfoModel svcInfoModel;
            for (GrantModel grantModel : grantModelList) {
                String svcUrl = "";
                Integer otherMark = 0;

                svcInfoModel = svcInfoService.getByID(grantModel.getSid());
                if (svcInfoModel != null) {

                    otherMark = svcInfoModel.getOtherMark();
                    Integer svcUrlId = svcInfoModel.getUrlId();
                    if (svcUrlId != null) {
                        SvcUrlModel urlModel = svcUrlService.getByID(svcUrlId);
                        if (urlModel != null) {
                            svcUrl = urlModel.getUrl();
                        }
                    } else if (!StringUtils.isEmpty(svcInfoModel.getUrl())) {
                        svcUrl = svcInfoModel.getUrl();
                    }
                }

                Integer state = grantModel.getApprove_state();
                if (state == 1 && otherMark.intValue() == 1) {
                    appId = appInfoService.getByID(aid).getAppId().trim();
                    if (!StringUtils.isEmpty(svcUrl)) {
                        String[] parm = svcUrl.split(":");
                        urlNOIpAndPort = parm[2].substring(parm[2].indexOf("/"));
                    }
                    Integer grantId = grantModel.getId();
                    List<GrantSvcStructureModel> grantSvcStructureModels = grantSvcStructureService.queryByGid(grantId);
                    List<String> codeList = new ArrayList<>();
                    for (GrantSvcStructureModel grantSvcStructureModel : grantSvcStructureModels) {
                        Integer ssid = grantSvcStructureModel.getSsid();
                        Integer pid = 0;
                        SvcStructureModel svcStructureModel = svcStructureService.queryById(ssid);
                        if (svcStructureModel != null) {
                            pid = svcStructureModel.getPid();
                            String code = null;
                            if (pid.intValue() > 0) {
                                code = svcStructureModel.getCode();
                            }
                            if (!StringUtils.isEmpty(code)) {
                                codeList.add(code);
                            }
                        }
                    }
                    for (String s : codeList) {
                        if (StringUtils.isEmpty(authLastStr)) {
                            authLastStr += s;
                        } else {
                            authLastStr += "," + s;
                        }
                    }
                    JSONObject jsonObject = new JSONObject();
                    if (!StringUtils.isEmpty(urlNOIpAndPort)) {
                        jsonObject.put(urlNOIpAndPort.trim(), authLastStr);
                    }
                    String authStr = JSON.toJSONString(jsonObject);
                    String auth = TokenUtils.generateToken(appId, authStr);
                    String tokenKey = appId + "." + svcInfoService.getByID(grantModel.getSid()).getCode();
                    Items items2 = new Items();
                    if (!StringUtils.isEmpty(tokenKey)) {
                        items2.setKey(tokenKey);
                    }
                    if (!StringUtils.isEmpty(auth)) {
                        items2.setValue(auth);
                    }
                    grantEntity1.getItems().add(items2);
                }
            }
        }
        String err = "";
        if (nameList != null && nameList.contains(GrantEnum.ReleaseTypeEnum.Token.getCode())) {
            grantEntity1.setId(tokenMapId);
            String jsonString1 = JSON.toJSONString(grantEntity1);
            HttpRequestUtils.httpPut(tokenMapUrl, userName, password, jsonString1);
        } else {
            String jsonString = JSON.toJSONString(grantEntity1);
            try {
                String responseJson2 = HttpRequestUtils.httpPost(tokenMapUrl, userName, password, jsonString, false);
            } catch (Exception e) {
                err = e.getMessage();
            }
        }


    }

    @Override
    public void releaseToken_Test(String url) {
        EsbUrl esbUrl = parseUrl(url);
        String odinIp = esbUrl.getIp();
        String odinPort = esbUrl.getPort();
        String userName = esbUrl.getUsername();
        String password = esbUrl.getPassword();
        String restfulUrl = "http://" + odinIp + ":" + odinPort + "/api/v1/codemapping/source";
        JSONArray responseJson = HttpRequestUtils.httpGet(restfulUrl, userName, password);
        List<String> nameList = new ArrayList<>();
        String tokenMapId = "";
        String tokenMapUrl = restfulUrl;
        for (int i = 0; i < responseJson.size(); i++) {
            String name = responseJson.getJSONObject(i).getString("name");
            nameList.add(name);
            if (GrantEnum.ReleaseTypeEnum.Token_Test.getCode().equals(name)) {
                tokenMapId = responseJson.getJSONObject(i).getString("id");
                tokenMapUrl = "http://" + odinIp + ":" + odinPort + "/api/v1/codemapping/source/" + tokenMapId;
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("otherMark", 1);
        List<SvcInfoModel> svcInfoModels = svcInfoService.list(map);

        //同步Token
        OdinGrantEntity grantEntity1 = new OdinGrantEntity();
        for (SvcInfoModel svcModel : svcInfoModels) {
            grantEntity1.setName(GrantEnum.ReleaseTypeEnum.Token_Test.getCode());
            grantEntity1.setType("MANUAL");
            String appId;
            String urlNOIpAndPort;
            String svcUrl = "";
            Integer svcUrlId = svcModel.getUrlId();
            if (svcUrlId != null) {
                SvcUrlModel urlModel = svcUrlService.getByID(svcUrlId);
                if (urlModel != null) {
                    svcUrl = urlModel.getUrl();
                }
            } else if (!StringUtils.isEmpty(svcModel.getUrl())) {
                svcUrl = svcModel.getUrl();
            }

            appId = "ESB_TEST";
            if (!StringUtils.isEmpty(svcUrl)) {
                String[] parm = svcUrl.split(":");
                urlNOIpAndPort = parm[2].substring(parm[2].indexOf("/"));
                String authStr = "{" + urlNOIpAndPort + ":[]";
                String auth = TokenUtils.generateToken(appId, authStr);
                String tokenKey = appId + "." + svcModel.getCode();
                Items items2 = new Items();
                if (!StringUtils.isEmpty(tokenKey)) {
                    items2.setKey(tokenKey);
                }
                if (!StringUtils.isEmpty(auth)) {
                    items2.setValue(auth);
                }
                grantEntity1.getItems().add(items2);

            }
        }

        if (nameList != null && nameList.contains(GrantEnum.ReleaseTypeEnum.Token_Test.getCode())) {
            grantEntity1.setId(tokenMapId);
            String jsonString1 = JSON.toJSONString(grantEntity1);
            HttpRequestUtils.httpPut(tokenMapUrl, userName, password, jsonString1);
        } else {
            String jsonString = JSON.toJSONString(grantEntity1);
            HttpRequestUtils.httpPost(tokenMapUrl, userName, password, jsonString, false);
        }

    }


    @Override
    public String releaseUrl(String url) {
        //同步到ESB
        releaseCommon(url, GrantEnum.ReleaseTypeEnum.SvcUrl.getCode());
        return null;
    }

    @Override
    public String releaseSecret(String url) {
        releaseCommon(url, GrantEnum.ReleaseTypeEnum.SecretKey.getCode());
        return null;
    }

    @Override
    public String releaseSvcApp(String url) {
        releaseCommon(url, GrantEnum.ReleaseTypeEnum.SvcApp.getCode());
        return null;
    }

    private void releaseCommon(String url, String tableName) {
        OdinGrantEntity grantEntity = new OdinGrantEntity();
        EsbUrl esbUrl = parseUrl(url);
        String odinIp = esbUrl.getIp();
        String odinPort = esbUrl.getPort();
        String userName = esbUrl.getUsername();
        String password = esbUrl.getPassword();
        String restfulUrl = "http://" + odinIp + ":" + odinPort + "/api/v1/codemapping/source";
        JSONArray responseJson = HttpRequestUtils.httpGet(restfulUrl, userName, password);
        List<String> nameList = new ArrayList<>();
        String id = "";
        for (int i = 0; i < responseJson.size(); i++) {
            String name = responseJson.getJSONObject(i).getString("name");
            nameList.add(name);
            if (tableName.equals(name)) {
                id = responseJson.getJSONObject(i).getString("id");
                restfulUrl = "http://" + odinIp + ":" + odinPort + "/api/v1/codemapping/source/" + id;
            }
        }

        // 根据类型来同步
        if (GrantEnum.ReleaseTypeEnum.SecretKey.getCode().equals(tableName)
                || GrantEnum.ReleaseTypeEnum.LicKey.getCode().equals(tableName)) {
            //同步授权码或密钥
            Map<String, Object> map = new HashMap<>();
            CommonObject commonObject = grantService.query(map);
            Collection<GrantModel> collection = commonObject.getDatas();
            for (GrantModel grantModel : collection) {
                Items items = new Items();
                grantEntity.setName(tableName);
                grantEntity.setType("MANUAL");
                Integer stat = grantModel.getApprove_state();
                String appId = null;
                String svcCode = null;
                if (stat.intValue() == GrantEnum.ApproveStateEnum.Approved.getCode()) {
                    AppInfoModel appInfoModel = appInfoService.getByID(grantModel.getAid());
                    SvcInfoModel svcInfoModel = svcInfoService.getByID(grantModel.getSid());
                    if (appInfoModel != null) {
                        appId = appInfoModel.getAppId();
                    }
                    if (svcInfoModel != null) {
                        svcCode = svcInfoModel.getCode();
                    }
                    if (!StringUtils.isEmpty(appId) && !StringUtils.isEmpty(svcCode)) {
                        if (!StringUtils.isEmpty(grantModel.getLic_key())
                                && GrantEnum.ReleaseTypeEnum.LicKey.getCode().equals(tableName)) {
                            items.setKey(appId + "." + svcCode);
                            items.setValue(grantModel.getLic_key());
                            grantEntity.getItems().add(items);
                        } else if (!StringUtils.isEmpty(grantModel.getSecret_key())
                                && GrantEnum.ReleaseTypeEnum.SecretKey.getCode().equals(tableName)) {
                            items.setKey(appId + "." + svcCode);
                            items.setValue(grantModel.getSecret_key());
                            grantEntity.getItems().add(items);
                        }
                    }
                }
            }
        } else if (GrantEnum.ReleaseTypeEnum.SvcUrl.getCode().equals(tableName)) {
            //同步地址
            Map<String, Object> map = new HashMap<>();
            List<SvcInfoExtModel> svcInfoExtModels = svcInfoService.listExt(map);
            for (SvcInfoExtModel svcInfoModel : svcInfoExtModels) {
                Integer urlId = svcInfoModel.getSvcInfo().getUrlId();
                Integer status = svcInfoModel.getSvcInfo().getStatus();
                if (status.intValue() == SvcInfoEnum.StatusEnum.Published.getCode()) {
                    String code = svcInfoModel.getSvcInfo().getCode();
                    String svcUrl = null;
                    String primaryUrl = svcInfoModel.getSvcInfo().getUrl();
                    if (urlId != null) {
                        SvcUrlModel svcUrlModel = svcUrlService.getByID(urlId);
                        svcUrl = svcUrlModel.getUrl();
                    } else if (!StringUtils.isEmpty(primaryUrl)) {
                        svcUrl = primaryUrl;
                    }
                    if (!StringUtils.isEmpty(svcUrl)) {
                        Items items3 = new Items();
                        grantEntity.setName(tableName);
                        grantEntity.setType("MANUAL");
                        items3.setKey(code);
                        items3.setValue(svcUrl);
                        grantEntity.getItems().add(items3);
                    }
                }
            }
        } else if (GrantEnum.ReleaseTypeEnum.SvcApp.getCode().equals(tableName)) {
            Map<String, Object> map = new HashMap<>();
            List<SvcInfoExtModel> svcInfoExtModels = svcInfoService.listExt(map);
            for (SvcInfoExtModel svcInfoModel : svcInfoExtModels) {
                Integer aid = svcInfoModel.getSvcInfo().getAid();
                String code = svcInfoModel.getSvcInfo().getCode();
                String appId = appInfoService.getByID(aid).getAppId();
                Integer status = svcInfoModel.getSvcInfo().getStatus();
                if (status.intValue() == SvcInfoEnum.StatusEnum.Published.getCode()) {
                    if (!StringUtils.isEmpty(code) && !StringUtils.isEmpty(appId)) {
                        Items items5 = new Items();
                        grantEntity.setName(tableName);
                        grantEntity.setType("MANUAL");
                        items5.setKey(code);
                        items5.setValue(appId);
                        grantEntity.getItems().add(items5);
                    }
                }
            }

        }

        if (nameList.contains(tableName)) {
            grantEntity.setId(id);
            String jsonString1 = JSON.toJSONString(grantEntity);
            HttpRequestUtils.httpPut(restfulUrl, userName, password, jsonString1);
        } else {
            String jsonString = JSON.toJSONString(grantEntity);
            String responseJson2 = HttpRequestUtils.httpPost(restfulUrl, userName, password, jsonString, false);
        }
    }
}
