package com.metaShare.modules.bpm.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.pageTool.PageTool;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.common.utils.EntityChangeDto;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.bpm.dto.BpmDelegateDTO;
import com.metaShare.modules.bpm.entity.BpmDelegate;
import com.metaShare.modules.bpm.service.BpmDelegateService;
import com.metaShare.modules.sys.service.SysUserService;

@Controller
@RequestMapping(value = "api/bpm/delegate")
public class APIBpmDelegateController extends BaseController {
	@Autowired
	BpmDelegateService bpmDelegateService;
	@Autowired
	SysUserService sysUserService;
	/**
	 * 验证是否合法
	 * 
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
		bpmDelegate.setUserId(getUserId());
		return Result.resultInfo(ResultCode.SUCCESS, bpmDelegateService.checkBpmdelegate(bpmDelegate));
	}
	/**
	 * 列表数据，json格式 分页查询
	 * 
	 * @param searchable
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping("list")
	public Result listData(Model model,int pageSize,int pageNumber) {
		// 权限验证
		Map<String, Object> map = new HashMap<String, Object>();
		PageDTO<BpmDelegateDTO> pageDTO = null;
		try {
			Wrapper<BpmDelegate> wrapper = Condition.create().eq("deleted", false);
			List<BpmDelegate> list = bpmDelegateService.selectList(wrapper);
			List<BpmDelegateDTO> listDto = EntityChangeDto.objectToOtherObjectList(list, BpmDelegateDTO.class);
			pageDTO = new PageTool<BpmDelegateDTO>().getPage(listDto,pageNumber,pageSize);

		} catch (Exception e) {
			e.printStackTrace();
			return Result.resultInfo(ResultCode.FAILURE, null);
		}
		return Result.resultInfo(ResultCode.SUCCESS, pageDTO);
	}
}
