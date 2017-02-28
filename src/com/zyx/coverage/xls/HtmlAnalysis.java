package com.zyx.coverage.xls;

import com.zyx.coverage.util.FileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyouxuan on 2016/9/29.
 */
public class HtmlAnalysis {

    String parentPath;

    public static void main(String[] args) {
        String reportPath = "E:\\UnitTestSpace\\ReoportAnalysis\\测试";
        List<String> files = FileUtil.getDirs(reportPath);
        HtmlAnalysis htmlAnalysis;
        for (String f : files) {
            htmlAnalysis = new HtmlAnalysis(reportPath + "\\" + f + "\\jacocoTestReport\\html\\");
            List<String> classes = htmlAnalysis.getCoveredClasses("com.meizu.media.music.fragment");
            for (int j = 0; j < classes.size(); j = j + 2) {
                htmlAnalysis.getCoveredMethods(classes.get(j), classes.get(j + 1));
            }
        }
    }

    public HtmlAnalysis(String path) {
        parentPath = path;
    }


    public List<String> getCoveredPackages() {
        List<String> packages = new ArrayList<>();
        File input = new File(parentPath + "index.html");
        try {
            Document doc = Jsoup.parse(input, "UTF-8", "");
            Elements content = doc.getElementsByTag("tr");
            String value = null;
            Elements html = null;
            for (int i = 0; i < content.size(); i++) {
                value = content.get(i).getElementsByClass("ctr2").get(0).text();
                if (!value.isEmpty() && value.endsWith("%") && !value.startsWith("0")) {
                    html = content.get(i).getElementsByTag("a");
                    if (!html.isEmpty()) {
                        packages.add(html.get(0).text());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return packages;
    }

    public List<String> getCoveredClasses(String packageName) {
        List<String> classes = new ArrayList<>();
        File input = null;
        input = new File(parentPath + packageName + "/index.html");
        if (!input.exists())
            return null;
        try {
            Document doc = Jsoup.parse(input, "UTF-8", "");
            Elements content = doc.getElementsByTag("tr");
            String value = null;
            Elements html = null;
            for (int j = 0; j < content.size(); j++) {
                value = content.get(j).getElementsByClass("ctr2").get(0).text();
                if (!value.isEmpty() && value.endsWith("%") && !value.startsWith("0")) {
                    html = content.get(j).getElementsByTag("a");
                    if (!html.isEmpty()) {
                        classes.add(html.get(0).text());//类名
                        classes.add(packageName + "/" + html.get(0).attr("href"));//类名跳转链接
                        System.out.println(packageName + "/" + html.get(0).attr("href"));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    public List<String> getCoveredMethods(String className, String classUrl) {
        List<String> methods = new ArrayList<>();
        File input = null;
        input = new File(parentPath + classUrl);
        if (!input.exists())
            return null;
        try {
            Document doc = Jsoup.parse(input, "UTF-8", "");
            Elements content = doc.getElementsByTag("tr");
            String value = null;
            Elements html = null;
            for (int j = 0; j < content.size(); j++) {
                value = content.get(j).getElementsByClass("ctr2").get(0).text();
                if (!value.isEmpty() && value.endsWith("%") && !value.startsWith("0")) {

                    html = content.get(j).getElementsByTag("a");
                    if (!html.isEmpty()) {
                        methods.add(html.get(0).text());//方法名
                        methods.add(classUrl.split("/")[0] + "/" + html.get(0).attr("href"));//方法名跳转链接
                        methods.add(value);//方法覆盖率百分比
                        System.out.println(className + "  " + html.get(0).text() + " : " + value);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return methods;
    }

    public String getCoveredClassUrl(String packageName, String className) {
        File input;
        input = new File(parentPath + packageName + "/index.html");
        if (!input.exists())
            return null;
        try {
            Document doc = Jsoup.parse(input, "UTF-8", "");
            Elements content = doc.getElementsByTag("tr");
            String value = null;
            Elements html = null;
            for (int j = 0; j < content.size(); j++) {
                value = content.get(j).getElementsByClass("ctr2").get(0).text();
                if (!value.isEmpty() && value.endsWith("%") && !value.startsWith("0")) {
//                    html = content.get(j).getElementsByTag("a");
                    html = content.get(j).getElementsByClass("el_class");
                    if (!html.isEmpty()) {
                        if (html.get(0).text().contains(className)) {
                            return packageName + "/" + html.get(0).attr("href");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String isCoveredMethod(String packageName, String className, String methodName) {
        String classUrl = getCoveredClassUrl(packageName, className);
        if (classUrl != null) {
            File input = null;
            input = new File(parentPath + classUrl);
            try {
                Document doc = Jsoup.parse(input, "UTF-8", "");
                Elements content = doc.getElementsByTag("tr");
                String value = null;
                Elements html = null;
                for (int j = 0; j < content.size(); j++) {
                    value = content.get(j).getElementsByClass("ctr2").get(0).text();
                    if (!value.isEmpty() && value.endsWith("%") && !value.startsWith("0")) {

                        html = content.get(j).getElementsByTag("a");
                        if (!html.isEmpty()) {
                            if (html.get(0).text().contains(methodName)) {
                                return classUrl.split("/")[0] + "/" + html.get(0).attr("href") + ":" + value;//方法名跳转链接+方法覆盖率百分比
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
