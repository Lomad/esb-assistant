package com.winning.monitor.supervisor.receiver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.winning.monitor.agent.collector.api.entity.CollectDatas;
import com.winning.monitor.agent.collector.sender.io.CollectDatasMessageCodec;
import com.winning.monitor.agent.logging.message.MessageTree;
import com.winning.monitor.agent.logging.sender.netty.BufferHelper;
import com.winning.monitor.agent.logging.sender.netty.MessageByteContext;
import com.winning.monitor.agent.logging.sender.netty.MessageTreeMessageCodec;
import com.winning.monitor.message.MessageHead;
import com.winning.monitor.supervisor.core.message.handle.MessageHandlerManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import java.text.SimpleDateFormat;
//import java.util.Date;
import java.util.List;

/**
 * Created by nicholasyan on 16/9/7.
 */
public class MessageDecoder extends ByteToMessageDecoder {

    private static final byte TAB = '\t'; // tab character
    private static final byte LF = '\n'; // line feed character
    private static Logger logger = LoggerFactory.getLogger(MessageDecoder.class);
    private final MessageTreeMessageCodec messageTreeMessageCodec = new MessageTreeMessageCodec();
    private final CollectDatasMessageCodec collectorDataMessageCodec = new CollectDatasMessageCodec();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MessageHandlerManager messageHandlerManager;
    private BufferHelper helper = new BufferHelper();

    public MessageDecoder(MessageHandlerManager messageHandlerManager) {
        this.messageHandlerManager = messageHandlerManager;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        if (buffer.readableBytes() < 4) {
            return;
        }
        buffer.markReaderIndex();
        int length = buffer.readInt();
        buffer.resetReaderIndex();
        if (buffer.readableBytes() < length + 4) {
            return;
        }
        ByteBuf readBytes = null;
        try {
            if (length > 0) {
                readBytes = buffer.readBytes(length + 4);
                readBytes.markReaderIndex();
                readBytes.readInt();

                MessageByteContext byteContext = new MessageByteContext();
                byteContext.setBuffer(readBytes);
                String jsonHeader = helper.read(byteContext, LF);
                MessageHead head = this.objectMapper.readValue(jsonHeader, MessageHead.class);

                if (MessageTree.MESSAGE_TYPE.equals(head.getMessageType())) {
                    MessageTree tree = this.messageTreeMessageCodec.decode(byteContext);
                    this.messageHandlerManager.handle(tree);
                } else if (CollectDatas.MESSAGE_TYPE.equals(head.getMessageType())) {
                    CollectDatas collectDatas = this.collectorDataMessageCodec.decode(byteContext);
                    this.messageHandlerManager.handle(collectDatas);
                }
            } else {
                buffer.readBytes(length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //xuehao 2017-05-23：用完后，需要主动释放ByteBuf，以避免内存泄漏
            if (readBytes != null) {
                readBytes.release();
            }
        }
    }
}