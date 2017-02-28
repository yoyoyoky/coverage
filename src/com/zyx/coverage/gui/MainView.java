package com.zyx.coverage.gui;

import com.sun.tools.doclets.internal.toolkit.util.DocFinder;
import com.zyx.coverage.util.FileUtil;
import com.zyx.coverage.util.StringComparator;
import com.zyx.coverage.xls.HtmlAnalysis;
import com.zyx.coverage.xls.PoiUtil;
import com.zyx.git.GitLog;
import com.zyx.hierarchy.MethodFinder;
import com.zyx.hierarchy.ProjectResolver;
import com.zyx.hierarchy.bean.Method;
import com.zyx.hierarchy.bean.MethodLine;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;


/**
 * Created by zhangyouxuan on 2016/12/20.
 */
public class MainView {
    private JButton btnOK;
    private JButton btnCancel;
    private JPanel mainPanel;
    private JTextField textPath;
    private JButton reportSelectBtn;
    private JList listCase;
    private JScrollPane scrollPane;
    private JPanel numPanel;
    private JLabel numLabel;
    private JButton copyBtn;
    private JPanel daysPanel;
    private JComboBox branchComboBox;
    private JTextField daysTextField;
    private JPanel radioGroupPanel;
    private JRadioButton pointCheckRadioButton;
    private JRadioButton baseRadioButton;
    private JRadioButton systemRadioButton;
    private JButton markButton;
    private JTextField srcPathTextField;
    private JTextField gitPathTextFiled;
    private JButton gitSelectBtn;
    private JButton srcSelectBtn;
    private JTextField caseTextField;
    private JButton caseSelectButton;

    private String javaPath;
    private String reportPath;
    private Vector<String> vector = new Vector<>();
    private List<String> urls = new ArrayList<>();
    List<String> dirs;
    String caseName;
    String url;
    String testType;
    final String ALL_BRANCHES = "all branches";
    final String POINTCHECK = "POINTCHECK";
    final String BASE = "BASE";
    final String SYSTEM = "SYSTEM";

    GitLog gitLog;

    public MainView() {
        createRadioGroup();
        setSelectBtnAction();
        setBranchComboBox();
        setListCaseSuperLink();
        setMarkBtnAction();
        setCopyBtnAction();
        setOkBtnAction();
    }

    private void setOkBtnAction() {
        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reportPath = textPath.getText();
                dirs = FileUtil.getDirs(reportPath);
                listCase.clearSelection();
                vector.clear();
                urls.clear();
                gitLog = new GitLog(gitPathTextFiled.getText());
                gitLog.connect();
                if (branchComboBox.getSelectedItem().toString().equals(ALL_BRANCHES)) {
                    gitLog.getDiffMethods(Integer.parseInt(daysTextField.getText()));
                } else {
                    gitLog.getDiffMethods(Integer.parseInt(daysTextField.getText()), branchComboBox.getSelectedItem().toString());
                }
                ProjectResolver resolver = new ProjectResolver(srcPathTextField.getText());
                resolver.scanMethod();
                List<Method> methodList = resolver.getDataType().getMethodList();
                MethodFinder methodFinder = new MethodFinder(methodList);
                List<MethodLine> methodLineList = gitLog.getMethodLineList();
                Method method;
                for (MethodLine line : methodLineList) {
                    method = methodFinder.getMethodByClassAndLine(line.getPackageName(), line.getClassName(), line.getLine());
                    if (method != null)
                        filterCase(line.getPackageName(), line.getClassName(), method.getMethodName());
                }
                numLabel.setText(String.valueOf(vector.size()));
                Collections.sort(vector, new StringComparator());
                listCase.setListData(vector);
            }
        });
    }

    private void setSelectBtnAction() {
        caseSelectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = fileSelected(false);
                if (!fileName.isEmpty())
                    caseTextField.setText(fileName);
            }
        });

        reportSelectBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = fileSelected(true);
                if (!fileName.isEmpty())
                    textPath.setText(fileName);
            }
        });
        gitSelectBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = fileSelected(true);
                if (!fileName.isEmpty()) {
                    gitPathTextFiled.setText(fileName);
                    setBranchComboBox();
                }
            }
        });
        srcSelectBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = fileSelected(true);
                if (!fileName.isEmpty())
                    srcPathTextField.setText(fileName);
            }
        });
    }

    private String fileSelected(boolean isDirectoryOnly) {
        JFileChooser chooser = new JFileChooser(".");
        if (isDirectoryOnly)
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        else
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnval = chooser.showDialog(mainPanel, "确定");
        if (returnval == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getPath();
        }
        return "";
    }

    private void setListCaseSuperLink() {

        listCase.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JList caseList = (JList) e.getSource();

                    System.out.println(caseList.getSelectedIndex() + "   :    " + urls.get(caseList.getSelectedIndex()));
                    openDefaultBrowser(urls.get(caseList.getSelectedIndex()));
                }
            }
        });
    }

    private void setMarkBtnAction() {
        markButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Integer> ids = new ArrayList<Integer>();
                String caseName;
                for (Iterator ite = vector.iterator(); ite.hasNext(); ) {
                    caseName = (String) ite.next();
                    ids.add(Integer.valueOf(caseName.substring(0, caseName.indexOf("_"))));
                }
                PoiUtil poiUtil = new PoiUtil();
                poiUtil.setMark(caseTextField.getText(), ids);
                openExcel();
            }
        });
    }

    private void openExcel(){
        int response = JOptionPane.showConfirmDialog(null, "Do you want to open the case excel file?", "Open", JOptionPane.YES_NO_OPTION);
        if (response == 0) {//Yes
            try {
                Runtime.getRuntime().exec("cmd   /c   start   excel   \""   +   caseTextField.getText()   +   "\"");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (response == 1) {//No

        }
    }

    private void setCopyBtnAction() {
        copyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                setSysClipboardText(vector.toString().substring(1, vector.toString().lastIndexOf("]")).replace(", ", "\n"));
            }
        });
    }

    private void createRadioGroup() {

        pointCheckRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    testType = POINTCHECK;
                }
            }
        });

        baseRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    testType = BASE;
                }
            }
        });

        systemRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    testType = SYSTEM;
                }
            }
        });

        ButtonGroup radioGroup = new ButtonGroup();
        radioGroup.add(pointCheckRadioButton);
        radioGroup.add(baseRadioButton);
        radioGroup.add(systemRadioButton);
        baseRadioButton.setSelected(true);
    }

    private void filterCase(String packageName, String className, String methodName) {
        HtmlAnalysis htmlAnalysis;
        String isCoveredMethod;
        String htmlPath;
        String fileName;
        for (int i = 0; i < dirs.size(); i++) {
            fileName = dirs.get(i);
            if (fileName.equals("jacocoTestReport") || vector.indexOf(fileName) != -1) {
                continue;
            }
            if (testType.equals(POINTCHECK) && (fileName.contains("_B_") || fileName.contains("_S_"))) {
                continue;
            }
            if (testType.equals(BASE) && (fileName.contains("_S_"))) {
                continue;
            }
            htmlPath = reportPath + "/" + dirs.get(i) + "/jacocoTestReport/html/";
            htmlAnalysis = new HtmlAnalysis(htmlPath);
            isCoveredMethod = htmlAnalysis.isCoveredMethod(packageName, className, methodName);
            if (isCoveredMethod != null) {
                caseName = dirs.get(i);
                url = htmlPath + "index.html";
                vector.add(caseName);
                urls.add(url);
            }
        }
    }

    private void openDefaultBrowser(String url) {
        String commandText = "cmd /c start " + url;
        try {
            Runtime.getRuntime().exec(commandText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setSysClipboardText(String text) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();    //获得系统粘贴板
        StringSelection textInfoSelected = new StringSelection(text);    //建立一个粘贴板内容实例.
        clipboard.setContents(textInfoSelected, null);    //将textInfoSelected加入到粘贴板中;
    }

    private void setBranchComboBox() {
        gitLog = new GitLog(gitPathTextFiled.getText());
        gitLog.connect();
        List<String> branches = gitLog.getBranchList();
        gitLog.disconnect();
        branchComboBox.removeAllItems();
        branchComboBox.addItem(ALL_BRANCHES);
        for (String b : branches) {
            branchComboBox.addItem(b);
        }
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Case Precision Test Tool --by zhangyouxuan@meizu.com");
        frame.setContentPane(new MainView().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("carpetita1.png"));
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }

}
