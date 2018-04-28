/**
 * MonitorDealServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.winning.monitor.webservice.wsdl;

public class MonitorDealServiceLocator extends org.apache.axis.client.Service implements MonitorDealService {

    public MonitorDealServiceLocator() {
    }


    public MonitorDealServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public MonitorDealServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for MonitorDeal
    private String MonitorDeal_address = "http://localhost:8080//services/com/winning/monitor/webservice/entry/MonitorDeal";

    @Override
    public String getMonitorDealAddress() {
        return MonitorDeal_address;
    }

    // The WSDD service name defaults to the port name.
    private String MonitorDealWSDDServiceName = "MonitorDeal";

    public String getMonitorDealWSDDServiceName() {
        return MonitorDealWSDDServiceName;
    }

    public void setMonitorDealWSDDServiceName(String name) {
        MonitorDealWSDDServiceName = name;
    }

    @Override
    public MonitorDeal_PortType getMonitorDeal() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MonitorDeal_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMonitorDeal(endpoint);
    }

    @Override
    public MonitorDeal_PortType getMonitorDeal(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.winning.monitor.webservice.wsdl.MonitorDealSoapBindingStub _stub = new com.winning.monitor.webservice.wsdl.MonitorDealSoapBindingStub(portAddress, this);
            _stub.setPortName(getMonitorDealWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMonitorDealEndpointAddress(String address) {
        MonitorDeal_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @Override
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (MonitorDeal_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.winning.monitor.webservice.wsdl.MonitorDealSoapBindingStub _stub = new com.winning.monitor.webservice.wsdl.MonitorDealSoapBindingStub(new java.net.URL(MonitorDeal_address), this);
                _stub.setPortName(getMonitorDealWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @Override
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("MonitorDeal".equals(inputPortName)) {
            return getMonitorDeal();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    @Override
    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://entry.webservice.monitor.winning.com", "MonitorDealService");
    }

    private java.util.HashSet ports = null;

    @Override
    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://entry.webservice.monitor.winning.com", "MonitorDeal"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("MonitorDeal".equals(portName)) {
            setMonitorDealEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
