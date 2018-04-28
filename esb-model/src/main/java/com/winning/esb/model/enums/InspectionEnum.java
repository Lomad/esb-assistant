package com.winning.esb.model.enums;

/**
 * Created by xuehao on 2017/8/24.
 */
public class InspectionEnum {
    public enum ResultEnum {
        /**
         * 未知.
         */
        Unknown(0, "未知"),
        /**
         * 正常
         */
        Success(1, "正常"),
        /**
         * 有错误
         */
        Failure(2, "有错误");

        private int code;
        private String value;

        private ResultEnum(int code, String value) {
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
}