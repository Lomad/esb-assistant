package com.winning.monitor.agent.logging.sender.netty;

import io.netty.buffer.ByteBuf;

/**
 * Created by nicholasyan on 16/9/7.
 */
public class MessageByteContext {
    private ByteBuf m_buffer;

    private char[] m_data;

    public MessageByteContext() {
        m_data = new char[4 * 1024 * 1024];
    }

    public ByteBuf getBuffer() {
        return m_buffer;
    }

    public MessageByteContext setBuffer(ByteBuf buffer) {
        m_buffer = buffer;
        return this;
    }

    public char[] getData() {
        return m_data;
    }
}
