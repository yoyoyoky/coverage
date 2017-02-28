package com.zyx.coverage.xls;

import jxl.write.Label;
import jxl.write.WritableHyperlink;
import jxl.write.WritableSheet;
import jxl.write.WriteException;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhangyouxuan on 2016/9/30.
 */
public class ReportGenerator {

    String HTML_PATH;
    String XLS_PATH;
    String XLS_NAME;
    String CASE_ID;
    String RELATIVE_PATH;

    public ReportGenerator(String htmlPath, String xlsPath, String xlsName) {
        HTML_PATH = htmlPath;
        XLS_PATH = xlsPath;
        XLS_NAME = xlsName;
    }

    public void setCaseId(String id) {
        CASE_ID = id;
        if (HTML_PATH.contains(CASE_ID)) {
            RELATIVE_PATH = HTML_PATH.substring(HTML_PATH.indexOf(CASE_ID));
        } else {
            RELATIVE_PATH = HTML_PATH.substring(HTML_PATH.indexOf("jacocoTestReport"));
        }
    }

    public void output() {
        final JxlUtil jxlUtil = new JxlUtil(XLS_PATH);
        jxlUtil.create(XLS_NAME, "base", Arrays.asList("Case", "Package", "Class", "Method", "percent"), Arrays.asList(30, 40, 50, 60, 10));

        IDataWriter dataWriter = new IDataWriter() {

            HtmlAnalysis htmlAnalysis = new HtmlAnalysis(HTML_PATH);
            List<String> packages = htmlAnalysis.getCoveredPackages();
            List<String> classes;
            List<String> methods;

            @Override
            public void write(WritableSheet sheet) {

                int rows = sheet.getRows();//获取已有总行数
                int caseRow = rows;
                int packRow = rows;
                int classRow = rows;

                try {

                    Label lable = new Label(0, rows, CASE_ID);
                    lable.setCellFormat(jxlUtil.getWritableCellFormat(false));
                    sheet.addCell(lable);//无超链接单元格，案例id

                    WritableHyperlink link;

                    for (int i = 0; i < packages.size(); i++) {

                        //设置垂直居中
                        lable = new Label(1, rows, packages.get(i));
                        lable.setCellFormat(jxlUtil.getWritableCellFormat(false));
                        sheet.addCell(lable);

                        //添加包名，相对路径超链接
                        link = new WritableHyperlink(1, rows, new File(RELATIVE_PATH + packages.get(i) + "/index.html"));//new URL("file://" + HTML_PATH + packages.get(i) + "/index.html"),绝对路径
                        link.setDescription(packages.get(i));
                        sheet.addHyperlink(link);

                        classes = htmlAnalysis.getCoveredClasses(packages.get(i));

                        for (int j = 0; j < classes.size(); j = j + 2) {

                            //设置垂直居中
                            lable = new Label(2, rows, classes.get(j));
                            lable.setCellFormat(jxlUtil.getWritableCellFormat(false));
                            sheet.addCell(lable);

                            //设置超链接
                            link = new WritableHyperlink(2, rows, new File(RELATIVE_PATH + classes.get(j + 1)));//new URL("file://" + HTML_PATH + classes.get(j + 1))
                            link.setDescription(classes.get(j));
                            sheet.addHyperlink(link);

                            methods = htmlAnalysis.getCoveredMethods(classes.get(j), classes.get(j + 1));

                            for (int k = 0; k < methods.size(); k = k + 3) {

                                System.out.println(methods.get(k));

                                if (methods.get(k).contains("{...}")) {//过滤 {...}  ， static {...}
                                    continue;
                                }

                                link = new WritableHyperlink(3, rows, new File(RELATIVE_PATH + methods.get(k + 1)));// new URL( "file://" + HTML_PATH + methods.get(k + 1))
                                link.setDescription(methods.get(k));
                                sheet.addHyperlink(link);

                                //添加方法覆盖百分比
                                lable = new Label(4, rows, methods.get(k + 2));
                                lable.setCellFormat(jxlUtil.getWritableCellFormat(false));
                                sheet.addCell(lable);

                                rows++;//添加行
                            }
                            sheet.mergeCells(2, classRow, 2, rows - 1);//合并类相同列
                            classRow = rows;
                        }
                        sheet.mergeCells(1, packRow, 1, rows - 1);//合并包相同列
                        packRow = rows;
                    }
                    sheet.mergeCells(0, caseRow, 0, rows - 1);//合并案例ID相同列

                } catch (WriteException e) {
                    e.printStackTrace();
                }

            }
        };

        jxlUtil.addItems(XLS_NAME, 0, dataWriter);


        //可筛选sheet
        jxlUtil.create(XLS_NAME, "selector", Arrays.asList("Case", "Package", "Class", "Method", "percent"), Arrays.asList(30, 40, 50, 60, 10));

        IDataWriter selectorWriter = new IDataWriter() {

            HtmlAnalysis htmlAnalysis = new HtmlAnalysis(HTML_PATH);
            List<String> packages = htmlAnalysis.getCoveredPackages();
            List<String> classes;
            List<String> methods;

            @Override
            public void write(WritableSheet sheet) {

                int rows = sheet.getRows();//获取已有总行数
                int caseRow = rows;
                int packRow = rows;
                int classRow = rows;

                try {

                    Label lable;
                    WritableHyperlink link;

                    for (int i = 0; i < packages.size(); i++) {

                        classes = htmlAnalysis.getCoveredClasses(packages.get(i));

                        for (int j = 0; j < classes.size(); j = j + 2) {

                            methods = htmlAnalysis.getCoveredMethods(classes.get(j), classes.get(j + 1));

                            for (int k = 0; k < methods.size(); k = k + 3) {

                                System.out.println(methods.get(k));

                                if (methods.get(k).contains("{...}")) {//过滤 {...}  ， static {...}
                                    continue;
                                }

                                lable = new Label(0, rows, CASE_ID);
                                lable.setCellFormat(jxlUtil.getWritableCellFormat(false));
                                sheet.addCell(lable);//无超链接单元格，案例id

                                //设置垂直居中
                                lable = new Label(1, rows, packages.get(i));
                                lable.setCellFormat(jxlUtil.getWritableCellFormat(false));
                                sheet.addCell(lable);

                                //添加包名，相对路径超链接
                                link = new WritableHyperlink(1, rows, new File(RELATIVE_PATH + packages.get(i) + "/index.html"));//new URL("file://" + HTML_PATH + packages.get(i) + "/index.html"),绝对路径
                                link.setDescription(packages.get(i));
                                sheet.addHyperlink(link);

                                //设置垂直居中
                                lable = new Label(2, rows, classes.get(j));
                                lable.setCellFormat(jxlUtil.getWritableCellFormat(false));
                                sheet.addCell(lable);

                                //设置超链接
                                link = new WritableHyperlink(2, rows, new File(RELATIVE_PATH + classes.get(j + 1)));//new URL("file://" + HTML_PATH + classes.get(j + 1))
                                link.setDescription(classes.get(j));
                                sheet.addHyperlink(link);

                                //添加方法，相对路径超链接
                                link = new WritableHyperlink(3, rows, new File(RELATIVE_PATH + methods.get(k + 1)));// new URL( "file://" + HTML_PATH + methods.get(k + 1))
                                link.setDescription(methods.get(k));
                                sheet.addHyperlink(link);

                                //添加方法覆盖百分比
                                lable = new Label(4, rows, methods.get(k + 2));
                                lable.setCellFormat(jxlUtil.getWritableCellFormat(false));
                                sheet.addCell(lable);

                                rows++;//添加行
                            }
                        }
                    }

                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        };

        jxlUtil.addItems(XLS_NAME, 1, selectorWriter);

        System.out.println("report output done");
    }

}
