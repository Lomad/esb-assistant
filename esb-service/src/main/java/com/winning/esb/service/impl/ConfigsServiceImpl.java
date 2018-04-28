package com.winning.esb.service.impl;

import com.winning.esb.dao.IConfigsDao;
import com.winning.esb.model.ConfigsModel;
import com.winning.esb.model.common.CommonObject;
import com.winning.esb.model.enums.ConfigsCodeConst;
import com.winning.esb.model.enums.ConfigsEnum;
import com.winning.esb.service.IConfigsService;
import com.winning.esb.service.taskmark.SyncToEsbMark;
import com.winning.esb.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuehao on 17/07/27.
 */
@Service
public class ConfigsServiceImpl implements IConfigsService {
    @Autowired
    private IConfigsDao dao;

    @Override
    public CommonObject query(Map map) {
        return dao.query(map);
    }

    @Override
    public void insert(ConfigsModel obj) {
        dao.insert(obj);
        //同步API的URL
        if (ConfigsCodeConst.ESBUrl.equals(obj.getCode())) {
            SyncToEsbMark.setSyncApiUrl(true);
        }
    }

    @Override
    public String editValue(ConfigsModel obj) {
        String err = null;
        String code = obj.getCode();
        String regex = null;
        ConfigsEnum.TypeEnum typeEnum = null;
        //获取配置对象
        if (!StringUtils.isEmpty(code) && StringUtils.isEmpty(obj.getRegex())) {
            ConfigsModel configsModel = getByCode(code);
            if (configsModel != null) {
                regex = configsModel.getRegex();
            }
            //如果配置对象中不存在正则验证，则尝试使用公共的正则验证
            if (StringUtils.isEmpty(regex)) {
                typeEnum = ConfigsEnum.TypeEnum.getRegex(configsModel.getType());
                if (typeEnum != null) {
                    regex = typeEnum.getRegex();
                }
            }
        }
        //使用正则表达式验证
        if (!StringUtils.isEmpty(regex) && !RegexUtils.match(regex, obj.getValue())) {
            if (typeEnum != null) {
                err = typeEnum.getDesp();
            } else {
                err = "参数的取值不符合要求(参见描述)！";
            }
        }
        //更新数据库
        if (StringUtils.isEmpty(err)) {
            err = dao.editValue(obj);
        }
        //同步API的URL
        if (ConfigsCodeConst.ESBUrl.equals(obj.getCode())) {
            SyncToEsbMark.setSyncApiUrl(true);
        }
        return err;
    }

    @Override
    public ConfigsModel getByCode(String code) {
        List<ConfigsModel> list = dao.getByCode(code);
        return ListUtils.isEmpty(list) ? null : list.get(0);
    }

    @Override
    public List<ConfigsModel> getByCode(List<String> codeList) {
        return dao.getByCode(codeList);
    }

    @Override
    public String getValueByCode(String code) {
        ConfigsModel configsModel = getByCode(code);
        return (configsModel == null) ? null : configsModel.getValue();
    }

    @Override
    public Map<String, String> getValueByCode(List<String> codeList) {
        List<ConfigsModel> list = getByCode(codeList);
        if (ListUtils.isEmpty(list)) {
            return null;
        } else {
            Map<String, String> result = new HashMap<>();
            for (ConfigsModel item : list) {
                result.put(item.getCode(), item.getValue());
            }
            return result;
        }
    }

    @Override
    public void delete(String code) {
        dao.delete(code);
    }

    @Override
    public int getMonitorOverviewShowSysUpper() {
        String configValueStr = getValueByCode(ConfigsCodeConst.MONITOR_OVERVIEW_SHOW_SYS_UPPER);
        int configValue = !StringUtils.isEmpty(configValueStr) ? Integer.parseInt(configValueStr) : Integer.MAX_VALUE;
        if (configValue <= 0) {
            configValue = Integer.MAX_VALUE;
        }
        return configValue;
    }


    @Override
    public int getMonitorOverviewShowSysNoData() {
        String configValueStr = getValueByCode(ConfigsCodeConst.MONITOR_OVERVIEW_SHOW_SYS_NO_DATA);
        int configValue = !StringUtils.isEmpty(configValueStr) ? Integer.parseInt(configValueStr)
                : ConfigsCodeConst.MONITOR_OVERVIEW_SHOW_SYS_NO_DATA_ENUM.getDefault();
        return configValue;
    }

    @Override
    public String getEsbTestUrl() {
        String configValueStr = getValueByCode(ConfigsCodeConst.ESBTestUrl);
        return configValueStr;
    }

    @Transactional
    @Override
    public String save(Map map) {
        String err = "";
        //ESB类型
        String type = (String) map.get("ESB_Type");
        if (!StringUtils.isEmpty(type)) {
            ConfigsModel configsModel = getByCode(ConfigsCodeConst.ESBType);
            if (configsModel != null) {
                configsModel.setValue(type);
                err += editValue(configsModel);
            }
        }

        //ESB地址
        String ipAddress = (String) map.get("ESB_IP");
        String port = (String) map.get("ESB_Port");
        String userName = (String) map.get("ESB_UserName");
        String password = (String) map.get("ESB_Password");
        if (StringUtils.isEmpty(ipAddress)) {
            err += " 中间件IP地址不能为空! ";
        } else if (!NetUtils.isIP(ipAddress)) {
            err += "中间件IP地址格式不正确! ";
        }
        if (StringUtils.isEmpty(port)) {
            err += " 中间件端口号不能为空! ";
        } else if (!NetUtils.isIP(ipAddress)) {
            err += "中间件端口号格式不正确! ";
        }
        if (StringUtils.isEmpty(userName)) {
            err += " 中间件用户名不能为空! ";
        }
        if (StringUtils.isEmpty(password)) {
            err += " 中间件IP地址不能为空! ";
        }
        if (StringUtils.isEmpty(err)) {
            ConfigsModel configsModel = getByCode(ConfigsCodeConst.ESBUrl);
            if (configsModel != null) {
                String url = ipAddress + "," + port + "," + userName + "," + password;
                configsModel.setValue(url);
                editValue(configsModel);
            }
        }

        //ESB测试地址
        if (!StringUtils.isEmpty(ipAddress)) {
            ConfigsModel configsModel = getByCode(ConfigsCodeConst.ESBTestUrl);
            if (configsModel != null) {
                String testUrl = "http://" + ipAddress + ":21300/esbTest";
                boolean flag = NetUtils.checkHttp(testUrl);
                if (flag == false) {
                    err += "ESB测试地址不可用！请联系平台实施工程师修改! ";
                } else {
                    configsModel.setValue(testUrl);
                    editValue(configsModel);
                }
            }
        }
        return err;
    }

    @Override
    public String getMiddlewareInfo() {
        Map<String, Object> map = new HashMap<>(10);
        ConfigsModel configsModel;
        configsModel = getByCode(ConfigsCodeConst.ESBType);
        if (configsModel != null) {
            String esb_Type = configsModel.getValue();
            if (!StringUtils.isEmpty(esb_Type)) {
                map.put("ESB_Type", esb_Type);
            }
        }
        configsModel = getByCode(ConfigsCodeConst.ESBUrl);
        if (configsModel != null) {
            String esb_Url = configsModel.getValue();
            if (!StringUtils.isEmpty(esb_Url)) {
                String[] parms = esb_Url.split(",");
                map.put("ESB_IP", parms[0]);
                map.put("ESB_Port", parms[1]);
                map.put("ESB_UserName", parms[2]);
                map.put("ESB_Password", parms[3]);
            }
        }
        String jsonStr = JsonUtils.mapToJson(map);
        return jsonStr;
    }
}