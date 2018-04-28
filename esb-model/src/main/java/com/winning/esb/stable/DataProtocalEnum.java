package com.winning.esb.stable;

import java.util.ArrayList;
import java.util.List;

public class DataProtocalEnum {
    /**
     * 版本号
     */
    public enum Version {
        /**
         * 1.0
         */
        V_1_0("1.0"),
        /**
         * 1.1
         */
        V_1_1("1.1");

        private String code;

        private Version(String _code) {
            this.code = _code;
        }

        public String getCode() {
            return this.code;
        }

        //获取列表
        private static List<String> list = null;

        public static List<String> list() {
            if (list == null) {
                list = new ArrayList<String>();
                Version[] e = Version.values();
                for (int i = 0, len = e.length; i < len; i++) {
                    list.add(e[i].getCode());
                }
            }
            return list;
        }
    }

    /**
     * 传输类型
     */
    public enum TransferType {
        /**
         * 同步传输
         */
        Sync,
        /**
         * 异步传输
         */
        Async;

        //获取列表
        private static List<String> list = null;

        public static List<String> list() {
            if (list == null) {
                list = new ArrayList<String>();
                TransferType[] e = TransferType.values();
                for (int i = 0, len = e.length; i < len; i++) {
                    list.add(e[i].toString());
                }
            }
            return list;
        }
    }

    /**
     * 消息类型
     */
    public enum ContentType {
        /**
         * XML格式
         */
        TextXml("text/xml"),
        /**
         * JSON格式
         */
        TextJson("text/json"),
        /**
         * HL7格式
         */
        TextHL7("text/HL7"),
        /**
         * XML结果集形式
         */
        TableXml("table/xml"),
        /**
         * JSON结果集形式
         */
        TableTable("table/json");

        private String code;

        private ContentType(String _code) {
            this.code = _code;
        }

        public String getCode() {
            return this.code;
        }

        //获取列表
        private static List<String> list = null;

        public static List<String> list() {
            if (list == null) {
                list = new ArrayList<String>();
                ContentType[] e = ContentType.values();
                for (int i = 0, len = e.length; i < len; i++) {
                    list.add(e[i].getCode());
                }
            }
            return list;
        }
    }

    /**
     * 消息格式
     */
    public enum ContentEncoding {
        /**
         * GZIP压缩
         */
        GZIP("gzip");

        private String code;

        private ContentEncoding(String _code) {
            this.code = _code;
        }

        public String getCode() {
            return this.code;
        }

        //获取列表
        private static List<String> list = null;

        public static List<String> list() {
            if (list == null) {
                list = new ArrayList<String>();
                ContentEncoding[] e = ContentEncoding.values();
                for (int i = 0, len = e.length; i < len; i++) {
                    list.add(e[i].getCode());
                }
            }
            return list;
        }
    }

    /**
     * 客户端类型
     */
    public enum AppType {
        /**
         * 电脑
         */
        PC,
        /**
         * 平板
         */
        PD,
        /**
         * 手机
         */
        MP;

        //获取列表
        private static List<String> list = null;

        public static List<String> list() {
            if (list == null) {
                list = new ArrayList<String>();
                AppType[] e = AppType.values();
                for (int i = 0, len = e.length; i < len; i++) {
                    list.add(e[i].toString());
                }
            }
            return list;
        }
    }

    /**
     * 加密策略
     */
    public enum SecurityPolicy {
        /**
         * 使用AES加密算法
         * (CBC模式，密钥与位移相同，都是16位，Java语言补码方式为PKCS5Padding，
         * 对应.NET应该是PKCS7补码，且加密后的二进制数据转为base64编码方式传输)
         */
        AES;

        //获取列表
        private static List<String> list = null;

        public static List<String> list() {
            if (list == null) {
                list = new ArrayList<String>();
                SecurityPolicy[] e = SecurityPolicy.values();
                for (int i = 0, len = e.length; i < len; i++) {
                    list.add(e[i].toString());
                }
            }
            return list;
        }
    }

    /**
     * 结果代码
     */
    public enum AckCode {
        /**
         * 成功
         */
        Success("100", "成功"),
        /**
         * 成功（有数据）
         */
        SuccessData("100.1", "成功（有数据）"),
        /**
         * 成功（无数据）
         */
        SuccessNoData("100.2", "成功（无数据）"),
        /**
         * 客户端错误
         */
        ClientError("200", "客户端错误"),
        /**
         * 消息根节点错误(Request节点)
         */
        ClientErrorRequest("200.1", "消息根节点错误(Request节点)"),
        /**
         * 消息头错误(Head节点)
         */
        ClientErrorHead("200.2", "消息头错误(Head节点)"),
        /**
         * 消息体错误(Body节点)
         */
        ClientErrorBody("200.3", "消息体错误(Body节点)"),
        /**
         * 版本号缺失或错误
         */
        ClientErrorVersion("200.4", "版本号缺失或错误"),
        /**
         * 参数错误（具体错误详情见描述信息节点，可能包含多个参数错误）
         */
        ClientErrorParam("200.5", "参数错误（具体错误详情见描述信息节点 Head/AckMessage）"),
        /**
         * 客户端安全类错误
         */
        ClientSecurityError("201", "客户端安全类错误"),
        /**
         * 帐号或密码错误
         */
        ClientSecurityErrorIDPassword("201.1", "帐号或密码错误"),
        /**
         * 帐号过期
         */
        ClientSecurityErrorExpire("201.2", "帐号过期"),
        /**
         * 服务端错误
         */
        ServerSecurityError("300", "服务端错误"),
        /**
         * 程序错误（具体错误详情见描述信息节点）
         */
        ServerSecurityErrorProgram("300.1", "程序错误（具体错误详情见描述信息节点）"),
        /**
         * 数据库执行错误
         */
        ServerSecurityErrorDB("300.2", "数据库执行错误"),
        /**
         * 未知错误
         */
        UnexpectedError("400", "未知错误");

        private String code;
        private String name;

        private AckCode(String _code, String _name) {
            this.code = _code;
            this.name = _name;
        }

        public String getCode() {
            return this.code;
        }

        public String getName() {
            return this.name;
        }

        //获取列表
        private static List<String> list = null;

        public static List<String> list() {
            if (list == null) {
                list = new ArrayList<String>();
                AckCode[] e = AckCode.values();
                for (int i = 0, len = e.length; i < len; i++) {
                    list.add(e[i].getCode());
                }
            }
            return list;
        }

        //获取成功列表
        private static List<String> listSuccess = null;

        public static List<String> listSuccess() {
            if (listSuccess == null) {
                listSuccess = new ArrayList<String>();
                listSuccess.add(AckCode.Success.getCode());
                listSuccess.add(AckCode.SuccessData.getCode());
                listSuccess.add(AckCode.SuccessNoData.getCode());
            }
            return listSuccess;
        }
    }
}