package com.metaShare.common.tool.state;

import java.io.Serializable;

public class Result implements Serializable{
	private static final long serialVersionUID = 17644567890L;

	private Integer code;

    private String msg;

    private Object data;
    
    private int total;

    private Result() {}

    private void setResultCode(ResultCode code) {
        this.code = code.code();
        this.msg = code.message();
    }

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	/**
	 * 获取返回结果
	 * @param statueCode 状态码（参照ResultCode）
	 * @param data 返回的数据
	 * @return
	 */
	public static Result resultInfo(ResultCode statueCode, Object data) {
		Result result = new Result();
		ResultCode.findResultCode(statueCode);
		result.setResultCode(ResultCode.findResultCode(statueCode));
		result.setData(data);
		return result;
	}
	
	/**
	 * 获取返回结果
	 * @param statueCode 状态码（参照ResultCode）
	 * @param data 返回的数据
	 * @return
	 */
	public static Result resultInfo(ResultCode statueCode,int Total, Object data) {
		Result result = new Result();
		ResultCode.findResultCode(statueCode);
		result.setResultCode(ResultCode.findResultCode(statueCode));
		result.setTotal(Total);
		result.setData(data);
		return result;
	}
}
