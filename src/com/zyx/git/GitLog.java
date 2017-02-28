package com.zyx.git;

import com.zyx.hierarchy.bean.MethodLine;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.CommitTimeRevFilter;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangyouxuan on 2016/12/22.
 */
public class GitLog {

    private Repository repo;
    private Git git;
    private String packageName;
    private String className;
    private String methodName;
    private String gitPath;
    private List<String> methodList = new ArrayList<>();
    long DAY = 1000 * 60 * 60 * 24;
    private List<MethodLine> methodLineList;

    public GitLog() {
        methodLineList = new ArrayList<>();
        gitPath = "E:/UnitTestSpace/Media/Music/.git";
    }

    public GitLog(String gitPath) {
        methodLineList = new ArrayList<>();
        this.gitPath = gitPath;
    }


    public static void main(String[] args) {

        long DAY = 1000 * 60 * 60 * 24;

//        Calendar calendar = Calendar.getInstance();
//        calendar.set(2017, 1, 2);
//        Date before = calendar.getTime();
//        calendar.set(2017, 1, 3);
//        Date after = calendar.getTime();
//        System.out.println((after.getTime() - before.getTime()) / DAY);

        GitLog gitLog = new GitLog();
        gitLog.connect();
        gitLog.getDiffMethods("2017-01-01", "2017-02-06", "refs/remotes/origin/6.0.00-develop");
//        gitLog.getDiffMethods(before, after);

    }


    public void connect() {
        try {
            repo = new FileRepositoryBuilder().setGitDir(new File(gitPath)).build();
            git = new Git(repo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(){
        git.close();
        repo.close();
    }


    public List<String> getBranchList() {
        List<String> branchList = new ArrayList<>();
        try {
            List<Ref> branches = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();
            for (Ref b : branches) {
                branchList.add(b.getName());
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return branchList;
    }

    public List<String> getDiffMethods(String sinceDate, String untilDate, String... branch) {
        Calendar calendar = Calendar.getInstance();
        String[] date = sinceDate.split("-");
        calendar.set(Integer.valueOf(date[1]) == 1 ? Integer.valueOf(date[0]) - 1 : Integer.valueOf(date[0]), Integer.valueOf(date[1]) == 1 ? 12 : Integer.valueOf(date[1]) - 1, Integer.valueOf(date[2]), 0, 0, 0);
        Date since = calendar.getTime();
        date = untilDate.split("-");
        calendar.set(Integer.valueOf(date[1]) == 1 ? Integer.valueOf(date[0]) - 1 : Integer.valueOf(date[0]), Integer.valueOf(date[1]) == 1 ? 12 : Integer.valueOf(date[1]) - 1, Integer.valueOf(date[2]), 0, 0, 0);
        Date until = calendar.getTime();
        return getDiffMethods(since, until, branch);
    }

    public List<String> getDiffMethods(int days, String... branch) {
        Date since = new Date(new Date().getTime() - DAY * days);
        Date until = new Date();
        return getDiffMethods(since, until, branch);
    }


    public List<String> getDiffMethods(Date since, Date until, String... branch) {
        try {
            connect();
            RevWalk walk = new RevWalk(repo);
            RevFilter between = CommitTimeRevFilter.between(since, until);
            List<RevCommit> commitList = new ArrayList<RevCommit>();
            Iterable<RevCommit> commits;
            if (branch.length > 0) {
                commits = git.log().add(repo.resolve(branch[0])).setRevFilter(between).call();
            } else {
                commits = git.log().setRevFilter(between).call();
            }
            for (RevCommit commit : commits) {
                commitList.add(commit);
            }
            methodList.clear();
//            for (int i = 0; i < commitList.size() - 1; i++) {
            int i = commitList.size() - 2;
            AbstractTreeIterator newTree = prepareTreeParser(commitList.get(0));
            AbstractTreeIterator oldTree = prepareTreeParser(commitList.get(i + 1));
            List<DiffEntry> diff = git.diff().setOldTree(oldTree).setNewTree(newTree).setShowNameAndStatusOnly(true).call();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DiffFormatter df = new DiffFormatter(out);
            //设置比较器为忽略空白字符对比（Ignores all whitespace）
            df.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
            df.setRepository(git.getRepository());
            df.setContext(100);
            System.out.println(i + "------------------------------begin-----------------------------");
            System.out.println(commitList.get(i).getFullMessage());
            System.out.println(commitList.get(i).getAuthorIdent().getWhen());
            //每一个diffEntry都是第个文件版本之间的变动差异
            String javaPath;
            for (DiffEntry diffEntry : diff) {
                if (diffEntry.getNewPath().endsWith(".java")) {
                    javaPath = diffEntry.getNewPath();
                    packageName = javaPath.substring(javaPath.indexOf("com"), javaPath.lastIndexOf("/")).replace("/", ".");
                    className = javaPath.substring(javaPath.lastIndexOf("/") + 1, javaPath.indexOf(".java"));
                    System.out.println(packageName + "." + className);
                    df.format(diffEntry);
                    String diffText = out.toString("UTF-8");
                    methodList.addAll(getModiffiedMethods(diffText));
                    int linesDeleted = 0;
                    int linesAdded = 0;
                    for (Edit edit : df.toFileHeader(diffEntry).toEditList()) {
                        linesDeleted += edit.getEndA() - edit.getBeginA();
                        linesAdded += edit.getEndB() - edit.getBeginB();
                        methodLineList.add(new MethodLine(packageName, className, edit.getBeginB() + 1));
                        System.out.println("edit.getBeginA()---------------" + edit.getBeginA());
                        System.out.println("edit.getBeginB()---------------" + edit.getBeginB());
                    }
                    System.out.println("linesDeleted------------------------" + linesDeleted);
                    System.out.println("linesAdded------------------------" + linesAdded);
                }
            }
            out.reset();
            System.out.println(i + "------------------------------end-----------------------------");
//            }
        } catch (NoHeadException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return methodList;
    }

    public List<MethodLine> getMethodLineList() {
        return methodLineList;
    }


    public AbstractTreeIterator prepareTreeParser(RevCommit commit) {
        try {
            RevWalk walk = new RevWalk(repo);
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();

            ObjectReader oldReader = repo.newObjectReader();
            oldTreeParser.reset(oldReader, tree.getId());


            walk.dispose();

            return oldTreeParser;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    private Set<String> getModiffiedMethods(String text) {
        HashSet<String> methods = new HashSet<String>();
        String temp;
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            temp = getMethodName(lines[i]);
            if (temp != null) {
                methodName = temp;
            }
            if (lines[i].contains("+  ")) {
                methods.add(packageName + "_" + className + "_" + methodName);
            }
        }
        return methods;
    }


    private String getMethodName(String line) {
        if (line.replace(" ", "").endsWith("){") && !line.replace(" ", "").contains("=new")) {
            String[] wordTemp = line.split("\\(");
            if (wordTemp[0].trim().contains(" ")) {
                String[] words = wordTemp[0].trim().split(" ");
                if (words.length > 1 && matchFunctionName(words[0]) && matchFunctionName(words[words.length - 1])) {
//                    System.out.println(wordTemp[0].trim());
                    return words[words.length - 1] + line.substring(line.indexOf("("), line.indexOf(")") + 1);
                }
            }
        }
        return null;
    }

    private boolean matchFunctionName(String function) {
        String regex = "^[a-zA-Z_]+[a-zA-Z0-9_]*";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(function);
        return m.matches();
    }
}
