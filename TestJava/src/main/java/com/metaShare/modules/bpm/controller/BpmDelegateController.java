package com.metaShare.modules.bpm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.bpm.entity.BpmDelegate;
import com.metaShare.modules.bpm.service.BpmDelegateService;

@Controller
@RequestMapping(value="bpm/delegate")
public class BpmDelegateController extends BaseController {
	@Autowired
	BpmDelegateService bpmDelegateService;
	
	/**
	 * 验证是否合法
	 * @param bpmDelegate
	 * @return
	 * @throws Exception 
	 * @return Boolean
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年4月22日 上午11:02:38
	 */
	@ResponseBody
    @RequestMapping("check")
	public Result checkBpmdelete(BpmDelegate bpmDelegate) throws Exception {
		return Result.resultInfo(ResultCode.SUCCESS, bpmDelegateService
				.checkBpmdelegate(bpmDelegate));
    }
	
}
