package com.zyx.coverage;

import com.zyx.coverage.jacoco.EcFileProcessor;
import com.zyx.coverage.jacoco.PropertiesWriter;
import com.zyx.coverage.xls.ReportGenerator;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        PropertyUtil property = new PropertyUtil("local.properties");
        boolean isRecord = Boolean.parseBoolean(property.get("isRecord"));
        boolean isGenerateExcel = Boolean.parseBoolean(property.get("isGenerateExcel"));
        boolean isGenerateJacoco = Boolean.parseBoolean(property.get("isGenerateJacoco"));
//        boolean isRecord = false;
//        boolean isGenerateJacoco = false;

        File file = new File("local.properties");
        String reportRoot = file.getAbsolutePath().substring(0, file.getAbsolutePath().indexOf("local.properties") - 1).replace("\\", "/");
//        String reportRoot = "E:\\UnitTestSpace\\ReoportAnalysis";
        String ecFileDir = reportRoot + "/CoverageEc" + (isGenerateJacoco ? "" : "/finish");
        String APP="";
        String REPORT_PATH;
        String CASE_ID;
        String XLS_NAME = isRecord ? "Coverage_DataBase.xls" : "Coverage_Test.xls";

        ReportGenerator generator;
        PropertiesWriter propertiesWriter = new PropertiesWriter("local.properties");
        EcFileProcessor processor = new EcFileProcessor(ecFileDir);
        List<String> files = processor.getAllEcName();

        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).contains("_")) {
                APP = files.get(i).substring(0, files.get(i).indexOf("_"));
                CASE_ID = files.get(i).substring(files.get(i).indexOf("_") + 1, files.get(i).indexOf(".ec"));
                XLS_NAME = isRecord ? "Coverage_" + APP + "_DataBase.xls" : "Coverage_" + APP + "_Test.xls";
            } else {
                CASE_ID = files.get(i).substring(0, files.get(i).indexOf(".ec"));
            }

            //非录制情况，报告路径不包含案例名
            REPORT_PATH = isRecord ? reportRoot + "/" + APP + "/" + CASE_ID : reportRoot + "/" + APP;

            //修改配置文件ec文件及报告路径，供生成jacoco报告使用
            propertiesWriter.setReportPath(isRecord ? ecFileDir + "/" + files.get(i) : ecFileDir, REPORT_PATH);

            //生成jacoco报告
            generateJacoco(isGenerateJacoco);

            //生成excel统计表
            if(isGenerateExcel){
                generator = new ReportGenerator(REPORT_PATH + "/jacocoTestReport/html/", reportRoot + "/" + APP, XLS_NAME);
                generator.setCaseId(isRecord ? CASE_ID : files.toString().replace(APP + "_", "").replace(".ec", "").replace(", ", ",\n"));//非录制情况，案例名显示所有案例
                generator.output();
            }

            //非录制案例库情况，只需要生成1份报告
            if (!isRecord)
                break;

            //生成jacoco之后将ec文件移动至finish文件夹
            finshEc(isGenerateJacoco, files.get(i));

        }

    }

    private static void finshEc(boolean isGenerate, String name) {
        if (isGenerate) {
            try {
                //执行生成jacoco报告的命令
                Process p = Runtime.getRuntime().exec("cmd /c start cmd.exe /c move CoverageEc\\" + name + " CoverageEc\\finish\\" + name);
                //等待cmd命令执行完毕
                InputStreamReader ir = new InputStreamReader(p.getInputStream());
                LineNumberReader input = new LineNumberReader(ir);
                while (input.readLine() != null) {

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void generateJacoco(boolean isGenerate) {

        if (isGenerate) {
            try {

                //执行生成jacoco报告的命令
                Process p = Runtime.getRuntime().exec("cmd /c start cmd.exe /c jacoco.bat");

                //等待cmd命令执行完毕
                InputStreamReader ir = new InputStreamReader(p.getInputStream());
                LineNumberReader input = new LineNumberReader(ir);
                while (input.readLine() != null) {

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
