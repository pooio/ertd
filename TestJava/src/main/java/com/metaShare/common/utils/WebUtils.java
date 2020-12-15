package com.metaShare.common.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * web工具类
 */
public class WebUtils {

    /**
     * 是否为ajax请求
     *
     * @param request
     * @return
     */
    public static boolean isAjax(HttpServletRequest request) {
        String header = "x-requested-with", httpRequest = "XMLHttpRequest";
        //如果是ajax请求响应头会有，x-requested-with
        if (request.getHeader(header) != null
                && request.getHeader(header)
                .equalsIgnoreCase(httpRequest)) {
            return true;
        }
        return false;
    }

    /**
     * 页面输出
     *
     * @param response
     * @param o
     */
    public static void write(HttpServletResponse response, Object o) {
        try {
            response.setContentType("text/html;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.println(o.toString());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void download(File file, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //判断文件类型
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        response.setContentType(mimeType);
        //设置文件响应大小
        response.setContentLengthLong(file.length());
        //文件名编码，解决乱码问题
        String fileName = file.getName();
       /* String encodedFileName = null;
        String userAgentString = request.getHeader("User-Agent");
        String browser = UserAgent.parseUserAgentString(userAgentString).getBrowser().getGroup().getName();
        if (browser.equals("Chrome") || browser.equals("Internet Exploer") || browser.equals("Safari")) {
            encodedFileName = URLEncoder.encode(fileName, "utf-8").replaceAll("\\+", "%20");
        }else if (browser.equals("Firefox")){
        	encodedFileName =  new String(fileName.getBytes(), "ISO8859-1");
        } else {
            //encodedFileName = MimeUtility.encodeWord(fileName);
        }

        //设置Content-Disposition响应头，一方面可以指定下载的文件名，另一方面可以引导浏览器弹出文件下载窗口
        response.setHeader("content-Disposition", "attachment;fileName=\"" + encodedFileName + "\"");*/
        // Disable browser cache
 		response.setHeader("Expires", "Sat, 6 May 1971 12:00:00 GMT");
 		response.setHeader("Cache-Control", "must-revalidate");
 		response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate");
 		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
 		response.setHeader("Pragma", "no-cache");
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

        //文件下载
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        OutputStream os = response.getOutputStream();
        byte[] b = new byte[2048];
        int length;
        while ((length = in.read(b)) > 0) {
            os.write(b, 0, length);
        }

        // 这里主要关闭。
        os.close();

        in.close();
        //FileCopyUtils.copy(in, response.getOutputStream());
    }


    public static void download(File file,String fileName, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //判断文件类型
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        response.setContentType(mimeType);
        //设置文件响应大小
        response.setContentLengthLong(file.length());
        //文件名编码，解决乱码问题
       /* String encodedFileName = null;
        String userAgentString = request.getHeader("User-Agent");
        String browser = UserAgent.parseUserAgentString(userAgentString).getBrowser().getGroup().getName();
        if (browser.equals("Chrome") || browser.equals("Internet Exploer") || browser.equals("Safari")) {
            encodedFileName = URLEncoder.encode(fileName, "utf-8").replaceAll("\\+", "%20");
        }else if (browser.equals("Firefox")){
        	encodedFileName =  new String(fileName.getBytes(), "ISO8859-1");
        } else {
            //encodedFileName = MimeUtility.encodeWord(fileName);
        }

        //设置Content-Disposition响应头，一方面可以指定下载的文件名，另一方面可以引导浏览器弹出文件下载窗口
        response.setHeader("content-Disposition", "attachment;fileName=\"" + encodedFileName + "\"");*/
        // Disable browser cache
        response.setHeader("Expires", "Sat, 6 May 1971 12:00:00 GMT");
        response.setHeader("Cache-Control", "must-revalidate");
        response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setCharacterEncoding("UTF-8");
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

        //文件下载
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        OutputStream os = response.getOutputStream();
        byte[] b = new byte[2048];
        int length;
        while ((length = in.read(b)) > 0) {
            os.write(b, 0, length);
        }

        // 这里主要关闭。
        os.close();

        in.close();
        //FileCopyUtils.copy(in, response.getOutputStream());
    }

}
