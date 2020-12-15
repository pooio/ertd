package com.metaShare.common.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * 秦雪工具类
 * @author 秦雪
 *
 */
public class QxUtil {

	/**
	 * Date转换为指定格式的日期字符串
	 * @param date 日期
	 * @param pattern 匹配格式,如:yyyy-MM-dd HH:mm:ss
	 * @return  转换后的字符串
	 */
	public static String dateToStr(Date date, String pattern){
		if (date == null) {
			return null;
		}
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(pattern);
			return formatter.format(date);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 日期字符串转换为Date
	 * @param date 日期字符串
	 * @param pattern 匹配格式,如:yyyy-MM-dd HH:mm:ss
	 * @return 转换后的日期
	 */
	public static Date strToDate(String date, String pattern) {
		if (date == null || date.equals("")) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		try {
			if(date.length() < 12)
			{
				date += " 00:00:00";
			}
			return formatter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * 给逗号分隔的串加引号, 便于sql中的in
	 * @param s
	 * @return
	 */
	public static String addSingleQuote(String s){
		if(StringUtils.isEmpty(s)){
			return "";
		}
		String[] ss = s.split(",");
		String result = "'" + ss[0] + "'";
		for(int i=1;i<ss.length;i++){
			result += ",'" + ss[i] + "'";
		}
		return result;
	}
	
	/**
	 * 从一个map的list中找到key, value相同的map
	 * @param list
	 * @param key
	 * @param value
	 * @return
	 */
	public static Map getMapByValue(List list,String key, String value) {   
		for(Object o : list){
			Map map = (Map)o;
			if(((String)(map.get(key))).equals(value)){
				return map;
			}
		}
       return null;   
    }
	
	 /**  
     * 对double数据进行取精度.  
     * @param value  double数据.  
     * @param scale  精度位数(保留的小数位数).  
     * @param roundingMode  精度取值方式.  
     * @return 精度计算后的数据.  
     */  
    public static double round(double value, int scale, 
             int roundingMode) {   
        BigDecimal bd = new BigDecimal(value);   
        bd = bd.setScale(scale, roundingMode);   
        double d = bd.doubleValue();   
        bd = null;   
        return d;   
    }   


     /** 
     * double 相加 
     * @param d1 
     * @param d2 
     * @return 
     */ 
    public static double sum(double... d){ 
    	double result = 0d;
    	for(double d1 : d){
    		BigDecimal bd1 = new BigDecimal(Double.toString(result)); 
            BigDecimal bd2 = new BigDecimal(Double.toString(d1)); 
            result = bd1.add(bd2).doubleValue(); 
    	}
        return result; 
    } 


    /** 
     * double 相减 
     * @param d1 
     * @param d2 
     * @return 
     */ 
    public static double sub(double d1,double d2){ 
        BigDecimal bd1 = new BigDecimal(Double.toString(d1)); 
        BigDecimal bd2 = new BigDecimal(Double.toString(d2)); 
        return bd1.subtract(bd2).doubleValue(); 
    } 
    
    public static double sub(double d1,double... d2){ 
    	double result = d1;
    	for(double d : d2){
    		BigDecimal bd1 = new BigDecimal(Double.toString(result)); 
            BigDecimal bd2 = new BigDecimal(Double.toString(d)); 
            result = bd1.subtract(bd2).doubleValue(); 
    	}
    	return result; 
    } 
    

    /** 
     * double 乘法 
     * @param d1 
     * @param d2 
     * @return 
     */ 
    public static double mul(double... d){ 
    	double result = 1d;
    	for(double d1 : d){
    		BigDecimal bd1 = new BigDecimal(Double.toString(result)); 
            BigDecimal bd2 = new BigDecimal(Double.toString(d1)); 
            result = bd1.multiply(bd2).doubleValue(); 
    	}
        return result; 
    } 


    /** 
     * double 除法 
     * @param d1 
     * @param d2 
     * @param scale 四舍五入 小数点位数 
     * @return 
     */ 
    public static double div(double d1,double d2,int scale, int roundingMode){ 
        //  当然在此之前，你要判断分母是否为0，   
        //  为0你可以根据实际需求做相应的处理 

        BigDecimal bd1 = new BigDecimal(Double.toString(d1)); 
        BigDecimal bd2 = new BigDecimal(Double.toString(d2)); 
        return bd1.divide 
               (bd2,scale,roundingMode).doubleValue(); 
    } 
	
    /**
     * 得到两个日期之间的间隔天数
     * @param beginDate
     * @param endDate
     * @return
     * @throws ParseException
     */
    public static int getDaysBetween (Date bDate, Date eDate) 
    {
	  Calendar d1 = new GregorianCalendar();
	
	  d1.setTime(bDate);
	
	  Calendar d2 = new GregorianCalendar();
	
	  d2.setTime(eDate);
	
	  int days = d2.get(Calendar.DAY_OF_YEAR) - d1.get(Calendar.DAY_OF_YEAR);
	
	  int y2 = d2.get(Calendar.YEAR);
	
	  if (d1.get(Calendar.YEAR) != y2)
	
	  {
	
	  d1 = (Calendar) d1.clone();
	
	  do   {
	
	  days += d1.getActualMaximum(Calendar.DAY_OF_YEAR);//得到当年的实际天数
	
	  d1.add(Calendar.YEAR, 1);
	
	  }    while (d1.get(Calendar.YEAR) != y2);
	
	  }
	
	  return days;

  }
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		System.out.println(addSingleQuote("aaa,bbb,ccc,ddd"));
//		System.out.println(roundDouble(12333.2266,2));
		
		System.out.println(29.4565d + 29.4565d + 39.275d);
		System.out.println(sum(29.4565 , 29.4565, 39.275));
		System.out.println(round(29.4565d, 2, BigDecimal.ROUND_DOWN));
		System.out.println(mul(1.2 , 1.3, 39.275));
		System.out.println(div(1.2 , 1.3, 10, BigDecimal.ROUND_HALF_UP));
		System.out.println(getDaysBetween(strToDate("2015-01-27", "yyyy-MM-dd"), strToDate("2015-01-29", "yyyy-MM-dd")));

		System.out.println(Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_YEAR));
		
		Calendar   calendar   =   new   GregorianCalendar(); calendar.clear();
//		today.set(Calendar.YEAR, today.get(Calendar.YEAR) + 1);
//		today.set(Calendar.MONTH, 12);
//		today.set(Calendar.DAY_OF_MONTH, 1);
		System.out.println("2012-12-55".replaceAll("-", "/"));
	}

}
