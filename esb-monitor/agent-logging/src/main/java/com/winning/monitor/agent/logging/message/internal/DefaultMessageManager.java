package com.winning.monitor.agent.logging.message.internal;


import com.winning.monitor.agent.config.utils.NetworkInterfaceManager;
import com.winning.monitor.agent.logging.entity.ConfigManager;
import com.winning.monitor.agent.logging.entity.Domain;
import com.winning.monitor.agent.logging.message.LogMessage;
import com.winning.monitor.agent.logging.message.MessageManager;
import com.winning.monitor.agent.logging.message.MessageTree;
import com.winning.monitor.agent.logging.storage.MessageTreeStorage;
import com.winning.monitor.agent.logging.transaction.DefaultTransaction;
import com.winning.monitor.agent.logging.transaction.ForkedTransaction;
import com.winning.monitor.agent.logging.transaction.Transaction;

import java.util.*;

public class DefaultMessageManager implements MessageManager {

    private final ConfigManager configContainer;
    private final MessageTreeStorage logMessageStorage;

    private final Domain m_domain;
    // we don't use static modifier since MessageManager is configured as singleton
    private ThreadLocal<Context> m_context = new ThreadLocal<Context>();
    private long m_throttleTimes;

    private boolean m_firstMessage = true;

    private TransactionHelper m_validator = new TransactionHelper();


    public DefaultMessageManager(ConfigManager configContainer,
                                 MessageTreeStorage logMessageStorage) {
        this.configContainer = configContainer;
        this.logMessageStorage = logMessageStorage;
        this.m_domain = configContainer.getDomain();
    }


    @Override
    public void initialize() {
        if (m_domain.getIp() == null) {
            m_domain.setIp(NetworkInterfaceManager.INSTANCE.getLocalHostAddress());
        }
    }

    @Override
    public void add(LogMessage message) {
        Context ctx = getContext();

        if (ctx != null) {
            ctx.add(message);
        }
    }

    @Override
    public void bind(String tag, String title) {
//		TaggedTransaction t = m_taggedTransactions.get(tag);
//
//		if (t != null) {
//			MessageTree tree = getThreadLocalMessageTree();
//			String messageId = tree.getMessageId();
//
//			if (messageId == null) {
//				messageId = nextMessageId();
//				tree.setMessageId(messageId);
//			}
//			if (tree != null) {
//				t.start();
//				t.bind(tag, messageId, title);
//			}
//		}
    }


    @Override
    public void end(Transaction transaction) {
        Context ctx = getContext();

        if (ctx != null && transaction.isStandalone()) {
            if (ctx.end(this, transaction)) {
                m_context.remove();
            }
        }
    }

    private String nextMessageId() {
        return UUID.randomUUID().toString();
    }

    public void flush(MessageTree tree) {
        if (tree.getMessageId() == null) {
            tree.setMessageId(nextMessageId());
        }

        this.logMessageStorage.put(tree);
//        MessageSender sender = m_transportManager.getSender();

//		if (sender != null && isMessageEnabled()) {
//			sender.send(tree);
//
//			reset();
//		} else {
//			m_throttleTimes++;
//
//			if (m_throttleTimes % 10000 == 0 || m_throttleTimes == 1) {
//				m_logger.info("Cat Message is throttled! Times:" + m_throttleTimes);
//			}
//		}
    }

//    public ClientConfigManager getConfigManager() {
//        return m_configManager;
//	}

    private Context getContext() {
//		if (Cat.isInitialized()) {
        Context ctx = m_context.get();

        if (ctx != null) {
            return ctx;
        } else {
            if (m_domain != null) {
                ctx = new Context(m_domain.getGroup(), m_domain.getId(), m_domain.getHostName(), m_domain.getIp());
            } else {
                ctx = new Context("Unknown", "Unknown", m_domain.getHostName(), "");
            }

            m_context.set(ctx);
            return ctx;
        }
//		}

//        return null;
    }

    @Override
    public String getDomain() {
        return m_domain.getId();
    }

    public String getMetricType() {
        return "";
    }

    public void setMetricType(String metricType) {
    }

    @Override
    public Transaction getPeekTransaction() {
        Context ctx = getContext();

        if (ctx != null) {
            return ctx.peekTransaction(this);
        } else {
            return null;
        }
    }

    @Override
    public MessageTree getThreadLocalMessageTree() {
        Context ctx = m_context.get();

        if (ctx == null) {
            setup();
        }
        ctx = m_context.get();

        return ctx.m_tree;
    }

    @Override
    public boolean hasContext() {
        return m_context.get() != null;
    }

    /**
     * Check if current context logging is enabled or disabled.
     *
     * @return true if current context is enabled
     */
    @Override
    public boolean isMessageEnabled() {
        return true;
    }

    /**
     * Check if CAT logging is enabled or disabled.
     *
     * @return true if CAT is enabled
     */
    @Override
    public boolean isCatEnabled() {
        return true;
    }


    @Override
    public boolean isTraceMode() {
        Context content = getContext();

        if (content != null) {
            return content.isTraceMode();
        } else {
            return false;
        }
    }

    @Override
    public void setTraceMode(boolean traceMode) {
        Context context = getContext();

        if (context != null) {
            context.setTraceMode(traceMode);
        }
    }


    @Override
    public void reset() {
        // destroy current thread local data
        Context ctx = m_context.get();

        if (ctx != null) {
            if (ctx.m_totalDurationInMicros == 0) {
                ctx.m_stack.clear();
                ctx.m_knownExceptions.clear();
                m_context.remove();
            } else {
                ctx.m_knownExceptions.clear();
            }
        }
    }

    @Override
    public void setup() {
        Context ctx;

        if (m_domain != null) {
            ctx = new Context(m_domain.getGroup(), m_domain.getId(), m_domain.getHostName(), m_domain.getIp());
        } else {
            ctx = new Context("Unknown", "Unknown", m_domain.getHostName(), "");
        }

        m_context.set(ctx);
    }

    boolean shouldLog(Throwable e) {
        Context ctx = m_context.get();

        if (ctx != null) {
            return ctx.shouldLog(e);
        } else {
            return true;
        }
    }

    @Override
    public void start(Transaction transaction, boolean forked) {
        Context ctx = getContext();

        if (ctx != null) {
            ctx.start(transaction, forked);

//            if (transaction instanceof TaggedTransaction) {
//                TaggedTransaction tt = (TaggedTransaction) transaction;
//
//                m_taggedTransactions.put(tt.getTag(), tt);
//            }
        } else if (m_firstMessage) {
            m_firstMessage = false;
//            m_logger.warn("CAT client is not enabled because it's not initialized yet");
        }
    }

    class Context {
        private MessageTree m_tree;

        private Stack<Transaction> m_stack;

        private int m_length;

        private boolean m_traceMode;

        private long m_totalDurationInMicros; // for truncate message

        private Set<Throwable> m_knownExceptions;

        public Context(String group, String domain, String hostName, String ipAddress) {
            m_tree = new DefaultMessageTree();
            m_stack = new Stack<Transaction>();

            Thread thread = Thread.currentThread();
            String groupName = thread.getThreadGroup().getName();

            m_tree.setThreadGroupName(groupName);
            m_tree.setThreadId(String.valueOf(thread.getId()));
            m_tree.setThreadName(thread.getName());

            m_tree.setGroup(group);
            m_tree.setDomain(domain);
            m_tree.setHostName(hostName);
            m_tree.setIpAddress(ipAddress);
            m_length = 1;
            m_knownExceptions = new HashSet<Throwable>();
        }

        public void add(LogMessage message) {
            if (m_stack.isEmpty()) {
                MessageTree tree = m_tree.copy();

                tree.setMessage(message);
                flush(tree);
            } else {
                Transaction parent = m_stack.peek();

                addTransactionChild(message, parent);
            }
        }

        private void addTransactionChild(LogMessage message, Transaction transaction) {
            long treePeriod = trimToHour(m_tree.getMessage().getTimestamp());
            long messagePeriod = trimToHour(message.getTimestamp() - 10 * 1000L); // 10 seconds extra time allowed

//            if (treePeriod < messagePeriod || m_length >= m_configManager.getMaxMessageLength()) {
//                m_validator.truncateAndFlush(this, message.getTimestamp());
//            }

            transaction.addChild(message);
            m_length++;
        }

        private void adjustForTruncatedTransaction(Transaction root) {
//            DefaultEvent next = new DefaultEvent("TruncatedTransaction", "TotalDuration");
//            long actualDurationInMicros = m_totalDurationInMicros + root.getDurationInMicros();
//
//            next.addData(String.valueOf(actualDurationInMicros));
//            next.setStatus(Message.SUCCESS);
//            root.addChild(next);

            m_totalDurationInMicros = 0;
        }

        /**
         * return true means the transaction has been flushed.
         *
         * @param manager
         * @param transaction
         * @return true if message is flushed, false otherwise
         */
        public boolean end(DefaultMessageManager manager, Transaction transaction) {
            if (!m_stack.isEmpty()) {
                Transaction current = m_stack.pop();

                if (transaction == current) {
                    m_validator.validate(m_stack.isEmpty() ? null : m_stack.peek(), current);
                } else {
                    while (transaction != current && !m_stack.empty()) {
                        m_validator.validate(m_stack.peek(), current);

                        current = m_stack.pop();
                    }
                }

                if (m_stack.isEmpty()) {
                    MessageTree tree = m_tree.copy();

                    m_tree.setMessageId(null);
                    m_tree.setMessage(null);

                    if (m_totalDurationInMicros > 0) {
                        adjustForTruncatedTransaction((Transaction) tree.getMessage());
                    }

                    manager.flush(tree);
                    return true;
                }
            }

            return false;
        }

        public boolean isTraceMode() {
            return m_traceMode;
        }

        public void setTraceMode(boolean traceMode) {
            m_traceMode = traceMode;
        }


        public Transaction peekTransaction(DefaultMessageManager defaultMessageManager) {
            if (m_stack.isEmpty()) {
                return null;
            } else {
                return m_stack.peek();
            }
        }

        public boolean shouldLog(Throwable e) {
            if (m_knownExceptions == null) {
                m_knownExceptions = new HashSet<Throwable>();
            }

            if (m_knownExceptions.contains(e)) {
                return false;
            } else {
                m_knownExceptions.add(e);
                return true;
            }
        }

        public void start(Transaction transaction, boolean forked) {
            if (!m_stack.isEmpty()) {
                // Do NOT make strong reference from parent transaction to forked transaction.
                // Instead, we create a "soft" reference to forked transaction later, via linkAsRunAway()
                // By doing so, there is no need for synchronization between parent and child threads.
                // Both threads can complete() anytime despite the excute thread.
                if (!(transaction instanceof ForkedTransaction)) {
                    Transaction parent = m_stack.peek();
                    addTransactionChild(transaction, parent);
                }
            } else {
                m_tree.setMessage(transaction);
            }

            if (!forked) {
                m_stack.push(transaction);
            }
        }

        private long trimToHour(long timestamp) {
            return timestamp - timestamp % (3600 * 1000L);
        }
    }

    class TransactionHelper {

        public void validate(Transaction parent, Transaction transaction) {
            if (transaction.isStandalone()) {
                List<LogMessage> children = transaction.getChildren();
                int len = children.size();

                for (int i = 0; i < len; i++) {
                    LogMessage message = children.get(i);

                    if (message instanceof Transaction) {
                        validate(transaction, (Transaction) message);
                    }
                }

                if (!transaction.isCompleted() && transaction instanceof DefaultTransaction) {
                    // missing transaction end, log a BadInstrument event so that
                    // developer can fix the code
                    //markAsNotCompleted((DefaultTransaction) transaction);
                }
            } else if (!transaction.isCompleted()) {
//                if (transaction instanceof DefaultForkedTransaction) {
//                    // link it as run away message since the forked transaction is not completed yet
//                    linkAsRunAway((DefaultForkedTransaction) transaction);
//                } else if (transaction instanceof DefaultTaggedTransaction) {
//                    // link it as run away message since the forked transaction is not completed yet
//                    markAsRunAway(parent, (DefaultTaggedTransaction) transaction);
//                }
            }
        }
    }
}
