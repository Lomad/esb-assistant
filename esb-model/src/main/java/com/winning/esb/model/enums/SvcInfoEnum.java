package com.winning.esb.model.enums;

import com.winning.esb.model.common.SimpleObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuehao on 2017/8/9.
 */
public class SvcInfoEnum {

    /**
     * 下载类型
     */
    public enum DownloadTypeEnum {
        //下载选中
        Segment("0", "下载选中"),
        //下载全部
        Field("1", "下载全部");

        private String code;
        private String value;
        private DownloadTypeEnum(String code, String value) {
            this.code = code;
            this.value = value;
        }

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
    }

    /**
     * 消息格式
     */
    public enum MsgTypeEnum {
        //xml
        XML("XML", "XML"),
        //json
        JSON("JSON", "JSON"),
        //HL7
        HL7("HL7", "HL7");

        private String code;
        private String value;
        private MsgTypeEnum(String code, String value) {
            this.code=code;
            this.value=value;
        }
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

        public static List<SimpleObject> getSimpleList() {
            MsgTypeEnum[] items = MsgTypeEnum.values();
            List<SimpleObject> simpleObjects = new ArrayList<>();
            SimpleObject simpleObject;
            for (MsgTypeEnum item : items) {
                simpleObject = new SimpleObject();
                simpleObject.setItem1(item.getCode());
                simpleObject.setItem2(item.getValue());
                simpleObjects.add(simpleObject);
            }
            return simpleObjects;
        }
    }

    /**
     * 数据协议
     */
    public enum DataProtocalEnum {
        /**
         * <TEMPLATE_CONTENT>: 无
         */
        No(0, "无", ""),
        /**
         * <TEMPLATE_CONTENT>: 模版中的内容占位符
         */
        ESB_1_1(3, "集成平台1.1", "/template-file/data-protocal/esb_1_1"),
        /**
         * <TEMPLATE_CONTENT>: 模版中的内容占位符
         */
        ESB_1_0(2, "集成平台1.0", "/template-file/data-protocal/esb_1_0");

        private int code;
        private String value;
        /**
         * 模版路径
         */
        private String path;
        private DataProtocalEnum(int code, String value, String path) {
            this.code = code;
            this.value = value;
            this.path = path;
        }

        public int getCode() {
            return code;
        }
        public void setCode(int code) {
            this.code = code;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
        public String getPath() {
            return path;
        }
        public void setPath(String path) {
            this.path = path;
        }

        /**
         * 获取数据协议所在的路径
         */
        public static String getPath(Integer code) {
            if (code != null) {
                DataProtocalEnum[] items = DataProtocalEnum.values();
                for (DataProtocalEnum item : items) {
                    if (code.intValue() == item.getCode()) {
                        return item.getPath();
                    }
                }
            }
            return null;
        }

        public static List<SimpleObject> getSimpleList() {
            DataProtocalEnum[] items = DataProtocalEnum.values();
            List<SimpleObject> simpleObjects = new ArrayList<>();
            SimpleObject simpleObject;
            for (DataProtocalEnum item : items) {
                simpleObject = new SimpleObject();
                simpleObject.setItem1(String.valueOf(item.getCode()));
                simpleObject.setItem2(item.getValue());
                simpleObjects.add(simpleObject);
            }
            return simpleObjects;
        }
    }

    /**
     * 状态
     */
    public enum StatusEnum {
        /**
         * 未发布
         */
        Unpublished(0, "未发布"),
        /**
         * 已发布
         */
        Published(1, "已发布"),
        /**
         * 已下线
         */
        Rollbacked(2, "已下线");

        private int code;
        private String value;
        StatusEnum(int code, String value) {
            this.code=code;
            this.value=value;
        }
        public int getCode() {
            return code;
        }
        public void setCode(int code) {
            this.code = code;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }

        public static List<SimpleObject> getSimpleList() {
            StatusEnum[] items = StatusEnum.values();
            List<SimpleObject> simpleObjects = new ArrayList<>();
            SimpleObject simpleObject;
            for (StatusEnum item : items) {
                simpleObject = new SimpleObject();
                simpleObject.setItem1(String.valueOf(item.getCode()));
                simpleObject.setItem2(item.getValue());
                simpleObjects.add(simpleObject);
            }
            return simpleObjects;
        }
    }

    /**
     * 其他标志
     */
    public enum OtherMarkEnum {
        /**
         * 无
         */
        No(0, "无"),
        /**
         * 使用卫宁CDR公共接口token
         */
        WinningCdrApiToken(1, "使用卫宁CDR公共接口token");

        private int code;
        private String value;
        OtherMarkEnum(int code, String value) {
            this.code=code;
            this.value=value;
        }
        public int getCode() {
            return code;
        }
        public void setCode(int code) {
            this.code = code;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }

        public static List<SimpleObject> getSimpleList() {
            OtherMarkEnum[] items = OtherMarkEnum.values();
            List<SimpleObject> simpleObjects = new ArrayList<>();
            SimpleObject simpleObject;
            for (OtherMarkEnum item : items) {
                simpleObject = new SimpleObject();
                simpleObject.setItem1(String.valueOf(item.getCode()));
                simpleObject.setItem2(item.getValue());
                simpleObjects.add(simpleObject);
            }
            return simpleObjects;
        }
    }

}