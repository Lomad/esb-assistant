package com.winning.esb.service.middleware.rhapsody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.winning.esb.dao.IGrantDao;
import com.winning.esb.model.GrantModel;
import com.winning.esb.model.SvcUrlModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.ext.SvcInfoExtModel;
import com.winning.esb.model.url.EsbUrl;
import com.winning.esb.service.IAppInfoService;
import com.winning.esb.service.ISvcInfoService;
import com.winning.esb.service.ISvcUrlService;
import com.winning.esb.utils.Base64Utils;
import com.winning.esb.utils.HttpRequestUtils;
import com.winning.esb.utils.HttpsRequestUtils;
import com.winning.esb.utils.StringUtils;
import com.winning.esb.service.middleware.IMiddlewareService;
import org.dom4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.*;

@Component("Rhapsody")
public class RhapsodyServiceImpl implements IMiddlewareService{
    @Autowired
    private IGrantDao grantDao;
    @Autowired
    private ISvcInfoService svcInfoService;
    @Autowired
    private IAppInfoService appInfoService;
    @Autowired
    private ISvcUrlService svcUrlService;

    public EsbUrl parseUrl(String url){
        String[] urlParm = url.split(",");
        EsbUrl esbUrl = new EsbUrl();
        esbUrl.setIp(urlParm[1]);
        esbUrl.setPort(urlParm[2]);
        esbUrl.setUsername(urlParm[3]);
        esbUrl.setPassword(urlParm[4]);
        return esbUrl;
    }


    @Override
    public List<String> checkEndpoint(String url) {
        EsbUrl esbUrl = parseUrl(url);
        String restful = "https://"+esbUrl.getIp()+":"+esbUrl.getPort()+"/api/components";
        String responseStr = HttpsRequestUtils.httpsGet(restful,esbUrl.getUsername(),esbUrl.getPassword(),null);
        JSONObject jsonObject = JSON.parseObject(responseStr);
        JSONObject object = jsonObject.getJSONObject("data");
        JSONArray childFolders = object.getJSONArray("childFolders");
        JSONArray jsonArray= childNode(childFolders);
        List<String> resultList = new ArrayList<>();
        for(int i=0;i<jsonArray.size();i++){
            String type = jsonArray.getJSONObject(i).getString("type");
            if("COMMUNICATION_POINT".equals(type)){
                String state = jsonArray.getJSONObject(i).getString("state");
                if("STOPPED".equals(state)){
                    String id = jsonArray.getJSONObject(i).getString("id");
                    resultList.add(id);
                }
            }
        }
        return resultList;
    }

    @Override
    public List<String> checkRoute(String url) {
        EsbUrl esbUrl = parseUrl(url);
        String restful = "https://"+esbUrl.getIp()+":"+esbUrl.getPort()+"/api/components";
        String responseStr = HttpsRequestUtils.httpsGet(restful,esbUrl.getUsername(),esbUrl.getPassword(),null);
        JSONObject jsonObject = JSON.parseObject(responseStr);
        JSONObject object = jsonObject.getJSONObject("data");
        JSONArray childFolders = object.getJSONArray("childFolders");
        JSONArray jsonArray= childNode(childFolders);
        List<String> resultList = new ArrayList<>();
        for(int i=0;i<jsonArray.size();i++){
            String type = jsonArray.getJSONObject(i).getString("type");
            if("ROUTE".equals(type)){
                String state = jsonArray.getJSONObject(i).getString("state");
                if("STOPPED".equals(state)){
                    String id = jsonArray.getJSONObject(i).getString("id");
                    resultList.add(id);
                }
            }
        }
        return resultList;
    }

    @Override
    public Double checkCpu(String url) {
        EsbUrl esbUrl = parseUrl(url);
        String restful = "https://"+esbUrl.getIp()+":"+esbUrl.getPort()+"/api/statistics/cpuusage";
        String responseStr = HttpsRequestUtils.httpsGet(restful,esbUrl.getUsername(),esbUrl.getPassword(),null);
        Double result = Double.parseDouble(responseStr)/100;
        return result;
    }

    @Override
    public Double checkMemory(String url) {
        EsbUrl esbUrl = parseUrl(url);
        String restful = "https://"+esbUrl.getIp()+":"+esbUrl.getPort()+"/api/statistics/memoryusage";
        String responseStr = HttpsRequestUtils.httpsGet(restful,esbUrl.getUsername(),esbUrl.getPassword(),null);
        JSONObject jsonObject = JSON.parseObject(responseStr);
        Double inUse = jsonObject.getJSONObject("data").getDouble("inUse");
        Double totalAllocated = jsonObject.getJSONObject("data").getDouble("totalAllocated");
        Double result = inUse/totalAllocated;
        return result;
    }

    @Override
    public Double checkDisk(String url) {
        EsbUrl esbUrl = parseUrl(url);
        String restful = "https://"+esbUrl.getIp()+":"+esbUrl.getPort()+"/api/statistics/diskspace";
        String responseStr = HttpsRequestUtils.httpsGet(restful,esbUrl.getUsername(),esbUrl.getPassword(),null);
        JSONObject jsonObject = JSON.parseObject(responseStr);
        Double availableDataSpace = jsonObject.getJSONObject("data").getDouble("availableDataSpace");
        Double totalDataSpace = jsonObject.getJSONObject("data").getDouble("totalDataSpace");
        Double result = (availableDataSpace-availableDataSpace)/totalDataSpace;
        return result;
    }

    @Override
    public Integer checkErrorList(String url) {
        EsbUrl esbUrl = parseUrl(url);
        Integer result = 0;
        String restfulUrl = "https://"+esbUrl.getIp()+":"+esbUrl.getPort()+"/api/errorqueue/count";
        String respString = HttpsRequestUtils.httpsGet(restfulUrl, esbUrl.getUsername(), esbUrl.getPassword(),null);
        try {
            Document document = DocumentHelper.parseText(respString);
            Element root = document.getRootElement();
            Node node = root.selectSingleNode("//html/body/div/div/div");
            String nodeText = node.getText();
            String[] strParm = nodeText.split(":");
            result = Integer.valueOf(strParm[1].trim());
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return result;
    }

    //rhapsody 6.2
    @Override
    public void releaseGrant(String url){
        EsbUrl esbUrl = parseUrl(url);
        String restfulUrl = "https://"+esbUrl.getIp()+":"+esbUrl.getPort()+"/admin/lookuptables/"+"AppKeys";
        String response = HttpsRequestUtils.httpsGet(restfulUrl,esbUrl.getUsername(),esbUrl.getPassword(),null);
        Document document = null;
        String attr = document.getRootElement().attributeValue("guid");
        if(StringUtils.isEmpty(attr)){
             attr =  UUID.randomUUID().toString();
        }
        restfulUrl = "https://"+esbUrl.getIp()+":"+esbUrl.getPort()+"/admin/lookuptables/"+attr;

        try {
            //生成请求的xml
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document document1 =  builder.newDocument();
            org.w3c.dom.Element root = document1.createElement("lookupTable");
            root.setAttribute("version","5");
            root.setAttribute("guid",attr);
            document1.appendChild(root);
            org.w3c.dom.Element name = document1.createElement("name");
            name.appendChild(document1.createTextNode("AppKeys"));
            root.appendChild(name);
            org.w3c.dom.Element description = document1.createElement("description");
            description.appendChild(document1.createTextNode("授权表"));
            root.appendChild(description);
            org.w3c.dom.Element isRecordingFailures = document1.createElement("isRecordingFailures");
            isRecordingFailures.appendChild(document1.createTextNode("true"));
            root.appendChild(isRecordingFailures);
            org.w3c.dom.Element autoKeyDefinition = document1.createElement("autoKeyDefinition");
            autoKeyDefinition.appendChild(document1.createTextNode("true"));
            root.appendChild(autoKeyDefinition);
            org.w3c.dom.Element column = document1.createElement("column");
            org.w3c.dom.Element name1 = document1.createElement("name");
            name1.appendChild(document1.createTextNode("key"));
            column.appendChild(name1);
            org.w3c.dom.Element isKeyColumn = document1.createElement("isKeyColumn");
            isKeyColumn.appendChild(document1.createTextNode("true"));
            column.appendChild(isKeyColumn);
            org.w3c.dom.Element defaultValue = document1.createElement("defaultValue");
            defaultValue.appendChild(document1.createTextNode(""));
            column.appendChild(defaultValue);
            org.w3c.dom.Element column1 = document1.createElement("column");
            org.w3c.dom.Element name2 = document1.createElement("name");
            name2.appendChild(document1.createTextNode("value"));
            column1.appendChild(name2);
            org.w3c.dom.Element isKeyColumn1 = document1.createElement("isKeyColumn");
            isKeyColumn1.appendChild(document1.createTextNode("false"));
            column1.appendChild(isKeyColumn1);
            column1.appendChild(defaultValue);
            root.appendChild(column1);

            CommonObject commonObject = grantDao.query(null);
            Collection<GrantModel> collection = commonObject.getDatas();
            String requestBody = "";
            for(GrantModel grantModel:collection){
                String appId = appInfoService.getByID(grantModel.getAid()).getAppId().trim();
                String svcCode = svcInfoService.getByID(grantModel.getSid()).getCode().trim();
                String key = appId + "." + svcCode;
                String value = grantModel.getLic_key().trim();
                requestBody += "\""+key +"\""+","+ "\""+ value +"\""+ "\n";

            }
            String value = Base64Utils.getBase64(requestBody);
            org.w3c.dom.Element values = document1.createElement("values");
            values.setAttribute("compressed","false");
            isKeyColumn1.appendChild(document1.createTextNode(value));

            DOMSource source = new DOMSource(document1);
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory factory1 = TransformerFactory.newInstance();
            Transformer transformer = factory1.newTransformer();
            //transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            String str = stringWriter.getBuffer().toString();
            String body = str.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>","");

            HttpRequestUtils.httpPut(restfulUrl,esbUrl.getUsername(),esbUrl.getPassword(),body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void releaseToken(String url) {

    }

    @Override
    public void releaseToken_Test(String url) {

    }

    @Override
    public String releaseUrl(String url) {
        EsbUrl esbUrl = parseUrl(url);
        String restfulUrl = "https://"+esbUrl.getIp()+":"+esbUrl.getPort()+"/admin/lookuptables/"+"AppKeys";
        String response = HttpsRequestUtils.httpsGet(restfulUrl,esbUrl.getUsername(),esbUrl.getPassword(),null);
        Document document = null;
        String attr = document.getRootElement().attributeValue("guid");
        if(StringUtils.isEmpty(attr)){
            attr =  UUID.randomUUID().toString();
        }
        restfulUrl = "https://"+esbUrl.getIp()+":"+esbUrl.getPort()+"/admin/lookuptables/"+attr;

        try {
            //生成请求的xml
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document document1 =  builder.newDocument();
            org.w3c.dom.Element root = document1.createElement("lookupTable");
            root.setAttribute("version","5");
            root.setAttribute("guid",attr);
            document1.appendChild(root);
            org.w3c.dom.Element name = document1.createElement("name");
            name.appendChild(document1.createTextNode("SvcUrls"));
            root.appendChild(name);
            org.w3c.dom.Element description = document1.createElement("description");
            description.appendChild(document1.createTextNode("服务地址表"));
            root.appendChild(description);
            org.w3c.dom.Element isRecordingFailures = document1.createElement("isRecordingFailures");
            isRecordingFailures.appendChild(document1.createTextNode("true"));
            root.appendChild(isRecordingFailures);
            org.w3c.dom.Element autoKeyDefinition = document1.createElement("autoKeyDefinition");
            autoKeyDefinition.appendChild(document1.createTextNode("true"));
            root.appendChild(autoKeyDefinition);
            org.w3c.dom.Element column = document1.createElement("column");
            org.w3c.dom.Element name1 = document1.createElement("name");
            name1.appendChild(document1.createTextNode("key"));
            column.appendChild(name1);
            org.w3c.dom.Element isKeyColumn = document1.createElement("isKeyColumn");
            isKeyColumn.appendChild(document1.createTextNode("true"));
            column.appendChild(isKeyColumn);
            org.w3c.dom.Element defaultValue = document1.createElement("defaultValue");
            defaultValue.appendChild(document1.createTextNode(""));
            column.appendChild(defaultValue);
            org.w3c.dom.Element column1 = document1.createElement("column");
            org.w3c.dom.Element name2 = document1.createElement("name");
            name2.appendChild(document1.createTextNode("value"));
            column1.appendChild(name2);
            org.w3c.dom.Element isKeyColumn1 = document1.createElement("isKeyColumn");
            isKeyColumn1.appendChild(document1.createTextNode("false"));
            column1.appendChild(isKeyColumn1);
            column1.appendChild(defaultValue);
            root.appendChild(column1);

            CommonObject commonObject = grantDao.query(null);
            Collection<GrantModel> collection = commonObject.getDatas();
            String requestBody = ""; List<SvcInfoExtModel> svcInfoExtModels = svcInfoService.listExt(null);

            for(SvcInfoExtModel svcInfoModel:svcInfoExtModels){
                Integer urlId = svcInfoModel.getSvcInfo().getUrlId();
                String code = svcInfoModel.getSvcInfo().getCode();
                SvcUrlModel svcUrlModel = svcUrlService.getByID(urlId);
                String svcUrl = svcUrlModel.getUrl();
                String key = code.trim();
                String value = svcUrl.trim();
                requestBody += "\""+key +"\""+","+ "\""+ value +"\""+ "\n";

            }
            String value = Base64Utils.getBase64(requestBody);
            org.w3c.dom.Element values = document1.createElement("values");
            values.setAttribute("compressed","false");
            isKeyColumn1.appendChild(document1.createTextNode(value));

            DOMSource source = new DOMSource(document1);
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory factory1 = TransformerFactory.newInstance();
            Transformer transformer = factory1.newTransformer();
            //transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            String str = stringWriter.getBuffer().toString();
            String body = str.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>","");
            HttpRequestUtils.httpPut(restfulUrl,esbUrl.getUsername(),esbUrl.getPassword(),body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String releaseSecret(String url) {
        return null;
    }

    @Override
    public String releaseSvcApp(String url) {
        return null;
    }

    private static JSONArray childNode(JSONArray jsonArray){
        JSONArray childComponents = new JSONArray();
        try{
            for(int i=0;i<jsonArray.size();i++) {
                JSONArray childFolders2 = jsonArray.getJSONObject(i).getJSONArray("childFolders");
                if(childFolders2 != null && childFolders2.size()>0){
                    childComponents.addAll(childNode(childFolders2));
                }else{
                    childComponents.addAll(jsonArray.getJSONObject(i).getJSONArray("childComponents"));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return childComponents;
    }

    @Override
    public void releaseApiUrl(String url){

    }

}
