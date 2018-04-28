package com.winning.esb;

import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuehao on 2017/10/24.
 */
public class TestRestful {

    public static void main(String[] args) {
//        Object res = postForRest();
//        System.out.println(res);
    }

    private static Object postForRest(){
        String url = "http://127.0.0.1:9080/service/ajax_pub/serviceManage/svcInfo/query";

        url = addUrlParams(url, "datas");
        RestTemplate restTemplate = new RestTemplate();

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setBufferRequestBody(false);
        requestFactory.setConnectTimeout(1000);
        restTemplate.setRequestFactory(requestFactory);

        Map<String, Object> params = new HashMap();
        params.put("code", "测试");
        String para = JSON.toJSONString(params);

        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
//        headers.setContentType(MediaType.parseMediaType("application/json"));
        headers.setContentType(MediaType.parseMediaType("application/x-www-form-urlencoded"));

        Object response = restTemplate.postForObject(url, new HttpEntity<String>(headers), Object.class, para);

        return response;
    }

    private static String addUrlParams(String url, String key) {
        return url + "?" + key + "=" + "{json}";
    }
}