package com.winning.esb;

import com.winning.esb.utils.JsonUtils;
import com.winning.esb.utils.NetUtils;
import org.junit.Test;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by xuehao on 2017/8/17.
 */
public class Test1 {
    public static void main(String[] args) {
        try{
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()){
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()){
                    InetAddress ip = (InetAddress) addresses.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                            && ip.getHostAddress().indexOf(":")==-1){
                        System.out.println("本机的IP = " + ip.getHostAddress());
                        //return ip.getHostAddress();
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        String ipLoc = NetUtils.getHostIP();
        System.out.println(ipLoc);
    }
    @Test
    public void testJson() throws Exception {
        List<Integer> sidList = new ArrayList<>();
        sidList.add(2);
        sidList.add(3);
        sidList.add(4);
        TestClass testClass = new TestClass();
        testClass.setType(1);
        testClass.setSidList(sidList);
        String json = JsonUtils.toJson(testClass);
        System.out.println(json);
    }

    class TestClass {
        private Integer type;
        private List<Integer> sidList;

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public List<Integer> getSidList() {
            return sidList;
        }

        public void setSidList(List<Integer> sidList) {
            this.sidList = sidList;
        }
    }
}