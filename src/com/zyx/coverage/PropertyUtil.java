package com.zyx.coverage;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by zhangyouxuan on 2016/10/12.
 */
public class PropertyUtil {

    private Properties property = new Properties();
    private String filepath = "local.properties";

    public PropertyUtil(String path) {
        try {
            filepath = path;
            property.load(new InputStreamReader(new FileInputStream(filepath), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get(String key) {
        return property.getProperty(key);
    }

}
