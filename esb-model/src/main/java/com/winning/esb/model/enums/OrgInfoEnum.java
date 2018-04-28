package com.winning.esb.model.enums;

/**
 * Created by xuehao on 2017/12/18.
 */
public class OrgInfoEnum {
    public enum NameEnum {
        /**
         * 卫宁健康
         */
        Winning("卫宁健康"),
        /**
         * 医院信息平台
         */
        ESB("医院信息平台"),
        /**
         * 医院数据平台
         */
        Data("医院数据平台");

        private String value;
        private NameEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

}