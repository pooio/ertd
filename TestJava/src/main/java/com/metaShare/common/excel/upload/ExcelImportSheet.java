package com.metaShare.common.excel.upload;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author Ling
 * @email qinjinlingx@gmail.com
 * @date 2018/4/24 14:57
 */
public class ExcelImportSheet {

    private String name;
    private int headerIndex;
    private int dataStartIndex;
    private List<ExcelImportColumn> columns;
    private Class<?> sheetClass;

    public ExcelImportSheet(String name, int headerIndex, int dataStartIndex, ExcelImportColumn... columns) {
        this.name = name;
        this.headerIndex = headerIndex;
        this.dataStartIndex = dataStartIndex;
        this.columns = Lists.newArrayList(columns);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeaderIndex() {
        return headerIndex;
    }

    public void setHeaderIndex(int headerIndex) {
        this.headerIndex = headerIndex;
    }

    public int getDataStartIndex() {
        return dataStartIndex;
    }

    public void setDataStartIndex(int dataStartIndex) {
        this.dataStartIndex = dataStartIndex;
    }

    public List<ExcelImportColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<ExcelImportColumn> columns) {
        this.columns = columns;
    }

    public Class<?> getSheetClass() {
        return sheetClass;
    }

    public void setSheetClass(Class<?> sheetClass) {
        this.sheetClass = sheetClass;
    }

    public ExcelImportSheet withClass(Class<?> sheetClass) {
        setSheetClass(sheetClass);
        return this;
    }
}
