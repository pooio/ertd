package com.metaShare.common.utils;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * MD5加密工具
 */
public class MD5Utils {

	public static final String SALT = "1qazxsw2";

	public static final String ALGORITH_NAME = "MD5";

	public static final int HASH_ITERATIONS = 10;

	/**
	 * 使用md5生成加密后的密码
	 * 
	 * @param pswd
	 * @return
	 */
	public static String encrypt(String pswd) {
		String newPassword = new SimpleHash(ALGORITH_NAME, pswd, ByteSource.Util.bytes(SALT), HASH_ITERATIONS).toHex();
		return newPassword;
	}

	/**
	 * 使用md5生成加密后的密码
	 * 
	 * @param username
	 * @param pswd
	 * @return
	 */
	public static String encrypt(String username, String pswd) {
		String newPassword = new SimpleHash(ALGORITH_NAME, pswd, ByteSource.Util.bytes(username + SALT),
				HASH_ITERATIONS).toHex();
		return newPassword;
	}

}
