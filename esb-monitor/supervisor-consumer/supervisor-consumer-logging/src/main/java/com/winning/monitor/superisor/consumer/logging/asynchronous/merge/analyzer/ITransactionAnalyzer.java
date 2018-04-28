package com.winning.monitor.superisor.consumer.logging.asynchronous.merge.analyzer;

/**
 * @Author Lemod
 * @Version 2017/9/13
 */
public interface ITransactionAnalyzer<T> {

    boolean offer(T tree);

}
