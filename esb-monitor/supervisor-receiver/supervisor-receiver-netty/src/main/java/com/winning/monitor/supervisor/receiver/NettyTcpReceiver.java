package com.winning.monitor.supervisor.receiver;


import com.winning.monitor.supervisor.core.message.handle.MessageHandlerManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nicholasyan on 16/9/6.
 */
public class NettyTcpReceiver implements IMessageReceiver {

    private static Logger logger = LoggerFactory.getLogger(NettyTcpReceiver.class);

    private final int m_port;
    private MessageHandlerManager messageHandlerManager;
    private ChannelFuture m_future;
    private EventLoopGroup m_bossGroup;
    private EventLoopGroup m_workerGroup;

    public NettyTcpReceiver(MessageHandlerManager messageHandlerManager) {
        this(18880, messageHandlerManager);
    }

    public NettyTcpReceiver(int port, MessageHandlerManager messageHandlerManager) {
        this.m_port = port;
        this.messageHandlerManager = messageHandlerManager;
    }

    @Override
    public void start() {
        logger.info("正在启动Netty监听器,端口号{}", m_port);

        try {
            startServer(m_port);
            logger.info("Netty监听器启动完毕,端口号{}", m_port);
        } catch (Exception e) {
            logger.error("启动Netty监听器失败,{}", e.getMessage(), e);
        }
    }

    public synchronized void startServer(int port) throws InterruptedException {
        boolean linux = getOSMatches("Linux") || getOSMatches("LINUX");
        int threads = 24;
        ServerBootstrap bootstrap = new ServerBootstrap();

        m_bossGroup = linux ? new EpollEventLoopGroup(threads) : new NioEventLoopGroup(threads);
        m_workerGroup = linux ? new EpollEventLoopGroup(threads) : new NioEventLoopGroup(threads);
        bootstrap.group(m_bossGroup, m_workerGroup);
        bootstrap.channel(linux ? EpollServerSocketChannel.class : NioServerSocketChannel.class);

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("decode", new MessageDecoder(messageHandlerManager));
            }
        });

        bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        try {
            m_future = bootstrap.bind(port).sync();
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void shutdown() {
        this.destory();
    }

    public void destory() {
        logger.info("正在关闭Netty监听器,端口号{}", m_port);
        try {
            m_future.channel().closeFuture();
            m_bossGroup.shutdownGracefully();
            m_workerGroup.shutdownGracefully();
            logger.info("Netty监听器关闭完毕,端口号{}", m_port);
        } catch (Exception e) {
            logger.error("关闭Netty监听器失败", e);
        }
    }

    protected boolean getOSMatches(String osNamePrefix) {
        String os = System.getProperty("os.name");

        if (os == null) {
            return false;
        }
        return os.startsWith(osNamePrefix);
    }
}
