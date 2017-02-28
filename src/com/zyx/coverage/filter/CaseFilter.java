package com.zyx.coverage.filter;

import com.zyx.coverage.util.FileUtil;
import com.zyx.coverage.xls.HtmlAnalysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by zhangyouxuan on 2016/12/23.
 */
public class CaseFilter {


    private String reportPath;
    private Vector<String> vector;
    private List<String> urls;

    public CaseFilter(String reportPath) {
        this.reportPath = reportPath;
        vector = new Vector<>();
        urls = new ArrayList<>();
    }

    private void filterCase(String packageName, String className, String methodName) {
        HtmlAnalysis htmlAnalysis;
        String isCoveredMethod;
        String htmlPath;
        vector.clear();
        urls.clear();
        List<String> dirs = FileUtil.getDirs(reportPath);
        for (int i = 0; i < dirs.size(); i++) {
            if (dirs.get(i).equals("jacocoTestReport")) {
                continue;
            }
            htmlPath = reportPath + "/" + dirs.get(i) + "/jacocoTestReport/html/";
            htmlAnalysis = new HtmlAnalysis(htmlPath);
            isCoveredMethod = htmlAnalysis.isCoveredMethod(packageName, className, methodName);
            if (isCoveredMethod != null) {
                vector.add(dirs.get(i) + isCoveredMethod.substring(isCoveredMethod.indexOf(":")));
                urls.add(htmlPath + isCoveredMethod.substring(0, isCoveredMethod.indexOf("#")));
            }
        }
    }

    private void openDefaultBrowser(String url) {
        String commandText = "cmd /c start " + url;
        try {
            Runtime.getRuntime().exec(commandText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Vector<String> getVector(){
        return vector;
    }

    public List<String> getUrls(){
        return urls;
    }
}
