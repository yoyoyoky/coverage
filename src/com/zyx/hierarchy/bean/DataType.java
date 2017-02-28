package com.zyx.hierarchy.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangyouxuan on 2017/1/12.
 */
public class DataType {

    Map<String, String> variables;
    Map<String, String> imports;
    List<String> classes;
    Method method;
    List<Method> methodList;
    List<Relation> relationList;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public DataType() {
        variables = new HashMap<>();
        imports = new HashMap<>();
        classes = new ArrayList<>();
        methodList = new ArrayList<>();
        relationList = new ArrayList<>();
    }

    public void addVariable(String key, String value) {
        variables.put(key, value);
    }

    public void addImport(String key, String value) {
        imports.put(key, value);
    }

    public void addClass(String className) {
        classes.add(className);
    }

    public void addMethod(Method method) {
        methodList.add(method);
    }

    public void addRelation(Relation relation) {
        relationList.add(relation);
    }

    public void clearClasses() {
        classes.clear();
    }

    public void clearImports() {
        imports.clear();
    }

    public void clearVariables() {
        variables.clear();
    }

    public void clear() {
        imports.clear();
        variables.clear();
        classes.clear();
    }

    public List<String> getClasses() {
        return classes;
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    public Map<String, String> getImports() {
        return imports;
    }

    public List<Method> getMethodList() {
        return methodList;
    }

    public List<Relation> getRelationList() {
        return relationList;
    }

    public boolean hasMethod(int id) {
        for (Method m : methodList) {
            if (m.getId() == method.getId())
                return true;
        }
        return false;
    }

    public boolean hasMethod(Method method) {
        for (Method m : methodList) {
            if (m.equals(method))
                return true;
        }
        return false;
    }

    public boolean hasRelation(Relation relation) {
        for (Relation r : relationList) {
            if (r.equals(relation)) {
                return true;
            }
        }
        return false;
    }
}
