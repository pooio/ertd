package com.metaShare.common.tool.pageTool;

import java.util.List;

public class PageTool<T> {
	
	/**
	 * 分页
	 * @param records 数据
	 * @param size 条数
	 * @param current 当前页
	 * @return
	 */
	public PageDTO<T> getPage(List<T> records, int size, int current) {
		return pageChangePageDTO(new Page<T>(records, size, current));
	}
	
	public PageDTO<T> finishPage(List<T> records, int size, int current,int total) {
		return pageChangePageDTO(new Page<T>(records, size, current,total));
	}
	
	/**
	 * 分页-默认条数
	 * @param records 数据
	 * @param current 当前页
	 * @return
	 */
	public PageDTO<T> getPage(List<T> records, int current) {
		return pageChangePageDTO(new Page<T>(records, current));
	}
	
	private PageDTO<T> pageChangePageDTO(Page<T> page){
		PageDTO<T> pageDTO = new PageDTO<T>();
		pageDTO.setCurrent(page.getCurrent());
		pageDTO.setPages(page.getPages());
		pageDTO.setTotal(page.getTotal());
		pageDTO.setData(page.getRecords());
		return pageDTO;
	}
}
