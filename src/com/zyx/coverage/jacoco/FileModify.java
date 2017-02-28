package com.zyx.coverage.jacoco;

import java.io.*;

/**
 * Created by zhangyouxuan on 2016/10/8.
 */
public class FileModify {

    /***
     * 读取文件内容
     */
    public String read(String filePath, IFileLineModify lineModify) {
        BufferedReader br = null;
        String line = null;
        StringBuffer buf = new StringBuffer();
        InputStreamReader isr;

        try {
            // 根据文件路径创建缓冲输入流
            isr = new InputStreamReader(new FileInputStream(filePath), "UTF-8");
            br = new BufferedReader(isr);

            // 循环读取文件的每一行, 对需要修改的行进行修改, 放入缓冲对象中
            while ((line = br.readLine()) != null) {
                // 此处根据实际需要修改某些行的内容
                buf.append(lineModify.properties(line));
                buf.append(System.getProperty("line.separator"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    br = null;
                }
            }
        }

        return buf.toString();
    }

    /***
     * 将内容回写到文件中
     */
    public void write(String filePath, String content) {
        BufferedWriter bw = null;
        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(filePath);
            // 根据文件路径创建缓冲输出流
            bw = new BufferedWriter(new OutputStreamWriter(
                    outputStream, "UTF-8"));
            // 将内容写入文件中
            bw.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    bw = null;
                }
            }
        }
    }

    public static void main(String[] args) {
        String filePath = "E:/ReoportAnalysis/local.properties"; // 文件路径

        IFileLineModify lineModify = new IFileLineModify() {

            String ecPath = "";
            String reportsDir = "";

            @Override
            public String properties(String line) {
                if(line.startsWith("ecPath = ")){
                    return "ecPath = "+ecPath;
                }else if (line.startsWith("reportsDir1 = ")){
                    return "reportsDir = "+ reportsDir;
                }
                return line;
            }
        };

        FileModify obj = new FileModify();
        obj.write(filePath, obj.read(filePath,lineModify)); // 读取修改文件
    }

}
