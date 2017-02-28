package com.zyx.coverage.jacoco;

/**
 * Created by zhangyouxuan on 2016/10/8.
 */
public class PropertiesWriter {

    String PROPERTIES_PATH;

    public PropertiesWriter(String path) {
        this.PROPERTIES_PATH = path;
    }

    public void setReportPath(final String ecPath, final String outPath) {

//        String filePath = "E:/ReoportAnalysis/local.properties"; // 文件路径

        IFileLineModify lineModify = new IFileLineModify() {

            @Override
            public String properties(String line) {
                if (line.startsWith("ecDir = ")) {
                    System.out.println("ecDir = " + ecPath);
                    return "ecDir = " + ecPath.replace("\\","\\\\");
                } else if (line.startsWith("reportsDir = ")) {
                    System.out.println("reportsDir = " + outPath);
                    return "reportsDir = " + outPath.replace("\\","\\\\");
                }
                return line;
            }
        };

        FileModify obj = new FileModify();
        obj.write(PROPERTIES_PATH, obj.read(PROPERTIES_PATH, lineModify)); // 读取修改文件

    }
}
