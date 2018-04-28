package com.winning.esb.api;

import com.alibaba.fastjson.JSONObject;
import com.winning.esb.model.InspectionSysModel;
import com.winning.esb.model.SvcInfoModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.service.IInspectionSysService;
import com.winning.esb.service.ISvcInfoService;
import com.winning.esb.utils.JsonUtils;
import com.winning.esb.utils.ListUtils;
import com.winning.esb.utils.StringUtils;
import com.winning.webapp.framework.core.api.response.message.AjaxDataResponseMessage;
import com.winning.webapp.framework.core.api.response.message.AjaxResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wwh on 2017/12/06.
 */
@Controller
public class PublicApiReceiveException implements IPublicApiBiz {
    @Autowired
    private IInspectionSysService inspectionSysService;
    @Autowired
    private ISvcInfoService svcInfoService;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 接收ESB发来的消息
     */
    @Override
    public AjaxResponseMessage handle(Object obj) throws Exception {
        AjaxDataResponseMessage responseMessage = new AjaxDataResponseMessage();
        //获取输入消息
        String result;
        if (obj == null) {
            result = "请求为空";
        } else if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            String tranCode = jsonObject.getString("tranCode");
            result = JsonUtils.toJson(obj);
            if (!StringUtils.isEmpty(tranCode)) {
                List<SvcInfoModel> list = svcInfoService.getByCode(tranCode);
                if (!ListUtils.isEmpty(list)) {
                    Integer aid = list.get(0).getAid();
                    if (aid != null) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("aid", aid);
                        String date = simpleDateFormat.format(new Date());
                        map.put("check_time", date);
                        map.put("check_type", 1);
                        CommonObject commonObject = inspectionSysService.query(map);
                        if (commonObject.getTotalSize() == 0) {
                            InspectionSysModel model = new InspectionSysModel();
                            model.setAid(aid);
                            model.setCheck_type(2);
                            String exception = jsonObject.getString("exception");
                            model.setCheck_desp(exception);
                            model.setCheck_time(simpleDateFormat.parse(date));
                            model.setResult_type(0);
                            inspectionSysService.insert(model);
                        }
                    }
                }
            }
        } else {
            result = "msg不是json！";
        }

        responseMessage.setSuccess(true);
        responseMessage.setData(result);
        return responseMessage;
    }
}
