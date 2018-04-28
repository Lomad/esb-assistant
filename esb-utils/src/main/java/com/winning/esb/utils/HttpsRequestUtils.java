package com.winning.esb.utils;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpsRequestUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpRequestUtils.class);    //日志记录

    public static String httpsGet(String url,String userName, String password, String ext){
        //get请求返回结果
        String strResult = "";
        String auth = basicAuth(userName, password);
        try {
            TrustManager[] tm = {new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            URL newUrl = new URL(url);
            if("https".equalsIgnoreCase(newUrl.getProtocol())) {

                HttpsURLConnection httpUrlConn = new HttpsURLConnection(newUrl) {
                    @Override
                    public void connect() throws IOException {

                    }

                    @Override
                    public void disconnect() {

                    }

                    @Override
                    public boolean usingProxy() {
                        return false;
                    }

                    @Override
                    public String getCipherSuite() {
                        return null;
                    }

                    @Override
                    public Certificate[] getLocalCertificates() {
                        return new Certificate[0];
                    }

                    @Override
                    public Certificate[] getServerCertificates() throws SSLPeerUnverifiedException {
                        return new Certificate[0];
                    }
                };

                httpUrlConn.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        if("127.0.0.1".equals(s)) {
                            return true;
                        }
                        return true;
                    }
                });
                httpUrlConn.setRequestProperty("Authorization", auth);
                httpUrlConn.setRequestMethod("GET");
                httpUrlConn = (HttpsURLConnection) newUrl.openConnection();
                httpUrlConn.connect();

                // 将返回的输入流转换成字符串
                InputStream inputStream = httpUrlConn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String str = null;
                StringBuffer buffer = new StringBuffer();
                while ((str = bufferedReader.readLine()) != null) {
                    buffer.append(str);
                }
                bufferedReader.close();
                inputStreamReader.close();
                // 释放资源
                inputStream.close();
                httpUrlConn.disconnect();
                strResult = buffer.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strResult;
    }




    public static String basicAuth(String userName, String password){
        String token = userName + ":" + password;
        String hash = Base64Utils.getBase64(token);
        return "Basic " + hash;
    }
}
