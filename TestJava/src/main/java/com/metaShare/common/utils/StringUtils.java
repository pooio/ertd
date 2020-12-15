package com.metaShare.common.utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class StringUtils implements Serializable {
	/**
	 * @Fields serialVersionUID : TODO
	 * @since 2019-10-29
	 */
	private static final long serialVersionUID = 2787855838113180334L;

	public static boolean isEmpty(CharSequence cs) {
		return ((cs == null) || (cs.length() == 0) || "null".equals(cs));
	}

	public static boolean isNotEmpty(CharSequence cs) {
		return (!(isEmpty(cs)));
	}

	/**
	 * 字符串(aaa,bbb,ccc)转为 'aaa','bbb','ccc'
	 * 
	 * @param source
	 * @return
	 */
	public static String changeString(String source) {
		StringBuffer sbf = new StringBuffer();
		String[] sourceArr = source.split(",");
		for (int i = 0; i < sourceArr.length; i++) {
			sbf.append("'").append(sourceArr[i]).append("',");
		}
		if (sbf.toString().length() > 0) {
			return sbf.toString().substring(0, sbf.toString().length() - 1);
		}
		return sbf.toString();

	}

	/**
	 * 保留2位小数
	 * 
	 * @return
	 */
	public static String twoNum(double num) {
		BigDecimal bigDecimal = new BigDecimal(num).setScale(2, BigDecimal.ROUND_HALF_UP);
		return bigDecimal.toString();
	}
	public static void main(String[] args) {

		// 方法一
		String string = "001.006.001";
		String[] strArr = string.split("\\.");//注意分隔符是需要转译
		for (int i = 0; i < strArr.length; i++) {
			System.out.println(strArr[i]);
		}

	}
}
