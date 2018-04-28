package com.winning.esb.simulator.service.impl;

import com.winning.esb.simulator.service.api.ISimulatorService;
import com.winning.esb.simulator.utils.entity.LoggingEntity;
import com.winning.esb.utils.JsonUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author Lemod
 * @Version 2018/4/3
 */
@Service
public class SimulatorService implements ISimulatorService {

    private Map<String, Object> params = new HashMap<>();

    private volatile boolean isStarted = false;

    //设置默认值
    @PostConstruct
    private void init() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        params.put("sampleDate", calendar.getTimeInMillis());
        params.put("percent", new Float("0.1"));
        params.put("randomGap", new Integer("100"));
    }

    @Override
    public void startSimulator(Map params) {
        if (params != null) {
            setParams(params);
            isStarted = true;
        }
    }

    @Override
    public boolean checkStatus() {
        return isStarted;
    }

    @Override
    public Map<String, Object> getParams() {
        Set<String> keySet = params.keySet();
        if (keySet.size() == 0) {
            return null;
        } else {
            return params;
        }
    }

    @Override
    public LoggingEntity createSimulateMsg() {
        String json = getSimuStringMsg();
        LoggingEntity loggingEntity = JsonUtils.jsonToObject(json, LoggingEntity.class);
        return loggingEntity;
    }

    /**
     * 获取原始的字符串消息
     */
    private String getSimuStringMsg() {
        return "{\n" +
                "    \"sourceProvider\": \"HIP0101\",\n" +
                "    \"providerAddress\": \"192.168.11.202\",\n" +
                "    \"providerHostName\": \"HL7Engine\",\n" +
                "    \"remoteCaller\": {\n" +
                "        \"name\": \"HIS0109\",\n" +
                "        \"ip\": \"192.168.33.238\",\n" +
                "        \"type\": \"PC\"\n" +
                "    },\n" +
                "    \"transactionCopy\": {\n" +
                "        \"status\": \"0\",\n" +
                "        \"type\": \"HIP010118\",\n" +
                "        \"startTime\": \"2018-04-13 16:12:42.625\",\n" +
                "        \"endTime\": \"2018-04-13 16:12:42.725\",\n" +
                "        \"dataList\": [\n" +
                "            {\n" +
                "                \"key\": \"router\",\n" +
                "                \"value\": \"ESB\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"key\": \"sourceTime\",\n" +
                "                \"value\": \"2018-04-13 16:12:42.636&2018-04-13 16:12:42.725\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"key\": \"MessageID\",\n" +
                "                \"value\": \"867BFE5B-D22F-487D-938B-25084620C84A\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"key\": \"mainId\",\n" +
                "                \"value\": [\"1232\", \"5004\"]\n" +
                "            },\n" +
                "            {\n" +
                "                \"key\": \"输入信息\",\n" +
                "                \"value\": \"MSH|^~\\\\&|HIS_InPatient|WinningSoft|TECH|WinningSoft|20160615092612|pv1|ADT^A01|867BFE5B-D22F-487D-938B-25084620C84A|P|2.4|||||CHN\\rEVN|A01|20160615092612\\rPID|1|117^^^&PATID|117^^^&PATID~1600000030^^^&BLH~421281199507100051^^^&SFZH~0^^^&YEXH||rrr||19950710000000|F|||||^^^^^^132123321452|||S^未婚||||||17^汉族||||||40^中国\\rPV1|1|I|627^外一科^^^^^201^外一科病区||||||||||||||||119|02~普通病员||||||||||||||||||||0||||20160615092300|19000101000000|||||119\\rPV2||||||||||||||||||||||||||||||||||||N\\rDG1|||00004^伤寒|||0|0\\r\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"key\": \"输出信息\",\n" +
                "                \"value\": \"MSH|^~\\\\&|HIS|WinningSoft|LIS|WinningSoft|20171201102042||ACK^A01|285ca8a7-e9ca-486e-9c4f-4f2645b366b2|P|2.4|||||CHN\\rMSA|AA|867BFE5B-D22F-487D-938B-25084620C84A\\r\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"children\": [\n" +
                "            {\n" +
                "                \"name\": \"平台消息接收\",\n" +
                "                \"status\": \"0\",\n" +
                "                \"dataList\": [\n" +
                "                    {\n" +
                "                        \"key\": \"消息内容\",\n" +
                "                        \"value\": \"MSH|^~\\\\&|HIS_InPatient|WinningSoft|TECH|WinningSoft|20160615092612|pv1|ADT^A01|867BFE5B-D22F-487D-938B-25084620C84A|P|2.4|||||CHN\\rEVN|A01|20160615092612\\rPID|1|117^^^&PATID|117^^^&PATID~1600000030^^^&BLH~421281199507100051^^^&SFZH~0^^^&YEXH||rrr||19950710000000|F|||||^^^^^^132123321452|||S^未婚||||||17^汉族||||||40^中国\\rPV1|1|I|627^外一科^^^^^201^外一科病区||||||||||||||||119|02~普通病员||||||||||||||||||||0||||20160615092300|19000101000000|||||119\\rPV2||||||||||||||||||||||||||||||||||||N\\rDG1|||00004^伤寒|||0|0\\r\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"key\": \"异常消息\",\n" +
                "                        \"value\": \"\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"startTime\": \"2018-04-13 16:12:42.636\",\n" +
                "                \"endTime\": \"2018-04-13 16:12:42.665\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"name\": \"客户端应答消息\",\n" +
                "                \"status\": \"0\",\n" +
                "                \"dataList\": [\n" +
                "                    {\n" +
                "                        \"key\": \"消息内容\",\n" +
                "                        \"value\": \"MSH|^~\\\\&|HIS|WinningSoft|LIS|WinningSoft|20171201102042||ACK^A01|285ca8a7-e9ca-486e-9c4f-4f2645b366b2|P|2.4|||||CHN\\rMSA|AA|867BFE5B-D22F-487D-938B-25084620C84A\\r\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"key\": \"异常消息\",\n" +
                "                        \"value\": \"\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"startTime\": \"2018-04-13 16:12:42.665\",\n" +
                "                \"endTime\": \"2018-04-13 16:12:42.725\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
    }

    //将前台值写入静态变量
    private void setParams(Map in) {
        Object dateString = in.get("sampleDate");
        if (!StringUtils.isEmpty(dateString)) {
            params.replace("sampleDate", dateString);
        }
        Object percent = in.get("percent");
        if (!StringUtils.isEmpty(percent)) {
            params.replace("percent", Float.parseFloat((String) in.get("percent")));
        }
        Object randomGap = in.get("randomGap");
        if (!StringUtils.isEmpty(randomGap)) {
            params.replace("randomGap", Integer.parseInt((String) randomGap));
        }
    }

}
