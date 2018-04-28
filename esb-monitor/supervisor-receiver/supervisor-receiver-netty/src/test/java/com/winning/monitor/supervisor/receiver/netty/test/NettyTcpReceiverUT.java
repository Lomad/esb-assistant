package com.winning.monitor.supervisor.receiver.netty.test;

import com.winning.monitor.supervisor.receiver.NettyTcpReceiver;
import org.junit.Test;

/**
 * Created by nicholasyan on 16/9/7.
 */
public class NettyTcpReceiverUT {

    @Test
    public void testReceiverRun() throws InterruptedException {
        NettyTcpReceiver receiver = new NettyTcpReceiver(null);
        receiver.startServer(18880);
        Thread.sleep(Integer.MAX_VALUE);
    }

}
