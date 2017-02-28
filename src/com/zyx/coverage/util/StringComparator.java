package com.zyx.coverage.util;

import java.util.Comparator;

/**
 * Created by zhangyouxuan on 2017/2/8.
 */
public class StringComparator implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        String str1 = (String) o1;
        String str2 = (String) o2;
        if(str1.contains("_") && str2.contains("_")){
            return Integer.valueOf(str1.substring(0,str1.indexOf("_"))) - Integer.valueOf(str2.substring(0,str2.indexOf("_")));
        }
        return str1.compareTo(str2);
    }
}
