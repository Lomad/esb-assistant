package com.winning.esb.service.impl;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;
import com.winning.esb.model.*;
import com.winning.esb.model.biz.EsbDataProtocal;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.enums.SimulationTestStepLogEnum;
import com.winning.esb.model.enums.SvcInfoEnum;
import com.winning.esb.model.enums.SvcStructureEnum;
import com.winning.esb.model.ext.SvcStructureExtModel;
import com.winning.esb.service.*;
import com.winning.esb.service.dataprotocal.ProtocalChecker;
import com.winning.esb.service.msg.IParser;
import com.winning.esb.service.msg.MsgException;
import com.winning.esb.service.utils.EsbReceiverForTestFlow;
import com.winning.esb.service.utils.EsbReceiverForTestUnit;
import com.winning.esb.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author xuehao
 * @date 2017/8/21
 */
@Service
public class SimulationTestBusinessServiceImpl implements ISimulationTestBusinessService {
//    Logger logger = LoggerFactory.getLogger(SimulationTestBusinessServiceImpl.class);

    @Autowired
    private ISvcUrlService urlService;
    @Autowired
    private ISvcInfoService svcInfoService;
    @Autowired
    private ISvcStructureService svcStructureService;
    @Autowired
    private IValueListService valueListService;
    @Autowired
    private ISimulationTestStepLogService simulationTestStepLogService;
    @Autowired
    private ProtocalChecker protocalChecker;

    private Map<String, IParser> parserMap;

    private Integer localPort;

    @PostConstruct
    private void init() {
        parserMap = AppCtxUtils.getBeansOfType(IParser.class);
    }

    @Override
    public void startEsbService(SvcUrlModel obj) {
        String url = obj.getUrl();
        if (obj.getSvcType() == 2) {
            String[] strList = url.split(":");
            String ip = strList[0];
            localPort = Integer.parseInt(strList[1]);
        } else if (obj.getSvcType() == 1) {
            try {
                HttpRequestUtils.httpPost(url, null, null, "", false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public SimulationTestStepLogModel send(Integer sid, String msg, String esbTestUrl) throws Exception {
        SimulationTestStepLogModel resultLog = new SimulationTestStepLogModel();

        resultLog.setSid(sid);
        resultLog.setOut_msg(msg);
        try {
            //发送到ESB
            String result = HttpRequestUtils.httpPost(esbTestUrl, null, null, msg, false);
            resultLog.setAck_msg(result);
            //解析应答消息
            parseAck(resultLog);
        } catch (Exception e) {
            resultLog.setResult(SimulationTestStepLogEnum.ResultEnum.Failure.getCode());
            if (StringUtils.isEmpty(resultLog.getAck_msg())) {
                resultLog.setDesp(e.getMessage());
            }
            e.printStackTrace();
        }

        resultLog.setTid(0);
        simulationTestStepLogService.save(resultLog);
        return resultLog;
    }

    /**
     * 解析应答消息
     */
    private void parseAck(SimulationTestStepLogModel resultLog) {
        String resultValue = null;
        Integer sid = resultLog.getSid();
        String result = resultLog.getAck_msg();
        try {
            //获取服务对象
            SvcInfoModel svcInfoModel = svcInfoService.getByID(sid);
            //获取结果标志路径
            String resultPath = svcStructureService.getResultNodePath(sid);
            //根据结果标志路径获取结果
            if (!StringUtils.isEmpty(resultPath)) {
                if (SvcInfoEnum.MsgTypeEnum.JSON.getCode().equals(svcInfoModel.getMsgType())) {
                    //JSON
                    resultValue = JsonUtils.getValueByPath(result, resultPath);
                } else if (SvcInfoEnum.MsgTypeEnum.XML.getCode().equals(svcInfoModel.getMsgType())) {
                    //XML
                    resultPath = "/" + resultPath.replace(".", "/");
                    resultValue = XmlUtils.getValueByPath(result, resultPath);
                } else if (SvcInfoEnum.MsgTypeEnum.HL7.getCode().equals(svcInfoModel.getMsgType())) {
                    //HL7
                    PipeParser pipeParser = new PipeParser();
                    Message message = pipeParser.parse(result);
                    if (message instanceof ca.uhn.hl7v2.model.v251.message.ACK) {
                        resultValue = ((ca.uhn.hl7v2.model.v251.message.ACK) message).getMSA().getMsa1_AcknowledgmentCode().getValue();
                    } else if (message instanceof ca.uhn.hl7v2.model.v24.message.ACK) {
                        resultValue = ((ca.uhn.hl7v2.model.v24.message.ACK) message).getMSA().getMsa1_AcknowledgementCode().getValue();
                    }
                }
            }
            //判断结果
            if (!StringUtils.isEmpty(resultValue)) {
                List<String> successValues = valueListService.listValueByModel(
                        valueListService.queryResultNodeValuesSuccessBySid(sid));
                if (!ListUtils.isEmpty(successValues) && successValues.contains(resultValue)) {
                    resultLog.setResult(SimulationTestStepLogEnum.ResultEnum.Success.getCode());
                } else {
                    resultLog.setResult(SimulationTestStepLogEnum.ResultEnum.Failure.getCode());
                }
            } else {
                resultLog.setResult(SimulationTestStepLogEnum.ResultEnum.Unknown.getCode());
            }
        } catch (Exception e) {
            resultLog.setResult(SimulationTestStepLogEnum.ResultEnum.Failure.getCode());
            if (StringUtils.isEmpty(resultLog.getAck_msg())) {
                resultLog.setDesp(e.getMessage());
            }
            e.printStackTrace();
        }
    }

    @Override
    public void receiveStart(Integer tid, SvcInfoModel svcInfoModel) {
        //重置公共变量
        SimulationTestStepLogModel logModel = new SimulationTestStepLogModel();
        logModel.setTid(tid);
        logModel.setSid(svcInfoModel.getId());
        EsbReceiverForTestUnit.init(logModel, svcInfoModel);
    }

    @Override
    public SimulationTestStepLogModel receive() {
        SimulationTestStepLogModel resultLog = EsbReceiverForTestUnit.getLogModel();
        //记录日志
        if (resultLog != null && resultLog.getResult() != null) {
            simulationTestStepLogService.save(resultLog);
            EsbReceiverForTestUnit.reset();
            return resultLog;
        }
        return null;
    }

    @Override
    public String fillMsgFromTestUnit(String msg) throws Exception {
        String errInfo = null;

        //获取服务对象
        SvcInfoModel svcInfoModel = EsbReceiverForTestUnit.getSvcModel();
        if (svcInfoModel == null) {
            errInfo = "未开启单元测试接收模式！";
        } else {
            StringBuilder err = new StringBuilder();

            //设置日志模型
            SimulationTestStepLogModel logModel = EsbReceiverForTestUnit.getLogModel();
            logModel.setBtime(new Date());
            logModel.setCtime(logModel.getBtime());
            logModel.setOut_msg(msg);

            //判断请求消息，并生成应答消息
            if (StringUtils.isEmpty(msg)) {
                err.append("请求消息为空！");
            } else {
                //判断消息类型
                SvcInfoEnum.MsgTypeEnum msgTypeEnum = svcStructureService.checkMsgType(msg);
                //将原始消息转为消息结构对象
                IParser parser = parserMap.get(IParser.BEAN_PREFIX + msgTypeEnum.getCode().toUpperCase());
                SvcStructureExtModel extModel = null;
                String parseMsg = "";
                try {
                    parseMsg = parser.decode(msg);
                    extModel = parser.decode(msg, false);
                } catch (MsgException msgEx) {
                    err.append(msgEx.getMessage());
                }
                if (extModel == null) {
                    err.append("请求消息无法解析！");
                }

                //如果是XML或JSON，则验证是否符合ESB数据协议规范
                if (err.length() < 1
                        && (SvcInfoEnum.MsgTypeEnum.XML.equals(msgTypeEnum) || SvcInfoEnum.MsgTypeEnum.JSON.equals(msgTypeEnum))) {
                    SvcInfoEnum.DataProtocalEnum dataProtocalEnum = svcStructureService.checkEsbDataProtocal(extModel);
                    EsbDataProtocal esbDataProtocal = new EsbDataProtocal();
                    err.append(protocalChecker.check(extModel, dataProtocalEnum, esbDataProtocal));
                }

                //验证请求消息
                if (err.length() < 1) {
                    err.append(checkMsg(svcInfoModel, parseMsg, SvcStructureEnum.DirectionEnum.In));
                }

                //生成应答与规范封装
                if (err.length() < 1) {
                    //生成应答消息
                    logModel.setAck_msg(svcStructureService.export(svcInfoModel.getId(),
                            SvcStructureEnum.DirectionEnum.Ack.getCode(),
                            msgTypeEnum.getCode(), SvcStructureEnum.ReturnTypeEnum.DATA.getCode(),
                            SvcStructureEnum.ValueTypeEnum.VistualValue.getCode(), true));
                }
            }

            //判断结果状态及错误信息
            if (err.length() > 0) {
                errInfo = err.toString();
                logModel.setAck_msg(errInfo);
                logModel.setResult(SimulationTestStepLogEnum.ResultEnum.Failure.getCode());
            } else {
                logModel.setResult(SimulationTestStepLogEnum.ResultEnum.Success.getCode());
            }

            //设置日志模型
            logModel.setEtime(new Date());
            logModel.setTime_len(DateUtils.diffMilliSecond(logModel.getBtime(), logModel.getEtime()));

            //更新公共变量
            EsbReceiverForTestUnit.setLogModel(logModel);

            //如果没有错误，则将应答结果返回
            if (StringUtils.isEmpty(errInfo)) {
                return logModel.getAck_msg();
            }
        }

        return errInfo;
    }

    @Override
    public String fillMsgFromTestFlow(String msg, SvcStructureEnum.DirectionEnum directionEnum) {
        String errInfo;

        //获取服务对象
        SvcInfoModel svcInfoModel = EsbReceiverForTestFlow.getSvcModel();
        if (svcInfoModel == null) {
            errInfo = "未开启集成测试模式！";
        } else {
            //设置日志模型
            SimulationTestStepLogModel logModel = EsbReceiverForTestFlow.getLogModel();
            if (SvcStructureEnum.DirectionEnum.In.equals(directionEnum)) {
                logModel.setOut_msg(msg);
                logModel.setBtime(new Date());
                logModel.setCtime(logModel.getBtime());
            } else {
                logModel.setAck_msg(msg);
                logModel.setEtime(new Date());
                logModel.setTime_len(DateUtils.diffMilliSecond(logModel.getBtime(), logModel.getEtime()));
                //解析应答消息
                parseAck(logModel);
            }

            //更新公共变量
            EsbReceiverForTestFlow.setLogModel(logModel);

            errInfo = null;
        }

        return errInfo;
    }

    /**
     * 验证请求或应答消息，例如是否必须、数据类型、是否在枚举值范围内等等
     *
     * @param svcInfoModel  服务对象
     * @param parseMsg      消息
     * @param directionEnum 消息方向
     */
    private String checkMsg(SvcInfoModel svcInfoModel, String parseMsg, SvcStructureEnum.DirectionEnum directionEnum) {
        StringBuilder err = new StringBuilder();
        Integer sid = svcInfoModel.getId();
        //获取对应的节点list
        List<SvcStructureModel> structureModelList = svcStructureService.queryBySvcID(sid, directionEnum.getCode());
        Map<Integer, SvcStructureModel> svcMap = new HashMap<>();
        for (SvcStructureModel obj : structureModelList) {
            Integer ssid = obj.getId();
            svcMap.put(ssid, obj);
        }
        for (SvcStructureModel obj : structureModelList) {
            Integer pid = obj.getId();
            Map<String, Object> map = new HashMap<>();
            map.put("pid", pid);
            CommonObject commonObject = svcStructureService.query(map);
            if (commonObject.getTotalSize() == 0) {
                String value = "";
                Integer ssid = obj.getId();
                String path = svcStructureService.retrieveNodePath(obj, svcMap, null);
                if (SvcInfoEnum.MsgTypeEnum.JSON.getCode().equals(svcInfoModel.getMsgType())) {
                    value = JsonUtils.getValueByPath(parseMsg, path);
                } else {
                     path = "/" + svcStructureService.retrieveNodeXmlPath(obj, svcMap, null);
                    try {
                        value = XmlUtils.getValueByPath(parseMsg, path);
                    } catch (Exception e) {
                        err.append("消息有特殊字符，无法解析!");
                    }
                }
                Integer isRequired = obj.getRequired();
                Integer data_type = obj.getData_type();
                if (isRequired.intValue() == 1) {
                    if (StringUtils.isEmpty(value)) {
                        err.append("缺少必须字段" + path + "!");
                    }
                }

                if (!StringUtils.isEmpty(value)) {
                    String regex = SvcStructureEnum.DataTypeEnum.getRegex(data_type);
                    String value_default = obj.getValue_default();
                    if (!RegexUtils.match(regex, value)) {
                        err.append(path + "数据类型不匹配!");
                    }
                    if (!StringUtils.isEmpty(value_default) && !value_default.equals(value)) {
                        err.append(path + "与默认值不相同!");
                    }
                    List<String> valueList = new ArrayList<>();
                    List<ValueListModel> valueListModels = valueListService.queryBySsid(ssid);
                    if (!ListUtils.isEmpty(valueListModels)) {
                        for (ValueListModel valueObj : valueListModels) {
                            valueList.add(valueObj.getValue());
                        }
                        if (!ListUtils.isEmpty(valueList) && !valueList.contains(value)) {
                            err.append(path + "的值不在枚举范围内！");
                        }

                    }

                }
            }
        }

        return err.toString();
    }


    private List<String> codeList(List<String> mCodeList, SvcStructureExtModel extModel) {
        SvcStructureModel obj = extModel.getObj();
        String code = obj.getCode();
        String name = obj.getName();
        if (!StringUtils.isEmpty(name)) {
            mCodeList.remove(code);
        }
        List<SvcStructureExtModel> children = extModel.getChildren();
        if (!ListUtils.isEmpty(children)) {
            for (SvcStructureExtModel structureExtModel : children) {
                if (!ListUtils.isEmpty(mCodeList)) {
                    codeList(mCodeList, structureExtModel);
                }
            }
        }
        return mCodeList;

    }


}