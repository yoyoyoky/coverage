package com.zyx.hierarchy;

import com.zyx.hierarchy.bean.Method;
import com.zyx.hierarchy.bean.MethodLine;
import com.zyx.hierarchy.bean.Relation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhangyouxuan on 2017/2/7.
 */
public class CallHierarchy {

    private String srcPath;
    private ProjectResolver resolver;
    private List<Method> methodList;
    private List<Relation> relationList;
    private Integer[] vertex;
    private double[][] matrix;
    private Graph<Integer> graph;
    private MethodFinder methodFinder;

    public CallHierarchy(String srcPath) {
        this.srcPath = srcPath;
        this.resolver = new ProjectResolver(srcPath);
        setMethod();
        setGraph();
    }

    private void setGraph() {
        int methodNums = methodList.size();
        vertex = new Integer[methodNums];

        matrix = new double[methodNums][methodNums];
        for (int i = 0; i < methodNums; i++) {
            vertex[i] = i + 1;
            for (int j = 0; j < methodNums; j++) {
                matrix[i][j] = 0;
            }
        }

        for (Relation relation : relationList) {
            if (relation.getMethodid() != relation.getCallid()) {
                //避免递归回调，陷入死循环
                matrix[relation.getCallid() - 1][relation.getMethodid() - 1] = 1;
            }
        }
        graph = new Graph<>(matrix, vertex);
    }

    private void setMethod() {
        resolver.scanMethod();
        methodList = resolver.getDataType().getMethodList();
        resolver.scanCallMethod();
        relationList = resolver.getDataType().getRelationList();
        methodFinder = new MethodFinder(methodList);
    }

    public Set<List<Integer>> getCallChain(MethodLine method) {
        int node = methodFinder.getIdByClassAndLine(method.getPackageName(), method.getClassName(), method.getLine()) - 1;
        return getCallChain(node);
    }

    public Set<List<Integer>> getCallChain(int node) {
        Set<List<Integer>> idSet = new HashSet<>();
        if (node < 0)
            return idSet;
        List<List<Integer>> idList = graph.startSearch(node);
        for (List<Integer> id : idList) {
            idSet.add(id);
        }
        return idSet;
    }

    public List<String> printCallChain(Set<List<Integer>> idSet){
        List<String> chains = new ArrayList<>();
        String chain;
        for (List<Integer> id : idSet) {
            chain = "";
            for (int i = id.size() - 1; i > -1; i--) {
                chain += methodFinder.getNameById(id.get(i));
                if (i != 0) {
                    chain += ">";
                }
            }
            chains.add(chain);
            System.out.println(chain);
        }
        return chains;
    }

    public static void main(String[] args) {
        CallHierarchy call = new CallHierarchy("E:\\\\UnitTestSpace\\\\Media\\\\Music\\\\app\\\\src\\\\main\\\\java");
        call.getCallChain(6698);
    }

}
