package com.metaShare.common.tool.pageTool;

import java.util.Collections;
import java.util.List;

public final class Page<T> {
	// 总条数
	private long total = 0;

	// 页条数
	private int size = 10;

	// 总页数
	private long pages = 0;

	// 当前页
	private int current = 1;

	// 起始条数
	private int start = 0;

	// 结束条数
	private int end = 1;

	// 结果数据
	private List<T> records = Collections.emptyList();
	
	//所有数据
	private List<T> allRecords = Collections.emptyList();

	public long getTotal() {
		return total;
	}

	private void setTotal() {
		if (null != allRecords) {
			this.total = allRecords.size();
		}
	}

	public int getSize() {
		return size;
	}

	private void setSize(int size) {
		this.size = size;
	}

	public long getPages() {
		return pages;
	}

	private void setPages() {
		long result = 0;
		if (total != 0) {
			long remainder = total % size;
			long res = total / size;
			if (0 == remainder) {
				result = res;
			} else {
				result = res + 1;
			}
		}
		this.pages = result;
	}

	public int getCurrent() {
		return current;
	}

	private void setCurrent(int current) {
		this.current = current;
	}

	public int getStart() {
		return start;
	}

	private void setStart() {
		this.start = (current - 1) * size;
	}

	public int getEnd() {
		return end;
	}

	private void setEnd() {
		this.end = (current) * size;
	}

	public List<T> getRecords() {
		return records;
	}

	private void setRecords() {
		if (total<end) {
			this.records = allRecords.subList(start, (int)total);
		}else{
			this.records = allRecords.subList(start, end);
		}
	}
	
	public List<T> getAllRecords() {
		return allRecords;
	}

	public void setAllRecords(List<T> allRecords) {
		this.allRecords = allRecords;
	}

	public Page(List<T> records, int size, int current) {
		InitPage(records,size,current);
	}

	public Page(List<T> records, int size, int current,int all) {
		InitPage(records,size,current,all);
	}
	public Page(List<T> records, int current) {
		InitPage(records,10,current);
	}
	
	private void InitPage(List<T> records, int size, int current){
		setAllRecords(records);
		setTotal();
		setSize(size);
		setCurrent(current);
		setPages();
		setStart();
		setEnd();
		setRecords();
	}
	
	private void InitPage(List<T> pageRecords, int size, int current,int all){
		setTotal(all);
		setSize(size);
		setCurrent(current);
		setPages();
		setStart();
		setEnd();
		setRecords(pageRecords);
	}
	
	private void setTotal(int all) {
		this.total = all;
	}
	
	private void setRecords(List<T> pageRecords) {
		this.records = pageRecords;
	}
}
