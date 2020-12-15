package com.metaShare.common.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MimeTypeConfig {
	private static Logger log = LoggerFactory.getLogger(MimeTypeConfig.class);
	public static String MIME_UNDEFINED = "application/octet-stream";
	public static String MIME_RTF = "application/rtf";
	public static String MIME_PDF = "application/pdf";
	public static String MIME_ZIP = "application/zip";
	public static String MIME_MS_WORD = "application/msword";
	public static String MIME_MS_EXCEL = "application/vnd.ms-excel";
	public static String MIME_MS_POWERPOINT = "application/vnd.ms-powerpoint";
	public static String MIME_MS_WORD_2007 = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
	public static String MIME_MS_EXCEL_2007 = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	public static String MIME_MS_POWERPOINT_2007 = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
	public static String MIME_SWF = "application/x-shockwave-flash";
	public static String MIME_DXF = "image/vnd.dxf";
	public static String MIME_DWG = "image/vnd.dwg";
	public static String MIME_TIFF = "image/tiff";
	public static String MIME_JPEG = "image/jpeg";
	public static String MIME_GIF = "image/gif";
	public static String MIME_PNG = "image/png";
	public static String MIME_BMP = "image/bmp";
	public static String MIME_PSD = "image/x-psd";
	public static String MIME_ICO = "image/x-ico";
	public static String MIME_HTML = "text/html";
	public static String MIME_TEXT = "text/plain";
	public static String MIME_XML = "text/xml";
	public static String MIME_CSV = "text/csv";
	
	private static Map<String, String> extMap = new HashMap<String,String>();
	
	static {
		extMap.put(MIME_RTF, "rtf");
		extMap.put(MIME_PDF, "pdf");
		extMap.put(MIME_ZIP, "zip");
		extMap.put(MIME_MS_WORD, "doc");
		extMap.put(MIME_MS_EXCEL, "xls");
		extMap.put(MIME_MS_POWERPOINT, "ppt");
		extMap.put(MIME_MS_WORD_2007, "docx");
		extMap.put(MIME_MS_EXCEL_2007, "xlsx");
		extMap.put(MIME_MS_POWERPOINT_2007, "pptx");
		extMap.put(MIME_SWF, "swf");
		extMap.put(MIME_DXF, "dxf");
		extMap.put(MIME_DWG, "dwg");
		extMap.put(MIME_TIFF, "tiff");
		extMap.put(MIME_JPEG, "jpg");
		extMap.put(MIME_GIF, "gif");
		extMap.put(MIME_PNG, "png");
		extMap.put(MIME_BMP, "bmp");
		extMap.put(MIME_PSD, "psd");
		extMap.put(MIME_ICO, "ico");
		extMap.put(MIME_HTML, "html");
		extMap.put(MIME_TEXT, "txt");
		extMap.put(MIME_XML, "xml");
		extMap.put(MIME_CSV, "csv");
	}
	
	
	public static String getDocExt(String mimeType){
		String ext = "";
		if(StringUtils.isNotEmpty(extMap.get(mimeType))){
			ext = extMap.get(mimeType);
		}
		return ext;
	}
}
