
package com.metaShare.common.utils;

import com.google.common.base.Throwables;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * 
 * @author eric.xi
 *
 */
public class DateUtil {
	
	public static String timeStampPattern = "yyyy-MM-dd HH:mm:ss";
	public static String timeDatePattern  = "yyyy-MM-dd HH:mm";
	public static String hourDatePattern  = "yyyy-MM-dd HH";
	public static String datePattern = "yyyy-MM-dd";
	public static String timeStrPattern = "yyyyMMddHHmmss";
	public static String zhDatePattern = "yyyy年MM月dd日";
	public static String parsePattern ="yyyyMMdd";
	public static String monthdayPattern = "MM-dd";
	public static String monthdayPatternFormat = "MM月dd日";
	public static String datePattern2 = "yyyy.MM.dd";
	public static void setDateFromat(String dateFormat) {
		if (StrUtils.isEmpty(dateFormat))
			throw new IllegalArgumentException("dateFormat can not be blank.");
		DateUtil.datePattern = dateFormat;
	}
	
	public static void setTimeFromat(String timeFormat) {
		if (StrUtils.isEmpty(timeFormat))
			throw new IllegalArgumentException("timeFormat can not be blank.");
		DateUtil.timeStampPattern = timeFormat;
	}
	
	public static Date toDate(String dateStr) {
		try {
			if(StrUtils.isEmpty(dateStr))
				throw new IllegalArgumentException("dateStr must not be blank");
			return DateUtils.parseDate(dateStr, datePattern, hourDatePattern, timeDatePattern, timeStampPattern,timeStrPattern,zhDatePattern,parsePattern,monthdayPatternFormat);
		} catch (ParseException e) {
			Throwables.propagate(e);
			return null;
		}
	}
	
	public static String toStr(Date date) {
		return toStr(date, DateUtil.datePattern);
	}
	
	public static String toStrPattern(Date date) {
		return toStr(date, DateUtil.parsePattern);
	}
	
	public static String toStr(Date date, String parsePattern) {
		if(date!=null){
			return new SimpleDateFormat(parsePattern).format(date);
		}else{
			return "";
		}
	}
	
	public static Calendar setDayStart(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar;
	}
	
	public static Calendar setDayEnd(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY,23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar;
	}
	
	public static Calendar addField(int field, int value) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(field, value);
		return calendar;
	}
	
	public static long toLong(Date date){
		long temp = date.getTime() / 1000;
		return temp ;
	}
	public static Date toDate(int dateInt){
		long dateLong = Long.valueOf(dateInt)*1000;
		Date date= new Date(dateLong);
		return date ;
	}
	
	public static long getLongTime() {
		Timestamp time= new Timestamp(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat(DateUtil.timeStampPattern);
		Date date = null;
		try {
			date = format.parse(time.toString());
		} catch (ParseException e) {
			// TODO自动生成catch语句块
			e.printStackTrace();
		}
		 return  date.getTime()/1000;
	}
	
	public static String formatByPattern(int dateNum){
		String dateStr = "" ;
		if(dateNum!=0){
			SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtil.datePattern); 
			dateStr = dateFormat.format(new Date(dateNum));
		}
		return dateStr;
	}
	
	public static String formatByPattern(Date date, String pattern){
		String dateStr = "" ;
		if(date!= null){
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern); 
			dateStr = dateFormat.format(date);
		}
		return dateStr;
	}
	
	
	
		/**
		 * @description 下一天
		 * @method toNextDate
		 * @author 
		 * @date 
		 * @param @param date
		 */
		public static Date toNextDate(Date date,int time){
			 Calendar calendar = new GregorianCalendar();
			 calendar.setTime(date);
			 calendar.add(Calendar.DATE,time);//把日期往后增加一天.整数往后推,负数往前移动
			 date=calendar.getTime();
			 return date;
		}
		/**
		 * @description 下一周
		 * @method toNextWeek
		 * @author 
		 * @date 
		 * @param @param date
		 */
		public static Date toNextWeek(Date date){
			 Calendar calendar = new GregorianCalendar();
			 calendar.setTime(date);
			 calendar.add(Calendar.DATE,7);//把日期往后增加一天.整数往后推,负数往前移动
			 date=calendar.getTime();
			 return date;
		}
		/**
		 * @description 下一月
		 * @method toNextMonth
		 * @author 
		 * @date 
		 */
		public static Date toNextMonth(Date date,int time){
			 Calendar calendar = new GregorianCalendar();
			 calendar.setTime(date);
			 calendar.add(Calendar.MONTH,time);//把日期往后增加一天.整数往后推,负数往前移动
			 date=calendar.getTime();
			 return date;
		}
		/**
		 * @description 下一年
		 * @method toNextYear
		 * @author 
		 * @date 
		 * @param @param date
		 */
		public static Date toNextYear(Date date,int time){
			 Calendar calendar = new GregorianCalendar();
			 calendar.setTime(date);
			 calendar.add(Calendar.YEAR,time);//把日期往后增加一天.整数往后推,负数往前移动
			 date=calendar.getTime();
			 return date;
		}
		/**
		 * @description 一天时间，单位：秒
		 * @method getWeekMill
		 * @author 
		 * @date 
		 */
		public static  long getMill(){
			long mill = 60*60*24;
			return mill;
		}
		 /**
		  * @description 获取某年的第一天
		  * @method 
		  * @author 
		  * @date 2015年12月30日下午1:40:52
		  * @param @param year 年份
		  */
	    public static Date getYearFirst(int year){
	        Calendar calendar = Calendar.getInstance();
	        calendar.clear();
	        calendar.set(Calendar.YEAR, year);
	        Date currYearFirst = calendar.getTime();
	        return currYearFirst;
	    }
	     
	    /**
	     * @description 获取某年的最后一天
	     * @method 
	     * @author 
	     * @date 2015年12月30日下午1:41:25
	     * @param @param year
	     */
	    public static Date getYearLast(int year){
	        Calendar calendar = Calendar.getInstance();
	        calendar.clear();
	        calendar.set(Calendar.YEAR, year);
	        calendar.roll(Calendar.DAY_OF_YEAR, -1);
	        Date currYearLast = calendar.getTime();
	        return currYearLast;
	    }
	    /**
	     * @description 获取当月第一天
	     * @method getMonthFirst
	     * @author 
	     * @date 
	     * @param  date 时间
	     */
	    public static Date getMonthFirst(Date date){
	    	//获取前月的第一天
            Calendar   cal_1=new GregorianCalendar();
            cal_1.setTime(date);
//           cal_1.add(Calendar.MONTH, -1);
            cal_1.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天 
            return cal_1.getTime();
	    }
	    /**
	     * @description 获取当月最后一天
	     * @method getMonthLast
	     * @author 
	     * @date 
	     * @param @param datez
	     * @param @return
	     */
	    public static Date getMonthLast(Date date){
	    	Calendar cal = Calendar.getInstance(); 
			cal.setTime(date); 
			do{ 
				cal.add(Calendar.DATE, 1); 
			}
		  	while (cal.get(Calendar.DATE) != 1); 
		  	cal.add(Calendar.DATE, -1); 
		  	return cal.getTime(); 
	    }
		/**
		 * @description 下一天
		 * @method toNextDate
		 * @author 
		 * @date 
		 * @param @param date
		 */
		public static String toNextDate(String date,int time,String pattern){
			 Date da = DateUtil.toDate(date);
			 Calendar calendar = new GregorianCalendar();
			 calendar.setTime(da);
			 calendar.add(Calendar.DATE,time);//把日期往后增加一天.整数往后推,负数往前移动
			 da=calendar.getTime();
			 return DateUtil.toStr(da,pattern);
		}
		/**
		 * @description 下一天
		 * @method toNextDate
		 * @author 
		 * @date 
		 * @param @param date
		 */
		public static String toNextDate(Date da,int time,String pattern){
			 Calendar calendar = new GregorianCalendar();
			 calendar.setTime(da);
			 calendar.add(Calendar.DATE,time);//把日期往后增加一天.整数往后推,负数往前移动
			 da=calendar.getTime();
			 return DateUtil.toStr(da,pattern);
		}

		public static Date setDayStart(Date date) {
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(date);
			calendar=setDayStart(calendar);
			return calendar.getTime();
		}

		public static Date setDayEnd(Date date){
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(date);
			calendar=setDayEnd(calendar);
			return calendar.getTime();
		}
		
		public static int toInt(Date date){
			Long temp = date.getTime() / 1000;
			return temp.intValue();
		}
		
		/**
		 * 
		 * @Title: getNowTime 
		 * @Description: 获取当前时间的时间戳
		 * @return int
		 */
		public static int getNowTime(){
			Long time = System.currentTimeMillis()/1000; 
			return time.intValue();
		}
		
		/**
		 * 得到当前日期字符串 格式（yyyy-MM-dd）
		 */
		public static String getDate() {
			return getDate("yyyy-MM-dd");
		}
		
		/**
		 * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
		 */
		public static String getDate(String pattern) {
			return DateFormatUtils.format(new Date(), pattern);
		}
		
		/** 设置日期的小时数 */
		public static Date setHours(Date date,int hours) {
		    Calendar calendar = Calendar.getInstance();
		    calendar.setTime(date);
		    calendar.set(Calendar.HOUR_OF_DAY, hours);
		    return calendar.getTime();
		}
		
		/** 设置日期的分钟数 */
		public static Date setMinute(Date date,int minute) {
		    Calendar calendar = Calendar.getInstance();
		    calendar.setTime(date);
		    calendar.set(Calendar.MINUTE, minute);
		    return calendar.getTime();
		}
		
	    /** 获取日期的小时数 */
        public static int getHours(Date date) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            return hours;
        }
        
        /** 获取日期的分钟数 */
        public static int getMinute(Date date) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int minute = calendar.get(Calendar.MINUTE);
            return minute;
        }

	//获取整点时间戳
	public static Calendar getPointCalendar() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(DateUtil.getDate("HH")));
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	public static String getCurrentTime(){
        return toStr(new Date(), timeStampPattern);
	}

}
