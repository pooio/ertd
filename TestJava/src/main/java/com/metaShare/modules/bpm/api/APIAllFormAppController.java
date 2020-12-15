package com.metaShare.modules.bpm.api;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.metaShare.modules.sys.entity.SysUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.pageTool.PageTool;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.bpm.dto.AllFormAppDTO;
import com.metaShare.modules.bpm.service.BpmDelegateService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@Controller
@RequestMapping("api/allFormApp")
@Api(tags = "我发起的")
public class APIAllFormAppController extends BaseController{

		@Autowired
		private BpmDelegateService bpmDelegateService;
		@ResponseBody
		@RequestMapping(value = "findApp" , method = { RequestMethod.POST })
		@ApiOperation("我发起的")
		public Result findApp(String processName,int pageSize, int pageNum,
							  @RequestParam(value = "approvalType",required = false)Integer approvalType,
							  @RequestParam(value = "startDate",required = false)String startDate,
							  @RequestParam(value = "endDate",required = false)String endDate,
							  @RequestParam(value = "processDefinitionName",required = false)String processDefinitionName){
			PageDTO<AllFormAppDTO> pageDTO = null;
			try {
				SysUser sysUser = getUserInfo();
				if (sysUser == null) {
					return Result.resultInfo(ResultCode.LOGIN_NAME_NULL, "未获取到登录人信息");
				}
				if(StringUtils.isNotEmpty(endDate)){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(sdf.parse(endDate));
					calendar.add(Calendar.DATE,1);
					endDate = sdf.format(calendar.getTime());
				}
				List<AllFormAppDTO>  list = bpmDelegateService.findApp(processName,sysUser.getLoginName(),approvalType,startDate,endDate,processDefinitionName);
				pageDTO = new PageTool<AllFormAppDTO>().getPage(list, pageSize, pageNum);
			}catch (Exception e){
				e.printStackTrace();
			}
			return Result.resultInfo(ResultCode.SUCCESS, pageDTO);
		
		}
}
