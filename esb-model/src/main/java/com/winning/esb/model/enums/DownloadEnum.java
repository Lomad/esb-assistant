package com.winning.esb.model.enums;

import com.winning.esb.model.common.SimpleObject;

import java.util.ArrayList;
import java.util.List;

public class DownloadEnum {

    public enum SvcDirectionEnum {
        /**
         * 提供
         */
        Provided(1, "提供"),
        /**
         * 订阅
         */
        Subscription(2, "订阅");

        private int code;
        private String value;

        SvcDirectionEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }

        public static List<SimpleObject> getSimpleList() {
            SvcDirectionEnum[] items = SvcDirectionEnum.values();
            List<SimpleObject> simpleObjects = new ArrayList<>();
            SimpleObject simpleObject;
            for (SvcDirectionEnum item : items) {
                simpleObject = new SimpleObject();
                simpleObject.setItem1(String.valueOf(item.getCode()));
                simpleObject.setItem2(item.getValue());
                simpleObjects.add(simpleObject);
            }
            return simpleObjects;
        }
    }

}