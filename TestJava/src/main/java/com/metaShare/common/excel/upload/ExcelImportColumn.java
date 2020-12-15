package com.metaShare.common.excel.upload;

/**
 * 表示Excel中要导入的列信息
 *
 * @author Ling
 * @email qinjinlingx@gmail.com
 * @date 2018/4/24 10:30
 */
public class ExcelImportColumn {

    private String name;
    private String field;
    private String dateFormat = "yyyyMMdd";
    private ExcelCellConverter converter;
    private boolean checkUnique;

    public ExcelImportColumn(String name, String field) {
        this(name, field, false);
    }

    public ExcelImportColumn(String name, String field, boolean checkUnique) {
        this.name = name;
        this.field = field;
        this.checkUnique = checkUnique;
    }

    public ExcelImportColumn(String name, String field, ExcelCellConverter converter) {
        this(name, field, converter, false);
    }

    public ExcelImportColumn(String name, String field, ExcelCellConverter converter, boolean checkUnique) {
        this.name = name;
        this.field = field;
        this.converter = converter;
        this.checkUnique = checkUnique;
    }

    public ExcelImportColumn dateFormat(String format) {
        setDateFormat(format);
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public ExcelCellConverter getConverter() {
        return converter;
    }

    public void setConverter(ExcelCellConverter converter) {
        this.converter = converter;
    }

    public boolean isCheckUnique() {
        return checkUnique;
    }

    public void setCheckUnique(boolean checkUnique) {
        this.checkUnique = checkUnique;
    }
}
