/**
 * MonitorDealSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.winning.monitor.webservice.wsdl;

import com.winning.monitor.webservice.entry.MonitorDeal;

public class MonitorDealSoapBindingImpl implements MonitorDeal_PortType{
    @Override
    public String monitorLogging(String from) throws java.rmi.RemoteException {
        try {
            MonitorDeal monitorDeal = new MonitorDeal();
            String res = monitorDeal.monitorLogging(from);
            return res;
        }catch (Exception e){
            return e.getMessage();
        }
    }

}
