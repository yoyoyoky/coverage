package com.zyx.hierarchy.bean;

/**
 * Created by zhangyouxuan on 2017/1/11.
 */
public class Method {

//    @Column(name = "id")
    private Long id;

//    @Column(name = "package")
    private String packageName;

//    @Column(name = "class")
    private String className;

//    @Column(name = "method")
    private String methodName;

    private String parameters;

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public int getParametersNum() {
        return parametersNum;
    }

    public void setParametersNum(int parametersNum) {
        this.parametersNum = parametersNum;
    }

    private int parametersNum;

//    @Column(name = "return")
    private String returnType;

//    @Column(name = "begin")
    private int begin;

//    @Column(name = "end")
    private int end;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Method method = (Method) o;

        if (parametersNum != method.parametersNum) return false;
        if (!packageName.equals(method.packageName)) return false;
        if (!className.equals(method.className)) return false;
        return methodName.equals(method.methodName);

    }

    @Override
    public int hashCode() {
        int result = packageName.hashCode();
        result = 31 * result + className.hashCode();
        result = 31 * result + methodName.hashCode();
        result = 31 * result + parametersNum;
        return result;
    }

    @Override
    public String toString() {
        return "Method{" +
                "id=" + id +
                ", packageName='" + packageName + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameters='" + parameters + '\'' +
                ", parametersNum=" + parametersNum +
                ", returnType='" + returnType + '\'' +
                ", begin=" + begin +
                ", end=" + end +
                '}';
    }
}
