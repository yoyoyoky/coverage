package com.zyx.coverage.gui;

import com.zyx.coverage.util.StringComparator;
import com.zyx.git.GitLog;
import com.zyx.hierarchy.CallHierarchy;
import com.zyx.hierarchy.MethodRelationChain;
import com.zyx.hierarchy.ProjectResolver;
import com.zyx.hierarchy.bean.Method;
import com.zyx.hierarchy.bean.MethodLine;
import com.zyx.hierarchy.bean.Relation;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * Created by zhangyouxuan on 2017/1/17.
 */
public class ResolverView {
    private JPanel mainPanel;
    private JTextField gitPathTextField;
    private JButton selectGitButton;
    private JComboBox branchComboBox;
    private JTextField daysTextField;
    private JPanel numPanel;
    private JLabel numLabel;
    private JButton copyBtn;
    private JScrollPane scrollPane;
    private JList listCase;
    private JButton btnOK;
    private JButton btnCancel;
    private JTextField srcTextField;
    private JButton selectSrcButton;
    private Vector<String> vector = new Vector<>();

    final String ALL_BRANCHES = "all branches";

    public ResolverView() {
        setSelectButtonAction();
        setBranchComboBoxData();
        setCopyBtnAction();
        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vector.clear();
                GitLog gitLog = new GitLog();
                gitLog.connect();
                if (branchComboBox.getSelectedItem().toString().equals(ALL_BRANCHES)) {
                    gitLog.getDiffMethods(Integer.parseInt(daysTextField.getText()));
                } else {
                    gitLog.getDiffMethods(Integer.parseInt(daysTextField.getText()), branchComboBox.getSelectedItem().toString());
                }
                java.util.List<MethodLine> methodLineList = gitLog.getMethodLineList();


                long startTime = System.currentTimeMillis();
                ProjectResolver resolver = new ProjectResolver(srcTextField.getText());
                resolver.scanMethod();
                java.util.List<Method> methodList = resolver.getDataType().getMethodList();
                resolver.scanCallMethod();
                java.util.List<Relation> relationList = resolver.getDataType().getRelationList();
                System.out.println(relationList.size());
                System.out.println((System.currentTimeMillis() - startTime) / 1000);


                MethodRelationChain chain = new MethodRelationChain(methodList, relationList);
                CallHierarchy callHierarchy = new CallHierarchy(srcTextField.getText());

                Set<List<Integer>> oneCallSet = new HashSet<List<Integer>>();
                Set<List<Integer>> allCallSet = new HashSet<List<Integer>>();
                for (int i = 0, n = methodLineList.size(); i < n; i++) {
                    oneCallSet = callHierarchy.getCallChain(methodLineList.get(i));
                    if (oneCallSet != null && oneCallSet.size() > 0)
                        allCallSet.addAll(oneCallSet);
                }
                vector.addAll(callHierarchy.printCallChain(allCallSet));
                Collections.sort(vector,new StringComparator());
                numLabel.setText(String.valueOf(vector.size()));
                listCase.setListData(vector);
            }
        });
    }

    private void setCopyBtnAction() {
        copyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                setSysClipboardText(vector.toString().substring(1,vector.toString().lastIndexOf("]")).replace(", ","\n"));
            }
        });
    }

    private void setSysClipboardText(String text) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();    //获得系统粘贴板
        StringSelection textInfoSelected = new StringSelection(text);    //建立一个粘贴板内容实例.
        clipboard.setContents(textInfoSelected, null);    //将textInfoSelected加入到粘贴板中;
    }

    private void setBranchComboBoxData() {
        GitLog gitLog = new GitLog();
        gitLog.connect();
        java.util.List<String> branches = gitLog.getBranchList();
        branchComboBox.addItem(ALL_BRANCHES);
        for (String b : branches) {
            branchComboBox.addItem(b);
        }

    }

    private void setSelectButtonAction() {


        selectGitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(".");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnval = chooser.showDialog(mainPanel, "确定");
                if (returnval == JFileChooser.APPROVE_OPTION) {
                    String str = chooser.getSelectedFile().getPath();
                    gitPathTextField.setText(str);
                }
            }
        });

        selectSrcButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(".");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnval = chooser.showDialog(mainPanel, "确定");
                if (returnval == JFileChooser.APPROVE_OPTION) {
                    String str = chooser.getSelectedFile().getPath();
                    srcTextField.setText(str);
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Case Precision Test Tool --by zhangyouxuan@meizu.com");
        frame.setContentPane(new ResolverView().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("carpetita1.png"));
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

//        Vector vector = new Vector();
//        vector.add("b");
//        vector.add("a");
//        vector.add("c");
//        vector.add("e");
//        vector.add("d");
//        StringComparator comparator = new StringComparator();
//        Collections.sort(vector,comparator);
//        System.out.println(vector.toString());
//        System.out.println(vector.toString().substring(1,vector.toString().lastIndexOf("]")).replace(", ","\n"));
    }
}
