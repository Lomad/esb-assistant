package com.winning.esb.service.impl;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v251.message.ACK;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.PipeParser;
import com.winning.esb.model.*;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.common.SimpleObject;
import com.winning.esb.model.common.TreeModel;
import com.winning.esb.model.enums.*;
import com.winning.esb.service.*;
import com.winning.esb.service.msg.IParser;
import com.winning.esb.service.pdf.PdfHelper;
import com.winning.esb.utils.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

@Service
public class SvcUnitTestServiceImpl implements ISvcUnitTestService {
    @Autowired
    private ISvcStructureService svcStructureService;
    @Autowired
    private ISimulationTestStepLogService simulationTestStepLogService;
    @Autowired
    private ISvcInfoService svcInfoService;
    @Autowired
    private IValueListService valueListService;
    private Map<String, IParser> parserMap;

    private SimulationTestStepLogModel receiveMsg = new SimulationTestStepLogModel();

    @PostConstruct
    private void init() {
        parserMap = AppCtxUtils.getBeansOfType(IParser.class);
    }

//    @Override
//    public SimulationTestStepLogModel send(SvcUrlModel obj, String msg, Integer sid) {
//        Socket socket = null;
//        String url = obj.getUrl();
//        SvcInfoModel svcInfoModel = svcInfoService.getByID(sid);
//        receiveMsg.setSid(sid);
//        receiveMsg.setOut_msg(msg);
//        if (obj.getSvcType().intValue() == SvcUrlEnum.SvcTypeEnum.Socket.getCode()) {
//            String sendMsg;
//            if (svcInfoModel.getMsgType().equalsIgnoreCase(SvcInfoEnum.MsgTypeEnum.HL7.getCode())) {
//                sendMsg = START_OF_BLOCK + msg + END_OF_BLOCK + CARRIGE_RETURN;
//            } else {
//                sendMsg = msg;
//            }
//            try {
//                String[] strList = url.split(":");
//                String ip = strList[0];
//                Integer port = Integer.parseInt(strList[1]);
//                socket = new Socket();
//                socket.connect(new InetSocketAddress(ip, port), 5000);
//                //读取服务数据
//                DataInputStream input = new DataInputStream(socket.getInputStream());
//                //向服务器端发送数据
//                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
//                out.writeUTF(sendMsg);
//                byte[] data = new byte[4096];
//                int len = 0;
//                ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
//                len = input.read(data);
//                outSteam.write(data, 0, len);
//                byte[] str = outSteam.toByteArray();
//                for (int i = 0; i < str.length; i++) {
//                    if (str[i] == (char) 28 && str[i + 1] == (char) 13) {
//                        String receMsg = outSteam.toString().trim();
//                        receiveMsg.setAck_msg(receMsg);
//                        PipeParser pipeParser = new PipeParser();
//                        Message parseMsg = pipeParser.parse(receMsg);
//                        String flag = null;
//                        if (parseMsg instanceof ACK) {
//                            flag = ((ACK) parseMsg).getMSA().getMsa1_AcknowledgmentCode().getValue();
//                        } else if (parseMsg instanceof ca.uhn.hl7v2.model.v24.message.ACK) {
//                            flag = ((ca.uhn.hl7v2.model.v24.message.ACK) parseMsg).getMSA().getMsa1_AcknowledgementCode().getValue();
//                        }
//                        if ("AA".equals(flag)) {
//                            receiveMsg.setResult(SimulationTestStepLogEnum.ResultEnum.Success.getCode());
//                        } else {
//                            receiveMsg.setResult(SimulationTestStepLogEnum.ResultEnum.Failure.getCode());
//                        }
//                        break;
//                    }
//
//
//                }
//                socket.shutdownOutput();
//                input.close();
//                outSteam.close();
//                out.close();
//            } catch (Exception e) {
//                receiveMsg.setResult(SimulationTestLogEnum.ResultEnum.Failure.getCode());
//                receiveMsg.setAck_msg(e.getMessage());
//            } finally {
//                try {
//                    socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        } else if (obj.getSvcType().intValue() == SvcUrlEnum.SvcTypeEnum.Rest.getCode()) {
//            try {
//                String resultValue = null;
//                String result = HttpRequestUtils.httpPost(url, null, null, msg, false);
//                receiveMsg.setAck_msg(result);
//                String resultPath = svcStructureService.getResultNodePath(sid);
//                if (!StringUtils.isEmpty(resultPath)) {
//                    if (SvcInfoEnum.MsgTypeEnum.JSON.getCode().equals(svcInfoModel.getMsgType())) {
//                        resultValue = JsonUtils.getValueByPath(result, resultPath);
//                    } else if (SvcInfoEnum.MsgTypeEnum.XML.getCode().equals(svcInfoModel.getMsgType())) {
//                        String jsonString = JsonUtils.xml2Json(result).toJSONString();
//                        resultValue = JsonUtils.getValueByPath(jsonString, resultPath);
//                    }
//                }
//                if (!StringUtils.isEmpty(resultValue)) {
//                    List<ValueListModel> valueListModels = valueListService.queryResultNodeValuesBySid(sid);
//                    if (valueListModels != null) {
//                        for (ValueListModel valueObj : valueListModels) {
//                            String value = valueObj.getValue();
//                            if (resultValue.equals(value)) {
//                                if (valueObj.getType().intValue() == ValueListEnum.TypeEnum.Success.getCode())
//                                    receiveMsg.setResult(SimulationTestStepLogEnum.ResultEnum.Success.getCode());
//                                else if (valueObj.getType().intValue() == ValueListEnum.TypeEnum.Failure.getCode())
//                                    receiveMsg.setResult(SimulationTestStepLogEnum.ResultEnum.Failure.getCode());
//                                break;
//                            }
//                        }
//                    }
//                }
//                if (receiveMsg.getResult() == null) {
//                    receiveMsg.setResult(SimulationTestStepLogEnum.ResultEnum.Unknown.getCode());
//                }
//            } catch (Exception e) {
//                receiveMsg.setResult(SimulationTestStepLogEnum.ResultEnum.Failure.getCode());
//                if (StringUtils.isEmpty(receiveMsg.getAck_msg())) {
//                    receiveMsg.setAck_msg(e.getMessage());
//                }
//                e.printStackTrace();
//            }
//        } else {
//            receiveMsg.setAck_msg("暂时不支持webservice");
//            receiveMsg.setResult(SimulationTestLogEnum.ResultEnum.Failure.getCode());
//        }
//
//        receiveMsg.setTid(0);
//        simulationTestStepLogService.save(receiveMsg);
//        return receiveMsg;
//    }

    @Override
    public SimulationTestStepLogModel startService(Integer sid, Integer port, Integer time) {
        String err = "";
        ServerSocket serverSocket = null;
        Socket client;
        Map<Integer, SvcStructureModel> map = new HashMap<>();
        SvcInfoModel obj = svcInfoService.getByID(sid);
        receiveMsg.setSid(sid);
        long currentTime = System.currentTimeMillis();
        List<SvcStructureModel> svcStructureModelList = svcStructureService.queryBySvcID(sid, 1);
        for (SvcStructureModel svcStructureModel : svcStructureModelList) {
            Integer id = svcStructureModel.getId();
            map.put(id, svcStructureModel);
        }

        if (obj.getMsgType().equalsIgnoreCase(SvcInfoEnum.MsgTypeEnum.HL7.getCode())) {
            try {
                serverSocket = new ServerSocket(port);
                long treadTime = 0;
                if (time == null) {
                    while (treadTime < currentTime + 200 * 1000) {
                        client = serverSocket.accept();
                        //创建一个新的线程
                        ServerThread serverThread = new ServerThread(client, map, svcStructureModelList, err);
                        //启动线程
                        serverThread.start();
                        treadTime = System.currentTimeMillis();
                    }
                } else {
                    while (treadTime < currentTime + time) {
                        client = serverSocket.accept();
                        //创建一个新的线程
                        ServerThread serverThread = new ServerThread(client, map, svcStructureModelList, err);
                        //启动线程
                        serverThread.start();
                        treadTime = System.currentTimeMillis();
                    }
                }
            } catch (Exception e) {
                receiveMsg.setResult(0);
                return receiveMsg;
            } finally {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                //HttpRequestUtils.httpPost(url,null,null,"",false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return receiveMsg;
    }

    @Override
    public SimulationTestStepLogModel receive() {
        return receiveMsg;
    }


    @Override
    public CommonObject testLog(Map<String, Object> map) {
        return simulationTestStepLogService.query(map);
    }


    @Override
    public String downloadAck(String ackMsg) {
        String result = null;
//        PdfHelper pdfHelper = new PdfHelper();
//        String fileName = null;
//        try {
//            fileName = pdfHelper.createTestAck(ackMsg);
//            result.setItem2("/download/pdf/" + fileName);
//        } catch (Exception e) {
//            result.setItem1(e.getMessage());
//            e.printStackTrace();
//        }
        try {
            String path = FileUtils.getRootPath() + "/download";
            FileUtils.createPath(path);
            String fileName = UUID.randomUUID() + ".txt";
            String filePathName = path + "/" + fileName;
            FileUtils.writeFile(filePathName, ackMsg);
            result = "/download/" + fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public class ServerThread extends Thread {
        // 和本线程相关的Socket
        private Socket socket;
        //错误
        private String err;
        //用于生成节点路径 key-节点id，value-节点对象
        private Map<Integer, SvcStructureModel> map;
        //该服务所有请求消息对象list
        private List<SvcStructureModel> svcStructureModelList;

        public ServerThread(Socket socket,
                            Map<Integer, SvcStructureModel> map,
                            List<SvcStructureModel> svcStructureModelList,
                            String err) {
            this.socket = socket;
            this.map = map;
            this.svcStructureModelList = svcStructureModelList;
            this.err = err;
        }

        //线程执行的操作，响应客户端的请求
        @Override
        public void run() {
            try {
                OutputStream os = socket.getOutputStream();
                InputStream is = socket.getInputStream();
                PipeParser pipeParser = new PipeParser();
                byte[] data = new byte[2048];
                // 读取客户端数据
                int len = 0;
                ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
                while ((len = is.read(data)) != -1) {
                    outSteam.write(data, 0, len);
                    byte[] str = outSteam.toByteArray();
                    for (int i = 0; i < str.length; i++) {
                        if (str[i] == (char) 28 && str[i + 1] == (char) 13) {
                            String receMsg = outSteam.toString().trim();
                            receiveMsg.setOut_msg(receMsg);
                            Message parseMsg = pipeParser.parse(receMsg);
                            DefaultXMLParser xmlParserHL7 = new DefaultXMLParser();
                            String xml = xmlParserHL7.encode(parseMsg);
                            SAXReader reader = new SAXReader();
                            Document document = reader.read(new StringReader(xml));
                            Integer sid = 0;
                            List<ValueListModel> valueListModels = null;
                            List<String> valueList;
                            for (SvcStructureModel svcStructureModel : svcStructureModelList) {
                                Integer ssid = svcStructureModel.getId();
                                sid = svcStructureModel.getSid();
                                Integer dataType = svcStructureModel.getData_type();
                                //获取枚举值
                                valueListModels = valueListService.queryBySsid(ssid);
                                valueList = new ArrayList<>();
                                for (ValueListModel valueModel : valueListModels) {
                                    String value = valueModel.getValue();
                                    valueList.add(value);
                                }

                                //根据正则表达式验证节点值
                                String regexStr = SvcStructureEnum.DataTypeEnum.getRegex(dataType);

                                String path = "";
                                Element element;
                                List<Element> elementList = new ArrayList<>();
                                if (dataType.intValue() != SvcStructureEnum.DataTypeEnum.Complex.getCode()) {
                                    path = svcStructureService.retrieveNodeXmlPath(svcStructureModel, map, null);
                                }
                                if (!StringUtils.isEmpty(path)) {
                                    elementList = document.selectNodes(path);
                                }
                                if (elementList.size() != 0) {
                                    element = elementList.get(0);
                                    String text = element.getTextTrim();
                                    if (!valueList.contains(text)) {
                                        err += svcStructureModel.getCode() + "取值不对";
                                    }
                                    boolean flag = RegexUtils.match(regexStr, text);
                                    if (flag == false) {
                                        err += svcStructureModel.getCode() + " 取值不符合规范";
                                    }
                                }
                            }
                            String responseMsg = "";
                            List<TreeModel> treeModels = svcStructureService.createZTree(sid, 2, null);
                            if (!ListUtils.isEmpty(treeModels)) {
                                IParser parser = parserMap.get(IParser.BEAN_PREFIX + "HL7");
                                responseMsg = parser.encode(treeModels, SvcStructureEnum.ValueTypeEnum.VistualValue.getCode(), valueListModels);
                            }

                            // 向客户端回复信息
                            String respMsg = (char) 11 + responseMsg + (char) 28 + (char) 13;
                            os.write(respMsg.getBytes());

                            //socket.shutdownOutput();// 关闭输出流
                            receiveMsg.setAck_msg(responseMsg);
                        }
                    }
                }
                if (StringUtils.isEmpty(err)) {
                    receiveMsg.setResult(SimulationTestLogEnum.ResultEnum.Success.getCode());
                } else {
                    receiveMsg.setResult(SimulationTestLogEnum.ResultEnum.Failure.getCode());
                    receiveMsg.setAck_msg(err);
                }
                socket.shutdownOutput();// 关闭输出流
                outSteam.close();
                os.close();
                is.close();

            } catch (Exception e) {
                e.printStackTrace();
                receiveMsg.setAck_msg(e.getMessage());
                receiveMsg.setResult(SimulationTestLogEnum.ResultEnum.Failure.getCode());
            } finally {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            receiveMsg.setTid(0);
            simulationTestStepLogService.save(receiveMsg);
        }

    }

}
