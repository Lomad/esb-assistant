package com.winning.monitor.supervisor.core.message.analyzer;

import com.winning.monitor.superisor.consumer.api.analysis.MessageAnalyzer;
import com.winning.monitor.superisor.consumer.api.analysis.MessageAnalyzerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by nicholasyan on 16/9/12.
 */
public class DefaultMessageAnalyzerManager implements MessageAnalyzerManager, ApplicationContextAware {

    private static final long MINUTE = 60 * 1000L;
    protected Logger m_logger = LoggerFactory.getLogger(DefaultMessageAnalyzerManager.class);

    private long m_duration = 60 * MINUTE;
    private long m_extraTime = 3 * MINUTE;

    private List<String> m_analyzerNames;

    private Map<Long, Map<String, List<MessageAnalyzer>>> m_analyzers = new HashMap<Long, Map<String, List<MessageAnalyzer>>>();
    private ApplicationContext applicationContext;

    @PostConstruct
    public void initialize() {
        Map<String, MessageAnalyzer> map =
                this.applicationContext.getBeansOfType(MessageAnalyzer.class);

        m_analyzerNames = new ArrayList<String>(map.keySet());

        for (MessageAnalyzer analyzer : map.values()) {
            analyzer.destroy();
        }

        Collections.sort(m_analyzerNames, new Comparator<String>() {
            @Override
            public int compare(String str1, String str2) {
                String state = "state";
                String top = "top";

                if (state.equals(str1)) {
                    return 1;
                } else if (state.equals(str2)) {
                    return -1;
                }
                if (top.equals(str1)) {
                    return -1;
                } else if (top.equals(str2)) {
                    return 1;
                }
                return str1.compareTo(str2);
            }
        });
    }

    @Override
    public List<String> getAnalyzerNames() {
        return m_analyzerNames;
    }

    @Override
    public List<MessageAnalyzer> getAnalyzer(String name, long startTime) {
        try {
            // remove last two hour analyzer
            Map<String, List<MessageAnalyzer>> temp = m_analyzers.remove(startTime - m_duration * 2);

            if (temp != null) {
                for (List<MessageAnalyzer> anlyzers : temp.values()) {
                    for (MessageAnalyzer analyzer : anlyzers) {
                        analyzer.destroy();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //获取开始时间对应的分析器集合
        Map<String, List<MessageAnalyzer>> map = m_analyzers.get(startTime);

        //如果对应时间未进行初始化,初始化集合
        if (map == null) {
            synchronized (m_analyzers) {
                map = m_analyzers.get(startTime);

                if (map == null) {
                    map = new HashMap<String, List<MessageAnalyzer>>();
                    m_analyzers.put(startTime, map);
                }
            }
        }

        //获取分析器集合
        List<MessageAnalyzer> analyzers = map.get(name);

        if (analyzers == null) {
            synchronized (map) {
                analyzers = map.get(name);

                if (analyzers == null) {
                    analyzers = new ArrayList<MessageAnalyzer>();

                    MessageAnalyzer analyzer = this.applicationContext.getBean(name, MessageAnalyzer.class);
                    analyzer.setIndex(0);
                    analyzer.initialize(startTime, m_duration, m_extraTime);
                    analyzers.add(analyzer);

                    int count = analyzer.getAnanlyzerCount();

                    for (int i = 1; i < count; i++) {
                        MessageAnalyzer tempAnalyzer = this.applicationContext.getBean(name, MessageAnalyzer.class);
                        tempAnalyzer.setIndex(i);
                        tempAnalyzer.initialize(startTime, m_duration, m_extraTime);
                        analyzers.add(tempAnalyzer);
                    }
                    map.put(name, analyzers);
                }
            }
        }

        return analyzers;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
