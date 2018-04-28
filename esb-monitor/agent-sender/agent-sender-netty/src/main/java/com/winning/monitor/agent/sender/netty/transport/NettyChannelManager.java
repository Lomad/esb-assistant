package com.winning.monitor.agent.sender.netty.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by nicholasyan on 16/9/6.
 */
public class NettyChannelManager implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(NettyChannelManager.class);

    private Bootstrap m_bootstrap;

    private ChannelFuture future;

    private ChannelHolder m_activeChannelHolder;

    private int m_retriedTimes = 0;


    private boolean m_active = true;

    public NettyChannelManager(String address) {

        EventLoopGroup group = new NioEventLoopGroup(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        });
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
            }
        });

        m_bootstrap = bootstrap;
        List<InetSocketAddress> configedAddresses = this.parseSocketAddress(address);
        ChannelHolder holder = initChannel(configedAddresses);

        if (holder != null) {
            m_activeChannelHolder = holder;
        } else {
            m_activeChannelHolder = new ChannelHolder();
            m_activeChannelHolder.setServerAddresses(configedAddresses);
        }
    }


    private ChannelHolder initChannel(List<InetSocketAddress> addresses) {
        try {
            int len = addresses.size();

            for (int i = 0; i < len; i++) {
                InetSocketAddress address = addresses.get(i);
                String hostAddress = address.getAddress().getHostAddress();
                ChannelHolder holder = null;

                if (m_activeChannelHolder != null && hostAddress.equals(m_activeChannelHolder.getIp())) {
                    holder = new ChannelHolder();
                    holder.setActiveFuture(m_activeChannelHolder.getActiveFuture()).setConnectChanged(false);
                } else {
                    ChannelFuture future = createChannel(address);

                    if (future != null) {
                        holder = new ChannelHolder();
                        holder.setActiveFuture(future).setConnectChanged(true);
                    }
                }
                if (holder != null) {
                    holder.setActiveIndex(i).setIp(hostAddress);
//                    holder.setActiveServerConfig(serverConfig).setServerAddresses(addresses);

                    logger.info("success when init Monitor server, new active holder" + holder.toString());
                    return holder;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        try {
            StringBuilder sb = new StringBuilder();

            for (InetSocketAddress address : addresses) {
                sb.append(address.toString()).append(";");
            }
            logger.info("Error when init CAT server " + sb.toString());
        } catch (Exception e) {
            // ignore
        }
        return null;

    }


    private List<InetSocketAddress> parseSocketAddress(String content) {
        try {
            String[] strs = content.split(";");
            List<InetSocketAddress> address = new ArrayList<InetSocketAddress>();

            for (String str : strs) {
                String[] items = str.split(":");

                address.add(new InetSocketAddress(items[0], Integer.parseInt(items[1])));
            }
            return address;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new ArrayList<InetSocketAddress>();
    }

    public ChannelFuture getChannelFuture() {
        return this.future;
    }


    private ChannelFuture createChannel(InetSocketAddress address) {
        ChannelFuture future = null;

        try {
            future = m_bootstrap.connect(address);
            future.awaitUninterruptibly(100, TimeUnit.MILLISECONDS); // 100 ms

            if (!future.isSuccess()) {
                logger.error("Error when try connecting to " + address);
                closeChannel(future);
            } else {
                logger.info("Connected to Monitor server at " + address);
                return future;
            }
        } catch (Throwable e) {
            logger.error("Error when connect server " + address.getAddress(), e);

            if (future != null) {
                closeChannel(future);
            }
        }
        return null;
    }

    private void closeChannel(ChannelFuture channel) {
        try {
            if (channel != null) {
                logger.info("close channel " + channel.channel().remoteAddress());
                channel.channel().close();
            }
        } catch (Exception e) {
        }
    }

    /**
     * 检查连接是否有效,如果无效则关闭连接
     *
     * @param activeFuture
     */
    private void doubleCheckActiveServer(ChannelFuture activeFuture) {
        try {
            if (isChannelStalled(activeFuture) || isChannelDisabled(activeFuture)) {
                closeChannelHolder(m_activeChannelHolder);
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void closeChannelHolder(ChannelHolder channelHolder) {
        try {
            ChannelFuture channel = channelHolder.getActiveFuture();

            closeChannel(channel);
            channelHolder.setActiveIndex(-1);
        } catch (Exception e) {
            // ignore
        }
    }

    private boolean isChannelDisabled(ChannelFuture activeFuture) {
        return activeFuture != null && !activeFuture.channel().isOpen();
    }

    private boolean isChannelStalled(ChannelFuture activeFuture) {
        m_retriedTimes++;

        int size = 1000;
        boolean stalled = activeFuture != null && size >= 1000;

        if (stalled) {
            if (m_retriedTimes >= 5) {
                m_retriedTimes = 0;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    @Override
    public void run() {
        while (m_active) {
            // make save message id index asyc
//            m_idfactory.saveMark();
//            checkServerChanged(); 检查路由是否发生变化

            //获取活动的连接
            ChannelFuture activeFuture = m_activeChannelHolder.getActiveFuture();
            //获取所有的服务器地址
            List<InetSocketAddress> serverAddresses = m_activeChannelHolder.getServerAddresses();

            doubleCheckActiveServer(activeFuture);
            //reconnectDefaultServer(activeFuture, serverAddresses);

            try {
                Thread.sleep(10 * 1000L); // check every 10 seconds
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }
}
