package com.winning.esb.controller.utils;

import com.winning.esb.utils.JsonUtils;
import com.winning.esb.utils.StringUtils;
import com.winning.esb.utils.XmlUtils;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 主要用于格式数据，例如XML、JSON、HL7等
 *
 * @author xuehao
 * @date 2018/3/25
 */
@Controller
@RequestMapping(value = {"/ajax/utils/dataFormat", "/ajax/utils/dataFormat/"})
public class DataFormatController {
    /**
     * XML、JSON、HL7格式化返回
     */
    @RequestMapping(value = {"format"})
    @ResponseBody
    public AjaxDataResponseMessage format(String rawData) {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        try {
            String result;
            int dataFormat = StringUtils.checkDataFormat(rawData);
            if (dataFormat == StringUtils.DATA_FORMAT_XML) {
                result = XmlUtils.formatXML(rawData);
            } else if (dataFormat == StringUtils.DATA_FORMAT_JSON) {
                result = JsonUtils.format(rawData);
            } else {
                result = null;
            }

            if (StringUtils.isEmpty(result)) {
                responseMessage.setSuccess(false);
                responseMessage.setErrorMsg("格式化失败，可能消息格式有误，或不支持的格式(暂只支持XML、JSON)！");
            } else {
                responseMessage.setSuccess(true);
                responseMessage.setData(result);
            }
        } catch (Exception ex) {
            responseMessage.setSuccess(false);
            responseMessage.setErrorMsg("格式化失败，可能消息格式有误，或不支持的格式(暂只支持XML、JSON)！" + ex.getMessage());
        }

        return responseMessage;
    }

}