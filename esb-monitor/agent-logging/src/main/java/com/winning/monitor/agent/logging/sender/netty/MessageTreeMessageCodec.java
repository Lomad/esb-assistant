package com.winning.monitor.agent.logging.sender.netty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.winning.monitor.agent.logging.message.Caller;
import com.winning.monitor.agent.logging.message.LogMessage;
import com.winning.monitor.agent.logging.message.MessageTree;
import com.winning.monitor.agent.logging.message.internal.DefaultMessageTree;
import com.winning.monitor.agent.logging.transaction.DefaultTransaction;
import com.winning.monitor.message.MessageHead;
import io.netty.buffer.ByteBuf;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nicholasyan on 16/9/8.
 */
public class MessageTreeMessageCodec {

    public static final String MESSAGE_TYPE = "MessageTree";
    private static final byte TAB = '\t'; // tab character
    private static final byte LF = '\n'; // line feed character
    private final ObjectMapper objectMapper;
    private BufferHelper helper = new BufferHelper();

    public MessageTreeMessageCodec() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }


    public int encode(MessageTree tree, ByteBuf buf) {
        try {
            int count = 0;
            int index = buf.writerIndex();
            buf.writeInt(0);

            MessageHead head = new MessageHead();
            head.setMessageType(MESSAGE_TYPE);
            head.setIpAddress(tree.getIpAddress());
            head.setHostName(tree.getHostName());

            String header = objectMapper.writeValueAsString(head);
            count += helper.writeRaw(buf, header);
            count += helper.write(buf, LF);

            String msg = objectMapper.writeValueAsString(tree);
            count += helper.writeRaw(buf, msg);
            count += helper.write(buf, LF);

            buf.setInt(index, count);
            return count;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public MessageTree decode(MessageByteContext ctx) {
        DefaultMessageTree messageTree = new DefaultMessageTree();
        this.decodeMessage(ctx, messageTree);
        return messageTree;
    }

    protected void decodeMessage(MessageByteContext ctx, DefaultMessageTree messageTree) {
        String json = helper.read(ctx, LF);
        try {
            Map<String, Object> map = objectMapper.readValue(json, Map.class);
            messageTree.setHostName(String.valueOf(map.get("hostName")));
            messageTree.setDomain(String.valueOf(map.get("domain")));
            messageTree.setGroup(String.valueOf(map.get("group")));
            messageTree.setIpAddress(String.valueOf(map.get("ipAddress")));
            messageTree.setMessageId(String.valueOf(map.get("messageId")));
            messageTree.setParentMessageId(String.valueOf(map.get("parentMessageId")));
            messageTree.setRootMessageId(String.valueOf(map.get("rootMessageId")));
            if (map.containsKey("caller")) {
                Map<String, Object> callerMap = (Map<String, Object>) map.get("caller");
                Caller caller = new Caller();
                caller.setIp((String.valueOf(callerMap.get("ip"))));
                caller.setName((String.valueOf(callerMap.get("name"))));
                caller.setType((String.valueOf(callerMap.get("type"))));
                messageTree.setCaller(caller);
            }

            Map<String, Object> message = (Map<String, Object>) map.get("message");
            LogMessage logMessage = this.createMessage(message);
            messageTree.setMessage(logMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LogMessage createMessage(Map<String, Object> message) throws InvocationTargetException,
            IllegalAccessException {
        String messageType = message.get("messageType").toString();
        String type = message.get("type").toString();
        String name = message.get("name").toString();

        //如果是Transaction
        if ("DefaultTransaction".equals(messageType)) {
            DefaultTransaction transaction = new DefaultTransaction(type, name, null);
            BeanUtils.populate(transaction, message);
            LinkedHashMap<String, Object> datas = (LinkedHashMap<String, Object>) message.get("data");
            if (datas != null) {
                transaction.putDatas(datas);
            }

            List<LinkedHashMap<String, Object>> children = (List<LinkedHashMap<String, Object>>) message.get("children");
            if (children != null) {
                for (LinkedHashMap<String, Object> child : children) {
                    transaction.addChild(this.createMessage(child));
                }
            }
            return transaction;
        }

        return null;
    }

}
