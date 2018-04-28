package com.winning.esb.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.w3c.dom.Document;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.net.*;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetUtils {
    private static final Logger logger = LoggerFactory.getLogger(NetUtils.class);
    private static final String ProtocolHttp = "http://";
    private static final String ProtocolHttps = "https://";
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");
    private static volatile InetAddress LOCAL_ADDRESS = null;

    public static final String LOCALHOST = "127.0.0.1";
    public static final String ANYHOST = "0.0.0.0";
    public static final int PORT_MAX = 65535;
    public static final int PORT_MIN = 1;

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
        return (port >= PORT_MIN && port <= PORT_MAX) ? true : false;
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
     * 判断Socket地址是否可用
     * @return  true - 可用（已开启），false - 不可用（未开启）
     */
    public static boolean checkSocket(String ip, int port) {
        return checkSocket(ip + ":" + port);
    }

    /**
     * 判断Socket地址是否可用(地址格式：127.0.0.1:8080)
     * @return  true - 可用（已开启），false - 不可用（未开启）
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

    /**
     * 获取主机IP
     */
    public static String getHostIP() {
        InetAddress address = getLocalAddress();
        return address == null ? null : address.getHostAddress();
    }

    /**
     * 获取空闲端口号
     */
    public static int getIdlePort() {
        //从2001端口开始检测可用的端口号，返回一个可用的端口号
        int serverPort = 2001;
        while (serverPort <= NetUtils.PORT_MAX) {
            if (NetUtils.checkSocket("127.0.0.1", serverPort)) {
                serverPort++;
                if (serverPort > NetUtils.PORT_MAX) {
                    serverPort = 0;
                    break;
                }
            } else {
                break;
            }
        }
        return serverPort;
    }

    /**
     * 获取tomcat端口号
     */
    public static Integer getTomcatPortFromConfigXml() {
        Integer port;
        try {
            File serverXml = new File(System.getProperty("catalina.home") + "\\conf\\server.xml");
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true); // never forget this!
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(serverXml);
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath
                    .compile("/Server/Service[@name='Catalina']/Connector[count(@scheme)=0]/@port[1]");
            String result = (String) expr.evaluate(doc, XPathConstants.STRING);
            port = result != null && result.length() > 0 ? Integer.valueOf(result) : null;
        } catch (Exception e) {
            port = null;
        }
        return port;
    }

    /**
     * 获取Tomcat的工程目录（即webapps中的目录）
     */
    public static String getTomcatWebappName() {
        ServletContext srvCtx = ((WebApplicationContext) AppCtxUtils.getAppCtx())
                .getServletContext();
        String path = srvCtx.getRealPath("/");
        String splitMark = "\\";
        if (path.endsWith(splitMark)) {
            path = path.substring(0, path.lastIndexOf(splitMark));
        }
        return path.substring(path.lastIndexOf(splitMark) + 1);
    }

    /**
     * 遍历本地网卡，返回第一个合理的IP。
     *
     * @return 本地网卡IP
     */
    public static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }
        InetAddress localAddress = getLocalAddress0();
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }

    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }

        String name = address.getHostAddress();
        if (name.startsWith("v")) {
            return false;
        }

        return (name != null && !ANYHOST.equals(name) && !LOCALHOST.equals(name) && IP_PATTERN.matcher(name).matches());
    }

    private static InetAddress getLocalAddress0() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable e) {
            logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        if (addresses != null) {
                            while (addresses.hasMoreElements()) {
                                try {
                                    InetAddress address = addresses.nextElement();
                                    if (isValidAddress(address)) {
                                        return address;
                                    }
                                } catch (Throwable e) {
                                    logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
                    }
                }
            }
        } catch (Throwable e) {
            logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
        }
        logger.error("Could not get local host ip address, will use 127.0.0.1 instead.");
        return localAddress;
    }

}