package com.metaShare.common.utils;


import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 
 *<b>系统名称:</b><b>
 *中石油
 *</b>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br>
 *
 *
 *<b>文件名:</b><br>
 *
 *ExcelExportUtil.java<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br>
 *
 *<b>类名:</b><br>
 *&nbsp;&nbsp;&nbsp;&nbsp;
 *
 *ExcelExportUtil类.<br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br>
 *
 *<br>
 *<b>概要说明</b><br>
 *&nbsp;&nbsp;&nbsp;&nbsp;
 *
 *
 *ExcelExportUtil类的概要说明<br>
 **&nbsp;*&nbsp;*&nbsp;*&nbsp;*&nbsp;*&nbsp;*&nbsp;
 *
 *TODO:ExcelExportUtil类的概要描述<br>
 *
 *<b>***History*** </b/><br>
 *更新年月日， 更改原因，   姓名，   		更新内容<br>
 *2019年11月11日,    cause,    eric.xi,   	新建<br>
 *
 *@author :eric.xi
 *@since  :2019年11月11日
 *@version:1.0
 */
public class ExcelExportUtil {

    //excel表对象
    private Workbook workbook;
    //工作表对象
    private Sheet sheet;
    //标题
    private String title;
    //sheet各个列的表头
    private String[] headList;
    //各个列的元素key值
    private String[] headKey;
    //sheet需要填充的数据信息
    private List<Map> data;
    //工作表名称
//    private String sheetName = "sheet1";
    //工作表列宽
    private Integer columnWidth=20;
    //工作表行高
    private Integer rowHeight=10;
    //字体大小
    private int fontSize = 14;

    public static void doExportToExcel(HSSFWorkbook workbook, String[] columnKeys, List<Map> dataList, HSSFSheet sheet,String[] headers) throws IOException {
        CellStyle cellStyle = workbook.createCellStyle();
        //设置居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        //设置自动换行
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        ExcelExportUtil excelExportUtil = new ExcelExportUtil();
        excelExportUtil.setHeadKey(columnKeys);
        excelExportUtil.setData(dataList);
        excelExportUtil.setWorkbook(workbook);
        excelExportUtil.setSheet(sheet);
        excelExportUtil.writeTableHead(headers, cellStyle, 0);
        excelExportUtil.writeMainData(1);

    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public Integer getColumnWidth() {
        return columnWidth;
    }

    public void setColumnWidth(Integer columnWidth) {
        this.columnWidth = columnWidth;
    }

    public Integer getRowHeight() {
        return rowHeight;
    }

    public void setRowHeight(Integer rowHeight) {
        this.rowHeight = rowHeight;
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    public String[] getHeadKey() {
        return headKey;
    }

    public void setHeadKey(String[] headKey) {
        this.headKey = headKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getHeadList() {
        return headList;
    }

    public void setHeadList(String[] headList) {
        this.headList = headList;
    }

    public List<Map> getData() {
        return data;
    }

    public void setData(List<Map> data) {
        this.data = data;
    }





    /**
     * @return *
     * @Author
     * @Description //TODO 插入数据到表格（body）
     * @Date 2019/7/26 17:28
     * @Param startRow: 开始插入数据的行
     */
    public void writeMainData(Integer startRow) throws IOException {
        //设置每格数据的样式 （字体红色）
        CellStyle fontRed = ExcelStyleUtilFor2003.getFontStyle(this.workbook, this.fontSize, HSSFColor.RED.index);
        //设置每格数据的样式2（字体蓝色）
        CellStyle fontBlue = ExcelStyleUtilFor2003.getFontStyle(this.workbook, this.fontSize, HSSFColor.BLUE.index);
        //设置body样式
        CellStyle bodyStyle = ExcelStyleUtilFor2003.getBodyStyle(this.workbook, this.fontSize);
        CellStyle cellStyle = workbook.createCellStyle();
        //设置自动换行
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        //开始写入实体数据信息
        if (data.size() > 0 && null != data.get(0)) {
            for (int i = 0; i < data.size(); i++) {
                Row row = this.sheet.createRow(startRow);
                Map map = data.get(i);
                map.put("index",i);
                map.put("des","无");
                for (int j = 0; j < headKey.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellStyle(cellStyle);
                    //设置单个单元格的字体颜色
                    if ( "ddNum".equals(headKey[j]) || "sjNum".equals(headKey[j])) {
                        if (null != map.get("ddNum") && Integer.parseInt(map.get("ddNum").toString()) != 0) {
                            if (null == map.get("sjNum") || Integer.parseInt(map.get("sjNum").toString()) == 0) {
                                cell.setCellStyle(fontRed);
                            } else if (Integer.parseInt(map.get("ddNum").toString()) != Integer.parseInt(map.get("sjNum").toString())) {
                                if (Integer.parseInt((String) map.get("ddNum")) > Integer.parseInt((String) map.get("sjNum"))) {
                                    cell.setCellStyle(fontRed);
                                } else {
                                    cell.setCellStyle(fontBlue);
                                }
                            } else {
                                cell.setCellStyle(bodyStyle);
                            }
                        } else {
                            if (Integer.parseInt(map.get("ddNum").toString()) < Integer.parseInt(map.get("sjNum").toString())) {
                                cell.setCellStyle(fontBlue);
                            } else {
                                cell.setCellStyle(bodyStyle);
                            }
                        }
                        cell.setCellValue(Integer.parseInt(map.get(headKey[j]).toString()));
                    } else {
                        Object value = map.get(headKey[j]);
                        if (null == value) {
                            String valueN = "";
                            cell.setCellValue(valueN);
                        } else if (value instanceof Integer) {
                            Integer valueInt = Integer.valueOf(value.toString());
                            cell.setCellValue(valueInt);
                        } else if (value instanceof String) {
                            String valueStr = String.valueOf(value);
                            cell.setCellValue(valueStr);
                        } else if (value instanceof Float) {
                            Float valueStr = Float.valueOf(value.toString());
                            cell.setCellValue(valueStr);
                        } else if (value instanceof Double) {
                            Double valueStr = Double.valueOf(value.toString());
                            cell.setCellValue(valueStr);
                        } else if(value instanceof BigDecimal){
                            String valueStr = ((BigDecimal) value).stripTrailingZeros().toPlainString();
                            cell.setCellValue(valueStr);
                        }
                    }
                }
                startRow++;
            }
        }


    }


    /**
     * @return * @param null
     * @Author
     * @Description //TODO 添加表格标题
     * @Date 2019/7/26 17:58
     * @Param
     */
    public void writeTitle() throws IOException {
        checkConfig();
        //设置默认行宽
        this.sheet.setDefaultColumnWidth(20);
        //在第0行创建rows  (表标题)
        Row title = this.sheet.createRow(0);
        title.setHeightInPoints(this.rowHeight);//行高
        Cell cell = title.createCell(0);
        cell.setCellValue(this.title);
        CellStyle cellStyle = ExcelStyleUtilFor2003.getTitleStyle(this.workbook,true,16);
        cell.setCellStyle(cellStyle);
        ExcelStyleUtilFor2003.mergeCell(sheet,0,0,0, (this.headList.length - 1));
    }


    /**
     * @Author
     * @Description //TODO 添加表头
     * @Date 2019/7/26 15:41
     * @Param headRowNum: 添加表头所在行数
     * @return  *
     */

    public void writeTableHead(String[] head, CellStyle cellStyle,Integer headRowNum) {
        Row row = this.sheet.createRow(headRowNum);
        if (head.length > 0) {
            for (int i = 0; i < head.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(head[i]);
                cell.setCellStyle(cellStyle);
            }
        }
    }



    /**
     * 导出成本表
     * @param headers
     * @param cellStyle
     * @param rowNumber
     */
    public void writeTableCost(String[] headers, CellStyle cellStyle, int rowNumber) {
        Row row = this.sheet.createRow(rowNumber);
        Row row1 = this.sheet.createRow(rowNumber+1);
        for (int i = 0; i < 3; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(cellStyle);
        }
        Cell cell = row.createCell(7);
        cell.setCellValue(headers[3]);
        cell.setCellStyle(cellStyle);


        Cell cell1 = row.createCell(12);
        cell1.setCellValue(headers[4]);
        cell1.setCellStyle(cellStyle);

        for(int i = 5; i <=18; i++ ){
            cell = row1.createCell(i-3);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(cellStyle);
        }
    }


    /**
     * 检查数据配置问题
     *
     * @throws IOException 抛出数据异常类
     */
    protected void checkConfig() throws IOException {
        if (headKey == null || headList.length == 0) {
            throw new IOException("列名数组不能为空或者为NULL");
        }
        if (fontSize < 0) {
            throw new IOException("字体不能为负值");
        }
    }

    /**
     * @Author
     * @Description //TODO 写入拼接好的数据，不需要通过表头key值来对应
     * @Date 2019/7/27 11:01
     * @Param
     * @return  * @param null
     */
    public void writeMainData(List<List<String>> datas,Integer startRow){
        if(datas.size()>0){
            for(List<String> data : datas){
                Row row = this.sheet.createRow(startRow);
                for(int i =0; i<data.size(); i++){
                    Cell cell = row.createCell(i);
                    cell.setCellValue(data.get(i));
                    CellStyle cellStyle = ExcelStyleUtilFor2003.setCellBorder(this.workbook);
                    cell.setCellStyle(cellStyle);
                }
                startRow++;
            }
        }
    }



    public static void setResponseInfo(HttpServletResponse response, Workbook wb) throws IOException {
        //导出数据
        try {
            //设置Http响应头告诉浏览器下载这个附件
            response.setHeader("Content-Disposition", "attachment;Filename=" + System.currentTimeMillis() + ".xls");
            OutputStream outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IOException("导出Excel出现严重异常，异常信息：" + ex.getMessage());
        }
    }
    //压缩成zip
    public static void setResponseInfo(HttpServletResponse response, HSSFWorkbook wb, String fileName, ZipOutputStream zipOutputStream,String zipName,HttpServletRequest request) throws IOException {
        //导出数据
        try {
            //设置Http响应头告诉浏览器下载这个附件
            /*String userAgent = request.getHeader("User-Agent");
            if (userAgent.contains("MSIE")||userAgent.contains("Trident")) {
                //针对IE或者以IE为内核的浏览器：
                fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", " ");
            } else if (userAgent.contains("iphone") || userAgent.contains("ipad")) {
                //doNothing
            } else if(userAgent.contains("android")){
                //安卓
                fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", " ");
            }else{
                fileName = new String(fileName.getBytes("UTF-8"),"ISO-8859-1");
            }*/
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(zipName.getBytes("UTF-8"),"ISO8859-1") + ".zip");
            ZipEntry z = new ZipEntry(fileName +".xls");
            zipOutputStream.putNextEntry(z);
            wb.write(zipOutputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IOException("导出Excel出现严重异常，异常信息：" + ex.getMessage());
        }
    }



}
