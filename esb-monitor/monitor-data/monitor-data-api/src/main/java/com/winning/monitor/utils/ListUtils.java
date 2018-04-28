package com.winning.monitor.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by xuehao on 2017/7/27.
 */
public class ListUtils {
    public static List transferToList(Collection c) {
        List list = new ArrayList<>();
        list.addAll(c);
        return list;
    }

    public static boolean isEmpty(List list) {
        return (list == null || list.size() < 1) ? true : false;
    }
}