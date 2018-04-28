package com.winning.esb.controller.monitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.winning.monitor.data.api.IPatientTrackService;
import com.winning.monitor.data.api.transaction.domain.TransactionMessageList;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.winning.esb.controller.common.CommonOperation.resultError;

/**
 * @Author Lemod
 * @Version 2018/1/8
 */
@Controller
@RequestMapping(value = {"/view", "/ajax/patientTrack"})
public class PatientTrackController {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private IPatientTrackService patientTrackService;

    @RequestMapping(value = {"/patientTrack"})
    public ModelAndView indexServiceMonitor() {
        return new ModelAndView("monitor/patientTrack/patientTrack");
    }

    @RequestMapping(value = {"/queryServices"})
    @ResponseBody
    public AjaxDataResponseMessage queryServices(String datas) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();

        try {
            Map map = objectMapper.readValue(datas, Map.class);

            if (!StringUtils.isEmpty(map)) {
                String domain = (String) map.getOrDefault("providerId", "");
                String queryField = (String) map.getOrDefault("queryField", "");
                String limit = (String) map.getOrDefault("limit", "");
                String startTime = (String) map.getOrDefault("startTime", "");
                String endTime = (String) map.getOrDefault("endTime", "");

                int startIndex = Integer.parseInt(map.get("start").toString());
                int pageSize = Integer.parseInt(map.get("pageSize").toString());

                TransactionMessageList messageList = patientTrackService
                        .queryPatientServices(domain, queryField, limit, startTime, endTime, startIndex, pageSize);
                if (messageList != null) {
                    responseMessage.setData(messageList);
                    return responseMessage;
                }
            }
        } catch (IOException e) {
            resultError(responseMessage, e.getMessage());
        }
        return responseMessage;
    }

    @RequestMapping(value = {"/queryDataFields"})
    @ResponseBody
    public AjaxDataResponseMessage queryDataFields(String serverId) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            List<String> fields = patientTrackService.queryDataFields(serverId);
            if (fields != null) {
                responseMessage.setData(fields);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultError(responseMessage, e.getMessage());
        }
        return responseMessage;
    }
}
