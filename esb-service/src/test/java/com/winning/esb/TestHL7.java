package com.winning.esb;

import com.winning.esb.utils.NetUtils;
import com.winning.esb.utils.StringUtils;

import java.util.Properties;
import java.util.Set;

/**
 * Created by xuehao on 2017/9/4.
 */
public class TestHL7 {

    public static void main(String[] args) {
//      String msg = "\u000BM\u001C";
//      char[] chars = msg.toCharArray();
//      for (char a:chars)
//          if(a==(char)28)
//            System.out.print(a+"  ");
//      int len = msg.length();
//      if(msg.charAt(0)==(char)11 && msg.charAt(len-1)==(char)28)
//          System.out.println("hello");
//      String temp = msg.substring(1,msg.length()-1);
//
//      System.out.println(temp);
        String path = "";
        String hostIP = NetUtils.getHostIP();
        Properties properties = System.getProperties();
        Set<Object> keySet = properties.keySet();
        Integer port = NetUtils.getTomcatPortFromConfigXml();
        String webappName = NetUtils.getTomcatWebappName();
        if (!StringUtils.isEmpty(hostIP) && port != null && !StringUtils.isEmpty(webappName)) {
            path = hostIP + ":" + port + "/" + webappName;
        }
        System.out.println(path);
    }

}