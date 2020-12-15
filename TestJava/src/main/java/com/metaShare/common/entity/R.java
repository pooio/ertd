package com.metaShare.common.entity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.metaShare.common.excel.export.ExcelExportSheet;
import com.metaShare.common.excel.export.ExcelExportUtil;
import com.metaShare.common.utils.DateTimeUtils;
import com.metaShare.common.utils.SpringContextUtils;
import com.metaShare.config.SeedConfig;

/**
 * 页面响应entity
 */
public class R extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    public R() {
        put("code", 0);
    }

    public static R error() {
        return error(500, "数据异常，请重新输入");
    }

    public static R error(String msg) {
        return error(500, msg);
    }

    public static R error(int code, String msg) {
        R r = new R();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static R ok() {
        return new R();
    }

    public static R ok(String msg) {
        R r = new R();
        r.put("msg", msg);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    public static R ok(Page page) {
        R r = new R();
        r.put("total", page.getTotal());
        r.put("rows", page.getRecords());
        return r;
    }

    public R export(ExcelExportSheet... sheets) {
        SeedConfig config = SpringContextUtils.getBean(SeedConfig.class);
        Path path = Paths.get(config.getFileTempPath());
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String name = sheets[0].getName();
        String filePath = path.resolve(DateTimeUtils.format(new Date(), "yyyyMMddHHmmss") + "-" + name + ".xlsx").toString();
        String b64Path = Base64.getEncoder().encodeToString(filePath.getBytes(StandardCharsets.UTF_8));
        ExcelExportUtil.exportToFile(null,filePath, sheets);
        put("Location", "/export/excel/" + b64Path);
        //put("Location", "modules/timesheet/list_foreign.html");
        return this;
    }
    
    public R export(String[] info,ExcelExportSheet... sheets) {
        SeedConfig config = SpringContextUtils.getBean(SeedConfig.class);
        Path path = Paths.get(config.getFileTempPath());
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String name = sheets[0].getName();
        String filePath = path.resolve(DateTimeUtils.format(new Date(), "yyyyMMddHHmmss") + "-" + name + ".xlsx").toString();
        String b64Path = Base64.getEncoder().encodeToString(filePath.getBytes(StandardCharsets.UTF_8));
        ExcelExportUtil.exportToFile(info,filePath, sheets);
        put("Location", "/export/excel/" + b64Path);
        //put("Location", "modules/timesheet/list_foreign.html");
        return this;
    }
    @Override
    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
