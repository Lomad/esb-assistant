package com.winning.esb.model.enums;

import com.winning.esb.model.common.SimpleObject;

import java.util.ArrayList;
import java.util.List;

public class UserEnum {
    public enum RoleEnum {
        /**
         * 管理员
         */
        Admin(0, "管理员"),
        /**
         * 第三方业务系统用户
         */
        Normal(1, "第三方业务系统用户");

        private int code;
        private String value;

        private RoleEnum(int code, String value) {
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

        public static RoleEnum getByCode(Integer code) {
            if (code != null) {
                RoleEnum[] items = RoleEnum.values();
                for (RoleEnum item : items) {
                    if (item.getCode() == code.intValue()) {
                        return item;
                    }
                }
            }
            return null;
        }

        public static List<SimpleObject> getSimpleList() {
            RoleEnum[] items = RoleEnum.values();
            List<SimpleObject> simpleObjects = new ArrayList<>();
            SimpleObject simpleObject;
            for (RoleEnum item : items) {
                simpleObject = new SimpleObject();
                simpleObject.setItem1(String.valueOf(item.getCode()));
                simpleObject.setItem2(item.getValue());
                simpleObjects.add(simpleObject);
            }
            return simpleObjects;
        }
    }
}