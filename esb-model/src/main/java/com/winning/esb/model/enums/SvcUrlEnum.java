package com.winning.esb.model.enums;

import com.winning.esb.model.common.SimpleObject;

import java.util.ArrayList;
import java.util.List;

public class SvcUrlEnum {
    public enum SvcTypeEnum {
        /**
         * WebService
         */
        Ws(0, "Webservice"),
        /**
         * Restful
         */
        Rest(1, "Restful"),
        /**
         * Socket
         */
        Socket(2, "Socket(TCP)"),
        /**
         * XSocket
         */
        XSocket(3,"XSocket");
        private int code;
        private String value;

        SvcTypeEnum(int code, String value) {
            this.code = code;
            this.value = value;
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
            SvcTypeEnum[] items = SvcTypeEnum.values();
            List<SimpleObject> simpleObjects = new ArrayList<>();
            SimpleObject simpleObject;
            for (SvcTypeEnum item : items) {
                if(XSocket.equals(item)) {
                    continue;
                }
                simpleObject = new SimpleObject();
                simpleObject.setItem1(String.valueOf(item.getCode()));
                simpleObject.setItem2(item.getValue());
                simpleObjects.add(simpleObject);
            }
            return simpleObjects;
        }

        public static String getValueByCode(Integer code) {
            if(code !=null) {
                SvcTypeEnum[] items = SvcTypeEnum.values();
                for (SvcTypeEnum item : items) {
                    if (item.getCode() == code.intValue()) {
                        return item.getValue();
                    }
                }
            }
            return null;
        }
    }

    public enum StatusEnum {
        /**
         * 异常
         */
        Stop(0, "异常"),
        /**
         * 正常
         */
        Start(1, "正常");

        private int code;
        private String value;
        private StatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
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
    }

    /**
     * ESB代理标志
     */
    public enum EsbAgentEnum {
        /**
         * 否
         */
        No(0, "否"),
        /**
         * 是
         */
        Yes(1, "是");

        private int code;
        private String value;
        private EsbAgentEnum(int code, String value) {
            this.code = code;
            this.value = value;
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
            EsbAgentEnum[] items = EsbAgentEnum.values();
            List<SimpleObject> simpleObjects = new ArrayList<>();
            SimpleObject simpleObject;
            for (EsbAgentEnum item : items) {
                simpleObject = new SimpleObject();
                simpleObject.setItem1(String.valueOf(item.getCode()));
                simpleObject.setItem2(item.getValue());
                simpleObjects.add(simpleObject);
            }
            return simpleObjects;
        }
    }
}