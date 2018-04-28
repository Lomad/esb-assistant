package com.winning.esb.service.msg.hl7;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.PipeParser;
import com.winning.esb.model.ValueListModel;
import com.winning.esb.model.common.TreeModel;
import com.winning.esb.model.enums.SvcStructureEnum;
import com.winning.esb.model.ext.SvcStructureExtModel;
import com.winning.esb.service.msg.IParser;
import com.winning.esb.service.msg.MsgException;
import com.winning.esb.service.utils.TreeUtils;
import com.winning.esb.utils.XmlUtils;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

/**
 * @author xuehao
 * @date 2017/9/5
 */
@Component("parserHL7")
public class Parser implements IParser {
    @Resource(name = "parserXML")
    private IParser xmlParser;

    /**
     * 解析HL7
     */
    @Override
    public SvcStructureExtModel decode(String msg, boolean fillName) throws MsgException {
        try {
            PipeParser pipeParser = new PipeParser();
            Message message = pipeParser.parse(msg);
            //获取根节点名称
            String rootName = message.getName();
            //获取HL7消息对应的XML结构
            DefaultXMLParser xmlParserHL7 = new DefaultXMLParser();
            String xml = xmlParserHL7.encode(message);
            SAXReader reader = new SAXReader();
            org.dom4j.Document document = reader.read(new StringReader(xml));

            //获取段的名称数组，用于替换字段节点的名称 验证消息结构不需要替换，保存原有结构
            if(fillName == true) {
                String[] segmentNames = message.getNames();
                if (segmentNames != null && segmentNames.length > 0) {
                    Structure structure;
                    Segment segment;
                    String[] fieldNames;
                    String fieldName;
                    //遍历段
                    for (String segmentName : segmentNames) {
                        structure = message.get(segmentName);
                        //段(Segment)
                        if (structure instanceof Segment) {
                            segment = (Segment) message.get(segmentName);
                            //遍历字段，替换字段节点的名称
                            fieldNames = segment.getNames();
                            if (fieldNames != null && fieldNames.length > 0) {
                                for (int fieldIndex = 0, fieldLen = fieldNames.length; fieldIndex < fieldLen; fieldIndex++) {
                                    fieldName = removeExceptCharacterNumber(fieldNames[fieldIndex]);
                                    //xml = xml.replaceFirst("<" + segmentName + "." + (fieldIndex + 1) + ">", "<" + fieldName + ">");
                                    //xml = xml.replaceFirst("</" + segmentName + "." + (fieldIndex + 1) + ">", "</" + fieldName + ">");
                                    String name = segmentName + "." + (fieldIndex + 1);
                                    if (document.getRootElement().element(segmentName) != null && document.getRootElement().element(segmentName).element(name) != null) {
                                        document.getRootElement().element(segmentName).element(name).setText(fieldName);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            xml = document.asXML();

            return xmlParser.decode(xml, fillName);
        } catch (Exception ex) {
            throw new MsgException("解析HL7发生异常错误，HL7可能不符合规范或有异常字符！" + ex.getMessage());
        }
    }

    @Override
    public String decode(String msg) throws MsgException{
        try {
            PipeParser pipeParser = new PipeParser();
            Message message = pipeParser.parse(msg);
            //获取根节点名称
            String rootName = message.getName();
            //获取HL7消息对应的XML结构
            DefaultXMLParser xmlParserHL7 = new DefaultXMLParser();
            String xml = xmlParserHL7.encode(message);
            SAXReader reader = new SAXReader();
            org.dom4j.Document document = reader.read(new StringReader(xml));
            xml = document.asXML();
            return xml;
        }catch(Exception e){
            throw new MsgException("解析HL7发生异常错误，HL7可能不符合规范或有异常字符！" + e.getMessage());
        }
    }

    @Override
    public String encode(List<TreeModel> treeModels, Integer valueType, List<ValueListModel> valueListModels) throws MsgException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document doc = null;
        InputSource source = null;
        StringReader reader = null;
        //StringReader reader2 = new StringReader("123");
        //StringReader reader3 = new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ORU_R01>123</ORU_R01>");
        try {
            //设置命名空间验证
            factory.setNamespaceAware(true);

            if (!SvcStructureEnum.ValueTypeEnum.existCode(valueType)) {
                return null;
            }
            Map<String, Object> map = TreeUtils.treeToMap(treeModels, valueType, valueListModels);
            StringBuffer sb = new StringBuffer();
            //sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            XmlUtils.mapToXML(map, sb);
            String xml = sb.toString().replaceAll("&", "&amp;");
            xml = xml.replaceFirst(">", " xmlns=\"urn:hl7-org:v2xml\">");
            builder = factory.newDocumentBuilder();
            reader = new StringReader(xml.trim());
            source = new InputSource(reader);//使用字符流创建新的输入源
            doc = builder.parse(source);
            DefaultXMLParser xmlParserHL7 = new DefaultXMLParser();
            Message hl7Msg = xmlParserHL7.parseDocument(doc, "2.5.1");
            return hl7Msg.toString();
        } catch (Exception ex) {
            throw new MsgException("生成HL7发生异常错误，HL7可能不符合规范或有异常字符！" + ex.getMessage());
        }
    }

    @Override
    public String getValueByPath(String msg, String path) {
        return null;
    }

    /**
     * 除了字母与数字，其他字符都删除
     */
    private String removeExceptCharacterNumber(String str) {
        String reg = "[^a-zA-Z0-9]";
        return str.replaceAll(reg, "");
    }


}