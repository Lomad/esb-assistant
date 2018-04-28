/**
 * MonitorDealService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.winning.monitor.webservice.wsdl;

public interface MonitorDealService extends javax.xml.rpc.Service {
    public String getMonitorDealAddress();

    public com.winning.monitor.webservice.wsdl.MonitorDeal_PortType getMonitorDeal() throws javax.xml.rpc.ServiceException;

    public com.winning.monitor.webservice.wsdl.MonitorDeal_PortType getMonitorDeal(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
