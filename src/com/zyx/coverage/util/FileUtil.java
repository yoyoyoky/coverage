package com.zyx.coverage.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyouxuan on 2016/12/21.
 */
public class FileUtil {

    public static List<String> getDirs(String path) {
        List<String> files = new ArrayList<>();

        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            File[] temp = dir.listFiles();
            for (File f : temp) {
                if (f.isDirectory())
                    files.add(f.getName());
            }
        }
        return files;
    }
}
