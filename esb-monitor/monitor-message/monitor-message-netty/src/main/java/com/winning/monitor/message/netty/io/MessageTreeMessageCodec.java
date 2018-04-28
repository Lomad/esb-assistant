package com.winning.monitor.message.netty.io;

import com.winning.monitor.agent.logging.message.LogMessage;
import com.winning.monitor.agent.logging.message.MessageTree;
import com.winning.monitor.agent.logging.transaction.Transaction;
import io.netty.buffer.ByteBuf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nicholasyan on 16/9/8.
 */
public class MessageTreeMessageCodec {

    private static final byte TAB = '\t'; // tab character

    private static final byte LF = '\n'; // line feed character
    private static final String VERSION = "PT1"; // plain text version 1
    private DateHelper m_dateHelper = new DateHelper();
    private BufferHelper helper = new BufferHelper();

    public int encodeMessage(MessageTree tree, ByteBuf buf) {
        int count = 0;
        int index = buf.writerIndex();

        buf.writeInt(0); // place-holder
        count += encodeHeader(tree, buf);

        if (tree.getMessage() != null) {
            count += encodeMessage(tree.getMessage(), buf);
        }

        buf.setInt(index, count);

        return count;
    }

    protected int encodeHeader(MessageTree tree, ByteBuf buf) {
        int count = 0;

        count += helper.write(buf, VERSION);
        count += helper.write(buf, TAB);
        count += helper.write(buf, tree.getDomain());
        count += helper.write(buf, TAB);
        count += helper.write(buf, tree.getHostName());
        count += helper.write(buf, TAB);
        count += helper.write(buf, tree.getIpAddress());
        count += helper.write(buf, TAB);
        count += helper.write(buf, tree.getThreadGroupName());
        count += helper.write(buf, TAB);
        count += helper.write(buf, tree.getThreadId());
        count += helper.write(buf, TAB);
        count += helper.write(buf, tree.getThreadName());
        count += helper.write(buf, TAB);
        count += helper.write(buf, tree.getMessageId());
        count += helper.write(buf, TAB);
        count += helper.write(buf, tree.getParentMessageId());
        count += helper.write(buf, TAB);
        count += helper.write(buf, tree.getRootMessageId());
        count += helper.write(buf, TAB);
        count += helper.write(buf, tree.getSessionToken());
        count += helper.write(buf, LF);

        return count;
    }

    protected int encodeMessage(LogMessage message, ByteBuf buf) {
        if (message instanceof Transaction) {
            Transaction transaction = (Transaction) message;
            List<LogMessage> children = transaction.getChildren();

            if (children.isEmpty()) {
                return encodeLine(transaction, buf, 'A', Policy.WITH_DURATION);
            } else {
                int count = 0;
                int len = children.size();

                count += encodeLine(transaction, buf, 't', Policy.WITHOUT_STATUS);

                for (int i = 0; i < len; i++) {
                    LogMessage child = children.get(i);

                    if (child != null) {
                        count += encodeMessage(child, buf);
                    }
                }

                count += encodeLine(transaction, buf, 'T', Policy.WITH_DURATION);

                return count;
            }
//        } else if (message instanceof Event) {
//            return encodeLine(message, buf, 'E', Policy.DEFAULT);
//        } else if (message instanceof Trace) {
//            return encodeLine(message, buf, 'L', Policy.DEFAULT);
//        } else if (message instanceof Metric) {
//            return encodeLine(message, buf, 'M', Policy.DEFAULT);
//        } else if (message instanceof Heartbeat) {
//            return encodeLine(message, buf, 'H', Policy.DEFAULT);
        } else {
            throw new RuntimeException(String.format("Unsupported message type: %s.", message));
        }
    }

    protected int encodeLine(LogMessage message, ByteBuf buf, char type, Policy policy) {
        int count = 0;

        count += helper.write(buf, (byte) type);

        if (type == 'T' && message instanceof Transaction) {
            long duration = ((Transaction) message).getDurationInMillis();

            count += helper.write(buf, m_dateHelper.format(message.getTimestamp() + duration));
        } else {
            count += helper.write(buf, m_dateHelper.format(message.getTimestamp()));
        }

        count += helper.write(buf, TAB);
        count += helper.writeRaw(buf, message.getType());
        count += helper.write(buf, TAB);
        count += helper.writeRaw(buf, message.getName());
        count += helper.write(buf, TAB);

        if (policy != Policy.WITHOUT_STATUS) {
            count += helper.writeRaw(buf, message.getStatus());
            count += helper.write(buf, TAB);

            Object data = message.getData();

            if (policy == Policy.WITH_DURATION && message instanceof Transaction) {
                long duration = ((Transaction) message).getDurationInMicros();

                count += helper.write(buf, String.valueOf(duration));
                count += helper.write(buf, "us");
                count += helper.write(buf, TAB);
            }

            count += helper.writeRaw(buf, String.valueOf(data));
            count += helper.write(buf, TAB);
        }

        count += helper.write(buf, LF);

        return count;
    }

    protected static enum Policy {
        DEFAULT,

        WITHOUT_STATUS,

        WITH_DURATION;

        public static Policy getByMessageIdentifier(byte identifier) {
            switch (identifier) {
                case 't':
                    return WITHOUT_STATUS;
                case 'T':
                case 'A':
                    return WITH_DURATION;
                case 'E':
                case 'H':
                    return DEFAULT;
                default:
                    return DEFAULT;
            }
        }
    }

    protected static class DateHelper {
        private BlockingQueue<SimpleDateFormat> m_formats = new ArrayBlockingQueue<SimpleDateFormat>(20);

        private Map<String, Long> m_map = new ConcurrentHashMap<String, Long>();

        public String format(long timestamp) {
            SimpleDateFormat format = m_formats.poll();

            if (format == null) {
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            }

            try {
                return format.format(new Date(timestamp));
            } finally {
                if (m_formats.remainingCapacity() > 0) {
                    m_formats.offer(format);
                }
            }
        }

        public long parse(String str) {
            int len = str.length();
            String date = str.substring(0, 10);
            Long baseline = m_map.get(date);

            if (baseline == null) {
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                    format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                    baseline = format.parse(date).getTime();
                    m_map.put(date, baseline);
                } catch (ParseException e) {
                    return -1;
                }
            }

            long time = baseline.longValue();
            long metric = 1;
            boolean millisecond = true;

            for (int i = len - 1; i > 10; i--) {
                char ch = str.charAt(i);

                if (ch >= '0' && ch <= '9') {
                    time += (ch - '0') * metric;
                    metric *= 10;
                } else if (millisecond) {
                    millisecond = false;
                } else {
                    metric = metric / 100 * 60;
                }
            }
            return time;
        }
    }


}
