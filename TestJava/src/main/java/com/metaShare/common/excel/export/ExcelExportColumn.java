package com.metaShare.common.excel.export;

import org.apache.poi.ss.usermodel.HorizontalAlignment;

/**
 * 表示Excel中需要导出的列信息
 * @author Ling
 * @email qinjinlingx@gmail.com
 * @date 2018/4/22 18:26
 */
public class ExcelExportColumn {

    private String name;
    private String field;
    private String dateFormat = "yyyy-MM-dd";
    private String numFormat = "0.00";
    private HorizontalAlignment alignment;
    private int width; // 占几个字符的宽度

    public ExcelExportColumn(String name, String field) {
        this.name = name;
        this.field = field;
    }

    public ExcelExportColumn dateFormat(String format) {
        setDateFormat(format);
        return this;
    }

    public ExcelExportColumn numFormat(String format) {
        setNumFormat(format);
        return this;
    }

    public ExcelExportColumn alignment(HorizontalAlignment alignment) {
        setAlignment(alignment);
        return this;
    }

    public ExcelExportColumn width(int width) {
        setWidth(width);
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

    public String getNumFormat() {
        return numFormat;
    }

    public void setNumFormat(String numFormat) {
        this.numFormat = numFormat;
    }

    public HorizontalAlignment getAlignment() {
        return alignment;
    }

    public void setAlignment(HorizontalAlignment alignment) {
        this.alignment = alignment;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width * 256;
    }
}
