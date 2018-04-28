package com.winning.monitor.agent.collector.sender.io;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.winning.monitor.agent.collector.api.entity.CollectData;
import com.winning.monitor.agent.collector.api.entity.CollectDatas;
import com.winning.monitor.agent.config.utils.NetworkInterfaceManager;
import com.winning.monitor.agent.logging.sender.netty.BufferHelper;
import com.winning.monitor.agent.logging.sender.netty.MessageByteContext;
import com.winning.monitor.message.MessageHead;
import io.netty.buffer.ByteBuf;

import java.util.List;

/**
 * Created by nicholasyan on 16/9/8.
 */
public class CollectDatasMessageCodec {

    private static final byte TAB = '\t'; // tab character
    private static final byte LF = '\n'; // line feed character
    private final ObjectMapper objectMapper;
    private BufferHelper helper = new BufferHelper();

    public CollectDatasMessageCodec() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }


    public int encode(List<CollectData> collectDataList, ByteBuf buf) {
        try {
            int count = 0;
            int index = buf.writerIndex();
            buf.writeInt(0);

            MessageHead head = new MessageHead();
            head.setMessageType(CollectDatas.MESSAGE_TYPE);
            head.setIpAddress(NetworkInterfaceManager.INSTANCE.getLocalHostAddress());
            head.setHostName(NetworkInterfaceManager.INSTANCE.getLocalHostName());

            String header = objectMapper.writeValueAsString(head);
            count += helper.writeRaw(buf, header);
            count += helper.write(buf, LF);

            CollectDatas collectDatas = new CollectDatas();
            collectDatas.setDatas(collectDataList);
            collectDatas.setIpAddress(NetworkInterfaceManager.INSTANCE.getLocalHostAddress());
            collectDatas.setHostName(NetworkInterfaceManager.INSTANCE.getLocalHostName());

            String msg = objectMapper.writeValueAsString(collectDatas);
            count += helper.writeRaw(buf, msg);
            count += helper.write(buf, LF);

            buf.setInt(index, count);
            return count;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public CollectDatas decode(MessageByteContext ctx) {
        String json = helper.read(ctx, LF);

        if (json.contains("CollectDatas")) {
            int length = json.length();
            int point = json.indexOf("messageType");
            json = json.substring(0, point - 2) + json.substring(length - 1);
        }
        try {
            CollectDatas collectDatas = objectMapper.readValue(json, CollectDatas.class);
            return collectDatas;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
