package com.winning.monitor.superisor.consumer.api.analysis;

import java.util.List;

public interface MessageAnalyzerManager {

    List<String> getAnalyzerNames();

    List<MessageAnalyzer> getAnalyzer(String name, long startTime);
}
