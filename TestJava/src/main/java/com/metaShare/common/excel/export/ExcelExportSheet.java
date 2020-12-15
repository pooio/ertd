package com.metaShare.common.excel.export;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * @author Ling
 * @email qinjinlingx@gmail.com
 * @date 2018/4/22 20:36
 */
public class ExcelExportSheet {

    public ExcelExportSheet(String name, ExcelExportColumn... columns) {
        this.name = name;
        this.columns = Lists.newArrayList(columns);
    }

    public ExcelExportSheet(String name, Iterable<ExcelExportColumn> columns) {
        this.name = name;
        this.columns = Lists.newArrayList(columns);
    }

    private String name;
    private List<ExcelExportColumn> columns;
    private List<Map> dataList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ExcelExportColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<ExcelExportColumn> columns) {
        this.columns = columns;
    }

    public List<Map> getDataList() {
        return dataList;
    }

    public void setDataList(List<Map> dataList) {
        this.dataList = dataList;
    }

    public ExcelExportSheet withData(List<Map> dataList) {
        setDataList(dataList);
        return this;
    }
}
