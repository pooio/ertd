package com.metaShare.modules.core.excel;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.metaShare.common.utils.HttpContextUtils;
import com.metaShare.config.SeedConfig;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 导入模板
 *
 * @author Ling
 * @email qinjinlingx@gmail.com
 * @date 2018/4/24 21:09
 */
@Component
public class ImportTemplate {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SeedConfig seedConfig;

    public static final Map<String, String> NAME_MAP = ImmutableMap.<String, String>builder()
            .put("", "")
            .build();

    public String sync(String excelName) throws IOException, InvalidFormatException {
        File path = new File(ResourceUtils.getURL("classpath:").getPath());
        // 本地路径
        // String filePath = path.getAbsolutePath() + File.separator + "static" + File.separator + "excel" + File.separator + excelName + ".xlsx";
        // 发布路径
        //String filePath = seedConfig.getTempPath() + File.separator + "static" + File.separator + "excel" + File.separator + excelName + ".xlsx";
        // 8.30发布地址
        File directory = new File("");// 参数为空
        String filePath = seedConfig.getTempPath() + File.separator + "excel" + File.separator + excelName + ".xlsx";
        File templateFile = new File(filePath);
        Workbook workbook = WorkbookFactory.create(templateFile);
        Sheet sheet = workbook.getSheetAt(0);
        Integer num = workbook.getNumberOfSheets();
        for (int n = 1; n < num; n++) {
            Sheet dictSheet = workbook.getSheetAt(n);
            Row configRow = dictSheet.getRow(0);

            String name = dictSheet.getSheetName();
            Name range = workbook.getName(dictSheet.getSheetName());
            String sql = NAME_MAP.get(dictSheet.getSheetName());
            if (range != null && !Strings.isNullOrEmpty(sql)) {
                List<String> source = jdbcTemplate.queryForList(sql, String.class);
                for (int i = 0; i < source.size(); i++) {
                    Row row = dictSheet.createRow(i);
                    Cell cell = row.createCell(0);
                    cell.setCellValue(source.get(i));
                }
                // 添加引用关系
                //range.setRefersToFormula(StringUtils.stripEnd(range.getRefersToFormula(), "2") + source.size());
            }
        }

        FileOutputStream fileOutputStream = null;
        try {
            String downloadPath = FilenameUtils.concat(seedConfig.getFileTempPath(), excelName + ".xlsx");
            fileOutputStream = new FileOutputStream(downloadPath);
            workbook.write(fileOutputStream);
            return downloadPath;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
