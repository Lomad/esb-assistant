package com.winning.esb;


import javax.wsdl.*;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CreateSOAPRequestTemplate {
    /**
     * @param args
     */
    public static void main(String[] args) {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<soapenv:Envelope\n" +
                "    xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "    <soapenv:Body>");
        try {
            WSDLFactory factory = WSDLFactory.newInstance();
            WSDLReader reader = factory.newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", true);
            reader.setFeature("javax.wsdl.importDocuments", true);
            Definition def = reader.readWSDL("http://localhost:8080/axis/services/SayHelloService?wsdl");
            //解析服务名
            System.out.println("----------");
            System.out.println("nService Name:");
            String tns = "http://localhost:8080/axis/services/SayHelloService";
            Service service = def.getService(new QName(tns, "SayHelloService"));
            System.out.println(service.getQName().getLocalPart());
            //解析接口方法名
            System.out.println("nOperation Name:");
            Port port = service.getPort("SayHelloService");
            Binding binding = port.getBinding();
            PortType portType = binding.getPortType();
            List operations = portType.getOperations();
            Iterator operIter = operations.iterator();
            while (operIter.hasNext()) {
                Operation operation = (Operation) operIter.next();
                if (!operation.isUndefined()) {
                    System.out.println(operation.getName());
                }
            }
            //解析消息，输入输出
            System.out.println("nMessages:");
            Map messages = def.getMessages();
            Iterator msgIter = messages.values().iterator();
            while (msgIter.hasNext()) {
                Message msg = (Message) msgIter.next();
                if (!msg.isUndefined()) {
                    System.out.println(msg.getQName().getLocalPart());
                    Iterator partIter = msg.getParts().values().iterator();
                    while (partIter.hasNext()) {
                        Part part = (Part) partIter.next();
                        System.out.print("parameter name:" + part.getName() + "t");
                        System.out.println("parameter type:" + part.getTypeName().getLocalPart());
                    }
                }
            }
            //解析服务地址
            System.out.println("nService location:");
            List l = port.getExtensibilityElements();
            ExtensibilityElement element = (ExtensibilityElement) l.get(0);
            String s = element.toString();
            System.out.println(s.substring(s.indexOf("location")));
            System.out.println("---------");

        } catch (WSDLException e) {
            e.printStackTrace();
        }

    }
}
