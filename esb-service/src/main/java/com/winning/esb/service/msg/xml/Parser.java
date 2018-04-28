package com.winning.esb.service.msg.xml;

import com.winning.esb.model.SvcStructureModel;
import com.winning.esb.model.ValueListModel;
import com.winning.esb.model.common.TreeModel;
import com.winning.esb.model.enums.SvcStructureEnum;
import com.winning.esb.model.ext.SvcStructureExtModel;
import com.winning.esb.service.msg.IParser;
import com.winning.esb.service.msg.MsgException;
import com.winning.esb.service.utils.TreeUtils;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.StringUtils;
import com.winning.esb.utils.XmlUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuehao
 * @date 2017/9/4
 */
@Component("parserXML")
public class Parser implements IParser {

    private final String virtualBodyBegin = "<Body>";
    private final String virtualBodyEnd = "</Body>";

    /**
     * 解析XML
     */
    @Override
    public SvcStructureExtModel decode(String msg, boolean fillName) throws MsgException {
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new StringReader(msg));
            // 获取根元素
            Element root = document.getRootElement();
            //设置根节点的对象结构
            SvcStructureExtModel result = createStructure(root, fillName);
            return result;
        } catch (Exception ex) {
            throw new MsgException("解析XML发生异常错误，XML可能不符合规范或有异常字符！" + ex.getMessage());
        }
    }

    @Override
    public String decode(String msg) throws MsgException {
        try {
            return XmlUtils.formatXML(msg);
        } catch (Exception e) {
            throw new MsgException("解析XML发生异常错误，XML可能不符合规范或有异常字符！" + e.getMessage());
        }
    }

    @Override
    public String encode(List<TreeModel> treeModels, Integer valueType, List<ValueListModel> valueListModels) throws MsgException {
        if (!SvcStructureEnum.ValueTypeEnum.existCode(valueType)) {
            return null;
        }
        Map<String, Object> map = TreeUtils.treeToMap(treeModels, valueType, valueListModels);
        StringBuffer xml = new StringBuffer();
        XmlUtils.mapToXML(map, xml);
        String xmlStr = "";
        try {
            //如果节点数超过1个，会导致XML解析失败，所以需要添加一个虚拟的父节点，完成格式化后，再删除虚拟父节点
            if (!ListUtils.isEmpty(treeModels) && treeModels.size() > 1) {
                xml.insert(0, virtualBodyBegin).append(virtualBodyEnd);
            }

            //格式化
            xmlStr = XmlUtils.formatXML(xml.toString());

            //如果节点数超过1个，会导致XML解析失败，所以需要添加一个虚拟的父节点，完成格式化后，再删除虚拟父节点
            if (!ListUtils.isEmpty(treeModels) && treeModels.size() > 1) {
                xmlStr = xmlStr.substring(xmlStr.indexOf(virtualBodyBegin) + virtualBodyBegin.length(), xmlStr.lastIndexOf(virtualBodyEnd));
            }
        } catch (Exception e) {
            throw new MsgException("生成XML发生异常错误！" + e.getMessage());
        }
        return xmlStr;
    }

    @Override
    public String getValueByPath(String msg, String path) {
        return null;
    }

    /**
     * 根据XML节点元素生成服务的节点对象结构
     */
    private SvcStructureExtModel createStructure(Element element, boolean fillName) {
        SvcStructureExtModel extModel = new SvcStructureExtModel();
        SvcStructureExtModel childExt;
        SvcStructureModel model = new SvcStructureModel();
        model.setCode(element.getName());
        Map<String, SvcStructureExtModel> extChildren = null;
        String code, name;
        //获取所有属性
        List<Attribute> listAttr = element.attributes();
        if (!ListUtils.isEmpty(listAttr)) {
            for (Attribute attr : listAttr) {
                //设置命名空间属性
                name = attr.getNamespaceURI();
                if (!StringUtils.isEmpty(name)) {
                    code = "xmlns:" + attr.getNamespacePrefix();
                    setNamespaceAttr(code, name, extModel, fillName);
                }

                //设置属性
                code = attr.getQualifiedName();
                name = attr.getValue();
                setNamespaceAttr(code, name, extModel, fillName);
            }
        }
        //检测根节点是否有命名空间，如果有，则添加到属性
        if (element.isRootElement()) {
            name = element.getNamespaceURI();
            if (!StringUtils.isEmpty(name)) {
                code = element.getNamespacePrefix();
                if (StringUtils.isEmpty(code)) {
                    code = "xmlns";
                } else {
                    code = "xmlns:" + code;
                }
                setNamespaceAttr(code, name, extModel, fillName);
            }
        }

        // 获取所有子元素
        List<Element> childList = element.elements();
        if (ListUtils.isEmpty(childList)) {
            if (StringUtils.isEmpty(element.getTextTrim()) && fillName) {
                model.setName(element.getName());
            } else {
                model.setName(element.getTextTrim());
            }
            model.setData_type(SvcStructureEnum.DataTypeEnum.Strings.getCode());
        } else {
            Element child;
            extChildren = new LinkedHashMap<>();
            for (int i = 0, len = childList.size(); i < len; i++) {
                child = childList.get(i);
                code = child.getName();
                if (!extChildren.containsKey(code)) {
                    childExt = createStructure(child, fillName);
                    extChildren.put(code, childExt);
                } else {
                    extChildren.get(code).getObj().setIs_loop(SvcStructureEnum.IsLoopEnum.Yes.getCode());
                }
            }
            if (fillName) {
                model.setName(element.getName());
            }

            model.setData_type(SvcStructureEnum.DataTypeEnum.Complex.getCode());
            if (!ListUtils.isEmpty(extModel.getChildren())) {
                extModel.getChildren().addAll(ListUtils.transferToList(extChildren.values()));
            } else {
                extModel.setChildren(ListUtils.transferToList(extChildren.values()));
            }
        }
        extModel.setObj(model);
        return extModel;
    }

    /**
     * 设置属性
     */
    private void setNamespaceAttr(String code, String name, SvcStructureExtModel extModel, boolean fillName) {
        SvcStructureModel modelAttr = new SvcStructureModel();
        modelAttr.setCode(code);
        if (StringUtils.isEmpty(name) && fillName) {
            modelAttr.setName(code);
        } else {
            modelAttr.setName(name);
        }
        modelAttr.setIs_attr(SvcStructureEnum.IsAttrEnum.Yes.getCode());
        modelAttr.setData_type(SvcStructureEnum.DataTypeEnum.Strings.getCode());
        SvcStructureExtModel childExt = new SvcStructureExtModel();
        childExt.setObj(modelAttr);
        if (extModel.getChildren() == null) {
            extModel.setChildren(new ArrayList<>());
        }
        extModel.getChildren().add(childExt);
    }
}