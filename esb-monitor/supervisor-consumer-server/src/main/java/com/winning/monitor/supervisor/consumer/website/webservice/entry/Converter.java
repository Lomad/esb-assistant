package com.winning.monitor.supervisor.consumer.website.webservice.entry;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import com.winning.esb.stable.MonitorConst;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.StringUtils;
import com.winning.monitor.supervisor.consumer.website.entity.Data;
import com.winning.monitor.supervisor.consumer.website.entity.LoggingEntity;
import com.winning.monitor.supervisor.consumer.website.entity.TransactionCopy;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.List;

/**
 * @Author Lemod
 * @Version 2016/11/26
 */
public class Converter {
//    /**
//     * xuehao 2018-03-30：注册需要反序列化的类
//     */
//    static {
//        XBlink.registerClassesToBeUsed(new Class[]{
//                LoggingEntity.class, RemoteCaller.class, TransactionCopy.class, Data.class
//        });
//    }
//
//    public static LoggingEntity serialize(String xml) {
//        //反序列化XML
//        LoggingEntity loggingEntity = (LoggingEntity) XBlink.fromXml(xml);
//
////        //xuehao 2018-03-30：特殊处理mainId字段（必须使用英文逗号分隔）
////        TransactionCopy transactionCopy = loggingEntity.getTransactionCopy();
////        if(transactionCopy != null && !ListUtils.isEmpty(transactionCopy.getDataList())) {
////            List<Data> dataList = transactionCopy.getDataList();
////            String key, value;
////            for(Data item : dataList) {
////                key = item.getKey();
////                value = item.getValue();
////                if(MonitorConst.KeyMainId.equals(key) && !StringUtils.isEmpty(value)) {
////                    item.setValue(value.split(","));
////                }
////            }
////        }
//        return (LoggingEntity) XBlink.fromXml(xml);
//    }

    @SuppressWarnings("unchecked")
    public static LoggingEntity serialize(String xml) throws JAXBException {
        //替换旧版节点
        xml = xml.replace("<MonitorLogging>", "<loggingEntity>");
        xml = xml.replace("</MonitorLogging>", "</loggingEntity>");
        //反序列化XML
        JAXBContext context = JAXBContext.newInstance(LoggingEntity.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader reader = new StringReader(xml);
        LoggingEntity loggingEntity = (LoggingEntity) unmarshaller.unmarshal(reader);

        //特殊处理datalist的子节点value，转为字符串
        parseDatalistValue(loggingEntity.getTransactionCopy());

        return loggingEntity;
    }

    /**
     * xuehao 2018-03-30：
     * 1、针对webservice接口的XML参数：特殊处理datalist的子节点value，转为字符串，并将mainId转为数组；
     * 2、针对restful接口的XML参数：将mainId转为数组；
     */
    public static void parseDatalistValue(TransactionCopy transactionCopy) {
        if (transactionCopy != null) {
            if (!ListUtils.isEmpty(transactionCopy.getDataList())) {
                List<Data> dataList = transactionCopy.getDataList();
                String key;
                Object value;
                String valueAfterParse;
                Node node;
                for (Data item : dataList) {
                    key = item.getKey();
                    value = item.getValue();

                    //设置datalist的value值，转为字符串
                    if (value != null && value instanceof ElementNSImpl) {
                        item.setValue("");
                        node = (Node) value;
                        if (node != null) {
                            node = node.getFirstChild();
                            item.setValue(node.getNodeValue());
                        }
                    }

                    //将mainId转为数组
                    value = item.getValue();
                    if(MonitorConst.KeyMainId.equals(key) && value instanceof String
                            && !StringUtils.isEmpty(value) && ((String) value).contains(",")) {
                        valueAfterParse = (String) value;
                        item.setValue(valueAfterParse.split(","));
                    }
                }
            }

            //遍历子节点的datalist
            if(!ListUtils.isEmpty(transactionCopy.getChildren())) {
                for(TransactionCopy item : transactionCopy.getChildren()) {
                    parseDatalistValue(item);
                }
            }
        }
    }

    public static String CheckForLegal(LoggingEntity entity) {
        StringBuilder errorMessage = new StringBuilder();
        String sourceProvider = entity.getSourceProvider(),
                providerAddress = entity.getProviderAddress(),
                providerHostName = entity.getProviderHostName(),
                name = entity.getRemoteCaller().getName(),
                ip = entity.getRemoteCaller().getIp(),
                type = entity.getRemoteCaller().getType();
        if (StringUtils.isEmpty(sourceProvider)) {
            errorMessage.append("xml缺少服务提供方字段！");
        }
        if (StringUtils.isEmpty(providerAddress)) {
            errorMessage.append("xml缺少服务提供方ip地址！");
        }
        if (StringUtils.isEmpty(providerHostName)) {
            errorMessage.append("xml缺少服务提供方主机名！");
        }
        if (StringUtils.isEmpty(name)) {
            errorMessage.append("xml缺少服务调用方系统代码！");
        }
        if (StringUtils.isEmpty(ip)) {
            errorMessage.append("xml缺少服务调用方ip地址！");
        }
        if (StringUtils.isEmpty(type)) {
            errorMessage.append("xml缺少服务调用方客户端类型！");
        }
        return errorMessage.toString();
    }

}
