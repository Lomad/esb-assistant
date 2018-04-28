package com.winning.monitor.agent.collector.sender.io;

import com.winning.monitor.agent.collector.api.entity.CollectData;
import com.winning.monitor.agent.collector.sender.ICollectDataMessageTransport;
import com.winning.monitor.agent.logging.sender.netty.NettyChannelManager;
import com.winning.monitor.agent.sender.IDataEntityStorage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by nicholasyan on 16/9/8.
 */
public class NettyCollectDataMessageTransport implements ICollectDataMessageTransport {

    private static Logger logger = LoggerFactory.getLogger(NettyCollectDataMessageTransport.class);

    private final NettyChannelManager nettyChannelManager;
    private final CollectDatasMessageCodec collectDataMessageCodec;
    private AtomicInteger m_attempts = new AtomicInteger();


    public NettyCollectDataMessageTransport(String servers, IDataEntityStorage messageStorage) {
//        this.nettyChannelManager = NettyChannelManager.get(servers);
        this.nettyChannelManager = new NettyChannelManager(servers, messageStorage);
        this.collectDataMessageCodec = new CollectDatasMessageCodec();
    }

    @Override
    public void initialize() {
        logger.info("正在初始化 NettyCollectDataMessageTransport...");
        this.nettyChannelManager.start();
    }

    private boolean checkWritable(ChannelFuture future) {
        boolean isWriteable = false;
        Channel channel = future.channel();

        if (future != null && channel.isOpen()) {
            if (channel.isActive() && channel.isWritable()) {
                isWriteable = true;
            } else {
                int count = m_attempts.incrementAndGet();

                if (count % 1000 == 0 || count == 1) {
                    logger.error("Netty write buffer is full! Attempts: " + count);
                }
            }
        }

        return isWriteable;
    }

    @Override
    public void sendMessage(List<CollectData> collectData) {
        long current = System.currentTimeMillis();

        ChannelFuture future = nettyChannelManager.channel();

        if (future != null && checkWritable(future)) {

            ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(10 * 1024); // 10K

            this.collectDataMessageCodec.encode(collectData, buf);

            int size = buf.readableBytes();
            Channel channel = future.channel();
            channel.writeAndFlush(buf);
            current = System.currentTimeMillis() - current;

            logger.debug("发送消息长度:" + size + "bytes,用时" + current + "ms");
        }
    }

    @Override
    public void shutdown() {
        logger.info("正在关闭 NettyCollectDataMessageTransport");
        this.nettyChannelManager.shutdown();
    }
}
