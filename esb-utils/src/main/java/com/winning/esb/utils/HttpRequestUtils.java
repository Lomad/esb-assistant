package com.winning.esb.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLDecoder;

/**
 * 主要用于http请求等操作（restful方式）
 * @author 汪文汉
 */
public class HttpRequestUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpRequestUtils.class);    //日志记录

    /**
     * httpPost
     *
     * @param url       路径
     * @param jsonParam 参数
     * @return
     */
    public static JSONArray httpPost(String url,
                                     String userName,
                                     String password,
                                     String jsonParam) {
        try {
            String result = httpPost(url, userName, password, jsonParam, false);
            return JSONObject.parseArray(result);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * post请求
     *
     * @param url            url地址
     * @param jsonParam      参数
     * @param noNeedResponse 不需要返回结果
     * @return
     */
    public static String httpPost(String url, String userName, String password, String jsonParam, boolean noNeedResponse) {
        //post请求返回结果
        CloseableHttpClient httpClient = null;
        JSONArray jsonResult = null;
        String str = "";
        HttpPost method = new HttpPost(url);
        String auth = basicAuth(userName, password);
        try {
            if (null != jsonParam) {
                //解决中文乱码问题
                StringEntity entity = new StringEntity(jsonParam, "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                method.setEntity(entity);
                method.setHeader("Authorization", auth);
            }
            httpClient = HttpClients.createDefault();
            HttpResponse result = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            /**请求发送成功，并得到响应**/
            if (result.getStatusLine().getStatusCode() == 200) {
                /**读取服务器返回过来的json字符串数据**/
                str = EntityUtils.toString(result.getEntity());
                if (noNeedResponse) {
                    return null;
                }
                /**把json字符串转换成json对象**/
                // jsonResult = JSONObject.parseArray(str);
            }
        } catch (Exception e) {
            logger.error("post请求提交失败:" + url, e);

        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return str;
    }

    /**
     * 发送get请求
     *
     * @param url 路径
     * @return
     */
    public static JSONArray httpGet(String url, String userName, String password) {
        //get请求返回结果
        JSONArray jsonResult = null;
        try {
            String strResult = httpGet(url, userName, password, null);
            jsonResult = JSONObject.parseArray(strResult);
        } catch (Exception e) {
            logger.error("get请求提交失败:" + url, e);
        }
        return jsonResult;
    }

    public static String httpGet(String url, String userName, String password, String ext) {
        //get请求返回结果
        CloseableHttpClient client = null;
        String strResult = "";
        String auth = basicAuth(userName, password);
        try {
            client = HttpClients.createDefault();
            //发送get请求
            HttpGet request = new HttpGet(url);
            request.setHeader("Authorization", auth);
            HttpResponse response = client.execute(request);

            /**请求发送成功，并得到响应**/
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                /**读取服务器返回过来的json字符串数据**/
                strResult = EntityUtils.toString(response.getEntity());

                //jsonResult = JSONObject.parseArray(strResult);
                url = URLDecoder.decode(url, "UTF-8");
            } else {
                logger.error("get请求提交失败:" + url);
            }
        } catch (Exception e) {
            logger.error("get请求提交失败:" + url, e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return strResult;
    }

    /**
     * 发送put请求
     *
     * @param url       路径
     * @param jsonParam 参数
     * @return
     */
    public static JSONArray httpPut(String url, String userName, String password, String jsonParam, String ext) {
        //put请求返回结果
        JSONArray jsonResult = null;
        try {
            String strResult = httpPut(url, userName, password, null);
            jsonResult = JSONObject.parseArray(strResult);
        } catch (Exception e) {
            logger.error("put请求提交失败:" + url, e);
        }

        return jsonResult;
    }

    /**
     * 发送put请求
     *
     * @param url       路径
     * @param jsonParam 参数
     * @return
     */
    public static String httpPut(String url, String userName, String password, String jsonParam) {
        //put请求返回结果
        JSONArray jsonResult = null;
        String strResult = null;
        CloseableHttpClient client = null;
        String auth = basicAuth(userName, password);
        try {
            HttpPut request = new HttpPut(url);
            if (null != jsonParam) {
                //解决中文乱码问题
                StringEntity entity = new StringEntity(jsonParam, "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                request.setEntity(entity);
                request.setHeader("Authorization", auth);
            }
            client = HttpClients.createDefault();
            //发送put请求
            HttpResponse response = client.execute(request);

            /**请求发送成功，并得到响应**/
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                /**读取服务器返回过来的json字符串数据**/
                strResult = EntityUtils.toString(response.getEntity());
                /**把json字符串转换成json对象**/
                url = URLDecoder.decode(url, "UTF-8");
            } else {
                logger.error("put请求提交失败:" + url);
            }
        } catch (Exception e) {
            logger.error("put请求提交失败:" + url, e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return strResult;
    }


    public static String basicAuth(String userName, String password) {
        String token = userName + ":" + password;
        String hash = Base64Utils.getBase64(token);
        return "Basic " + hash;
    }

}
