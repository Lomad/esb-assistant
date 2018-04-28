package com.winning.monitor.webservice.entry;

import com.winning.monitor.webservice.logging.AgentLogging;

/**
 * @Author Lemod
 *@Version 2016/11/26
 */
public class MonitorDeal {

  public String monitorLogging(String from) {

    AgentLogging agentLogging = new AgentLogging();
    String response = agentLogging.mainDeal(from);

    return response;

  }

}
