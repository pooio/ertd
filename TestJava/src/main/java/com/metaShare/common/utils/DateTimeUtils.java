package com.metaShare.common.utils;

import com.google.common.base.Strings;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Ling
 * @email qinjinlingx@gmail.com
 * @date 2018/4/27 14:32
 */
public class DateTimeUtils {

	/**
	 * 年(yyyy)
	 */
	public static final String YEAR = "yyyy";

	/**
	 * 年-月(yyyy-MM)
	 */
	public static final String YEAR_MONTH = "yyyy-MM";

	/**
	 * 年-月-日(yyyy-MM-dd)
	 */
	public static final String YEAR_MONTH_DAY = "yyyy-MM-dd";

	/**
	 * 年月日(yyyyMMdd)
	 */
	public static final String YEAR_MONTH_DAY_SIMPLE = "yyyyMMdd";

	/**
	 * 年-月-日 小时(yyyy-MM-dd HH)
	 */
	public static final String YEAR_MONTH_DAY_HOUR = "yyyy-MM-dd HH";

	/**
	 * 年-月-日 小时(yyyy-MM-dd HH)中文输出
	 */
	public static final String YEAR_MONTH_DAY_HOUR_CN = "yyyy年MM月dd日HH时";

	/**
	 * 年-月-日 小时:分钟(yyyy-MM-dd HH:mm)
	 */
	public static final String YEAR_MONTH_DAY_HOUR_MINUTE = "yyyy-MM-dd HH:mm";

	/**
	 * 年-月-日 小时:分钟:秒钟(yyyy-MM-dd HH:mm:ss)
	 */
	public static final String YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 年月日小时分钟秒钟(yyyyMMddHHmmss)
	 */
	public static final String YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_SIMPLE = "yyyyMMddHHmmss";

	/**
	 * 小时:分钟:秒钟(HH:mm:ss)
	 */
	public static final String HOUR_MINUTE_SECOND = "HH:mm:ss";

	/**
	 * 小时:分钟(HH:mm)
	 */
	public static final String HOUR_MINUTE = "HH:mm";

	/**
	 * 月.日(M.d)
	 */
	public static final String MONTH_DAY = "M.d";

	/**
	 * 一天的秒数
	 */
	private static final int DAY_SECOND = 24 * 60 * 60;

	/**
	 * 一小时的秒数
	 */
	private static final int HOUR_SECOND = 60 * 60;

	/**
	 * 一分钟的秒数
	 */
	private static final int MINUTE_SECOND = 60;

	/**
	 * 格式化日期时间
	 *
	 * @param date
	 *            Date对象
	 * @param pattern
	 *            模式
	 * @return 格式化后的日期时间字符串
	 */
	public static String format(Date date, String pattern) {
		if (date == null)
			return "";
		return new DateTime(date).toString(pattern);
	}

	/**
	 * 格式化日期时间字符串
	 *
	 * @param dateString
	 *            日期时间字符串
	 * @param pattern
	 *            模式
	 * @return Date对象
	 */
	public static Date formatDateString(String dateString, String pattern) {
		try {
			DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(pattern);
			return dateTimeFormatter.parseDateTime(dateString).toDate();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 根据秒数获得x天x小时x分钟x秒字符串
	 *
	 * @param second
	 *            秒数
	 * @return x天x小时x分钟x秒字符串
	 */
	public static String getDayHourMinuteSecond(int second) {
		if (second == 0) {
			return "0秒";
		}
		StringBuilder sb = new StringBuilder();
		int days = second / DAY_SECOND;
		if (days > 0) {
			sb.append(days);
			sb.append("天");
			second -= days * DAY_SECOND;
		}

		int hours = second / HOUR_SECOND;
		if (hours > 0) {
			sb.append(hours);
			sb.append("小时");
			second -= hours * HOUR_SECOND;
		}

		int minutes = second / MINUTE_SECOND;
		if (minutes > 0) {
			sb.append(minutes);
			sb.append("分钟");
			second -= minutes * MINUTE_SECOND;
		}
		if (second > 0) {
			sb.append(second);
			sb.append("秒");
		}
		return sb.toString();
	}

	/**
	 * 根据秒数获得x天x小时x分钟字符串
	 *
	 * @param second
	 *            秒数
	 * @return x天x小时x分钟字符串
	 */
	public static String getDayHourMinute(int second) {
		if (second == 0) {
			return "0分钟";
		}
		StringBuilder sb = new StringBuilder();
		int days = second / DAY_SECOND;
		if (days > 0) {
			sb.append(days);
			sb.append("天");
			second -= days * DAY_SECOND;
		}

		int hours = second / HOUR_SECOND;
		if (hours > 0) {
			sb.append(hours);
			sb.append("小时");
			second -= hours * HOUR_SECOND;
		}
		int minutes = second / MINUTE_SECOND;
		if (minutes > 0) {
			sb.append(minutes);
			sb.append("分钟");
		}
		return sb.toString();
	}

	/**
	 * 获取只含有年月日的DateTime对象
	 *
	 * @param dateTime
	 *            DateTime对象
	 * @return 只含有年月日的DateTime对象
	 */
	public static DateTime getDateOnly(DateTime dateTime) {
		return new DateTime(dateTime.toString(YEAR_MONTH_DAY));
	}

	/**
	 * 获取当前周的周一和下周一
	 *
	 * @return 日期数组（索引0为周一，索引1为下周一）
	 */
	public static Date[] getMondayAndNextMonday() {
		DateTime dateTime = getDateOnly(new DateTime());
		DateTime monday = dateTime.dayOfWeek().withMinimumValue();
		DateTime nextMonday = monday.plusDays(7);
		return new Date[] { monday.toDate(), nextMonday.toDate() };
	}

	/**
	 * 获取指定时间的周一和周日
	 *
	 * @param dateTime
	 *            DateTime对象
	 * @return 日期数组（索引0为周一，索引1为周日）
	 */
	public static Date[] getMondayAndSunday(DateTime dateTime) {
		dateTime = getDateOnly(dateTime);
		DateTime monday = dateTime.dayOfWeek().withMinimumValue();
		DateTime sunday = monday.plusDays(6);
		return new Date[] { monday.toDate(), sunday.toDate() };
	}

	/**
	 * 和当前时间相比的天数差（正数为大于天数，负数为小于天数，零为同一天）
	 *
	 * @param date
	 *            Date对象
	 * @return 和当前时间相比的天数差
	 */
	public static int compareDaysWithNow(Date date) {
		return Days.daysBetween(new DateTime(), new DateTime(date)).getDays();
	}

	/**
	 * 和今天相比的天数差（正数为大于天数，负数为小于天数，零为同一天）
	 *
	 * @param date
	 *            Date对象
	 * @return 和今天相比的天数差
	 */
	public static int compareDaysWithToday(Date date) {
		DateTime today = new DateTime();
		today = new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 0, 0, 0, 0);
		DateTime compareDay = new DateTime(date);
		compareDay = new DateTime(compareDay.getYear(), compareDay.getMonthOfYear(), compareDay.getDayOfMonth(), 0, 0,
				0, 0);
		return Days.daysBetween(today, compareDay).getDays();
	}

	/**
	 * 比较时间a到时间b的天数差
	 *
	 * @param a
	 *            时间a
	 * @param b
	 *            时间b
	 * @return 相差天数
	 */
	public static int compareDaysWithDay(Date a, Date b) {
		DateTime today = new DateTime(b);
		today = new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 0, 0, 0, 0);
		DateTime compareDay = new DateTime(a);
		compareDay = new DateTime(compareDay.getYear(), compareDay.getMonthOfYear(), compareDay.getDayOfMonth(), 0, 0,
				0, 0);
		return Days.daysBetween(today, compareDay).getDays();
	}

	/**
	 * 比较两个时间是否相等（省略毫秒）
	 *
	 * @param date
	 *            Date对象
	 * @param compareDate
	 *            比较Date对象
	 * @return 是否相等
	 */
	public static boolean compareDateIgnoreMillisecond(Date date, Date compareDate) {
		if (date == null && compareDate == null) {
			return true;
		} else if (date == null && compareDate != null) {
			return false;
		} else if (date != null && compareDate == null) {
			return false;
		}

		return (date.getTime() / 1000 == compareDate.getTime() / 1000);
	}

	/**
	 * 根据秒数获取天数
	 *
	 * @param second
	 *            秒数
	 * @return 天数
	 */
	public static int getDay(int second) {
		return second / DAY_SECOND;
	}

	/**
	 * 获取和今天相比的日期字符串
	 *
	 * @param date
	 *            Date对象
	 * @return 和今天相比的日期字符串
	 */
	public static String getCompareWithTodayDateString(Date date) {
		int days = Math.abs(DateTimeUtils.compareDaysWithToday(date));
		String dateString = "";
		if (days == 0) {
			dateString = "今天";
		} else if (days == 1) {
			dateString = "昨天";
		} else if (days == 2) {
			dateString = "2天前";
		} else if (days == 3) {
			dateString = "3天前";
		} else if (days == 4) {
			dateString = "4天前";
		} else if (days == 5) {
			dateString = "5天前";
		} else if (days == 6) {
			dateString = "6天前";
		} else if (days > 6 && days <= 14) {
			dateString = "1周前";
		} else if (days > 14 && days <= 21) {
			dateString = "2周前";
		} else if (days > 21 && days <= 30) {
			dateString = "3周前";
		} else if (days > 30) {
			dateString = "1月前";
		} else if (days > 365) {
			dateString = "1年前";
		} else if (days > 365 * 3) {
			dateString = "3年前";
		}
		return dateString;
	}

	/**
	 * 比较两个时间相差分钟数
	 *
	 * @param now
	 *            当前时间
	 * @param compareDate
	 *            比较时间
	 * @return 相差分钟数
	 */
	public static int compareMinutes(Date now, Date compareDate) {
		return (int) (now.getTime() - compareDate.getTime()) / 60000;
	}

	/**
	 * 比较时间是本月的第几天
	 *
	 * @param date
	 * @return
	 */
	public static int getDayOfMonth(Date date) {
		DateTime dateTime = new DateTime(date);
		return dateTime.getDayOfMonth();
	}

	/**
	 * 计算当月有几天
	 *
	 * @param date
	 * @return
	 */
	public static int getDateOfMonth(Date date) {
		DateTime dateTime = new DateTime(date);
		return dateTime.dayOfMonth().getMaximumValue();
	}

	/**
	 * 指定时间,判断该时间到现在时间的年数
	 *
	 * @param date
	 *            指定时间
	 * @return 到现在时间的年数
	 */
	public static int compareYear(Date date) {
		DateTime btd = new DateTime(date);
		DateTime nowDate = new DateTime();
		int year = 0;
		if (nowDate.getMonthOfYear() > btd.getMonthOfYear()) {
			year = nowDate.getYear() - btd.getYear();
		} else if (nowDate.getMonthOfYear() < btd.getMonthOfYear()) {
			year = nowDate.getYear() - btd.getYear() - 1;
		} else if (nowDate.getMonthOfYear() == btd.getMonthOfYear()) {
			if (nowDate.getDayOfMonth() >= btd.getDayOfMonth()) {
				year = nowDate.getYear() - btd.getYear();
			} else {
				year = nowDate.getYear() - btd.getYear() - 1;
			}
		}
		return year;
	}

	/**
	 * 判断2个时间的时间差 返回字符串形式
	 *
	 * @param date
	 *            要对比的字符串
	 * @param date2
	 *            要对比的字符串
	 * @return 字符串形式 如1小时 ，2天2小时
	 */
	public static String compareDaysWithDate(Date date, Date date2) {
		StringBuilder msg = new StringBuilder();
		int minutes = (int) Math.abs((date.getTime() - date2.getTime()) / 60000);
		if (minutes / 60 > 0 && minutes / 60 / 24 <= 0) {
			msg.append(minutes / 60 + "小时");
		}
		if (minutes / 60 / 24 > 0) {
			msg.append(minutes / 60 / 24 + "天");
			msg.append(minutes / 60 % 24 + "小时");
		}
		return msg.toString();
	}

	public static final String REG_EXP_DATE = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";

	/**
	 * 自动解析多种格式的时间字符串为时间对象<br>
	 * 支持格式为：yyyy-MM-dd HH:mm:ss 支持多种分隔符，以及多种日期精度。 如yyyy年MM月。 HH时mm分ss秒
	 *
	 * @param dateString
	 *            时间字符串 <br>
	 * @return 格式正确则返回对应的java.util.Date对象 格式错误返回null
	 */
	public static Date formatUnknownString2Date(String dateString) {
		try {
			if (Strings.isNullOrEmpty(dateString)) {
				return null;
			}
			dateString = dateString.replace("T", " ");
			String hms = "00:00:00";
			dateString = dateString.trim();
			if (dateString.contains(" ")) {
				// 截取时分秒
				hms = dateString.substring(dateString.indexOf(" ") + 1, dateString.length());
				// 重置日期
				dateString = dateString.substring(0, dateString.indexOf(" "));
				// 多中分隔符的支持
				hms = hms.replace("：", ":");
				hms = hms.replace("时", ":");
				hms = hms.replace("分", ":");
				hms = hms.replace("秒", ":");
				hms = hms.replace("-", ":");
				hms = hms.replace("－", ":");
				// 时间不同精确度的支持
				if (hms.endsWith(":")) {
					hms = hms.substring(0, hms.length() - 1);
				}
				if (hms.split(":").length == 1) {
					hms += ":00:00";
				}
				if (hms.split(":").length == 2) {
					hms += ":00";
				}
			}
			String[] hmsarr = hms.split(":");
			// 不同日期分隔符的支持
			dateString = dateString.replace(".", "-");
			dateString = dateString.replace("/", "-");
			dateString = dateString.replace("－", "-");
			dateString = dateString.replace("年", "-");
			dateString = dateString.replace("月", "-");
			dateString = dateString.replace("日", "");
			// 切割年月日
			String yearStr, monthStr, dateStr;
			// 截取日期
			String[] ymd = dateString.split("-");
			// 判断日期精确度
			yearStr = ymd[0];
			monthStr = ymd.length > 1 ? ymd[1] : "";
			dateStr = ymd.length > 2 ? ymd[2] : "";
			monthStr = monthStr == "" ? Integer.toString(1) : monthStr;
			dateStr = dateStr == "" ? Integer.toString(1) : dateStr;
			String dtr = (yearStr + "-" + monthStr + "-" + dateStr + " " + hms);
			if (!dtr.matches(REG_EXP_DATE))
				return null;
			// 返回日期
			return new DateTime(Integer.parseInt(yearStr.trim()), Integer.parseInt(monthStr.trim()),
					Integer.parseInt(dateStr.trim()), Integer.parseInt(hmsarr[0].trim()),
					Integer.parseInt(hmsarr[1].trim()), Integer.parseInt(hmsarr[2].trim()), 0).toDate();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 解析多个时间，指定时间之间的分隔符和时间的格式符 分隔符不能与格式符相同
	 *
	 * @param dateString
	 *            传入一个时间段字符串
	 * @param spaceChar
	 *            指定格式符
	 * @param splitChar
	 *            指定分隔符
	 * @return 格式正确返回分割后的时间对象数组 格式错误返回null <br>
	 *         指定了格式符为. 分隔符为- 返回值为 时间长度为2的Date类型数组<br>
	 *         时间转换的方式详见
	 *         {@link org.webbuilder.utils.common.DateTimeUtils#formatUnknownString2Date(String dateString)}
	 */
	public static Date[] formatDatesByString(String dateString, String spaceChar, String splitChar) {
		if (spaceChar.equals(splitChar)) {
			return null;
		}
		String[] dateStrs = dateString.split(splitChar);
		Date[] dates = new Date[dateStrs.length];
		for (int i = 0, size = dateStrs.length; i < size; i++) {
			dates[i] = formatUnknownString2Date(dateStrs[i]);
		}
		return dates;
	}

	/**
	 * 身份证号转生日
	 *
	 * @param identityCard
	 *            身份证
	 * @return 生日
	 */
	public static Date identityCard2Date(String identityCard) {
		try {
			String dateStr;
			if (identityCard.length() == 18) {
				dateStr = identityCard.substring(6, 14);// 截取18位身份证身份证中生日部分
				return formatDateString(dateStr, "yyyyMMdd");
			}
			if (identityCard.length() == 15) {
				dateStr = identityCard.substring(6, 12);// 截取15位身份证中生日部分
				return formatDateString(dateStr, "yyMMdd");
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean validDate(String str) {
		try {
			Date date = formatUnknownString2Date(str);
			return date != null;
		} catch (Exception e) {
			return false;
		}
	}

	public static String getTimeInterval(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		if (1 == dayWeek) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		// System.out.println("要计算日期为:" + sdf.format(cal.getTime())); // 输出要计算日期
		// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		// 获得当前日期是一个星期的第几天
		int day = cal.get(Calendar.DAY_OF_WEEK);
		// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String imptimeBegin = sdf.format(cal.getTime());
		// System.out.println("所在周星期一的日期：" + imptimeBegin);
		cal.add(Calendar.DATE, 6);
		String imptimeEnd = sdf.format(cal.getTime());
		// System.out.println("所在周星期日的日期：" + imptimeEnd);
		return imptimeBegin + "," + imptimeEnd;
	}

	/**
	 * 字符串转换成日期
	 * 
	 * @param str
	 * @return date
	 */
	public static Date StrToDate(String str) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 获取当前时间时间戳
	 * 
	 * 
	 */
	public static long getLongDate() {
		long time = System.currentTimeMillis();
		return time;
	}

	/**
	 * 获取当前时间
	 * 
	 * 
	 */
	public static String getNowDate(String pattern) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.format(calendar.getTime()).toString();
	}

	public static void main(String args[]) throws ParseException {
		String str_begin = "2018-01-15";
		String str_end = "2020-07-01";
		List<String> dd = getQuarter(str_begin, str_end);
		for (String string : dd) {
			String d1 = string;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = null;
			try {
				// 注意格式需要与上面一致，不然会出现异常
				date = sdf.parse(d1);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			int dddd = getSeason(date);
		}

		// getMonths(str_begin, str_end) ;
		// getYears(str_begin, str_end) ;
	}

	public static List<String> getQuarter(String begins, String ends) {
		List<String> fruits = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date begin = new Date();
		Date end = new Date();
		try {
			begin = sdf.parse(begins);
			end = sdf.parse(ends);
		} catch (ParseException e) {
			System.out.println("日期输入格式不对");
			return fruits;
		}
		Calendar cal_begin = Calendar.getInstance();
		cal_begin.setTime(begin);
		Calendar cal_end = Calendar.getInstance();
		cal_end.setTime(end);
		while (true) {
			String str_end = getMonthEnd(cal_begin.getTime());
			Date begin_date = parseDate(str_end);
			Date end_date = parseDate(str_end);
			String Quarter_begin = formatDate(getFirstDateOfSeason(begin_date));
			String Quarter_end = formatDate(getLastDateOfSeason(end_date));
			Date Quarter_begin_date = parseDate(Quarter_begin);
			Date Quarter_end_date = parseDate(Quarter_end);

			if (Quarter_end_date.getTime() == end_date.getTime()) {

				if (Quarter_begin_date.getTime() <= begin.getTime()) {
					Quarter_begin = begins;
				}
				if (Quarter_end_date.getTime() >= end.getTime()) {
					Quarter_end = ends;
				}
				fruits.add(Quarter_end);
				// System.out.println(Quarter_begin + "~" + Quarter_end);
				if (end.getTime() <= end_date.getTime()) {
					break;
				}
			} else if (Quarter_begin_date.getTime() == begin_date.getTime()) {
				if (Quarter_begin_date.getTime() <= begin.getTime()) {
					Quarter_begin = begins;
				}
				if (Quarter_end_date.getTime() >= end.getTime()) {
					Quarter_end = ends;
				}
				System.out.println(Quarter_begin + "~" + Quarter_end);
			}

			cal_begin.add(Calendar.MONTH, 1);
			cal_begin.set(Calendar.DAY_OF_MONTH, 1);
		}
		return fruits;
	}

	/**
	 * 取得指定月份的最后一天
	 *
	 * @param strdate
	 *            String
	 * @return String
	 */
	public static String getMonthEnd(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		return formatDateByFormat(calendar.getTime(), "yyyy-MM-dd");
	}

	/**
	 * 以指定的格式来格式化日期
	 *
	 * @param date
	 *            Date
	 * @param format
	 *            String
	 * @return String
	 */
	public static String formatDateByFormat(Date date, String format) {
		String result = "";
		if (date != null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				result = sdf.format(date);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 
	 * @param strDate
	 * @return
	 */
	public static Date parseDate(String strDate) {
		return parseDate(strDate, null);
	}

	/**
	 * 日期解析
	 *
	 * @param strDate
	 * @param pattern
	 * @return
	 */
	public static Date parseDate(String strDate, String pattern) {
		Date date = null;
		try {
			if (pattern == null) {
				pattern = YEAR_MONTH_DAY;
			}
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			date = format.parse(strDate);
		} catch (Exception e) {

		}
		return date;
	}

	/**
	 * 取得季度第一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstDateOfSeason(Date date) {
		return getFirstDateOfMonth(getSeasonDate(date)[0]);
	}

	/**
	 * 取得季度最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getLastDateOfSeason(Date date) {
		return getLastDateOfMonth(getSeasonDate(date)[2]);
	}

	/**
	 * 取得月最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getLastDateOfMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		return c.getTime();
	}

	/**
	 * 取得月第一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstDateOfMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
		return c.getTime();
	}

	/**
	 * 取得季度月
	 * 
	 * @param date
	 * @return
	 */
	public static Date[] getSeasonDate(Date date) {
		Date[] season = new Date[3];

		Calendar c = Calendar.getInstance();
		c.setTime(date);

		int nSeason = getSeason(date);
		if (nSeason == 1) {// 第一季度
			c.set(Calendar.MONTH, Calendar.JANUARY);
			season[0] = c.getTime();
			c.set(Calendar.MONTH, Calendar.FEBRUARY);
			season[1] = c.getTime();
			c.set(Calendar.MONTH, Calendar.MARCH);
			season[2] = c.getTime();
		} else if (nSeason == 2) {// 第二季度
			c.set(Calendar.MONTH, Calendar.APRIL);
			season[0] = c.getTime();
			c.set(Calendar.MONTH, Calendar.MAY);
			season[1] = c.getTime();
			c.set(Calendar.MONTH, Calendar.JUNE);
			season[2] = c.getTime();
		} else if (nSeason == 3) {// 第三季度
			c.set(Calendar.MONTH, Calendar.JULY);
			season[0] = c.getTime();
			c.set(Calendar.MONTH, Calendar.AUGUST);
			season[1] = c.getTime();
			c.set(Calendar.MONTH, Calendar.SEPTEMBER);
			season[2] = c.getTime();
		} else if (nSeason == 4) {// 第四季度
			c.set(Calendar.MONTH, Calendar.OCTOBER);
			season[0] = c.getTime();
			c.set(Calendar.MONTH, Calendar.NOVEMBER);
			season[1] = c.getTime();
			c.set(Calendar.MONTH, Calendar.DECEMBER);
			season[2] = c.getTime();
		}
		return season;
	}

	/**
	 * 
	 * 1 第一季度 2 第二季度 3 第三季度 4 第四季度
	 * 
	 * @param date
	 * @return
	 */
	public static int getSeason(Date date) {

		int season = 0;

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int month = c.get(Calendar.MONTH);
		switch (month) {
		case Calendar.JANUARY:
		case Calendar.FEBRUARY:
		case Calendar.MARCH:
			season = 1;
			break;
		case Calendar.APRIL:
		case Calendar.MAY:
		case Calendar.JUNE:
			season = 2;
			break;
		case Calendar.JULY:
		case Calendar.AUGUST:
		case Calendar.SEPTEMBER:
			season = 3;
			break;
		case Calendar.OCTOBER:
		case Calendar.NOVEMBER:
		case Calendar.DECEMBER:
			season = 4;
			break;
		default:
			break;
		}
		return season;
	}

	/**
	 * format date
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date) {
		return formatDate(date, null);
	}

	/**
	 * format date
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String formatDate(Date date, String pattern) {
		String strDate = null;
		try {
			if (pattern == null) {
				pattern = YEAR_MONTH_DAY;
			}
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			strDate = format.format(date);
		} catch (Exception e) {

		}
		return strDate;
	}

	/**
	 * 获取某个时间段内所有月份
	 * 
	 * @param minDate
	 * @param maxDate
	 * @return
	 * @throws ParseException
	 */
	public static  List<String> getMonthBetween(String minDate, String maxDate) throws Exception {
		ArrayList<String> result = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");// 格式化为年月

		Calendar min = Calendar.getInstance();
		Calendar max = Calendar.getInstance();

		min.setTime(sdf.parse(minDate));
		min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

		max.setTime(sdf.parse(maxDate));
		max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

		Calendar curr = min;
		while (curr.before(max)) {
			result.add(sdf.format(curr.getTime()));
			curr.add(Calendar.MONTH, 1);
		}
		return result;
	}

}
