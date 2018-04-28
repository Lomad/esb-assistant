package com.winning.monitor.agent.logging.transaction;

import com.winning.monitor.agent.logging.message.LogMessage;

import java.util.List;

/**
 * Created by nicholasyan on 16/9/7.
 */
public interface Transaction extends LogMessage {

    /**
     * Add one nested child message to current transaction.
     *
     * @param message to be added
     */
    public Transaction addChild(LogMessage message);

    /**
     * Get all children message within current transaction.
     * <p/>
     * <p>
     * Typically, a <code>Transaction</code> can nest excute <code>Transaction</code>s, <code>Event</code>s and
     * <code>Heartbeat</code> s, while an <code>Event</code> or <code>Heartbeat</code> can't nest excute messages.
     * </p>
     *
     * @return all children messages, empty if there is no nested children.
     */
    public List<LogMessage> getChildren();

    /**
     * How long the transaction took from construction to complete. Time unit is microsecond.
     *
     * @return duration time in microsecond
     */
    public long getDurationInMicros();

    /**
     * How long the transaction took from construction to complete. Time unit is millisecond.
     *
     * @return duration time in millisecond
     */
    public long getDurationInMillis();

    /**
     * Has children or not. An atomic transaction does not have any children message.
     *
     * @return true if child exists, else false.
     */
    public boolean hasChildren();

    /**
     * Check if the transaction is stand-alone or belongs to another one.
     *
     * @return true if it's an root transaction.
     */
    public boolean isStandalone();


    void success();

}
