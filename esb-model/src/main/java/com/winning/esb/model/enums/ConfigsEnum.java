package com.winning.esb.model.enums;

/**
 * Created by xuehao on 2017/12/11.
 */
public class ConfigsEnum {
    /**
     * 类型
     */
    public enum TypeEnum {
        /**
         * 数字
         */
        Number(2, "必须为数字", "^-?[0-9]*$"),
        /**
         * 日期时间
         */
        Datetime(3, "必须为日期时间格式，示例：2017-12-05 15:09:45", "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s((([0-1][0-9])|(2?[0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");

        private int code;
        private String regex;
        private String desp;

        private TypeEnum(int code, String desp, String regex) {
            this.code = code;
            this.desp = desp;
            this.regex = regex;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getRegex() {
            return regex;
        }

        public void setRegex(String regex) {
            this.regex = regex;
        }

        public String getDesp() {
            return desp;
        }

        public void setDesp(String desp) {
            this.desp = desp;
        }

        public static TypeEnum getRegex(Integer code) {
            TypeEnum regex = null;
            if (code != null) {
                TypeEnum[] items = TypeEnum.values();
                for (TypeEnum item : items) {
                    if (item.getCode() == code.intValue()) {
                        regex = item;
                        break;
                    }
                }
            }
            return regex;
        }
    }
}