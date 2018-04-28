package com.winning.test;

/**
 * Created by xuehao on 2017/11/29.
 */
public class Test1 {
    public static void main(String[] args) {
        String s = null;
        assert s != null ? true : false;
        assert false;
        System.out.println("end");
    }
}