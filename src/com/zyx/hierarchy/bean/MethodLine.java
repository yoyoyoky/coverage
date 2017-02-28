package com.zyx.hierarchy.bean;

/**
 * Created by zhangyouxuan on 2017/2/6.
 */
public class MethodLine {

    private String packageName;

    private String className;

    private int line;

    public MethodLine(String packageName, String className, int line) {
        this.packageName = packageName;
        this.className = className;
        this.line = line;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
