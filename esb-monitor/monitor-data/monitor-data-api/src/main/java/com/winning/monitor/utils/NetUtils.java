package com.winning.monitor.utils;

import org.springframework.util.StringUtils;

import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xuehao on 2017/8/1.
 */
public class NetUtils {
    private static final String ProtocolHttp = "http://";
    private static final String ProtocolHttps = "https://";

    /**
     * 匹配格式为：127.0.0.1:8080
     */
    public static boolean isIpPort(String addr) {
        if (addr == null || StringUtils.isEmpty(addr) || addr.length() < 9 || addr.length() > 21) {
            return false;
        }

        String[] ipAddress = addr.split(":");
        if (ipAddress.length == 2 && isIP(ipAddress[0]) && isPort(ipAddress[1])) {
            return true;
        }

        return false;
    }

    /**
     * 判断IP地址
     */
    public static boolean isIP(String ip) {
        if (ip == null || StringUtils.isEmpty(ip) || ip.length() < 7 || ip.length() > 15 || "".equals(ip)) {
            return false;
        }

        //判断IP格式和范围
        String rexp = "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(ip);
        return mat.find();
    }

    /**
     * 判断端口号
     */
    public static boolean isPort(String port) {
        if (port == null || StringUtils.isEmpty(port) || port.length() > 5) {
            return false;
        }

        //判断端口号
        String rexp = "([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-5]{2}[0-3][0-5])";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(port);
        return mat.find();
    }
    /**
     * 判断端口号
     */
    public static boolean isPort(int port) {
        return (port > 0 && port < 65535) ? true : false;
    }

    /**
     * 判断端口号
     */
    public static boolean isHttp(String strUrl) {
        if (strUrl.startsWith(ProtocolHttp) || strUrl.startsWith(ProtocolHttps)) {
            return true;
        }
        return false;
    }

    /**
     * 判断HTTP地址是否可用
     */
    public static boolean checkHttp(String strUrl) {
        boolean result = false;
        try {
            if (!isHttp(strUrl)) {
                return result;
            }

            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            if (connection.getResponseCode() == 200) {
                result = true;
            }
        } catch (Exception e) {

        }
        return result;
    }

    /**
     * 判断Socket地址是否可用(地址格式：127.0.0.1:8080)
     */
    public static boolean checkSocket(String addr) {
        boolean result = false;
        try {
            if (!isIpPort(addr)) {
                return result;
            }

            String[] ipAddress = addr.split(":");
            if (ipAddress.length == 2 && isIP(ipAddress[0]) && isPort(ipAddress[1])) {
                Socket socket = new Socket(ipAddress[0], Integer.parseInt(ipAddress[1]));
                result = true;
            }
        } catch (Exception e) {

        }
        return result;
    }
}