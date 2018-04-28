package com.winning.esb.model.enums;

public class ValueListEnum {
    public enum TypeEnum {
        /**
         * 普通枚举值
         */
        Normal(0, "普通枚举值"),
        /**
         * 成功
         */
        Success(1, "成功"),
        /**
         * 失败
         */
        Failure(2, "失败"),
        /**
         * 成功正则表达式
         */
        SuccessRegex(3, "成功正则表达式"),
        /**
         * 失败正则表达式
         */
        FailureRegex(4, "失败正则表达式");

        private int code;
        private String value;

        private TypeEnum(int code, String value) {
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