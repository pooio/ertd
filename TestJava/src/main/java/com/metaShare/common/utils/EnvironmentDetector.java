package com.metaShare.common.utils;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvironmentDetector {
	private static Logger log = LoggerFactory.getLogger(EnvironmentDetector.class);
	
	private static final String JBOSS_PROPERTY = "jboss.home.dir";
	private static final String TOMCAT_PROPERTY = "catalina.home";
	private static final String CUSTOM_HOME_PROPERTY = "custom.home";
	
	private static final String OS_LINUX = "linux";
	private static final String OS_WINDOWS = "windows";
	private static final String OS_MAC = "mac os";
	
	/**
	 * Guess the application server home directory
	 */
	public static String getServerHomeDir() {
		// Try custom environment variable
		String dir = System.getProperty(CUSTOM_HOME_PROPERTY);
		
		if (dir != null) {
			log.debug("Using custom home: {}", dir);
			return dir;
		}
		
		// Try JBoss
		dir = System.getProperty(JBOSS_PROPERTY);
		
		if (dir != null) {
			log.debug("Using JBoss: {}", dir);
			return dir;
		}
		
		// Try Tomcat
		dir = System.getProperty(TOMCAT_PROPERTY);
		
		if (dir != null) {
			log.debug("Using Tomcat: {}", dir);
			return dir;
		}
		
		// Otherwise GWT hosted mode
		dir = System.getProperty("user.dir") + "/src/test/resources";
		log.debug("Using default dir: {}", dir);
		return dir;
	}
	
	public static String getClassPath() {
		String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		return path;
	}
	
	
	/**
	 * Detect if running in JBoss 
	 */
	public static boolean isServerJBoss() {
		return System.getProperty(JBOSS_PROPERTY) != null;
	}
	
	/**
	 * Detect if running in Tomcat
	 */
	public static boolean isServerTomcat() {
		return !isServerJBoss() && System.getProperty(TOMCAT_PROPERTY) != null;
	}
	
	
	/**
	 * Guess if running in Windows
	 */
	public static boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		return os.contains(OS_WINDOWS);
	}
	
	/**
	 * Guess if running in Linux
	 */
	public static boolean isLinux() {
		String os = System.getProperty("os.name").toLowerCase();
		return os.contains(OS_LINUX);
	}
	
	/**
	 * Guess if running in Mac
	 */
	public static boolean isMac() {
		String os = System.getProperty("os.name").toLowerCase();
		return os.contains(OS_MAC);
	}
	
	/**
	 * Test if is running in application server
	 */
	public static boolean inServer() {
		return isServerJBoss() || isServerTomcat();
	}
	
	/**
	 * Get user home
	 */
	public static String getUserHome() {
		return System.getProperty("user.home");
	}
	
	
	/**
	 * Guess pdf2swf application
	 */
	public static String detectSwftoolsPdf2Swf() {
		String  params = "fileIn -o fileOut -f -T 9 -t -s storeallcharacters";

		if(StringUtils.isNotBlank(Config.SYSTEM_SWFTOOLS_PDF2SWF_PAGE)){
			params += " -p "+ Config.SYSTEM_SWFTOOLS_PDF2SWF_PAGE ;
		}
		
		if (isLinux()) {
			File app = new File(Config.SYSTEM_SWFTOOLS_PDF2SWF_PATH + "/pdf2swf");
			
			if (app.exists() && app.isFile()) {
				return app.getAbsolutePath() + " " + params;
			} else {
				return "";
			}
		} if (isWindows()) {
			File app = new File(Config.SYSTEM_SWFTOOLS_PDF2SWF_PATH + "\\pdf2swf.exe");

			if (app.exists() && app.isFile()) {
				return app.getAbsolutePath() + " " + params;
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

}
