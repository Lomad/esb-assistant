package com.winning.monitor.message.netty.io;

import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;

/**
 * Created by nicholasyan on 16/9/6.
 */
public class BufferHelper {

    private EscapingBufferWriter bufferWriter = new EscapingBufferWriter();

    public int write(ByteBuf buf, byte b) {
        buf.writeByte(b);
        return 1;
    }

    public int write(ByteBuf buf, String str) {
        if (str == null) {
            str = "null";
        }

        byte[] data = str.getBytes();

        buf.writeBytes(data);
        return data.length;
    }

    public int writeRaw(ByteBuf buf, String str) {
        if (str == null) {
            str = "null";
        }

        byte[] data;

        try {
            data = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            data = str.getBytes();
        }

        return bufferWriter.writeTo(buf, data);
    }

    public String read(MessageByteContext ctx, byte separator) {
        ByteBuf buf = ctx.getBuffer();
        char[] data = ctx.getData();
        int from = buf.readerIndex();
        int to = buf.writerIndex();
        int index = 0;
        boolean flag = false;

        for (int i = from; i < to; i++) {
            byte b = buf.readByte();

            if (b == separator) {
                break;
            }

            if (index >= data.length) {
                char[] data2 = new char[to - from];

                System.arraycopy(data, 0, data2, 0, index);
                data = data2;
            }

            char c = (char) (b & 0xFF);

            if (c > 127) {
                flag = true;
            }

            if (c == '\\' && i + 1 < to) {
                byte b2 = buf.readByte();

                if (b2 == 't') {
                    c = '\t';
                    i++;
                } else if (b2 == 'r') {
                    c = '\r';
                    i++;
                } else if (b2 == 'n') {
                    c = '\n';
                    i++;
                } else if (b2 == '\\') {
                    c = '\\';
                    i++;
                } else {
                    // move back
                    buf.readerIndex(i + 1);
                }
            }

            data[index] = c;
            index++;
        }

        if (!flag) {
            return new String(data, 0, index);
        } else {
            byte[] ba = new byte[index];

            for (int i = 0; i < index; i++) {
                ba[i] = (byte) (data[i] & 0xFF);
            }

            try {
                return new String(ba, 0, index, "utf-8");
            } catch (UnsupportedEncodingException e) {
                return new String(ba, 0, index);
            }
        }
    }


}
