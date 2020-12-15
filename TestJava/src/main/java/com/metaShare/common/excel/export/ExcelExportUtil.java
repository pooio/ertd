package com.metaShare.common.excel.export;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.metaShare.common.excel.utils.FieldReflectionUtil;

/**
 * Excel导出工具类
 *
 * @author Ling
 * @email qinjinlingx@gmail.com
 * @date 2018/4/22 20:04
 */
public class ExcelExportUtil {
	
	private static SXSSFWorkbook workbook;
	
	private static SXSSFSheet newSheet;
	
    private static Logger logger = LoggerFactory.getLogger(ExcelExportUtil.class);

    public static Workbook exportWorkbook(String[] info,ExcelExportSheet... sheets) {
        if (sheets == null) {
            throw new RuntimeException("导出数据不能为空");
        }
        workbook = new SXSSFWorkbook();
        
        for (ExcelExportSheet sheet : sheets) {
        	
        	System.out.println("************" + sheet.getName());
        	
        	System.out.println(sheet.getName().equals("自聘外籍雇员工资-工资单导出"));
        	
        	if(sheet.getName().equals("外籍雇员考勤年度ATO考勤兑现计算")) {
        		makeSheet1(info,workbook, sheet);
        	}else if(sheet.getName().equals("自聘外籍雇员工资-工资单导出")){
        		makeSheet2(info,workbook, sheet);
        	}else if(sheet.getName().equals("自聘外籍雇员工资-工资单导出1")){ 	
        		makeSheet3(info,workbook, sheet);
        	}else{
        		
        		makeSheet(workbook, sheet);
        	}
        }
        return workbook;
    }       
    
//    private static void desighColor(String[] info, SXSSFSheet newSheet2, String[] employeeName, String name, String position, String department) {
      private static void desighColor(String[] info, SXSSFSheet newSheet2) {
    	//合并单元格
    	newSheet2.addMergedRegion(new CellRangeAddress(0, 0, 1, 7));
    	newSheet2.addMergedRegion(new CellRangeAddress(0, 0, 13, 24));
    	newSheet2.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));
    	newSheet2.addMergedRegion(new CellRangeAddress(1, 1, 3, 12));
    	newSheet2.addMergedRegion(new CellRangeAddress(1, 1, 13, 17));
    	newSheet2.addMergedRegion(new CellRangeAddress(1, 1, 18, 21));
    	newSheet2.addMergedRegion(new CellRangeAddress(1, 1, 22, 25));
    	newSheet2.addMergedRegion(new CellRangeAddress(1, 1, 26, 32));
    	newSheet2.addMergedRegion(new CellRangeAddress(1, 1, 33, 36));
    	newSheet2.addMergedRegion(new CellRangeAddress(1, 1, 37, 39));
        
        Row row = newSheet2.createRow(0);
        
        Row row1 = newSheet2.createRow(1);
        
        Cell cell1 = row.createCell(1);
        
        Cell cell2 = row.createCell(13);
        
        Cell cell3 = row1.createCell(0);
        
        Cell cell4 = row1.createCell(13);
        
        Cell cell5 = row1.createCell(22);
        
        Cell cell6 = row1.createCell(33);
        
        Cell cell7 = row1.createCell(3);
        
        Cell cell8 = row1.createCell(18);
        
        Cell cell9 = row1.createCell(26);
        
        Cell cell10 = row1.createCell(37);
        
        cell1.setCellValue("BGP Offshore");
        
        cell2.setCellValue("Employee ATO Tracking Log");
        
        cell3.setCellValue("Employee Name:");
        
        cell4.setCellValue("Date of Hire:");
        
        cell5.setCellValue("Position:");
        
        cell6.setCellValue("Vessel Name:");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        cell7.setCellValue(info[0]);
        
        cell8.setCellValue( info[1]);
        
        cell9.setCellValue(info[2]);
        
        cell10.setCellValue(info[3]);
        
        CellStyle cellStyle1 = workbook.createCellStyle();
        
        cellStyle1.setAlignment(HorizontalAlignment.CENTER);
        
        CellStyle cellStyle2 = workbook.createCellStyle();
        
        cellStyle2.setAlignment(HorizontalAlignment.CENTER);
        
        CellStyle cellStyle3 = workbook.createCellStyle();
        
        cellStyle3.setAlignment(HorizontalAlignment.CENTER);
        
        cell1.setCellStyle(cellStyle1);
        cell2.setCellStyle(cellStyle1);
        cell3.setCellStyle(cellStyle2);
        cell4.setCellStyle(cellStyle2);
        cell5.setCellStyle(cellStyle2);
        cell6.setCellStyle(cellStyle2);
        cell7.setCellStyle(cellStyle3);
        cell8.setCellStyle(cellStyle3);
        cell9.setCellStyle(cellStyle3);
        cell10.setCellStyle(cellStyle3);
        
        Font curFont =  workbook.createFont();
        
        //设置字体类型以及大小
        curFont.setFontName("微软雅黑");
        curFont.setFontHeightInPoints((short) 15);
        
        cellStyle1.setFont(curFont);
        
        Font curFont1 =  workbook.createFont();
        
        curFont1.setFontName("微软雅黑");
        curFont1.setFontHeightInPoints((short) 10);
        
        cellStyle2.setFont(curFont1);
        
    }
    
    private static void desighColor1(String[] info,SXSSFSheet newSheet2) {
    	
    	//合并单元格
    	newSheet2.addMergedRegion(new CellRangeAddress(0, 0, 0, 11));
    	newSheet2.addMergedRegion(new CellRangeAddress(1, 1, 0, 11));
    	newSheet2.addMergedRegion(new CellRangeAddress(2, 2, 0, 3));
//    	newSheet2.addMergedRegion(new CellRangeAddress(2, 3, 0, 0));
    	newSheet2.addMergedRegion(new CellRangeAddress(2, 2, 4, 7));
    	newSheet2.addMergedRegion(new CellRangeAddress(2, 2, 8, 11));
    	
        Row row = newSheet2.createRow(0);
        
        Row row1 = newSheet2.createRow(1);
        Row row2 = newSheet2.createRow(2);
        
        Cell cell1 = row.createCell(0);
        
        Cell cell2 = row1.createCell(0);
        
        Cell cell3 = row2.createCell(4);
        
        cell1.setCellValue("Personal account information ("+info[0]+"月)"+info[1]+"("+info[2]+")");
        
        cell2.setCellValue("The SURNAME is underlined.");
        
        cell3.setCellValue("beneficiary bank");
        
//        cell3.setCellValue("beneficiary bank");
        
        CellStyle cellStyle1 = workbook.createCellStyle();
        CellStyle cellStyle2 = workbook.createCellStyle();
        
        cellStyle1.setAlignment(HorizontalAlignment.CENTER);
        cellStyle2.setAlignment(HorizontalAlignment.CENTER);
      
        Font curFont =  workbook.createFont();
        Font curFont1 =  workbook.createFont();
        
        //设置字体类型以及大小
        curFont.setFontName("微软雅黑");
        curFont.setFontHeightInPoints((short) 20);
        
        cellStyle1.setFont(curFont);
        
        curFont1.setFontName("微软雅黑");
        curFont1.setFontHeightInPoints((short) 12);
        
        cellStyle2.setFont(curFont1);
        
        cell1.setCellStyle(cellStyle1);
        cell3.setCellStyle(cellStyle2);
        
    }
    
	private static void desighColor2(String[] info,SXSSFSheet newSheet) {
	    	
	    	//合并单元格
	    	newSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 20));
	    	
	        Row row = newSheet.createRow(0);
	        
	        Cell cell = row.createCell(newSheet.getFirstRowNum());
	        
	        cell.setCellValue("Personal account information ("+info[0]+"月)");
	        
	        CellStyle cellStyle = workbook.createCellStyle();
	        
	        cellStyle.setAlignment(HorizontalAlignment.CENTER);
	      
	        Font curFont =  workbook.createFont();
	        
	        //设置字体类型以及大小
	        curFont.setFontName("微软雅黑");
	        curFont.setFontHeightInPoints((short) 12);
	        
	        cellStyle.setFont(curFont);
	        
	        cell.setCellStyle(cellStyle);
	        
	    }

    private static void makeSheet(SXSSFWorkbook workbook, ExcelExportSheet sheet) {

        if (sheet.getDataList() == null || sheet.getDataList().size() == 0) {
            throw new RuntimeException("导出数据不能为空");
        }
        if (sheet.getColumns() == null || sheet.getColumns().size() == 0) {
            throw new RuntimeException("请指定要导出的列配置");
        }

        String sheetName = sheet.getName();
        
    	newSheet = workbook.createSheet(sheetName);
        
        // sheet header row
        CellStyle[] fieldDataStyleArr = new CellStyle[sheet.getColumns().size()];
        int[] fieldWidthArr = new int[sheet.getColumns().size()];
        Row headRow = newSheet.createRow(0);
        
        for (int i = 0; i < sheet.getColumns().size(); i++) {
        	
            ExcelExportColumn column = sheet.getColumns().get(i);
            
            fieldWidthArr[i] = column.getWidth();
            CellStyle fieldDataStyle = workbook.createCellStyle();
            if (column.getAlignment() != null) {
                fieldDataStyle.setAlignment(column.getAlignment());
            }
            
            fieldDataStyleArr[i] = fieldDataStyle;
            
            CellStyle headStyle = workbook.createCellStyle();
            headStyle.cloneStyleFrom(fieldDataStyle);
            Cell cellX = headRow.createCell(i, CellType.STRING);
            //设置标头样式字体居中
            headStyle.setAlignment(HorizontalAlignment.CENTER);
            
            cellX.setCellStyle(headStyle);
            cellX.setCellValue(column.getName());
        }

        // sheet data rows
        for (int dataIndex = 0; dataIndex < sheet.getDataList().size(); dataIndex++) {
        	int rowIndex = dataIndex + 1;
            
        	Map rowData = sheet.getDataList().get(dataIndex);

            Row rowX = newSheet.createRow(rowIndex);

            for (int i = 0; i < sheet.getColumns().size(); i++) {
                ExcelExportColumn column = sheet.getColumns().get(i);
                
                if (rowData.containsKey(column.getField())) {
                    try {
                        Object fieldValue = rowData.get(column.getField());
                        
                        String value = FieldReflectionUtil.formatValue(column, fieldValue);
                        
                        Cell cellX = null;
                        
                        if (null != fieldValue && fieldValue.getClass().equals(BigDecimal.class)) {
                            if (fieldDataStyleArr[i].getAlignmentEnum() == HorizontalAlignment.GENERAL) {
                                fieldDataStyleArr[i].setAlignment(HorizontalAlignment.RIGHT);
                            }
                            cellX = rowX.createCell(i, CellType.NUMERIC);
                            
                        } else {
                            cellX = rowX.createCell(i, CellType.STRING);
                        }
                        
                        
                        cellX.setCellStyle(fieldDataStyleArr[i]);
                        cellX.setCellValue(value == null ? "" : value);
                        
                        //设置单元格样式
                        CellStyle cellStyle = workbook.createCellStyle();
                    	
                        Font font =  workbook.createFont();
                        
      	                if ( value != null && !value.equals("") ) {
      	                  //表格中单元格字体颜色修改判断
                            if(value.equals("W")){                          
           	                 	font.setColor(IndexedColors.RED.getIndex()); 
           	                 	cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
           	                 	cellStyle.setFillForegroundColor(HSSFColorPredefined.YELLOW.getIndex());
                            }else if(value.equals("T") || value.equals("Tr") || value.equals("M")){                          	
                            	font.setColor(IndexedColors.BLUE.getIndex());
                            	cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
           	                 	cellStyle.setFillForegroundColor(HSSFColorPredefined.YELLOW.getIndex());
                            }else if(value.equals("B")){                            	
                            	font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
                            	cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
           	                 	cellStyle.setFillForegroundColor(HSSFColorPredefined.YELLOW.getIndex());
                            }
      	            	}else if(value == null || value.equals("")) {
      	            		if(sheet.getName().equals("外籍雇员考勤查询")) {
      	            			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
           	                 	cellStyle.setFillForegroundColor(HSSFColorPredefined.YELLOW.getIndex());
      	            		}
      	            	}
                      
                        //设置字体居中
                        cellStyle.setAlignment(HorizontalAlignment.CENTER);
                        
                        cellStyle.setFont(font);
      	 	            cellX.setCellStyle(cellStyle);
      	 	            
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new RuntimeException("不存在列:" + column.getField());
                }
            }
        }

        // sheet finally
        newSheet.trackAllColumnsForAutoSizing();
        for (int i = 0; i < sheet.getColumns().size(); i++) {
            int fieldWidth = fieldWidthArr[i];
            if (fieldWidth > 0) {
                newSheet.setColumnWidth(i, fieldWidth);
            } else {
                newSheet.autoSizeColumn((short) i);
            }
        }
    }
    
    //外籍雇员考勤年度ATO考勤兑现
    private static void makeSheet1(String[] info,SXSSFWorkbook workbook, ExcelExportSheet sheet) {

        if (sheet.getDataList() == null || sheet.getDataList().size() == 0) {
            throw new RuntimeException("导出数据不能为空");
        }
        if (sheet.getColumns() == null || sheet.getColumns().size() == 0) {
            throw new RuntimeException("请指定要导出的列配置");
        }

        String sheetName = sheet.getName();
        
    	newSheet = workbook.createSheet(sheetName);
        
       
        // sheet header row
        CellStyle[] fieldDataStyleArr = new CellStyle[sheet.getColumns().size()];
        int[] fieldWidthArr = new int[sheet.getColumns().size()];
//        Row headRow = newSheet.createRow(0);
        
        //ATO考勤表
        Row headRow = newSheet.createRow(3);
        String name = null ;
        String position = null ;
        String department = null ;
        for (int i = 0; i < sheet.getColumns().size(); i++) {
        	
            ExcelExportColumn column = sheet.getColumns().get(i);
            
            fieldWidthArr[i] = column.getWidth();
            CellStyle fieldDataStyle = workbook.createCellStyle();
            if (column.getAlignment() != null) {
                fieldDataStyle.setAlignment(column.getAlignment());
            }
            
            fieldDataStyleArr[i] = fieldDataStyle;
            
            CellStyle headStyle = workbook.createCellStyle();
            headStyle.cloneStyleFrom(fieldDataStyle);
            Cell cellX = headRow.createCell(i, CellType.STRING);
            //设置标头样式字体居中
            headStyle.setAlignment(HorizontalAlignment.CENTER);
            
            cellX.setCellStyle(headStyle);
            cellX.setCellValue(column.getName());
        }
        
        // sheet data rows
        int rowIndex = 0;
        ExcelExportColumn column = null;
        for (int dataIndex = 0; dataIndex < sheet.getDataList().size(); dataIndex++) {
//        	int rowIndex = dataIndex + 1;

        	//ATO考勤表
        	rowIndex = dataIndex + 4;
           
        	Map rowData = sheet.getDataList().get(dataIndex);

            Row rowX = newSheet.createRow(rowIndex);
            
            for (int i = 0; i < sheet.getColumns().size(); i++) {
            	
                 column = sheet.getColumns().get(i);
                
                if (rowData.containsKey(column.getField())) {
                    try {
                        Object fieldValue = rowData.get(column.getField());
                        
                        String value = FieldReflectionUtil.formatValue(column, fieldValue);
                        
                        Cell cellX = null;
                        
                        if (null != fieldValue && fieldValue.getClass().equals(BigDecimal.class)) {
                            if (fieldDataStyleArr[i].getAlignmentEnum() == HorizontalAlignment.GENERAL) {
                                fieldDataStyleArr[i].setAlignment(HorizontalAlignment.RIGHT);
                            }
                            cellX = rowX.createCell(i, CellType.NUMERIC);
                            
                        } else {
                            cellX = rowX.createCell(i, CellType.STRING);
                        }
                        
                        
                        
                        cellX.setCellStyle(fieldDataStyleArr[i]);
                        cellX.setCellValue(value == null ? "" : value);
                        
                        //设置单元格样式
                        CellStyle cellStyle = workbook.createCellStyle();
                    	
      	                Font font =  workbook.createFont();
                        
      	                if ( value != null && !value.equals("") ) {
      	                  //表格中单元格字体颜色修改判断
                            if(value.equals("W")){                          
           	                 	font.setColor(IndexedColors.RED.getIndex()); 
                            }else if(value.equals("T")){                          	
                            	font.setColor(IndexedColors.BLUE.getIndex());
                            }else if(value.equals("B")){                            	
                            	font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
                            }
      	            	}
      	                
      	              
                      
                        //设置字体居中
                        cellStyle.setAlignment(HorizontalAlignment.CENTER);
                        
                        cellStyle.setFont(font);
      	 	            cellX.setCellStyle(cellStyle);
      	 	            
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new RuntimeException("不存在列:" + column.getField());
                }
            }
            
            
        }

        desighColor(info, newSheet);
        
        
        
        newSheet.addMergedRegion(new CellRangeAddress(newSheet.getLastRowNum(), newSheet.getLastRowNum(), 31, 33));
        
        // sheet finally
        newSheet.trackAllColumnsForAutoSizing();
        for (int i = 0; i < sheet.getColumns().size(); i++) {
            int fieldWidth = fieldWidthArr[i];
            if (fieldWidth > 0) {
                newSheet.setColumnWidth(i, fieldWidth);
            } else {
                newSheet.autoSizeColumn((short) i);
            }
        }
    }
    
    //自聘外籍雇员工资-工资单导出
    private static void makeSheet2(String[] info,SXSSFWorkbook workbook, ExcelExportSheet sheet) {

        if (sheet.getDataList() == null || sheet.getDataList().size() == 0) {
            throw new RuntimeException("导出数据不能为空");
        }
        if (sheet.getColumns() == null || sheet.getColumns().size() == 0) {
            throw new RuntimeException("请指定要导出的列配置");
        }

        String sheetName = sheet.getName();
        
    	newSheet = workbook.createSheet(sheetName);
    	
    	//工资单导出样式设计
        desighColor1(info,newSheet);
       
        // sheet header row
        CellStyle[] fieldDataStyleArr = new CellStyle[sheet.getColumns().size()];
        int[] fieldWidthArr = new int[sheet.getColumns().size()];
//        Row headRow = newSheet.createRow(0);
        
        //工资单导出
        Row headRow = newSheet.createRow(3);
        
        headRow.setHeight((short) (25*20));
        
        for (int i = 0; i < sheet.getColumns().size(); i++) {
        	
            ExcelExportColumn column = sheet.getColumns().get(i);
            
//            System.out.println("************" + column.getName() + "************" );
            
            fieldWidthArr[i] = column.getWidth();
            CellStyle fieldDataStyle = workbook.createCellStyle();
            if (column.getAlignment() != null) {
                fieldDataStyle.setAlignment(column.getAlignment());
            }
            
            fieldDataStyleArr[i] = fieldDataStyle;
            
            CellStyle headStyle = workbook.createCellStyle();
            headStyle.cloneStyleFrom(fieldDataStyle);
            Cell cellX = headRow.createCell(i, CellType.STRING);
            //设置标头样式字体居中
            headStyle.setAlignment(HorizontalAlignment.CENTER);
            headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            
            cellX.setCellStyle(headStyle);
            cellX.setCellValue(column.getName());
        }
        
        // sheet data rows
        int temp = 3;
        
        CellStyle cellStyle ;
        
        for (int dataIndex = 0; dataIndex < sheet.getDataList().size(); dataIndex++) {
//        	int rowIndex = dataIndex + 1;
        	temp++;
        	
        	//工资单导出
        	int rowIndex = dataIndex + 4;
           
        	Map rowData = sheet.getDataList().get(dataIndex);

            Row rowX = newSheet.createRow(rowIndex);
            
            for (int i = 0; i < sheet.getColumns().size(); i++) {
            	
                ExcelExportColumn column = sheet.getColumns().get(i);
                
                if (rowData.containsKey(column.getField())) {
                    try {
                        Object fieldValue = rowData.get(column.getField());
                        
                        String value = FieldReflectionUtil.formatValue(column, fieldValue);
                        
                        Cell cellX = null;
                        
                        if (null != fieldValue && fieldValue.getClass().equals(BigDecimal.class)) {
                            if (fieldDataStyleArr[i].getAlignmentEnum() == HorizontalAlignment.GENERAL) {
                                fieldDataStyleArr[i].setAlignment(HorizontalAlignment.RIGHT);
                            }
                            cellX = rowX.createCell(i, CellType.NUMERIC);
                            
                        } else {
                            cellX = rowX.createCell(i, CellType.STRING);
                        }
                       
                        //设置单元格样式
                        cellStyle = workbook.createCellStyle();
                        
                        cellX.setCellStyle(fieldDataStyleArr[i]);
                        
                        
                        cellX.setCellValue(value == null ? "" : value);
                        
                        
                        //设置字体居中
                        cellStyle.setAlignment(HorizontalAlignment.CENTER);
                        
      	 	            cellX.setCellStyle(cellStyle);
      	 	            
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new RuntimeException("不存在列:" + column.getField());
                }
            }
            
            
        }
        
        /*if((temp+2) == (newSheet.getLastRowNum()+2)) {
          	
          	Row row = newSheet.createRow(newSheet.getLastRowNum()+2);
          	
          	Cell cell = row.createCell(0);
//          	Cell cell1 = row.createCell(1);
            
//          	cell.setCellValue("total number  ");
          	
//          	cell1.setCellFormula("sum(C5:C" + newSheet.getLastRowNum()+ ")");
          	
          	cellStyle = workbook.createCellStyle();
          	Font font = workbook.createFont();
          	font.setFontName("微软雅黑");
          	font.setFontHeightInPoints((short) 9);;
          	cellStyle.setFont(font);
          	
          	cell.setCellStyle(cellStyle);
          			
          }*/
        
        newSheet.addMergedRegion(new CellRangeAddress(newSheet.getLastRowNum()+1, newSheet.getLastRowNum()+1, 0, 3));
        newSheet.addMergedRegion(new CellRangeAddress(newSheet.getLastRowNum()+1, newSheet.getLastRowNum()+1, 4, 7));
        newSheet.addMergedRegion(new CellRangeAddress(newSheet.getLastRowNum()+1, newSheet.getLastRowNum()+1, 8, 11));
        
        if((temp+1) == (newSheet.getLastRowNum()+1)) {
          	
          	Row row = newSheet.createRow(newSheet.getLastRowNum()+1);
          	
          	Cell cell = row.createCell(0);
          	
          	Cell cell1 = row.createCell(4);
          	
          	Cell cell2 = row.createCell(8);
            
          	cell.setCellValue("Confirmed by:  ");
          	cell1.setCellValue("Made by:  ");
          	cell2.setCellValue("海洋物探处: ");
          	
          }

        // sheet finally
        newSheet.trackAllColumnsForAutoSizing();
        for (int i = 0; i < sheet.getColumns().size(); i++) {
            int fieldWidth = fieldWidthArr[i];
            if (fieldWidth > 0) {
                newSheet.setColumnWidth(i, fieldWidth);
            } else {
                newSheet.autoSizeColumn((short) i);
            }
        }
    }
    
    private static void makeSheet3(String[] info,SXSSFWorkbook workbook, ExcelExportSheet sheet) {

        if (sheet.getDataList() == null || sheet.getDataList().size() == 0) {
            throw new RuntimeException("导出数据不能为空");
        }
        if (sheet.getColumns() == null || sheet.getColumns().size() == 0) {
            throw new RuntimeException("请指定要导出的列配置");
        }

        String sheetName = sheet.getName();
        
    	newSheet = workbook.createSheet(sheetName);
        
        // sheet header row
        CellStyle[] fieldDataStyleArr = new CellStyle[sheet.getColumns().size()];
        int[] fieldWidthArr = new int[sheet.getColumns().size()];
        Row headRow = newSheet.createRow(1);
        
        for (int i = 0; i < sheet.getColumns().size(); i++) {
        	
            ExcelExportColumn column = sheet.getColumns().get(i);
            
            fieldWidthArr[i] = column.getWidth();
            CellStyle fieldDataStyle = workbook.createCellStyle();
            if (column.getAlignment() != null) {
                fieldDataStyle.setAlignment(column.getAlignment());
            }
            
            fieldDataStyleArr[i] = fieldDataStyle;
            
            CellStyle headStyle = workbook.createCellStyle();
            headStyle.cloneStyleFrom(fieldDataStyle);
            Cell cellX = headRow.createCell(i, CellType.STRING);
            //设置标头样式字体居中
            headStyle.setAlignment(HorizontalAlignment.CENTER);
            
            cellX.setCellStyle(headStyle);
            cellX.setCellValue(column.getName());
        }

        // sheet data rows
        for (int dataIndex = 0; dataIndex < sheet.getDataList().size(); dataIndex++) {
        	int rowIndex = dataIndex + 2;
            
        	Map rowData = sheet.getDataList().get(dataIndex);

            Row rowX = newSheet.createRow(rowIndex);
            
            for (int i = 0; i < sheet.getColumns().size(); i++) {
                ExcelExportColumn column = sheet.getColumns().get(i);
                
                if (rowData.containsKey(column.getField())) {
                    try {
                        Object fieldValue = rowData.get(column.getField());
                        
                        String value = FieldReflectionUtil.formatValue(column, fieldValue);
                        
                        Cell cellX = null;
                        
                        if(column.getField().equals("Sallary Level/day rate-work")){
                        	
                        	Integer value1 = Integer.valueOf(value);
                        	
                        }
                        
                        
                        if (null != fieldValue && fieldValue.getClass().equals(BigDecimal.class)) {
                            if (fieldDataStyleArr[i].getAlignmentEnum() == HorizontalAlignment.GENERAL) {
                                fieldDataStyleArr[i].setAlignment(HorizontalAlignment.RIGHT);
                            }
                            cellX = rowX.createCell(i, CellType.NUMERIC);
                            
                        } else {
                            cellX = rowX.createCell(i, CellType.STRING);
                        }
                       
                        cellX.setCellStyle(fieldDataStyleArr[i]);
                        cellX.setCellValue(value == null ? "" : value);
                        
                        //设置单元格样式
                        CellStyle cellStyle = workbook.createCellStyle();
                    	
                        Font font =  workbook.createFont();
                        
      	                if ( value != null && !value.equals("") ) {
      	                  //表格中单元格字体颜色修改判断
                            if(value.equals("W")){                          
           	                 	font.setColor(IndexedColors.RED.getIndex()); 
           	                 	cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
           	                 	cellStyle.setFillForegroundColor(HSSFColorPredefined.YELLOW.getIndex());
                            }else if(value.equals("T") || value.equals("Tr") || value.equals("M")){                          	
                            	font.setColor(IndexedColors.BLUE.getIndex());
                            	cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
           	                 	cellStyle.setFillForegroundColor(HSSFColorPredefined.YELLOW.getIndex());
                            }else if(value.equals("B")){                            	
                            	font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
                            	cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
           	                 	cellStyle.setFillForegroundColor(HSSFColorPredefined.YELLOW.getIndex());
                            }
      	            	}else if(value == null || value.equals("")) {
      	            		if(sheet.getName().equals("外籍雇员考勤查询")) {
      	            			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
           	                 	cellStyle.setFillForegroundColor(HSSFColorPredefined.YELLOW.getIndex());
      	            		}
      	            	}
                      
                        //设置字体居中
                        cellStyle.setAlignment(HorizontalAlignment.CENTER);
                        
                        cellStyle.setFont(font);
      	 	            cellX.setCellStyle(cellStyle);
      	 	            
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new RuntimeException("不存在列:" + column.getField());
                }
            }
        }	
        
        	desighColor2(info,newSheet);
          	
          	newSheet.addMergedRegion(new CellRangeAddress(newSheet.getLastRowNum(), newSheet.getLastRowNum(),0, 2));
          	newSheet.addMergedRegion(new CellRangeAddress(newSheet.getLastRowNum(), newSheet.getLastRowNum(),3, 9));
          	newSheet.addMergedRegion(new CellRangeAddress(newSheet.getLastRowNum(), newSheet.getLastRowNum(),10, 13));
          	newSheet.addMergedRegion(new CellRangeAddress(newSheet.getLastRowNum(), newSheet.getLastRowNum(),14, 20));
          	newSheet.addMergedRegion(new CellRangeAddress(newSheet.getLastRowNum()-1, newSheet.getLastRowNum()-1,0, 2));
          	newSheet.addMergedRegion(new CellRangeAddress(newSheet.getLastRowNum()-1, newSheet.getLastRowNum()-1,3, 18));
          	
          	
          	
        // sheet finally
        newSheet.trackAllColumnsForAutoSizing();
        for (int i = 0; i < sheet.getColumns().size(); i++) {
            int fieldWidth = fieldWidthArr[i];
            if (fieldWidth > 0) {
                newSheet.setColumnWidth(i, fieldWidth);
            } else {
                newSheet.autoSizeColumn((short) i);
            }
        }
    }
    
    public static void exportToFile(String[] info ,String filePath, ExcelExportSheet... sheets) {
        
    	//创建工作簿
    	Workbook workbook = exportWorkbook(info,sheets);
    	
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filePath);
            
            workbook.write(fileOutputStream);
            
            // flush
            fileOutputStream.flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }

    public static byte[] exportToBytes(ExcelExportSheet... sheets) {
        Workbook workbook = exportWorkbook(null,sheets);

        ByteArrayOutputStream byteArrayOutputStream = null;
        byte[] result = null;
        try {
            // workbook 2 ByteArrayOutputStream
            byteArrayOutputStream = new ByteArrayOutputStream();
            workbook.write(byteArrayOutputStream);

            // flush
            byteArrayOutputStream.flush();

            result = byteArrayOutputStream.toByteArray();
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }
}
