package com.winning.esb.model.enums;

import com.winning.esb.model.common.SimpleObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuehao on 2017/8/22.
 */
public class AppInfoEnum {
    public enum AppTypeEnum {
        /**
         * 普通
         */
        Normal(0, "普通"),
        /**
         * ESB
         */
        ESB(1, "ESB"),
        /**
         * 互联网
         */
        Internet(2, "互联网"),
        /**
         * 传统桌面程序
         */
        CS(3, "传统桌面程序(CS模式)"),
        /**
         * 院内网站
         */
        BS(4, "院内网站(BS模式)"),
        /**
         * 手机APP
         */
        APP(5, "手机APP");

        private int code;
        private String value;

        private AppTypeEnum(int code, String value) {
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
            AppInfoEnum.AppTypeEnum[] items = AppInfoEnum.AppTypeEnum.values();
            List<SimpleObject> simpleObjects = new ArrayList<>();
            SimpleObject simpleObject;
            for (AppInfoEnum.AppTypeEnum item : items) {
                simpleObject = new SimpleObject();
                simpleObject.setItem1(String.valueOf(item.getCode()));
                simpleObject.setItem2(item.getValue());
                simpleObjects.add(simpleObject);
            }
            return simpleObjects;
        }
    }

    public enum StatusEnum {
        /**
         * 已启用
         */
        Normal(1, "已启用"),
        /**
         * 已停用
         */
        Stop(0, "已停用");

        private int code;
        private String value;

        StatusEnum(int code, String value) {
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
            AppInfoEnum.StatusEnum[] items = AppInfoEnum.StatusEnum.values();
            List<SimpleObject> simpleObjects = new ArrayList<>();
            SimpleObject simpleObject;
            for (AppInfoEnum.StatusEnum item : items) {
                simpleObject = new SimpleObject();
                simpleObject.setItem1(String.valueOf(item.getCode()));
                simpleObject.setItem2(item.getValue());
                simpleObjects.add(simpleObject);
            }
            return simpleObjects;
        }
    }

    /**
     * 系统方向
     */
    public enum DirectionEnum {
        /**
         * 提供方|消费方
         */
        All(0, "提供方|消费方"),
        /**
         * 提供方
         */
        Provider(1, "提供方"),
        /**
         * 消费方
         */
        Consumer(2, "消费方");

        private int code;
        private String value;

        DirectionEnum(int code, String value) {
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
            AppInfoEnum.DirectionEnum[] items = AppInfoEnum.DirectionEnum.values();
            List<SimpleObject> simpleObjects = new ArrayList<>();
            SimpleObject simpleObject;
            for (AppInfoEnum.DirectionEnum item : items) {
                simpleObject = new SimpleObject();
                simpleObject.setItem1(String.valueOf(item.getCode()));
                simpleObject.setItem2(item.getValue());
                simpleObjects.add(simpleObject);
            }
            return simpleObjects;
        }
    }

    /**
     * 保留的业务系统代码字符
     */
    public enum ReservedCodeEnum {
        /**
         * ESB单元测试代码
         */
        EsbTestUnit("ESB-TestUnit"),
        /**
         * 单元测试的业务系统代码后缀（真实的业务系统代码不能使用）
         */
        TestUnit("-TestUnit"),
        /**
         * 集成测试的业务系统代码后缀（真实的业务系统代码不能使用）
         */
        TestFlow("-TestFlow");

        private String value;

        ReservedCodeEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        /**
         * 检测业务系统代码是否使用保留字符
         */
        public static String check(String code) {
            String err = "";
            if (code != null && code.trim().length() > 0) {
                if (EsbTestUnit.getValue().equalsIgnoreCase(code)) {
                    err += "“" + code + "”是系统保留，禁止使用！";
                } else if (code.toLowerCase().endsWith(TestUnit.getValue().toLowerCase())) {
                    err += "业务系统代码不能以“" + TestUnit.getValue() + "”为后缀！";
                } else if (code.toLowerCase().endsWith(TestFlow.getValue().toLowerCase())) {
                    err += "业务系统代码不能以“" + TestFlow.getValue() + "”为后缀！";
                }
            }
            return err;
        }
    }
}