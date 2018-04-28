package com.winning.monitor.agent.logging.sender.netty;

import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Created by nicholasyan on 16/9/18.
 */
public class ChannelHolder {
    
    private ChannelFuture m_activeFuture;

    private int m_activeIndex = -1;

    private String m_activeServerConfig;

    private List<InetSocketAddress> m_serverAddresses;

    private String m_ip;

    private boolean m_connectChanged;

    public ChannelFuture getActiveFuture() {
        return m_activeFuture;
    }

    public ChannelHolder setActiveFuture(ChannelFuture activeFuture) {
        m_activeFuture = activeFuture;
        return this;
    }

    public int getActiveIndex() {
        return m_activeIndex;
    }

    public ChannelHolder setActiveIndex(int activeIndex) {
        m_activeIndex = activeIndex;
        return this;
    }

    public String getActiveServerConfig() {
        return m_activeServerConfig;
    }

    public ChannelHolder setActiveServerConfig(String activeServerConfig) {
        m_activeServerConfig = activeServerConfig;
        return this;
    }

    public String getIp() {
        return m_ip;
    }

    public ChannelHolder setIp(String ip) {
        m_ip = ip;
        return this;
    }

    public List<InetSocketAddress> getServerAddresses() {
        return m_serverAddresses;
    }

    public ChannelHolder setServerAddresses(List<InetSocketAddress> serverAddresses) {
        m_serverAddresses = serverAddresses;
        return this;
    }

    public boolean isConnectChanged() {
        return m_connectChanged;
    }

    public ChannelHolder setConnectChanged(boolean connectChanged) {
        m_connectChanged = connectChanged;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("active future :").append(m_activeFuture.channel().remoteAddress());
        sb.append(" index:").append(m_activeIndex);
        sb.append(" ip:").append(m_ip);
        sb.append(" server config:").append(m_activeServerConfig);
        return sb.toString();
    }
}
