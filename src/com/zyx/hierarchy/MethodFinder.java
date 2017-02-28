package com.zyx.hierarchy;

import com.zyx.hierarchy.bean.Method;

import java.util.List;

/**
 * Created by zhangyouxuan on 2017/2/6.
 */
public class MethodFinder {

    List<Method> methodList;

    public MethodFinder(List<Method> methodList) {
        this.methodList = methodList;
    }

    public String getNameById(int id) {
        if (id >= 0 && methodList.size() >= id) {
            return methodList.get(id - 1).getClassName() + "." + methodList.get(id - 1).getMethodName();
        }
        return null;
    }


    public Method getMethodByClassAndLine(String packageName, String className, int line) {
        Method m;
        for (int i = 0; i < methodList.size(); i++) {
            m = methodList.get(i);
            if (m.getPackageName().equals(packageName) && m.getClassName().equals(className) && m.getBegin() <= line && m.getEnd() >= line) {
                return m;
            }
        }
        return null;
    }

    public int getIdByClassAndLine(String packageName, String className, int line) {
        Method m;
        for (int i = 0; i < methodList.size(); i++) {
            m = methodList.get(i);
            if (m.getPackageName().equals(packageName) && m.getClassName().equals(className) && m.getBegin() <= line && m.getEnd() >= line) {
                return (i + 1);
            }
        }
        return -1;
    }

    public int getIdByFullName(String packageName, String className, String methodName) {
        Method m;
        for (int i = 0; i < methodList.size(); i++) {
            m = methodList.get(i);
            if (m.getPackageName().equals(packageName) && m.getClassName().equals(className) && m.getMethodName().equals(methodName)) {
                return (i + 1);
            }
        }
        return -1;
    }
}
