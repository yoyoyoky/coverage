package com.zyx.hierarchy;

import com.zyx.hierarchy.bean.Method;
import com.zyx.hierarchy.bean.Relation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyouxuan on 2017/1/12.
 */
public class MethodRelationChain {
    List<Relation> relationList;
    List<Relation> chainList;
    List<List<Integer>> chains;
    List<Integer> chain;
    int id;
    MethodFinder methodFinder;

    public MethodRelationChain(List<Method> methodList, List<Relation> relationList, int id) {
        this.methodFinder = new MethodFinder(methodList);
        this.relationList = relationList;
        this.id = id;
        this.chainList = new ArrayList<>();
        this.chains = new ArrayList<>();
        this.chain = new ArrayList<>();
    }

    public MethodRelationChain(List<Method> methodList, List<Relation> relationList) {
        this.methodFinder = new MethodFinder(methodList);
        this.relationList = relationList;
        this.chainList = new ArrayList<>();
        this.chains = new ArrayList<>();
        this.chain = new ArrayList<>();
    }

    public void setId(String packageName, String className, int line) {
        this.id = methodFinder.getIdByClassAndLine(packageName, className, line);
        chains.clear();
        chain.clear();
        if (id != -1)
            System.out.println(id);
    }

    public void setId(String packageName, String className, String methodName) {
        this.id = methodFinder.getIdByFullName(packageName, className, methodName);
        chains.clear();
        chain.clear();
        if (id != -1)
            System.out.println(id);
    }

    public List<String> printChain() {
        List<String> chainString = new ArrayList<>();
        getChain();
        getMethods(getMethods(id));

        if (methodFinder.getNameById(id) == null)
            return chainString;
        String string;
        for (int i = 0; i < chains.size() && i < 20; i++) {
            string = "";
            List<Integer> chain = chains.get(i);
            for (int j = chain.size() - 1; j > -1; j--) {
                string += methodFinder.getNameById(chain.get(j)) + ">";
                System.out.print(methodFinder.getNameById(chain.get(j)) + ">");
            }
            string += methodFinder.getNameById(id);
            chainString.add(string);
            System.out.println(methodFinder.getNameById(id));
        }
        return chainString;
    }

    private void getChain() {
        getChain(id);
    }

    private void getChain(int id) {
        for (int i = 0; i < relationList.size(); i++) {
            if (relationList.get(i).getCallid() == id && relationList.get(i).getMethodid() != id) {
                if (chainList.indexOf(relationList.get(i)) == -1) {
                    chainList.add(relationList.get(i));
                    getChain(relationList.get(i).getMethodid());
                }
            }
        }
    }

    private void getMethods(List<Integer> methods) {
        for (int i = 0; i < methods.size(); i++) {
            try {
                getMethods(getMethods(methods.get(i)));
            } catch (StackOverflowError e) {
                break;
            }
        }
    }

    private List<Integer> getMethods(int callid) {
        if (callid != id) {
            chain.add(callid);
        }
        List<Integer> methods = new ArrayList<>();
        for (Relation relation : chainList) {
            if (relation.getCallid() == callid) {
                methods.add(relation.getMethodid());
            }
        }
        if (methods == null || methods.size() == 0) {
            chains.add(chain);
            chain = new ArrayList<>();
        }
        return methods;
    }

}
