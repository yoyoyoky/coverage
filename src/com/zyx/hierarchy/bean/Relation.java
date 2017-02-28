package com.zyx.hierarchy.bean;

/**
 * Created by zhangyouxuan on 2017/1/11.
 */
public class Relation {

    private Long id;

    private int methodid;

    private int callid;

    public int getMethodid() {
        return methodid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMethodid(int methodid) {
        this.methodid = methodid;
    }

    public int getCallid() {
        return callid;
    }

    public void setCallid(int callid) {
        this.callid = callid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Relation relation = (Relation) o;

        if (methodid != relation.methodid) return false;
        return callid == relation.callid;

    }

    @Override
    public int hashCode() {
        int result = methodid;
        result = 31 * result + callid;
        return result;
    }

    @Override
    public String toString() {
        return "Relation{" +
                "id=" + id +
                ", methodid=" + methodid +
                ", callid=" + callid +
                '}';
    }
}
