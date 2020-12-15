package com.metaShare.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
	static Logger logger = LoggerFactory.getLogger(Config.class);
	public static final String PROPERTY_SYSTEM_OPENOFFICE_PATH = "system.openoffice.path";
	public static final String PROPERTY_SYSTEM_OPENOFFICE_TASKS = "system.openoffice.tasks";
	public static final String PROPERTY_SYSTEM_OPENOFFICE_PORT = "system.openoffice.port";
	public static final String PROPERTY_SYSTEM_OPENOFFICE_SERVER = "system.openoffice.server";
	
	public static String UPLOADFILEPATH_BASEPATH = "";
	public static String SYSTEM_OPENOFFICE_PATH = "";
	public static int SYSTEM_OPENOFFICE_TASKS = 200;//默认值200
	public static int SYSTEM_OPENOFFICE_PORT = 8100;
	public static String SYSTEM_OPENOFFICE_SERVER = "";
	public static String REMOTE_CONVERSION_SERVER = "";
	public static String SYSTEM_SWFTOOLS_PDF2SWF_PATH = "";
	public static String SYSTEM_SWFTOOLS_PDF2SWF_PAGE = "1-100";
	//调用外部应用执行命令的超时时间配置
	public static int SYSTEM_EXECUTION_TIMEOUT = 10; // 10 min
	
	static{
		try {
			load();
		} catch (IOException e) {
			logger.error("初始化Config配置时出错",e);
		}
	}
	public static void load() throws IOException{
		Properties prop = new Properties();
		InputStream ins = Thread.currentThread().getContextClassLoader().getResourceAsStream("/resources.properties");
		prop.load(ins);
		String uploadFileBasePath = prop.getProperty("uploadFilePath.basePath");
		if(StringUtils.isNotBlank(uploadFileBasePath)){
			UPLOADFILEPATH_BASEPATH = uploadFileBasePath.trim();
		}
		String openofficePath = prop.getProperty("openoffice.path");
		if(StringUtils.isNotBlank(openofficePath)){
			SYSTEM_OPENOFFICE_PATH = openofficePath.trim();
		}
		String openofficePort = prop.getProperty("openoffice.port");
		if(StringUtils.isNotBlank(openofficePort)){
			SYSTEM_OPENOFFICE_PORT = Integer.parseInt(openofficePort.trim());
		}
		String openofficeTasks = prop.getProperty("openoffice.tasks");
		if(StringUtils.isNotBlank(openofficeTasks)){
			SYSTEM_OPENOFFICE_TASKS = Integer.parseInt(openofficeTasks.trim());
		}
		String openofficeServer = prop.getProperty("openoffice.server");
		if(StringUtils.isNotBlank(openofficeServer)){
			SYSTEM_OPENOFFICE_SERVER = openofficeServer.trim();
		}
		String conversionServer = prop.getProperty("conversion.server");
		if(StringUtils.isNotBlank(conversionServer)){
			REMOTE_CONVERSION_SERVER = conversionServer.trim();
		}
		String pdf2swfPath = prop.getProperty("pdf2swf.path");
		if(StringUtils.isNotBlank(pdf2swfPath)){
			SYSTEM_SWFTOOLS_PDF2SWF_PATH = pdf2swfPath.trim();
		}
		String pdf2swfPage = prop.getProperty("pdf2swf.page");
		if(StringUtils.isNotBlank(pdf2swfPage)){
			SYSTEM_SWFTOOLS_PDF2SWF_PAGE = pdf2swfPage.trim();
		}
		String executionTimeout = prop.getProperty("execution.timeout");
		if(StringUtils.isNotBlank(executionTimeout)){
			SYSTEM_EXECUTION_TIMEOUT = Integer.parseInt(executionTimeout.trim());
		}
	}
}
