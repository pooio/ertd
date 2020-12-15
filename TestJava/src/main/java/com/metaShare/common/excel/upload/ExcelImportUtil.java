package com.metaShare.common.excel.upload;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.metaShare.common.excel.utils.FieldReflectionUtil;

/**
 * Excel导入工具类
 *
 * @author Ling
 * @email qinjinlingx@gmail.com
 * @date 2018/4/24 14:29
 */
public class ExcelImportUtil {

    private static Logger logger = LoggerFactory.getLogger(ExcelImportUtil.class);

    /**
     * 从Workbook导入Excel文件，并封装成对象
     *
     * @param workbook
     * @param importSheet
     * @return
     */
    public static List<Object> importSheet(Workbook workbook, ExcelImportSheet importSheet) throws Exception {
        Class<?> sheetClass = importSheet.getSheetClass();
        String sheetName = importSheet.getName();
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new Exception("不存在此Sheet: " + sheetName);
        }

        Map<String, List<String>> uniqueMap = Maps.newHashMap();
        Map<String, Integer> headerMap = getHeaderMap(sheet, importSheet.getHeaderIndex());
        Iterator<Row> sheetIterator = sheet.rowIterator();
        int rowIndex = 0;
        List<Object> dataList = new ArrayList<Object>();
        while (sheetIterator.hasNext()) {
            Row rowX = sheetIterator.next();
            if (rowIndex >= importSheet.getDataStartIndex()) {
                Object rowObj = sheetClass.newInstance();
                for (int i = 0; i < importSheet.getColumns().size(); i++) {
                    ExcelImportColumn column = importSheet.getColumns().get(i);
                    Object object = column.getName();
                    Cell cell = rowX.getCell(headerMap.get(object));
                    if (cell == null) {
                        continue;
                    }
                    cell.setCellType(CellType.STRING);
                    //String cellValue = cell.getStringCellValue();
                    String cellValue = cell.toString();

                    if (column.isCheckUnique()) {
                        checkUnique(uniqueMap, rowIndex, column, cellValue);
                    }
                    Field field = sheetClass.getDeclaredField(column.getField());
                    Object fieldValue = null;
                    try {
                        if (column.getConverter() != null) {
                            fieldValue = column.getConverter().convert(cellValue);
                        } else {
                            fieldValue = FieldReflectionUtil.parseValue(field, column, cellValue);
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(String.format("第%d行[%s]: %s",
                                rowIndex + 1, column.getName(), ex.getMessage()));
                    }

                    if (fieldValue != null) {
                        field.setAccessible(true);
                        field.set(rowObj, fieldValue);
                    }
                }
                dataList.add(rowObj);
            }
            rowIndex++;
        }
        return dataList;
    }

    // 检查列中的值是否重复
    private static void checkUnique(Map<String, List<String>> uniqueMap, int rowIndex, ExcelImportColumn column, String cellValue) {
        List<String> ulist = Lists.newArrayList();
        if (uniqueMap.containsKey(column.getField())) {
            ulist = uniqueMap.get(column.getField());
            if (ulist.contains(cellValue))
                throw new RuntimeException(String.format("第%d行[%s]: 存在重复数据", rowIndex + 1, column.getName()));
            ulist.add(cellValue);
        } else {
            ulist.add(cellValue);
            uniqueMap.put(column.getField(), ulist);
        }
    }

    // 获取表头标题和索引的Map
    private static Map<String, Integer> getHeaderMap(Sheet sheet, int headerIndex) {
        Map<String, Integer> headerMap = Maps.newHashMap();
        Row headerRow = sheet.getRow(headerIndex);
        Iterator<Cell> headerCellIterator = headerRow.cellIterator();
        int cellIndex = 0;
        while (headerCellIterator.hasNext()) {
            Cell cellX = headerCellIterator.next();
            //String title = cellX.getStringCellValue();
            String title = cellX.toString();
            if (!Strings.isNullOrEmpty(title)) {
                headerMap.put(title, cellX.getColumnIndex());
            }
        }
        return headerMap;
    }

    /**
     * 导入Excel文件，并封装成对象
     *
     * @param excelFile
     * @param importSheet
     * @return
     */
    public static List<Object> importExcel(File excelFile, ExcelImportSheet importSheet) throws Exception {
        Workbook workbook = WorkbookFactory.create(excelFile);
        List<Object> dataList = importSheet(workbook, importSheet);
        return dataList;
    }

    /**
     * 从文件路径导入Excel文件，并封装成对象
     *
     * @param filePath
     * @param importSheet
     * @return
     */
    public static List<Object> importExcel(String filePath, ExcelImportSheet importSheet) throws Exception {
        File excelFile = new File(filePath);
        List<Object> dataList = importExcel(excelFile, importSheet);
        return dataList;
    }

    /**
     * 导入Excel数据流，并封装成对象
     *
     * @param inputStream
     * @param importSheet
     * @return
     */
    public static List<Object> importExcel(InputStream inputStream, ExcelImportSheet importSheet) throws Exception {
        Workbook workbook = WorkbookFactory.create(inputStream);
        List<Object> dataList = importSheet(workbook, importSheet);
        return dataList;
    }

}
