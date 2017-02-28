package com.zyx.coverage;

import java.io.IOException;

/**
 * Created by zhangyouxuan on 2016/10/8.
 */

//        cmd /c dir 是执行完dir命令后关闭命令窗口。
//        cmd /k dir 是执行完dir命令后不关闭命令窗口。
//        cmd /c start dir 会打开一个新窗口后执行dir指令，原窗口会关闭。
//        cmd /k start dir 会打开一个新窗口后执行dir指令，原窗口不会关闭。
//        注：增加了start，就会打开新窗口，可以用cmd /?查看帮助信息。
public class CmdRunner {

    public static void main(String[] args) {

        try {
            Process p = Runtime.getRuntime().exec("cmd /k start cmd.exe /k E:\\ReoportAnalysis\\cmd.bat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
