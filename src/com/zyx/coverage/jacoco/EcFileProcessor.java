package com.zyx.coverage.jacoco;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyouxuan on 2016/10/8.
 */
public class EcFileProcessor {

    String DIR_PATH;

    public EcFileProcessor(String path) {
        this.DIR_PATH = path;
        if(!DIR_PATH.contains("finish")){
            File finishDir = new File(DIR_PATH + "/finish");
            if (!finishDir.exists())
                finishDir.mkdirs();
        }
    }

    public List<String> getAllEcName() {
        List<String> files = new ArrayList<>();

        File dir = new File(DIR_PATH);
        if (dir.exists() && dir.isDirectory()) {
            File[] temp = dir.listFiles();
            for (File f : temp) {
                if (f.getName().contains(".ec")) {
                    files.add(f.getName());
                }
            }
        }
        return files;
    }

    public static void main(String[] args) {
        EcFileProcessor processor = new EcFileProcessor("E:\\UnitTestSpace\\ReoportAnalysis\\CoverageEc");
        List<String> files = processor.getAllEcName();
        for (String f : files
                ) {
            System.out.println("------>" + f);
        }
    }
}
