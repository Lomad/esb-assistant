package com.winning.monitor.agent.logging.message;

/**
 * Created by nicholasyan on 16/9/8.
 */
public interface LogMessage {

    public static final String SUCCESS = "0";

    public String getMessageType();

    /**
     * add one or multiple key-value pairs to the message.
     *
     * @param keyValuePairs key-value pairs like 'a=1&b=2&...'
     */
//    public void addData(String keyValuePairs);

    /**
     * add one key-value pair to the message.
     *
     * @param key
     * @param value
     */
    public void addData(String key, Object value);

    /**
     * Complete the message construction.
     */
    public void complete();

    /**
     * @return key value pairs data
     */
    public Object getData();

    /**
     * Message name.
     *
     * @return message name
     */
    public String getName();

    /**
     * Get the message status.
     *
     * @return message status. "0" means success, otherwise error code.
     */
    public String getStatus();

    /**
     * Set the message status with exception class name.
     *
     * @param e exception.
     */
    public void setStatus(Throwable e);

    /**
     * Set the message status.
     *
     * @param status message status. "0" means success, otherwise error code.
     */
    public void setStatus(String status);

    /**
     * The time stamp the message was created.
     *
     * @return message creation time stamp in milliseconds
     */
    public long getTimestamp();

    /**
     * Message type.
     * <p/>
     * <p>
     * Typical message types are:
     * <ul>
     * <li>URL: maps to one method of an action</li>
     * <li>Service: maps to one method of service call</li>
     * <li>Search: maps to one method of search call</li>
     * <li>SQL: maps to one SQL statement</li>
     * <li>Cache: maps to one cache access</li>
     * <li>Error: maps to java.lang.Throwable (java.lang.Exception and java.lang.Error)</li>
     * </ul>
     * </p>
     *
     * @return message type
     */
    public String getType();

    /**
     * Message type.
     * <p/>
     * <p>
     * Typical message types are:
     * <ul>
     * <li>URL: maps to one method of an action</li>
     * <li>Service: maps to one method of service call</li>
     * <li>Search: maps to one method of search call</li>
     * <li>SQL: maps to one SQL statement</li>
     * <li>Cache: maps to one cache access</li>
     * <li>Error: maps to java.lang.Throwable (java.lang.Exception and java.lang.Error)</li>
     * </ul>
     * </p>
     *
     * @return void
     */
    public void setType(String type);

    /**
     * If the complete() method was called or not.
     *
     * @return true means the complete() method was called, false otherwise.
     */
    public boolean isCompleted();

    /**
     * @return
     */
    public boolean isSuccess();
}

