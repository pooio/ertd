package com.metaShare.common.excel.upload;

/**
 * Excel单元格转化器
 *
 * @author Ling
 * @email qinjinlingx@gmail.com
 * @date 2018/4/24 10:38
 */
public interface ExcelCellConverter {

    /**
     * 将单元格里面的值转为需要的类型
     *
     * @param cellValue
     * @return
     */
    Object convert(String cellValue) throws Exception;
}
