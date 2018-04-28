package com.winning.monitor.agent.config.utils;

import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum NetworkInterfaceManager {
    INSTANCE;

    private InetAddress m_local;

    private NetworkInterfaceManager() {
        load();
    }

    public InetAddress findValidateIp(List<InetAddress> addresses) {
        InetAddress local = null;
        for (InetAddress address : addresses) {
            if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                if (local == null){
                    local = address;
                }else {
                    if (address.isSiteLocalAddress()){
                        if (local.isSiteLocalAddress()){
                            if (local.getHostName().equals(local.getHostAddress())
                                    && !address.getHostName().equals(address.getHostAddress())) {
                                local = address;
                            }
                        }else {
                            local = address;
                        }
                    }
                }
            }
        }
        return local;
    }

    public String getLocalHostAddress() {
        return m_local.getHostAddress();
    }

    public String getLocalHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return m_local.getHostName();
        }
    }

    private String getProperty(String name) {
        String value = null;

        value = System.getProperty(name);

        if (value == null) {
            value = System.getenv(name);
        }

        return value;
    }

    private void load() {
        String ip = getProperty("host.ip");

        if (ip != null) {
            try {
                m_local = InetAddress.getByName(ip);
                return;
            } catch (Exception e) {
                System.err.println(e);
                // ignore
            }
        }

        try {
            List<NetworkInterface> nis = Collections.list(NetworkInterface.getNetworkInterfaces());
            List<InetAddress> addresses = new ArrayList<InetAddress>();
            InetAddress local = null;

            try {
                for (NetworkInterface ni : nis) {
                    if (ni.isUp()) {
                        addresses.addAll(Collections.list(ni.getInetAddresses()));
                    }
                }
                local = findValidateIp(addresses);
            } catch (Exception e) {
                // ignore
            }
            m_local = local;
        } catch (SocketException e) {
            // ignore it
        }
    }
}
