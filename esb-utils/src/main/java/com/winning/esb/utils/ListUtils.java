package com.winning.esb.utils;

import java.util.*;

/**
 * Created by xuehao on 2017/7/27.
 */
public class ListUtils {
    public static List transferToList(Collection c) {
        if (c == null) {
            return null;
        }
        List list = new ArrayList<>();
        list.addAll(c);
        return list;
    }

    public static List transferToList(Set s) {
        if (s == null) {
            return null;
        }
        List list = new ArrayList<>();
        list.addAll(s);
        return list;
    }

    public static <T> List<T> transferToList(T t) {
        if (t == null) {
            return null;
        }
        List<T> list = new ArrayList<>();
        list.add(t);
        return list;
    }

    public static <T> List<T> transferToList(T[] t) {
        if (t == null) {
            return null;
        }
        List<T> list = Arrays.asList(t);
        return list;
    }

    public static <T> List<T> transferToList(T t, T t2) {
        if (t == null && t2 == null) {
            return null;
        }
        List<T> list = new ArrayList<>();
        if (t != null) {
            list.add(t);
        }
        if (t2 != null) {
            list.add(t2);
        }
        return list;
    }

    public static boolean isEmpty(List list) {
        return (list == null || list.size() < 1) ? true : false;
    }
}