package com.zyx.hierarchy;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.zyx.git.GitLog;
import com.zyx.hierarchy.bean.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyouxuan on 2017/1/11.
 */
public class ProjectResolver {

    DataType dataType;
    File src;

    public ProjectResolver(String srcPath) {
        src = new File(srcPath);
        dataType = new DataType();
    }

    class ClassVisitor extends VoidVisitorAdapter<String> {
        private ClassName className;
        private MethodVisitor methodVisitor;
        private boolean isScanCall;

        public ClassVisitor() {
            methodVisitor = new MethodVisitor();
        }

        public ClassVisitor(boolean isScanCall) {
            this.isScanCall = isScanCall;
            methodVisitor = new MethodVisitor(isScanCall);
            dataType.clearImports();
            dataType.clearClasses();
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration n, String packageName) {
            className = new ClassName(packageName, n.getNameAsString());
            methodVisitor.visit(n, className);
            dataType.addClass(n.getNameAsString());
        }

        @Override
        public void visit(ImportDeclaration n, String arg) {
            dataType.addImport(n.getName().toString().substring(n.getName().toString().lastIndexOf(".") + 1), n.getName().toString().substring(0, n.getName().toString().lastIndexOf(".")));
            super.visit(n, arg);
        }

    }

    class MethodVisitor extends VoidVisitorAdapter<ClassName> {
        private Method method;
        private boolean isScanCall;

        MethodVisitor() {
            this.isScanCall = false;
        }

        MethodVisitor(boolean isScanCall) {
            this.isScanCall = isScanCall;
        }

        @Override
        public void visit(VariableDeclarator n, ClassName arg) {
            if (!isScanCall)
                dataType.addVariable(n.getNameAsString(), n.getType().toString());
            super.visit(n, arg);
        }

        @Override
        public void visit(MethodDeclaration n, ClassName className) {
            NodeList<Parameter> parameters = n.getParameters();
            List<String> pars = new ArrayList<>();
            for (Parameter p : parameters) {
                pars.add(p.getType().toString());
            }
            method = new Method();
            method.setPackageName(className.getPackageName());
            method.setClassName(className.getClassName());
            method.setMethodName(n.getNameAsString());
            method.setParameters(pars.toString());
            method.setParametersNum(parameters.size());
            method.setReturnType(n.getType().toString());
            method.setBegin(n.getBegin().get().line);
            method.setEnd(n.getEnd().get().line);
            dataType.setMethod(method);
//            System.out.println(method.toString());
            if (isScanCall) {
                new MethodCallVisitor().visit(n, dataType);
            } else {
                NodeList<Parameter> nodeList = n.getParameters();
                for (int i = 0; i < nodeList.size(); i++) {
                    dataType.addVariable(nodeList.get(i).getNameAsString(), nodeList.get(i).getType().toString());
                }
                dataType.addMethod(method);
            }
        }

    }

    class MethodCallVisitor extends VoidVisitorAdapter<DataType> {

        Relation relation;

        @Override
        public void visit(MethodCallExpr n, DataType data) {
            relation = analysisExpression(n, data);
            if (relation != null && relation.getCallid() != 0 && !dataType.hasRelation(relation)) {
                dataType.addRelation(relation);
//                System.out.println(relation.toString());
            }
        }

    }

    public Relation analysisExpression(MethodCallExpr n, DataType data) {
        Relation relation = null;
        Method call = new Method();
        Expression expression = n.getScope();
        if (expression != null && data.getImports().get(expression.toString()) != null) {//其他包下的静态方法
            call.setClassName(expression.toString());
            call.setPackageName(data.getImports().get(expression.toString()));
//            System.out.println(n.getNameAsString() + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + expression.toString() + ">>>>>>>>>>" + data.getImports().get(expression.toString()));
        } else if (expression == null || data.getClasses().indexOf(expression.toString()) != -1 || data.getClasses().indexOf(data.getVariables().get(expression.toString())) != -1) { //当前类的方法 包括匿名内部类的方法
            call.setClassName(data.getMethod().getClassName());
            call.setPackageName(data.getMethod().getPackageName());
//            System.out.println(n.getNameAsString() + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + data.getMethod().getClassName() + ">>>>>>>>>>" + data.getMethod().getPackageName());
        } else {
            if (data.getVariables().get(expression.toString()) == null) {
                call.setClassName(expression.toString());
                if (Character.isUpperCase(expression.toString().charAt(0))) { //同包名静态方法
                    call.setPackageName(data.getMethod().getPackageName());
//                    System.out.println(n.getNameAsString() + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + expression.toString() + ">>>>>>>>>>" + data.getMethod().getPackageName());
                } else { // 其他未解析的方法
//                    System.out.println(n.getNameAsString() + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + expression.toString());
                }
            } else {
                if (data.getImports().get(data.getVariables().get(expression.toString())) == null) { //匿名内部类 或 同包名下的其他类
                    call.setClassName(data.getVariables().get(expression.toString()));
                    call.setPackageName(data.getMethod().getPackageName());
//                    System.out.println(n.getNameAsString() + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + data.getVariables().get(expression.toString()) + ">>>>>>>>>>>>>>>>>>>" + data.getMethod().getPackageName());
                } else {//其他包普通方法
                    call.setClassName(data.getVariables().get(expression.toString()));
                    call.setPackageName(data.getImports().get(data.getVariables().get(expression.toString())));
//                    System.out.println(n.getNameAsString() + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + data.getVariables().get(expression.toString()) + ">>>>>>>>>>>>>>>>>>>" + data.getImports().get(data.getVariables().get(expression.toString())));
                }
            }
        }
        call.setMethodName(n.getNameAsString());
        call.setParametersNum(n.getArguments().size());
        if (call.getPackageName() != null && call.getPackageName().startsWith("com.meizu")) {
            relation = new Relation();
            for (int i = 0; i < data.getMethodList().size(); i++) {
                if (data.getMethodList().get(i).equals(call)) {
                    relation.setCallid(i + 1);
                }
                if (data.getMethodList().get(i).equals(data.getMethod())) {
                    relation.setMethodid(i + 1);
                }
            }
        }
        return relation;
    }


    public void scanMethod() {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            try {
                CompilationUnit compilationUnit = JavaParser.parse(file);
                ClassVisitor methodVisitor = new ClassVisitor();
                methodVisitor.visit(compilationUnit, compilationUnit.getPackageDeclaration().get().getName().asString());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }).explore(src);
    }

    public void scanCallMethod() {
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            try {
                CompilationUnit compilationUnit = JavaParser.parse(file);

                ClassVisitor methodCallVisitor = new ClassVisitor(true);
                methodCallVisitor.visit(compilationUnit, compilationUnit.getPackageDeclaration().get().getName().asString());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }).explore(src);
    }

    public DataType getDataType() {
        return dataType;
    }

    public void scanClass(String java) {
        try {
            File file = new File(src + "/" + java);
            CompilationUnit compilationUnit = JavaParser.parse(file);
            ClassVisitor methodCallVisitor = new ClassVisitor(true);
            methodCallVisitor.visit(compilationUnit, compilationUnit.getPackageDeclaration().get().getName().asString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        GitLog gitLog = new GitLog();
        gitLog.connect();
        gitLog.getDiffMethods(30, "refs/remotes/origin/6.0.00-develop");
//        gitLog.getDiffMethods("2017-01-01", "2017-02-06", "refs/remotes/origin/6.0.00-develop");
        List<MethodLine> methodLineList = gitLog.getMethodLineList();
        gitLog.disconnect();


        long startTime = System.currentTimeMillis();
        ProjectResolver resolver = new ProjectResolver("E:\\\\UnitTestSpace\\\\Media\\\\Music\\\\app\\\\src\\\\main\\\\java");
//        ProjectResolver resolver = new ProjectResolver("E:\\\\UnitTestSpace\\\\Server\\\\music\\\\music-service\\\\src\\\\main\\\\java");
        resolver.scanMethod();
        List<Method> methodList = resolver.getDataType().getMethodList();
        resolver.scanCallMethod();
        List<Relation> relationList = resolver.getDataType().getRelationList();
        System.out.println(relationList.size());
        System.out.println((System.currentTimeMillis() - startTime) / 1000);


        MethodFinder methodFinder = new MethodFinder(methodList);
        System.out.println("method------------" + methodFinder.getNameById(6725));

        MethodRelationChain chain = new MethodRelationChain(methodList, relationList, 6699);
//        chain.setId("com.meizu.media.music.util", "SongUtil", "deleteSongs");
//        chain.setId("com.meizu.music.album.dao.impl", "AlbumDaoImpl", "findAlbumStatus");
//        chain.printChain();
//        System.out.println();
//
//        for (int i = 0, n = methodLineList.size(); i < n; i++) {
//            chain.setId(methodLineList.get(i).getPackageName(),methodLineList.get(i).getClassName(),methodLineList.get(i).getLine());
//            chain.printChain();
//            System.out.println();
//        }
//        System.out.println("finish");


//        chain.setId("com.meizu.media.music.util", "MusicUtils", "isFastDoubleClick");
        chain.printChain();
        System.out.println();

        for (Relation relation : relationList) {
            if (relation.getCallid() == 7010 || relation.getMethodid() == 8424) {
                System.out.println(relation.getMethodid() + ">" + relation.getCallid());
            }
        }
//
//
//        chain.setId("com.meizu.media.music.player", "PlaybackService", "getRepeat");
//        chain.printChain();
//        System.out.println();
    }
}
