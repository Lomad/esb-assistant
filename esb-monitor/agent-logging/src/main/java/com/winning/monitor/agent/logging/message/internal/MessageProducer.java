package com.winning.monitor.agent.logging.message.internal;

import com.winning.monitor.agent.logging.entity.ConfigManager;
import com.winning.monitor.agent.logging.message.Caller;
import com.winning.monitor.agent.logging.message.MessageManager;
import com.winning.monitor.agent.logging.transaction.DefaultTransaction;
import com.winning.monitor.agent.logging.transaction.Transaction;

/**
 * Created by nicholasyan on 16/9/8.
 */
public class MessageProducer {
    private final ConfigManager configContainer;
    private final MessageManager m_manager;


    public MessageProducer(ConfigManager configContainer,
                           MessageManager messageManager) {
        this.configContainer = configContainer;
        this.m_manager = messageManager;
    }

    public void initialize() {

    }

    public Transaction newTransaction(String type, String name) {
        // this enable CAT client logging cat message without explicit setup
        if (!m_manager.hasContext()) {
            m_manager.setup();
        }

        if (m_manager.isMessageEnabled()) {
            DefaultTransaction transaction = new DefaultTransaction(type, name, m_manager);
            m_manager.start(transaction, false);
            return transaction;
        } else {
            return null;
        }
    }

    public void setCaller(String callerName, String callerIP, String callerType) {
        Caller caller = m_manager.getThreadLocalMessageTree().getCaller();
        if (caller == null) {
            caller = new Caller();
        }
        caller.setIp(callerIP);
        caller.setName(callerName);
        caller.setType(callerType);
        m_manager.getThreadLocalMessageTree().setCaller(caller);
    }
}
