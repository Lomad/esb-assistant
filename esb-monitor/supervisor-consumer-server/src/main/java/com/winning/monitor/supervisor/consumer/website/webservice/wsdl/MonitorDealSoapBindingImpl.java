/**
 * MonitorDealSoapBindingImpl.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.winning.monitor.supervisor.consumer.website.webservice.wsdl;


import com.winning.monitor.supervisor.consumer.website.webservice.entry.MonitorDeal;
import com.winning.monitor.utils.ApplicationContextUtils;

public class MonitorDealSoapBindingImpl implements MonitorDeal_PortType {
    @Override
    public String monitorLogging(String from) throws java.rmi.RemoteException {
        try {
            MonitorDeal monitorDeal = ApplicationContextUtils.getBean("monitorDeal", MonitorDeal.class);
            return monitorDeal.monitorLogging(from);
        } catch (Exception e) {
            return e.toString();
        }
    }

}
