package com.winning.esb.simulator.task;

import com.winning.esb.simulator.repository.api.ISimulatorRepository;
import com.winning.esb.simulator.service.api.ISimulatorService;
import com.winning.esb.simulator.utils.entity.LoggingEntity;
import com.winning.monitor.data.storage.mongodb.po.message.MessageTreePO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static com.winning.esb.simulator.utils.EntityConvertor.convertMessageTreePO;
import static com.winning.esb.simulator.utils.EntityConvertor.updateKeyInfo;
import static com.winning.esb.simulator.utils.GlobalConstant.GSON;
import static com.winning.esb.simulator.utils.GlobalUtils.getSampleTimestamp;
import static com.winning.esb.simulator.utils.GlobalUtils.getTargetCount;

/**
 * @Author Lemod
 * @Version 2018/4/3
 */
@Component
@PropertySource(value = "classpath:/META-INF/rest.properties", encoding = "utf-8")
public class SimulatorTask {

    private static final Logger logger = LoggerFactory.getLogger(SimulatorTask.class);

    @Value("${Address}")
    private String restUrl;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ISimulatorService simulatorService;
    @Autowired
    private ISimulatorRepository simulatorRepository;

    private HttpHeaders headers;

    @PostConstruct
    private void initRestTemplate() {
        headers = new HttpHeaders();
        MediaType mediaType = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(mediaType);
    }

    @Scheduled(cron = "0 0/10 0-23 * * ?")
    private void startSimulate() {
        if (simulatorService.checkStatus()) {
            Set<String> collectionNames = simulatorRepository.querySampleCollections();
            //前台所选样本日期
            String sampleDate = (String) simulatorService.getParams().get("sampleDate");
            Integer gap = (Integer) simulatorService.getParams().get("randomGap");

            //当前小时
            Calendar calender = Calendar.getInstance();
            int hour = calender.get(Calendar.HOUR_OF_DAY);
            int minus = calender.get(Calendar.MINUTE);
            minus = minus - (minus % 10);
            //样本时间戳
            Long startTimestamp = getSampleTimestamp(sampleDate, hour, minus);
            Long endTimestamp = getSampleTimestamp(sampleDate, hour, minus + 10);
            //按表进行模拟
            collectionNames.forEach(collection -> {
                //查询出样本当天总量
                long sampleCount = simulatorRepository
                        .querySampleCount(collection, startTimestamp, endTimestamp);
                //计算出当前小时目标数量
                Integer targetCount = getTargetCount(sampleCount,
                        (Float) simulatorService.getParams().get("percent"));
                //目标生成数量10~100
                Integer random = new Random().nextInt(gap) + 10;
                //根据目标数，查询出样本明细
                List<MessageTreePO> treePOS = simulatorRepository.queryHourMessageTree(
                        collection, startTimestamp, endTimestamp, targetCount + random);
                treePOS.forEach(messageTreePO -> {
                    LoggingEntity entity = convertMessageTreePO(messageTreePO);
                    postRestApi(entity);
                });
            });
        }
    }

    /**
     * 无需样本，即可生成模拟数据【5分钟生成一次】
     */
    @Scheduled(cron = "0 0/5 0-23 * * ?")
//    @Scheduled(cron = "0/30 * 0-23 * * ?")
    private void startSimulateWithoutSample() {
        //设置待生成的colloection
        List<String> collectionNames = new ArrayList<>();
        collectionNames.add("HIP0101");
        collectionNames.add("HIP0102");
        collectionNames.add("HIP0107");
        collectionNames.add("HIP0301");
        collectionNames.add("HIP0302");
        collectionNames.add("HIS0101");
        collectionNames.add("HIS0102");
        collectionNames.add("HIS0105");
        collectionNames.add("HIS0106");
        collectionNames.add("HIS0107");
        collectionNames.add("HIS0108");
        collectionNames.add("HIS0109");

        //设置每个小时生成的数据量
        Map<Integer, Integer> hourCount = new HashMap<>();
        hourCount.put(0, 102);
        hourCount.put(1, 122);
        hourCount.put(2, 110);
        hourCount.put(3, 96);
        hourCount.put(4, 65);
        hourCount.put(5, 350);
        hourCount.put(6, 470);
        hourCount.put(7, 660);
        hourCount.put(8, 954);
        hourCount.put(9, 1206);
        hourCount.put(10, 1102);
        hourCount.put(11, 909);
        hourCount.put(12, 807);
        hourCount.put(13, 706);
        hourCount.put(14, 904);
        hourCount.put(15, 809);
        hourCount.put(16, 801);
        hourCount.put(17, 605);
        hourCount.put(18, 503);
        hourCount.put(19, 706);
        hourCount.put(20, 404);
        hourCount.put(21, 203);
        hourCount.put(22, 104);
        hourCount.put(23, 67);

        //当前小时
        Calendar calender = Calendar.getInstance();
        int hour = calender.get(Calendar.HOUR_OF_DAY);
        int count = hourCount.get(hour);
        int collLen = collectionNames.size();
        LoggingEntity entity = simulatorService.createSimulateMsg();

        //按表进行模拟
        collectionNames.forEach(collection -> {
            int countNew = count + new Random().nextInt(20);
            for (int i = 0; i < countNew; i++) {
                updateKeyInfo(entity.getTransactionCopy());

                try {
                    //提供方
                    entity.setSourceProvider(collection);
                    //消费方
                    Integer consumerIndex = new Random().nextInt(collLen);
                    entity.getRemoteCaller().setName(collectionNames.get(consumerIndex));
                    //服务代码
                    String svcCode = collection + "01";
                    entity.getTransactionCopy().setType(svcCode);
                } catch (Exception ex) {

                }

                postRestApi(entity);
            }
        });
    }

    private void postRestApi(LoggingEntity entity) {
        try {
            //包装消息体，避免中文乱码
            byte[] bodyBytes = GSON.toJson(entity).getBytes("UTF-8");
            String bodyContext = new String(bodyBytes, "UTF-8");

            HttpEntity<String> httpEntity = new HttpEntity<>(bodyContext, headers);

            String result = restTemplate.postForObject(restUrl, httpEntity, String.class);
            logger.info("rest埋点结果：" + result);
        } catch (UnsupportedEncodingException e) {
            logger.error("包装REST body出错！", e);
        }
    }
}
