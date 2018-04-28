package com.winning.monitor.agent.logging.sender.netty;

import com.winning.monitor.agent.logging.MonitorLogger;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.zip.*;

/**
 * Created by nicholasyan on 16/9/6.
 */
public class BufferHelper {

    private EscapingBufferWriter bufferWriter = new EscapingBufferWriter();

    //xuehao 2017-05-19：压缩后的前缀和后缀标志
    private final byte[] CompressMarker = { '\t', '\r', '\n', '\\' };
    //xuehao 2017-05-19：压缩类型（1-deflater，2-gzip）
    private final byte CompressTypeNot = 48;       //对应数字“0”
    private final byte CompressTypeDeflater = 49; //对应数字“1”
    private final byte CompressTypeGzip = 50;      //对应数字“2”
    //xuehao 2017-05-19：压缩的最小标准数
    private final int CompressMinNumber = 1000;

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

    //xuehao 2017-05-18：停用，改为使用带压缩的方法
    @Deprecated
    public int writeRaw_OLD(ByteBuf buf, String str) {
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

    /**
     * xuehao 2017-05-18：使用压缩方式发送数据
     */
    public int writeRaw(ByteBuf buf, String str) {
        if (str == null) {
            str = "null";
        }

        byte[] data;
        try {
            byte[] dataRaw = str.getBytes("utf-8");
            if(dataRaw.length>CompressMinNumber && MonitorLogger.compressType != CompressTypeNot) {
                if(CompressTypeGzip == MonitorLogger.compressType) {
                    data = gzipCompress(dataRaw);
                } else {
                    data = deflaterCompress(dataRaw);
                }
                data = addCompressMark(data);
            } else {
                data = dataRaw;
            }
        } catch (Exception e) {
            data = str.getBytes();
        }

        return bufferWriter.writeTo(buf, data);
    }

    //xuehao 2017-05-18：停用，改为使用带解压的方法
    @Deprecated
    public String read_OLD(MessageByteContext ctx, byte separator) {
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

    /**
     * xuehao 2017-05-18：使用解压方式获取数据
     */
    public String read(MessageByteContext ctx, byte separator) {
        ByteBuf buf = ctx.getBuffer();
        int from = buf.readerIndex();
        int to = buf.writerIndex();
        byte[] data = new byte[to - from];
        int index = 0;
        boolean flag = false;

        for (int i = from; i < to; i++) {
            byte b = buf.readByte();
            if (b == separator) {
                break;
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

                data[index] = (byte) c;
            } else {
                data[index] = b;
            }

            index++;
        }

        try {
            byte[] ba;
            data = Arrays.copyOfRange(data, 0, index);
            ba = removeCompressMark(data);
            if(ba != null && ba.length != index) {
                //获取压缩类型，并解压
                if(data[4]==CompressTypeGzip) {
                    ba = gzipUncompress(ba);
                } else {
                    ba = inflaterUncompress(ba);
                }
                return new String(ba, 0, ba.length, "utf-8");
            } else {
                return new String(data, 0, index, "utf-8");
            }
        } catch (Exception e) {
            return new String(data, 0, index);
        }
    }

    /**
     * xuehao 2017-05-19：设置压缩的前后缀信息
     */
    private byte[] addCompressMark(byte[] srcBytes) {
        int lenSrc = srcBytes.length;
        int len = lenSrc + 9;
        byte[] data = new byte[len];
        //将原始信息添加到中间部分
        System.arraycopy(srcBytes, 0, data, 5, lenSrc);
        //前4位后后4位放置压缩标志
        System.arraycopy(CompressMarker, 0, data, 0, 4);
        System.arraycopy(CompressMarker, 0, data, len - 4, 4);
        //第5位放置压缩类型
        data[4] = MonitorLogger.compressType;
        return data;
    }

    /**
     * xuehao 2017-05-19：删除压缩的前后缀信息
     */
    private byte[] removeCompressMark(byte[] srcBytes) {
        byte[] data = null;
        int lenSrc = srcBytes.length;
        if(srcBytes.length>9 && Arrays.equals(CompressMarker, Arrays.copyOfRange(srcBytes, 0, 4))
                && Arrays.equals(CompressMarker, Arrays.copyOfRange(srcBytes, lenSrc - 4, lenSrc))) {
            data = Arrays.copyOfRange(srcBytes, 5, lenSrc - 4);
        }
        return data;
    }

    /**
     * xuehao 2017-05-17：Gzip压缩
     */
    private byte[] gzipCompress(byte[] srcBytes) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(srcBytes);
            gzip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    /**
     * xuehao 2017-05-17：Gzip解压
     */
    private byte[] gzipUncompress(byte[] bytes) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[2048];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    /**
     * xuehao 2017-05-17：Deflater压缩
     */
    public byte[] deflaterCompress(byte[] input) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Deflater compressor = new Deflater(1);
        try {
            compressor.setInput(input);
            compressor.finish();
            final byte[] buf = new byte[2048];
            while (!compressor.finished()) {
                int count = compressor.deflate(buf);
                bos.write(buf, 0, count);
            }
        } finally {
            compressor.end();
        }
        return bos.toByteArray();
    }

    /**
     * xuehao 2017-05-17：Inflater压缩
     */
    public byte[] inflaterUncompress(byte[] input) throws DataFormatException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Inflater decompressor = new Inflater();
        try {
            decompressor.setInput(input);
            final byte[] buf = new byte[2048];
            while (!decompressor.finished()) {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            }
        } finally {
            decompressor.end();
        }
        return bos.toByteArray();
    }

}