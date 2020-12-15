package com.metaShare.common.tool.pageTool;

import java.util.List;

public class PageDTO<T> {
	// 总条数
	private long total = 0;

	// 总页数
	private long pages = 0;

	// 当前页
	private int current = 1;

	// 结果数据
	private List<T> data = null;

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getPages() {
		return pages;
	}

	public void setPages(long pages) {
		this.pages = pages;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}
	
}
