package com.winning.esb.model.biz.enums;

/**
 * ESB数据协议相关的枚举值
 *
 * @author xuehao
 */
public class EsbDataProtocal {
    /**
     * 结果代码
     */
    public enum AckCodeEnum {
        Success("100", "成功"),
        SuccessHasData("100.1", "成功（有数据）"),
        SuccessNoData("100.2", "成功（无数据）"),
        ClientError("200", "客户端错误"),
        ClientErrorRoot("200.1", "消息根节点错误(Request节点)"),
        ClientErrorHead("200.2", "消息头错误(Head节点)"),
        ClientErrorBody("200.3", "消息体错误(Body节点)"),
        ClientErrorVersion("200.4", "版本号缺失或错误"),
        ClientErrorParams("200.5", "参数错误"),
        ClientSecurityError("201", "客户端安全类错误"),
        ClientSecurityErrorUidPwd("201.1", "帐号或密码错误"),
        ClientSecurityErrorExpire("201.2", "帐号过期"),
        ServerError("300", "服务端错误"),
        ServerErrorSystem("300.1", "程序错误"),
        ServerErrorDatabase("300.2", "数据库执行错误"),
        Unknown("400", "未知错误");

        private String code;
        private String value;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        AckCodeEnum(String code, String value) {
            this.code = code;
            this.value = value;
        }

    }
}