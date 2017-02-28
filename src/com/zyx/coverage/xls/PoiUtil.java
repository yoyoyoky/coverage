package com.zyx.coverage.xls;

import com.zyx.coverage.util.WDWUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhangyouxuan on 2017/2/13.
 */
public class PoiUtil {

    public void setMark(String filePath, List<Integer> ids) {
        try {
            boolean isExcel2003 = true;
            if (WDWUtil.isExcel2007(filePath)) {
                isExcel2003 = false;
            }
            File file = new File(filePath);
            Workbook wb = null;
            FileInputStream in = new FileInputStream(file);
            FileOutputStream os = null;

            if (isExcel2003) {
                wb = new HSSFWorkbook(in);
            } else {
                wb = new XSSFWorkbook(in);
            }

            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);

            Sheet sheet = wb.getSheetAt(0);
            Row row = null;
            Cell cell = null;
            int markNum = 0;
            for (int i = 1, n = sheet.getLastRowNum(); i <= n; i++) {
                row = sheet.getRow(i);
                if (row != null) {
                    cell = row.getCell(row.getLastCellNum() - 1);
                    cell.setCellStyle(cellStyle);
                    if (markNum < ids.size() && row.getCell(0).getNumericCellValue() == ids.get(markNum)) {
                        markNum++;
                        cell.setCellValue("√");
                    } else {
                        cell.setCellValue("");
                    }
                }
            }
            os = new FileOutputStream(file);
            wb.write(os);
            os.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        PoiUtil poiUtil = new PoiUtil();
        List<Integer> ids = Arrays.asList(1, 3, 10, 15, 19);
        poiUtil.setMark("E:\\UnitTestSpace\\ReoportAnalysis\\Flyme6.0_Music-System_TestCase .xlsx", ids);
    }

}
