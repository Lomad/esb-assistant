package com.winning.monitor.superisor.consumer.logging.asynchronous.merge.analyzer;

import com.winning.monitor.agent.logging.message.LogMessage;
import com.winning.monitor.agent.logging.message.MessageTree;
import com.winning.monitor.agent.logging.transaction.Transaction;
import com.winning.monitor.superisor.consumer.api.report.ReportManager;
import com.winning.monitor.superisor.consumer.logging.transaction.TransactionAnalyzer;
import com.winning.monitor.superisor.consumer.logging.transaction.entity.TransactionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @Author Lemod
 * @Version 2017/10/11
 */
public class TransactionAnalyzerImpl<T extends MessageTree>
        implements ITransactionAnalyzer<T>, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TransactionAnalyzerImpl.class);

    private final BlockingQueue<MessageTree> queue;
    private Thread m_thread;
    private volatile boolean running = true;

    private TransactionAnalyzer s_analyzer;

    //@Resource(name = "asynchronousAnalyzer")
    private ReportManager<TransactionReport> reportManager;

    public TransactionAnalyzerImpl(int maxSize) {
        this.queue = new ArrayBlockingQueue<MessageTree>(maxSize);
        s_analyzer = new TransactionAnalyzer(reportManager);
    }

    @Override
    public boolean offer(MessageTree tree) {
        try {
            return queue.offer(tree,5, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void run() {
        while (running){
            if (!queue.isEmpty()){
                try {
                    MessageTree tree = queue.poll(5,TimeUnit.MILLISECONDS);
                    process(tree);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void process(MessageTree tree) {
        String domain = tree.getDomain();
        String group = tree.getGroup();

        TransactionReport report = reportManager.getHourlyReport(getStartTime(tree), group, domain, true);
        LogMessage message = tree.getMessage();

        report.addIp(tree.getIpAddress());

        if (message instanceof Transaction) {
            Transaction root = (Transaction) message;
            s_analyzer.processTransaction(report, tree, root);
        }
    }

    private long getStartTime(MessageTree tree) {
        long timestamp = tree.getMessage().getTimestamp();
        timestamp = timestamp - timestamp % 3600000;
        return timestamp;
    }
}
