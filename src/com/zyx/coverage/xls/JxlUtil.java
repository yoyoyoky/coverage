package com.zyx.coverage.xls;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.CellFormat;
import jxl.read.biff.BiffException;
import jxl.write.*;
import jxl.write.biff.RowsExceededException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhangyouxuan on 2016/9/29.
 */
public class JxlUtil {

    String REPORTH_PATH;

    public JxlUtil(String path) {
        REPORTH_PATH = path;
    }

    public JxlUtil(){

    }

    public void addItems(String fileName, int sheetNum, IDataWriter dataWriter) {
        try {
            FileInputStream in = new FileInputStream(new File(REPORTH_PATH + "/" + fileName));
            Workbook wb = Workbook.getWorkbook(in);
            WritableWorkbook book = Workbook.createWorkbook(new File(REPORTH_PATH + "/" + fileName), wb);

            WritableSheet sheet = book.getSheet(sheetNum);
            dataWriter.write(sheet);//接口写入数据

            book.write();
            book.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    public void create(String fileName, String sheetNameStr, List<String> title, List<Integer> columnSize) {
        try {
            File dir = new File(REPORTH_PATH);
            WritableWorkbook book;
            WritableSheet sheet;
            if (!dir.exists())
                dir.mkdir();
            File file = new File(REPORTH_PATH + "/" + fileName);
            if (!file.exists()) {
                book = Workbook.createWorkbook(file);
            } else {
                Workbook wb = Workbook.getWorkbook(file);
                book = Workbook.createWorkbook(file, wb);
            }
            if (book.getSheet(sheetNameStr) == null) {
                int sheetNum = book.getNumberOfSheets();
                sheet = book.createSheet(sheetNameStr, sheetNum);
                for (int i = 0; i < columnSize.size(); i++) {
                    sheet.setColumnView(i, columnSize.get(i)); // 设置列的宽度
                }
                // 将定义好的单元格添加到工作表中
                WritableCellFormat wcf = getWritableCellFormat(true);

                Label lable;
                for (int i = 0; i < title.size(); i++) {
                    lable = new Label(i, 0, title.get(i));
                    lable.setCellFormat(wcf);
                    sheet.addCell(lable);
                }
            }
            // 写入数据并关闭文件
            book.write();
            book.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void setMark(String filePath, List<Integer> ids) {
        try {
            File file = new File(filePath);
            Workbook wb = Workbook.getWorkbook(file);
            WritableWorkbook book = Workbook.createWorkbook(file, wb);
            WritableSheet sheet = book.getSheet(0);
            int column = sheet.getColumns();

            WritableCell cell;
            CellFormat cf;
            Label label;
            int markNum = 0;
            for (int i = 1, n = sheet.getRows() - 1; i < n; i++) {
                cell = sheet.getWritableCell(i, column);//获取指定单元格
                cf = cell.getCellFormat();//获取第单元格的格式
                if (Integer.valueOf(sheet.getWritableCell(i, 0).getContents()) == ids.get(markNum)) {
                    markNum++;
                    label = new Label(i, column, "√");//将指定单元格的值改为“√”
                }else{
                    label = new Label(i,column,"");//将指定单元格的值改为“”
                }
                label.setCellFormat(cf);//将修改后的单元格的格式设定成跟原来一样
                sheet.addCell(label);//将改过的单元格保存到sheet
            }

            // 写入数据并关闭文件
            book.write();
            book.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WritableCellFormat getWritableCellFormat(boolean isBackground) {
        // 将定义好的单元格添加到工作表中
        WritableCellFormat wcf = new WritableCellFormat();
        try {
            // 设置居中
            wcf.setAlignment(Alignment.LEFT);
//            // 设置边框线
//            wcf.setBorder(Border.ALL, BorderLineStyle.THIN);
            //设置垂直居中
            wcf.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
            if (isBackground) {
                // 设置单元格的背景颜色
                wcf.setBackground(jxl.format.Colour.GRAY_25);
            }
        } catch (WriteException e) {
            e.printStackTrace();
        }

        return wcf;
    }

    public static void main(String[] args) {

        JxlUtil jxlUtil = new JxlUtil();
        List<Integer> ids = Arrays.asList(1,5,8,9,12);
        jxlUtil.setMark("E:\\UnitTestSpace\\ReoportAnalysis\\Flyme6.0_Music-System_TestCase .xlsx",ids);
    }

}
