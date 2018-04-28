package com.winning.esb.utils;

import org.apache.commons.collections.map.LinkedMap;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

public class XmlUtils {
    /**
     * 节点名称的前缀（即Map的key的前缀）
     */
    public static final String ATTR_PREFIX = "<attr>";

    /**
     * 将Map转为XML
     */
    public static void mapToXML(Map map, StringBuffer sb) {
        Set set = map.keySet();
        Object temp;
        String attrs;
        for (Iterator it = set.iterator(); it.hasNext(); ) {
            String key = (String) it.next();
            Object value = map.get(key);
            if (null == value) {
                value = "";
            }
            if ("java.util.ArrayList".equals(value.getClass().getName())) {
                ArrayList list = (ArrayList) value;
                if (!ListUtils.isEmpty(list)) {
                    temp = list.get(0);
                    if (temp instanceof LinkedMap) {
                        StringBuffer sbTemp = new StringBuffer();
                        LinkedMap linkedMap = (LinkedMap) temp;
                        attrs = createAttrs(linkedMap);
                        mapToXML(linkedMap, sbTemp);
                        sb.append("<" + key + attrs + ">").append(sbTemp).append("</" + key + ">");
                        sb.append("<" + key + attrs + ">").append(sbTemp).append("</" + key + ">");
                    } else {
                        sb.append("<" + key + ">" + temp + "</" + key + ">");
                        sb.append("<" + key + ">" + temp + "</" + key + ">");
                    }
                }
            } else {
                if (value instanceof LinkedMap) {
                    LinkedMap linkedMap = (LinkedMap) value;
                    attrs = createAttrs(linkedMap);
                    sb.append("<" + key + attrs + ">");
//                    mapToXML((LinkedMap) value, sb);
                    mapToXML(linkedMap, sb);
                    sb.append("</" + key + ">");
                } else {
                    sb.append("<" + key + ">" + value + "</" + key + ">");
                }

            }

        }
    }

    /**
     * 生成属性
     */
    private static String createAttrs(LinkedMap linkedMap) {
        if (!MapUtils.isEmpty(linkedMap)) {
            StringBuffer attrs = null;
            List<String> attrKeyList = null;
            for (Iterator itChild = linkedMap.keySet().iterator(); itChild.hasNext(); ) {
                String keyChild = (String) itChild.next();
                Object valueChild = linkedMap.get(keyChild);
                if (keyChild.startsWith(ATTR_PREFIX)) {
                    if (attrs == null) {
                        attrs = new StringBuffer();
                    }
                    if (attrKeyList == null) {
                        attrKeyList = new ArrayList<>();
                    }
                    attrs.append(" ").append(keyChild.replace(ATTR_PREFIX, ""))
                            .append("=\"").append(valueChild).append("\"");
                    attrKeyList.add(keyChild);
                }
            }

            if (attrKeyList != null && attrKeyList.size() > 0) {
                for (String attrKey : attrKeyList) {
                    linkedMap.remove(attrKey);
                }
            }
            if (attrs != null) {
                return attrs.toString();
            }
        }
        return "";
    }

    /**
     * 将XML格式化
     */
    public static String formatXML(String inputXML) throws Exception {
        SAXReader reader = new SAXReader();
        String xml = inputXML.replaceAll("&", "&amp;");
        Document document = reader.read(new StringReader(xml));
        String requestXML = null;
        XMLWriter writer = null;
        if (document != null) {
            try {
                StringWriter stringWriter = new StringWriter();
                OutputFormat format = new OutputFormat();
                format.setSuppressDeclaration(true);
                format.setIndent(true);
                format.setIndent("    ");
//                format.setNewlines(true);
                writer = new XMLWriter(stringWriter, format);
                writer.write(document);
                writer.flush();
                requestXML = stringWriter.getBuffer().toString();
                requestXML = requestXML.replace("\n\n", "\n");
                if (requestXML.startsWith("\n")) {
                    requestXML = requestXML.substring(1);
                }
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return requestXML;
    }

    /**
     * 根据字段路径获取值
     *
     * @param path 节点路径，格式：/根节点/子节点/子节点/……
     */
    public static String getValueByPath(String xml, String path) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //不考虑命名空间
        dbf.setNamespaceAware(false);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(xml)));
        XPathFactory factory = XPathFactory.newInstance();
        javax.xml.xpath.XPath x = factory.newXPath();
        XPathExpression expr = x.compile(path);
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
        return node == null ? null : node.getTextContent();
    }

    public static void main(String[] args) {
        //测试
        test1();
    }

    public static void test1() {
        String xml = "<RegisterPatientResponse xmlns=\"urn:hl7-org:v2xml\"><Ack><ResultCode>AA</ResultCode><ResultDetail>GUID</ResultDetail></Ack></RegisterPatientResponse>";
//        String xml = "<RegisterPatientResponse><Ack><ResultCode>AA</ResultCode><ResultDetail>GUID</ResultDetail></Ack></RegisterPatientResponse>";
        try {
            System.out.println(getValueByPath(xml, "/RegisterPatientResponse/Ack/ResultCode"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}