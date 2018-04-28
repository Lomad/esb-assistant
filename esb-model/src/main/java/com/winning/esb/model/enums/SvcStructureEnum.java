package com.winning.esb.model.enums;

import com.winning.esb.model.common.SimpleObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuehao on 2017/8/9.
 */
public class SvcStructureEnum {
    /**
     * 树类型
     */
    public enum TreeTypeEnum {
        /**
         * 段
         */
        Segment("1", "段"),
        /**
         * 字段
         */
        Field("2", "字段"),
        /**
         * 组
         */
        Component("3", "组");

        private String code;
        private String value;

        private TreeTypeEnum(String code, String value) {
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
     * 返回类型
     */
    public enum ReturnTypeEnum {
        /**
         * 返回下载的URL地址
         */
        URL(0),
        /**
         * 返回消息数据
         */
        DATA(1);

        private int code;

        private ReturnTypeEnum(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

    /**
     * 参数方向
     */
    public enum DirectionEnum {
        /**
         * 请求消息
         */
        In(1, "请求消息"),
        /**
         * 应答消息
         */
        Ack(2, "应答消息");

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
            DirectionEnum[] items = DirectionEnum.values();
            List<SimpleObject> simpleObjects = new ArrayList<>();
            SimpleObject simpleObject;
            for (DirectionEnum item : items) {
                simpleObject = new SimpleObject();
                simpleObject.setItem1(String.valueOf(item.getCode()));
                simpleObject.setItem2(item.getValue());
                simpleObjects.add(simpleObject);
            }
            return simpleObjects;
        }

        public static DirectionEnum getByCode(Integer code) {
            if (code != null) {
                DirectionEnum[] items = DirectionEnum.values();
                for (DirectionEnum item : items) {
                    if (item.getCode() == code.intValue()) {
                        return item;
                    }
                }
            }
            return null;
        }
    }

    /**
     * 是否可编辑
     */
    public enum CanEditEnum {
        /**
         * 是
         */
        Yes(1, "是"),
        /**
         * 否
         */
        No(0, "否");

        private int code;
        private String value;

        private CanEditEnum(int code, String value) {
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
     * 是否属性节点
     */
    public enum IsAttrEnum {
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

        private IsAttrEnum(int code, String value) {
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
            IsAttrEnum[] items = IsAttrEnum.values();
            List<SimpleObject> simpleObjects = new ArrayList<>();
            SimpleObject simpleObject;
            for (IsAttrEnum item : items) {
                simpleObject = new SimpleObject();
                simpleObject.setItem1(String.valueOf(item.getCode()));
                simpleObject.setItem2(item.getValue());
                simpleObjects.add(simpleObject);
            }
            return simpleObjects;
        }
    }

    /**
     * 是否必须
     */
    public enum RequiredEnum {
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

        private RequiredEnum(int code, String value) {
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
            RequiredEnum[] items = RequiredEnum.values();
            List<SimpleObject> simpleObjects = new ArrayList<>();
            SimpleObject simpleObject;
            for (RequiredEnum item : items) {
                simpleObject = new SimpleObject();
                simpleObject.setItem1(String.valueOf(item.getCode()));
                simpleObject.setItem2(item.getValue());
                simpleObjects.add(simpleObject);
            }
            return simpleObjects;
        }
    }

    /**
     * 是否循环
     */
    public enum IsLoopEnum {
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

        private IsLoopEnum(int code, String value) {
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
            IsLoopEnum[] items = IsLoopEnum.values();
            List<SimpleObject> simpleObjects = new ArrayList<>();
            SimpleObject simpleObject;
            for (IsLoopEnum item : items) {
                simpleObject = new SimpleObject();
                simpleObject.setItem1(String.valueOf(item.getCode()));
                simpleObject.setItem2(item.getValue());
                simpleObjects.add(simpleObject);
            }
            return simpleObjects;
        }
    }

    /**
     * 生成的消息节点内容
     */
    public enum ValueTypeEnum {
        /**
         * 空值
         */
        Empty(0, "空值"),
        /**
         * 以节点名称填充
         */
        Name(1, "以节点名称填充"),
        /**
         * 生成模拟值填充
         */
        VistualValue(2, "生成模拟值填充");

        private int code;
        private String value;

        private ValueTypeEnum(int code, String value) {
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

        /**
         * 判断代码是否存在
         */
        public static boolean existCode(Integer code) {
            if (code != null) {
                ValueTypeEnum[] items = ValueTypeEnum.values();
                for (ValueTypeEnum item : items) {
                    if (item.getCode() == code.intValue()) {
                        return true;
                    }
                }
            }
            return false;
        }

    }

    /**
     * 是否结果标志
     */
    public enum ResultMarkEnum {
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

        private ResultMarkEnum(int code, String value) {
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
            ResultMarkEnum[] items = ResultMarkEnum.values();
            List<SimpleObject> simpleObjects = new ArrayList<>();
            SimpleObject simpleObject;
            for (ResultMarkEnum item : items) {
                simpleObject = new SimpleObject();
                simpleObject.setItem1(String.valueOf(item.getCode()));
                simpleObject.setItem2(item.getValue());
                simpleObjects.add(simpleObject);
            }
            return simpleObjects;
        }
    }

    /**
     * 数据类型
     */
    public enum DataTypeEnum {
        /**
         * 对象
         */
        Complex(99999999, "对象", ""),
        /**
         * 字符串
         */
        Strings(12, "字符串", "^.{1,}$"),
        /**
         * 数字
         */
        Number(8, "数字", "^[0-9]*$"),
        /**
         * 格式：2017-09-13
         */
        Date(91, "日期", "^((((19|20)(([02468][048])|([13579][26]))-02-29))|((20[0-9][0-9])|(19[0-9][0-9]))-((((0[1-9])|(1[0-2]))-((0[1-9])|(1\\\\d)|(2[0-8])))|((((0[13578])|(1[02]))-31)|(((01,3-9])|(1[0-2]))-(29|30)))))$ "),
        /**
         * 格式：08:46:35
         */
        Time(92, "时间", "^(?:[01]\\d|2[0-3])(?::[0-5]\\d){2}$ "),
        /**
         * 格式：2017-09-13 08:46:35
         */
        Datetime(93, "日期时间", "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s((([0-1][0-9])|(2?[0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$"),

        HL7Datetime(100001, "HL7日期时间", "^((?:19|20)\\d\\d)(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])(0\\d|1\\d|2[0-3])(0\\d|[1-5]\\d)(0\\d|[1-5]\\d)$");

        private int code;
        private String value;
        private String regex;

        private DataTypeEnum(int code, String value, String regex) {
            this.code = code;
            this.value = value;
            this.regex = regex;
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

        public String getRegex() {
            return regex;
        }

        public void setRegex(String regex) {
            this.regex = regex;
        }

        /**
         * 判断代码是否存在
         */
        public static boolean existCode(Integer code) {
            if (code != null) {
                DataTypeEnum[] items = DataTypeEnum.values();
                for (DataTypeEnum item : items) {
                    if (item.getCode() == code.intValue()) {
                        return true;
                    }
                }
            }
            return false;
        }

        public static List<SimpleObject> getSimpleList() {
            DataTypeEnum[] items = DataTypeEnum.values();
            List<SimpleObject> simpleObjects = new ArrayList<>();
            SimpleObject simpleObject;
            for (DataTypeEnum item : items) {
                simpleObject = new SimpleObject();
                simpleObject.setItem1(String.valueOf(item.getCode()));
                simpleObject.setItem2(item.getValue());
                simpleObjects.add(simpleObject);
            }
            return simpleObjects;
        }


        public static String getRegex(Integer code) {
            String regex = null;
            if (code != null) {
                DataTypeEnum[] items = DataTypeEnum.values();
                for (DataTypeEnum item : items) {
                    if (item.getCode() == code.intValue()) {
                        regex = item.getRegex();
                    }
                }
            }
            return regex;
        }
    }
}