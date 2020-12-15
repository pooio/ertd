package com.metaShare.common.utils;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class DocUtil {
	public static final String UPLOAD_FOLDER = "d:/uploadFiles";
    
	private static String uploadFilePath ;
	static{
		/*String basePath = DocUtil.class.getResource("/").getPath();
		if(System.getProperty("os.name").toLowerCase().indexOf("linux")>=0){
			//basePath = basePath.substring(0, basePath.indexOf("/WEB-INF"));
			basePath = Config.UPLOADFILEPATH_BASEPATH;
		}else{
			basePath = Config.UPLOADFILEPATH_BASEPATH;
			//basePath = basePath.substring(1, basePath.indexOf("/target"));
//			basePath =  Config.UPLOADFILEPATH_BASEPATH;
		}
		uploadFilePath = basePath + UPLOAD_FOLDER;*/
		uploadFilePath = UPLOAD_FOLDER;
	}
	
	/**
	 * 上传文件
	 * @param uploadFilePath 文件的保存目录
	 * @param file
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException 
	 * @return String 文件相对于  uploadFilePath 的路径
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年2月1日 下午5:12:22
	 */
	public static String uploadFile(String rootPath,MultipartFile file) throws IllegalStateException, IOException {
		if(StringUtils.isNotBlank(rootPath)){
			String filePath = "";
			//String fileName = FileNameUtil.getFileName();
			String fileName = UUID.randomUUID().toString().replaceAll("-","");
			filePath = getDir(rootPath);
			String name = file.getOriginalFilename();
			int lastIndex = name.lastIndexOf(".");
			if(lastIndex>=0){
				fileName += name.substring(lastIndex);
			}
			File targetFile = new File(rootPath+"/"+filePath, fileName);  
			file.transferTo(targetFile);
			ImageUtil.zoomPictureDefault(targetFile.getAbsolutePath());
			return filePath+"/"+fileName;
		}else{
			return null;
		}
	}
	
	/**
	 * 上传Excel文件
	 * @param uploadFilePath 文件的保存目录
	 * @param file
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException 
	 * @return String 文件相对于  uploadFilePath 的路径
	 * @author: wb_madi/ 
	 * @version: 2016年2月1日 下午5:12:22
	 */
	public static String uploadFileExcel(String rootPath,MultipartFile file,String time) throws IllegalStateException, IOException {
		if(StringUtils.isNotBlank(rootPath)){
			String filePath = "";
			//String fileName = FileNameUtil.getFileName();
			//String fileName = UUID.randomUUID().toString().replaceAll("-","");
			String fileName = time;
			filePath = getDir(rootPath);
			String name = file.getOriginalFilename();
			int lastIndex = name.lastIndexOf(".");
			if(lastIndex>=0){
				fileName += name.substring(lastIndex);
			}
			File targetFile = new File(rootPath+"/"+filePath, fileName);  
			file.transferTo(targetFile);
			ImageUtil.zoomPictureDefault(targetFile.getAbsolutePath());
			return filePath+"/"+fileName;
		}else{
			return null;
		}
	}
	
	/**
	 * 获取文件的保存路径
	 * @param uploadFilePath
	 * @return 
	 * @return String
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年2月1日 下午5:12:55
	 */
	private static String getDir(String rootPath){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
		String str = sdf.format(new Date());
		File file  = new File(rootPath+"/"+str);
		File pdfFile  = new File(rootPath+"/pdf/"+str);
		File flashFile  = new File(rootPath+"/flash/"+str);
		synchronized (DocUtil.class) {
			if(!file.exists()){
				file.mkdirs();
			}
			if(!pdfFile.exists()){
				pdfFile.mkdirs();
			}
			if(!flashFile.exists()){
				flashFile.mkdirs();
			}
		}
		
		return str;
	}
	
	
	/**
	 * 删除文件
	 * @param rootPath
	 * @param filePath 
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年2月2日 上午10:04:06
	 */
	public static void deleteFile(String rootPath,String filePath){
		File targetFile = new File(rootPath+"/"+filePath); 
		String tempStr="";
		if(filePath.lastIndexOf(".")>0){
			 tempStr = filePath.substring(0, filePath.lastIndexOf("."));
		}
		 tempStr = filePath;
    	String swfPath = "/flash/"+tempStr+".swf";
    	String pdfPath = "/pdf/"+tempStr+".pdf";
    	File swfFile = new File(rootPath+"/"+swfPath);  
    	File pdfFile = new File(rootPath+"/"+pdfPath);  
		if(targetFile.exists()){
			targetFile.delete();
		}
		if(swfFile.exists()){
			swfFile.delete();
		}
		if(pdfFile.exists()){
			pdfFile.delete();
		}
	}
	
//	public static Boolean preview(String mimeType){
//		Boolean flag = false;
//		DocConverter docConverter = DocConverter.getInstance();
//		if( docConverter.convertibleToPdf(mimeType)
//    			|| MimeTypeConfig.MIME_PDF.equals(mimeType)){
//			flag = true;
//    	}
//		return flag;
//	}
	
    public static void  prepareDownloadFile(String fileName,
    		HttpServletRequest request,
    		HttpServletResponse response) throws UnsupportedEncodingException{
    	response.setCharacterEncoding("utf-8");  
	    response.setContentType("multipart/form-data");
	    
    	// Disable browser cache
		response.setHeader("Expires", "Sat, 6 May 1971 12:00:00 GMT");
		response.setHeader("Cache-Control", "must-revalidate");
		response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Content-Type","application/msexcel");
		response.setHeader("Content-disposition", String.format("attachment; filename=\"%s\"", fileName));
		response.setContentType("application/vnd.ms-excel;charset=utf-8");
		
    	//针对不同浏览器对文件名进行编码
		String userAgent = request.getHeader("User-Agent"); 
		if (userAgent.contains("MSIE")||userAgent.contains("Trident")) {
			//针对IE或者以IE为内核的浏览器：
			fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", " ");
		} else if (userAgent.contains("iphone") || userAgent.contains("ipad")) {
			//doNothing
		} else if(userAgent.contains("android")){
			//安卓
			fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", " ");
		}else{
			fileName = new String(fileName.getBytes("UTF-8"),"ISO-8859-1");
		}
		//文件名加双引号避免非ie浏览器因为空格截断文件名
 	    response.setHeader("Content-Disposition", "attachment;fileName=\""+fileName+"\"");  
    }

	public static String getUploadFilePath() {
		return uploadFilePath;
	}

	public static void setUploadFilePath(String uploadFilePath) {
		DocUtil.uploadFilePath = uploadFilePath;
	}
    
    
}
