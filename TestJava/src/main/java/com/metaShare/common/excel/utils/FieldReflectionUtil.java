package com.metaShare.common.excel.utils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.metaShare.common.excel.export.ExcelExportColumn;
import com.metaShare.common.excel.upload.ExcelImportColumn;

/**
 * @author Ling
 * @email qinjinlingx@gmail.com
 * @date 2018/4/22 17:22
 */
public class FieldReflectionUtil {

    private FieldReflectionUtil() {
    }

    public static Byte parseByte(String value) {
        try {
            value = value.replaceAll("　", "");
            return Byte.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("数值转换失败: " + value, e);
        }
    }

    public static Boolean parseBoolean(String value) {
        value = value.replaceAll("　", "");
        if (Boolean.TRUE.toString().equalsIgnoreCase(value)) {
            return Boolean.TRUE;
        } else if (Boolean.FALSE.toString().equalsIgnoreCase(value)) {
            return Boolean.FALSE;
        } else {
            throw new RuntimeException("布尔类型转换失败: " + value);
        }
    }

    public static Integer parseInt(String value) {
        try {
            value = value.replaceAll("　", "");
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("数值转换失败: " + value, e);
        }
    }

    public static Short parseShort(String value) {
        try {
            value = value.replaceAll("　", "");
            return Short.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("数值转换失败: " + value, e);
        }
    }

    public static Long parseLong(String value) {
        try {
            value = value.replaceAll("　", "");
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("数值转换失败: " + value, e);
        }
    }

    public static Float parseFloat(String value) {
        try {
            value = value.replaceAll("　", "");
            return Float.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("数值转换失败: " + value, e);
        }
    }

    public static Double parseDouble(String value) {
        try {
            value = value.replaceAll("　", "");
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("数值转换失败: " + value, e);
        }
    }

    public static BigDecimal parseBigDecimal(String value) {
        try {
            value = value.replaceAll("　", "");
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator(',');
            symbols.setDecimalSeparator('.');
            String pattern = "#,##0.0#";
            DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
            decimalFormat.setParseBigDecimal(true);
            BigDecimal bigDecimal = (BigDecimal) decimalFormat.parse(value);
            return bigDecimal;
        } catch (ParseException e) {
            throw new RuntimeException("数值转换失败: " + value, e);
        }
    }

    public static Date parseDate(String value, String datePattern) {
        try {
        	String splitChar = "-";
        	if(value.indexOf("/") != -1){
        		splitChar = "/";
        	}

        	String newValue = "";
        	for(String v : value.split(splitChar)){
        		if(v.length() == 1){
        			newValue = newValue + "0" + v;
        		}else{
        			newValue = newValue + v;
        		}
        	}
        	
            SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
            return dateFormat.parse(newValue);
        } catch (ParseException e) {
            throw new RuntimeException("日期转换失败: " + value, e);
        }
    }

    /**
     * 参数解析
     *
     * @param field
     * @param value
     * @return
     */
    public static Object parseValue(Field field, ExcelImportColumn column, String value) {
        if (value == null || value.trim().length() == 0)
            return null;
        value = value.trim();
        Class<?> fieldType = field.getType();
        if (Byte.class.equals(fieldType) || Byte.TYPE.equals(fieldType)) {
            return parseByte(value);
        } else if (Boolean.class.equals(fieldType) || Boolean.TYPE.equals(fieldType)) {
            return parseBoolean(value);
        } else if (Character.class.equals(fieldType) || Character.TYPE.equals(fieldType)) {
            return value.toCharArray()[0];
        } else if (String.class.equals(fieldType)) {
            return value;
        } else if (Short.class.equals(fieldType) || Short.TYPE.equals(fieldType)) {
            return parseShort(value);
        } else if (Integer.class.equals(fieldType) || Integer.TYPE.equals(fieldType)) {
            return parseInt(value);
        } else if (Long.class.equals(fieldType) || Long.TYPE.equals(fieldType)) {
            return parseLong(value);
        } else if (Float.class.equals(fieldType) || Float.TYPE.equals(fieldType)) {
            return parseFloat(value);
        } else if (Double.class.equals(fieldType) || Double.TYPE.equals(fieldType)) {
            return parseDouble(value);
        } else if (BigDecimal.class.equals(fieldType)) {
            return parseBigDecimal(value);
        } else if (Date.class.equals(fieldType)) {
            return parseDate(value, column.getDateFormat());
        } else {
            throw new RuntimeException("未支持的类型: " + fieldType);
        }
    }

    /**
     * 参数格式化为String
     *
     * @param field
     * @param value
     * @return
     */
    public static String formatValue(ExcelExportColumn column, Object value) {
        if (value == null) {
            return null;
        }

        Class<?> fieldType = value.getClass();
        if (Boolean.class.equals(fieldType) || Boolean.TYPE.equals(fieldType)) {
            return String.valueOf(value);
        } else if (String.class.equals(fieldType)) {
            return String.valueOf(value);
        } else if (Short.class.equals(fieldType) || Short.TYPE.equals(fieldType)) {
            return String.valueOf(value);
        } else if (Integer.class.equals(fieldType) || Integer.TYPE.equals(fieldType)) {
            return String.valueOf(value);
        } else if (Long.class.equals(fieldType) || Long.TYPE.equals(fieldType)) {
            return String.valueOf(value);
        } else if (Float.class.equals(fieldType) || Float.TYPE.equals(fieldType)) {
            return String.valueOf(value);
        } else if (Double.class.equals(fieldType) || Double.TYPE.equals(fieldType)) {
            return String.valueOf(value);
        } else if (BigDecimal.class.equals(fieldType)) {
            String numFormat = column.getNumFormat();
            DecimalFormat decimalFormat = new DecimalFormat(numFormat);
            return decimalFormat.format(value);
        } else if (Date.class.equals(fieldType) || Timestamp.class.equals(fieldType)) {
            String datePattern = column.getDateFormat();
            SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
            return dateFormat.format(value);
        }else if (java.sql.Date.class.equals(fieldType) || java.sql.Timestamp.class.equals(fieldType)) {
            String datePattern = column.getDateFormat();
            SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
            return dateFormat.format(value);
        } else {
            throw new RuntimeException("未支持的类型: " + fieldType);
        }
    }

}
