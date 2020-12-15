package com.metaShare.common.utils;

public enum ImageScale {
	//与bootstrap命名保持一致
	XS("xs", 30, 30, false),//extreme small
	SM("sm", 98, 78, true),//small
	MD("md", 300, 300, true),//medium
	LG("lg", 600, 600, false);//large 
	
	private String name;  
    private int width;  
    private int height;
    private boolean enable;//启用/禁用
    
    private ImageScale(String name, int width, int height, boolean enable){
    	this.name=name;
    	this.width=width;
    	this.height=height;
    	this.enable=enable;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
    
    
}
