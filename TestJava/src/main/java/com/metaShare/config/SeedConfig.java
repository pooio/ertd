package com.metaShare.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Ling
 * @email qinjinlingx@gmail.com
 * @date 2018/4/29 19:56
 */
@Component
@ConfigurationProperties(prefix = "seed")
public class SeedConfig {
    private String fileTempPath;
    private String fileUploadPath;
    private String tempPath;

    
    public String getTempPath() {
		return tempPath;
	}

	public void setTempPath(String tempPath) {
		this.tempPath = tempPath;
	}

	public String getFileTempPath() {
        return fileTempPath;
    }

    public void setFileTempPath(String fileTempPath) {
        this.fileTempPath = fileTempPath;
    }

    public String getFileUploadPath() {
        return fileUploadPath;
    }

    public void setFileUploadPath(String fileUploadPath) {
        this.fileUploadPath = fileUploadPath;
    }
}
