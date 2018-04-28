package com.winning.esb.model.enums;

import com.winning.esb.model.common.SimpleObject;

import java.util.ArrayList;
import java.util.List;

public class GrantEnum {
    public enum ApproveStateEnum {
        /**
         * 申请中
         */
        Apply(0, "申请中"),
        /**
         * 已通过
         */
        Approved(1, "已通过"),
        /**
         * 已驳回
         */
        Refused(2, "已驳回");

        private int code;
        private String value;

        private ApproveStateEnum(int code, String value) {
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

        public static ApproveStateEnum getByCode(Integer code) {
            if (code != null) {
                ApproveStateEnum[] items = ApproveStateEnum.values();
                for (ApproveStateEnum item : items) {
                    if (item.getCode() == code.intValue()) {
                        return item;
                    }
                }
            }
            return null;
        }

        public static String getValueByCode(Integer code) {
            if (code != null) {
                ApproveStateEnum[] items = ApproveStateEnum.values();
                for (ApproveStateEnum item : items) {
                    if (item.getCode() == code.intValue()) {
                        return item.getValue();
                    }
                }
            }
            return null;
        }

        public static List<SimpleObject> getStatusList() {
            ApproveStateEnum[] items = ApproveStateEnum.values();
            List<SimpleObject> simpleObjects = new ArrayList<>();
            SimpleObject simpleObject;
            for (ApproveStateEnum item : items) {
                simpleObject = new SimpleObject();
                simpleObject.setItem1(String.valueOf(item.getCode()));
                simpleObject.setItem2(item.getValue());
                simpleObjects.add(simpleObject);
            }
            return simpleObjects;
        }
    }

    public enum ReleaseTypeEnum{
        /**
         * 授权码
         */
        LicKey("ESB_AppKeys","授权码"),
        /**
         * 密钥
         */
        SecretKey("ESB_SecretKeys","密钥"),
        /**
         * 服务地址
         */
        SvcUrl("ESB_SvcUrls","服务地址"),
        /**
         * token
         */
        Token("ESB_Tokens","token"),
        /**
         * 服务系统对应表
         */
        SvcApp("ESB_SvcApp","服务系统对应表"),
        /**
         * 测试token
         */
        Token_Test("ESB_Tokens_Test","token_test");


        String code;
        String value;

        private ReleaseTypeEnum(String code, String value){
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

}