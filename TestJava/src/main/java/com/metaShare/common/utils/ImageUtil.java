package com.metaShare.common.utils;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;


import net.coobird.thumbnailator.Thumbnails;

/**
 * 图片处理工具,当前版本可以压缩图片
 * 日期：2012-5-4 下午02:06:47
 * @author 秦雪
 * @since 1.0
 */
public class ImageUtil {

    private static final String BMP = "bmp";
    private static final String PNG = "png";
    private static final String GIF = "gif";
    private static final String JPEG = "jpeg";
    private static final String JPG = "jpg";
//    public static final String THUMBNAIL = "thumbnail.gif";
    
    /**
     * 生成缩略图
     * @param source
     */
    public static void zoomPictureDefault(String source){
    	if(isPicture(source)){
//    		for(ImageScale imageScale : ImageScale.values()){
//    			if(imageScale.isEnable()){
//    	       		zoomPicture(source, imageScale.getWidth(), imageScale.getHeight(), true, imageScale.getName());			
//    			}
//    		}
    	}
    }
    
    public static String zoomPicture(
            String source, 
            int width, 
            int height,
            boolean adjustSize){
    	return zoomPicture(source, width, height, adjustSize, null);
    }
    
    /**
     * 与所传入图片等比例的压缩图片
     * @param source 源图片路径
     * @param width 想要压缩成的宽度
     * @param height 想要压缩成的高度 
     * @param adjustSize 是否智能调整比例
     * @return 压缩后的文件地址
     */
    public static String zoomPicture(
            String source, 
            int width, 
            int height,
            boolean adjustSize, String suffix) {
        if (source == null || source.equals("") || width < 1 || height < 1) {
            return null;
        }
//        source = source.toLowerCase();
        if (source.endsWith(BMP)) {
            return BMPThumbnailHandler(source, width, height, adjustSize, suffix);
        } 
//        else if (source.endsWith(PNG) || source.endsWith(GIF)
//                || source.endsWith(JPEG) || source.endsWith(JPG)) {
//        	return thumbnailHandler(source, width, height, adjustSize);
//        }
        else{
        	return thumbnailHandler(source, width, height, adjustSize, suffix);
        }
//        return null;
    }

    private static String thumbnailHandler(
            String source, 
            int width, 
            int height,
            boolean adjustSize,
            String suffix
    		) {
        try { 
            File sourceFile = new File(source);
            String compressImagePath = null;
            if (sourceFile.exists()) {
                Image image = ImageIO.read(sourceFile);
                int theImgWidth = image.getWidth(null);
                int theImgHeight = image.getHeight(null);
                int[] size = { theImgWidth, theImgHeight };
                if (adjustSize) {
                    size = adjustImageSize(theImgWidth, theImgHeight, width,height);
                }
                
                if(StringUtils.isEmpty(suffix)){
                	suffix = "_" + size[0] + "-" + size[1];
                }else{
                	suffix = "_" + suffix;
                }
                
                int idx = source.lastIndexOf("/");    
                String sourceName = source.substring(idx + 1);
                int idx2 = sourceName.lastIndexOf(".");                  
                StringBuffer thumbnailFile=new StringBuffer();
                if(idx2 > 0){
                	thumbnailFile.append(source.substring(0, idx + 1));
                	thumbnailFile.append(sourceName.substring(0, idx2));
                    thumbnailFile.append(suffix);
                    thumbnailFile.append(sourceName.substring(idx2));
                }else{
                	thumbnailFile.append(source.substring(0, idx + 1));
                	thumbnailFile.append(sourceName);
                    thumbnailFile.append(suffix);
                }
                
                Thumbnails.of(source).outputQuality(1f).size(size[0],size[1]).toFile(thumbnailFile.toString());

//				writeFile(image, size[0], size[1], thumbnailFile.toString());
                
//                int idx2 = source.lastIndexOf(".");                  
//                thumbnailFile=new StringBuffer();
//                if(idx2 > 0){
//                	thumbnailFile.append(source.substring(0, idx2));
//                    thumbnailFile.append(suffix);
//                    thumbnailFile.append(source.substring(idx2));
//                }else{
//                	thumbnailFile.append(source);
//                    thumbnailFile.append(suffix);
//                }
                
//                thumbnailFile.append(source.substring(0, idx2));
//                thumbnailFile.append(size[0]);
//                thumbnailFile.append(size[1]);
//                thumbnailFile.append(source.substring(idx2));
                compressImagePath = thumbnailFile.toString();
            }
            return compressImagePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String BMPThumbnailHandler(
            String source, 
            int width,
            int height,
            boolean adjustSize,
            String suffix
            ) {
        try {
            File sourceFile = new File(source);
            String compressImagePath = null;
            if (sourceFile.exists()) {
//                Image image = getBMPImage(source);
            	Image image = getBMPImage(source);
                int theImgWidth = image.getWidth(null);
                int theImgHeight = image.getHeight(null);
                int[] size = { theImgWidth, theImgHeight };
                if (adjustSize) {
                    size = adjustImageSize(theImgWidth, theImgHeight, width, height);
                }
                
                if(StringUtils.isEmpty(suffix)){
                	suffix = "_" + size[0] + "-" + size[1];
                }else{
                	suffix = "_" + suffix;
                }
                
                int idx = source.lastIndexOf(".");                  
                StringBuffer thumbnailFile=new StringBuffer();
                thumbnailFile.append(source.substring(0, idx));
                thumbnailFile.append(suffix);
                thumbnailFile.append(source.substring(idx));
               
//                writeFile(image, size[0], size[1], thumbnailFile.toString());
                Thumbnails.of(source).outputQuality(1f).size(size[0],size[1]).toFile(thumbnailFile.toString());

                
                int idx2 = source.lastIndexOf(".");                  
                thumbnailFile=new StringBuffer();
                thumbnailFile.append(source.substring(0, idx2));
                thumbnailFile.append(suffix);
                thumbnailFile.append(source.substring(idx2));
                compressImagePath = thumbnailFile.toString();
            }
            return compressImagePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Image getBMPImage(String source) throws Exception {

        FileInputStream fs = null;
        Image image = null;
        try {
            fs = new FileInputStream(source);
            int bfLen = 14;
            byte bf[] = new byte[bfLen];
            fs.read(bf, 0, bfLen); // 读取14字节BMP文件头
            int biLen = 40;
            byte bi[] = new byte[biLen];
            fs.read(bi, 0, biLen); // 读取40字节BMP信息头

            // 源图宽度
            int nWidth = (((int) bi[7] & 0xff) << 24)
                    | (((int) bi[6] & 0xff) << 16)
                    | (((int) bi[5] & 0xff) << 8) | (int) bi[4] & 0xff;

            // 源图高度
            int nHeight = (((int) bi[11] & 0xff) << 24)
                    | (((int) bi[10] & 0xff) << 16)
                    | (((int) bi[9] & 0xff) << 8) | (int) bi[8] & 0xff;

            // 位数
            int nBitCount = (((int) bi[15] & 0xff) << 8) | (int) bi[14] & 0xff;

            // 源图大小
            int nSizeImage = (((int) bi[23] & 0xff) << 24)
                    | (((int) bi[22] & 0xff) << 16)
                    | (((int) bi[21] & 0xff) << 8) | (int) bi[20] & 0xff;

            // 对24位BMP进行解析
            if (nBitCount == 24) {
                int nPad = (nSizeImage / nHeight) - nWidth * 3;
                int nData[] = new int[nHeight * nWidth];
                byte bRGB[] = new byte[(nWidth + nPad) * 3 * nHeight];
                fs.read(bRGB, 0, (nWidth + nPad) * 3 * nHeight);
                int nIndex = 0;
                for (int j = 0; j < nHeight; j++) {
                    for (int i = 0; i < nWidth; i++) {
                        nData[nWidth * (nHeight - j - 1) + i] = (255 & 0xff) << 24
                                | (((int) bRGB[nIndex + 2] & 0xff) << 16)
                                | (((int) bRGB[nIndex + 1] & 0xff) << 8)
                                | (int) bRGB[nIndex] & 0xff;
                        nIndex += 3;
                    }
                    nIndex += nPad;
                }
                Toolkit kit = Toolkit.getDefaultToolkit();
                image = kit.createImage(new MemoryImageSource(nWidth, nHeight,
                        nData, 0, nWidth));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            if (fs != null) {
                fs.close();
            }
        }
        return image;
    }

    private static void writeFile(
            Image image, 
            int width, 
            int height,
            String thumbnailFile) throws Exception {
        
        if (image == null) return;
        
        
        
        BufferedImage tag = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
        tag.getGraphics().drawImage(image, 0, 0, width, height, null);
//        tag.getGraphics().drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
       
        
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(thumbnailFile);
            //JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            //height.encoder.encode(tag);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            if (out != null) {
                out.close();
            }
        }

    }

    private static int[] adjustImageSize(int theImgWidth, int theImgHeight,
            int defWidth, int defHeight) {        
        int[] size = { 0, 0 };
        
        float theImgHeightFloat=Float.parseFloat(String.valueOf(theImgHeight));
        float theImgWidthFloat=Float.parseFloat(String.valueOf(theImgWidth));
        if(defWidth > theImgWidth && defHeight > theImgHeight)	//比当前的尺寸大,返回当前图片尺寸
        {
        	size[0]=theImgWidth;
            size[1]=theImgHeight;
        }
        else if (theImgWidth<theImgHeight){
            float scale=theImgHeightFloat/theImgWidthFloat;
            size[0]=Math.round(defHeight/scale);
            size[1]=defHeight;
            if(size[0] == 0)//至少给1px
            {
            	size[0] = 1;
            }
        }else{
            float scale=theImgWidthFloat/theImgHeightFloat;
            size[0]=defWidth;
            size[1]=Math.round(defWidth/scale);
            if(size[1] == 0)//至少给1px
            {
            	size[1] = 1;
            }
        }
        return size;
    }
    
    public static boolean isPicture(String source){
    	boolean valid = true;
    	try {
    	    Image image =ImageIO.read(new File(source));
    	    if (image == null) {
    	        valid = false;
    	    }
    	} catch(IOException ex) {
    	    valid=false;
    	}
    	return valid;
    }

    public static void main(String[] agrs) {
        System.out.println(zoomPicture("g:\\test.jpg", 150, 96, true));
        
//        System.out.println(isPicture("G:\\QQ消息记录\\181190468\\FileRecv\\3777b17594b141d280e07e04127c1666"));
//        System.out.println("aaaaa".substring(0,-1));
    }


}
