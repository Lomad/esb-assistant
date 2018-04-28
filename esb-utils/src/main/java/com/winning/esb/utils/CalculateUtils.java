package com.winning.esb.utils;

import java.math.BigDecimal;

/**
 * 主要用于数据计算
 */
public class CalculateUtils {
    /**
     * 加法
     */
    public static double add(double var1, double var2) {
        BigDecimal b1 = new BigDecimal(Double.toString(var1));
        BigDecimal b2 = new BigDecimal(Double.toString(var2));
        return b1.add(b2).doubleValue();
    }

    /**
     * 减法
     */
    public static double sub(double var1, double var2) {
        BigDecimal b1 = new BigDecimal(Double.toString(var1));
        BigDecimal b2 = new BigDecimal(Double.toString(var2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 乘法
     */
    public static double mul(double var1, double var2) {
        BigDecimal b1 = new BigDecimal(Double.toString(var1));
        BigDecimal b2 = new BigDecimal(Double.toString(var2));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 除法
     * @param scale 精度，到小数点后几位
     */
    public static double div(Object v1, Object v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("精度不能小于0");
        }
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 四舍五入
     * @param scale 精确位数
     */
    public static double round(Object v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("精度不能小于0");
        }
        return div(v, 1, scale);
    }
}