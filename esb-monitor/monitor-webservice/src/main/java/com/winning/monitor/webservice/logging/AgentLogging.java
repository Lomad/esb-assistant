package com.winning.monitor.webservice.logging;

import com.winning.monitor.agent.logging.MonitorLogger;
import com.winning.monitor.agent.logging.transaction.DefaultTransaction;
import com.winning.monitor.webservice.request_entity.Data;
import com.winning.monitor.webservice.request_entity.LoggingEntity;
import com.winning.monitor.webservice.request_entity.RemoteCaller;
import com.winning.monitor.webservice.request_entity.TransactionCopy;
import com.winning.monitor.webservice.response_entity.ResultCode;
import org.apache.axis.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xblink.XBlink;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author Lemod
 * @Version 2016/11/26
 */
public class AgentLogging {

    private static Logger logger = LoggerFactory.getLogger(AgentLogging.class);

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

    public String mainDeal(String inxml){

        LoggingEntity loggingEntity = null;
        StringBuffer res = null;

        StringBuffer error = new StringBuffer();
        try {

            if (StringUtils.isEmpty(inxml)) {
                error.append("XML为空");
            } else {
                loggingEntity = this.serialize(inxml, error);
            }
            if (error.length() == 0){
                res = this.mainLogging(loggingEntity,error);
            }
        }catch (Exception e){
            error.append(e.getMessage());
        }
        if (error.length() != 0){
            res.append(error);
        }

        return res.toString();
    }

    public StringBuffer mainLogging(LoggingEntity entity,StringBuffer error) throws Exception {

        String provider = entity.getSourceProvider();
        MonitorLogger.setInputDomain(provider);

        RemoteCaller remoteCaller = entity.getRemoteCaller();
        if (null != remoteCaller) {
            MonitorLogger.setCaller(remoteCaller.getName(), remoteCaller.getIp(), remoteCaller.getType());
        }else {
            error.append("调用方信息为空！"+entity.toString());
            logger.error("调用方信息为空！"+entity.toString());
        }

        if (error.length() == 0) {
            TransactionCopy transactionCopy = entity.getTransactionCopy();
            if (null != transactionCopy) {
                String type = transactionCopy.getType();
                if (null == type) {
                    error.append("根监控对象名字为空！");
                } else {
                    DefaultTransaction parentTran = (DefaultTransaction) MonitorLogger.beginTransactionType(type);

                    List<Data> dataList = transactionCopy.getDataList();
                    if (null != dataList && dataList.size() > 0) {
                        for (Data data : dataList) {
                            parentTran.addData(data.getKey(), data.getValue());
                        }
                    }

                    List<TransactionCopy> children = transactionCopy.getChildren();
                    if (children != null && children.size() > 0) {
                        this.generateTran(children, parentTran);
                    }

                    if (!StringUtils.isEmpty(transactionCopy.getStatus())) {
                        parentTran.setStatus(transactionCopy.getStatus());
                    }

                    parentTran.complete();

                    String start = transactionCopy.getStartTime();
                    String end = transactionCopy.getEndTime();
                    if (!start.isEmpty() && !end.isEmpty()) {
                        long duration = this.calDuration(start, end);
                        parentTran.setDurationInMillis(duration);
                        parentTran.setDurationInMicros(duration * 1000L);
                    }
                }
            } else {
                error.append("根监控对象为空！");
            }
        }

        StringBuffer res = new StringBuffer();
        if (error.length() == 0){
            res.append(ResultCode.WHETHER_SUCCESS.AA.toString());
        }else {
            res.append(ResultCode.WHETHER_SUCCESS.AE.toString()).append(":").append(error);
        }
        return res;
    }

    public void generateTran(List<TransactionCopy> children , DefaultTransaction parentTran) throws Exception {
        for (TransactionCopy child : children){
            DefaultTransaction childTran = (DefaultTransaction) MonitorLogger.beginTransactionName(parentTran,child.getName());
            List<Data> dataList = child.getDataList();
            if (!dataList.isEmpty() && dataList.size()>0){
                for (Data data : dataList){
                    childTran.addData(data.getKey(),data.getValue());
                }
            }

            List<TransactionCopy> s_children = child.getChildren();
            if (s_children != null && s_children.size()>0) {
                this.generateTran(s_children, childTran);
            }

            if (!StringUtils.isEmpty(child.getStatus())) {
                childTran.setStatus(child.getStatus());
            }
            childTran.complete();

            String start = child.getStartTime();
            String end = child.getEndTime();
            if(StringUtils.isEmpty(start)) {
                throw new Exception("开始时间(startTime)节点不能为空！");
            }
            if(StringUtils.isEmpty(end)) {
                throw new Exception("结束时间(endTime)节点不能为空！");
            }

            long duration = this.calDuration(start,end);
            childTran.setDurationInMillis(duration);
            childTran.setDurationInMicros(duration * 1000L);
        }
    }

    public LoggingEntity serialize(String xml,StringBuffer res){

        LoggingEntity loggingEntity = null;

        xml = xml.replace("\n","").replace("\r","");
        if (StringUtils.isEmpty(xml)){
            res.append("传入消息为空");
        }else {
            int begin = xml.indexOf("<"+ResultCode.LoggingDefine+">")+xml.indexOf("</"+ResultCode.LoggingDefine+">");
            if (begin < 0){
                res.append("根标签有误");
            }
        }
        if (res.length() == 0){
            try {
                XBlink.registerClassesToBeUsed(new Class[]{
                        LoggingEntity.class,RemoteCaller.class,TransactionCopy.class,Data.class
                });
                loggingEntity = (LoggingEntity) XBlink.fromXml(xml);
            }catch (Exception e){
                res.append(e.getMessage());
            }
        }
        return loggingEntity;
    }

    public long calDuration(String startTime,String endTime){
        Long duration = null;
        try {
            Date start = DATE_FORMAT.parse(startTime);
            Date end = DATE_FORMAT.parse(endTime);

            duration = end.getTime() - start.getTime();
        }catch (Exception e){
            e.printStackTrace();
        }
        return duration;
    }

}
