package com.winning.esb.model.enums;

/**
 * Created by xuehao on 2017/8/16.
 * 配置参数代码
 */
public class ConfigsCodeConst {
    /**
     * HL7Engine的数据库
     */
    public static final String DB_HL7Engine = "0001";

    /**
     * 监控概览显错下限值（监控概览页星状图，如果错误数低于配置值，则显示为正常状态，超过或等于才会显示为错误状态）
     */
    public static final String MONITOR_OVERVIEW_SHOW_ERROR_LOWER = "0002";

    /**
     * 监控概览显示的系统数量最大值
     */
    public static final String MONITOR_OVERVIEW_SHOW_SYS_UPPER = "0003";

    /**
     * 监控概览显示无数据的系统
     */
    public static final String MONITOR_OVERVIEW_SHOW_SYS_NO_DATA = "0004";
    /**
     * 监控概览显示无数据的系统【枚举值】
     */
    public enum MONITOR_OVERVIEW_SHOW_SYS_NO_DATA_ENUM {
        /**
         * 隐藏
         */
        Hide(0),
        /**
         * 显示
         */
        Show(1);

        private int code;
        MONITOR_OVERVIEW_SHOW_SYS_NO_DATA_ENUM(int code) {
            this.code = code;
        }
        public int getCode() {
            return code;
        }

        /**
         * 获取默认值
         */
        public static int getDefault() {
            return Show.getCode();
        }
    }

    /**
     * HIS_MZ的数据库
     */
    public static final String DB_HISMZ = "0006";

    /**
     * HIS_ZY的数据库
     */
    public static final String DB_HISZY = "0007";

    /**
     * ESBUrl
     */
    public static final String ESBUrl = "0008";

    /**
     * ESBType
     */
    public static final String ESBType = "0009";

    /**
     * 系统启动时间（系统或ESB的系统时间）
     */
    public static final String StartTime = "0010";

    /**
     * 监控中的历史调用总量
     */
    public static final String MonitorHistoryCallCount = "0011";
    /**
     * 监控数据清理周期
     */
    public static final String MonitorClearPeriod = "0012";

    /**
     * ODR数据库连接
     */
    public static final String DB_ODR = "0013";
    /**
     * CDR数据库连接
     */
    public static final String DB_CDR = "0014";
    /**
     * EMPI数据库连接
     */
    public static final String DB_EMPI = "0015";
    /**
     * MDM数据库连接
     */
    public static final String DB_MDM = "0016";
    /**
     * 互联互通数据库连接
     */
    public static final String DB_HLHT = "0017";
    /**
     * 数据仓库(DW)数据库连接
     */
    public static final String DB_DW = "0018";
    /**
     * CM数据库连接
     */
    public static final String DB_CM = "0019";
    /**
     * BU数据库连接（这是琚晨大屏，数据中心监控里的一个指标，是邵总那边提供的，说是用于HIS查询）
     * 2018-01-09：殷奇隆增加
     */
    public static final String DB_BU = "0020";
    /**
     * ESB测试地址：主要用于管理平台与ESB的测试交互（restful地址）
     * 2018-01-09：薛浩增加
     */
    public static final String ESBTestUrl = "0021";

}