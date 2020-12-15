/**
 * 
 */
package com.metaShare.modules.sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.common.utils.DateUtil;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.sys.entity.SysEmail;
import com.metaShare.modules.sys.service.SysEmailService;

import io.swagger.annotations.Api;

/**
 * @author pc
 *
 */
@Controller
@CrossOrigin
@Api(tags = "邮件服务")
@RequestMapping(value = "/api/sys/email")
public class SysEmailController extends BaseController {
	@Autowired
	private SysEmailService sysEmailService;
	@Value("${spring.mail.username}")
    private String sender;
	@RequestMapping(value = "/sendMail", method = { RequestMethod.POST })
	@ResponseBody
	public Result sendMail(SysEmail sysEmail,String userName) {
        sysEmail.setDeliver(sender);
        sysEmail.setState(0);
        sysEmail.setCreateTime(DateUtil.getDate(DateUtil.timeDatePattern));
        sysEmail.setType(1);
        sysEmail.setCreateUser(userName);
        sysEmailService.insert(sysEmail);
        return Result.resultInfo(ResultCode.SUCCESS, "邮件保存成功");
    }
	
	public void SendEmailList(){
		try {
			sysEmailService.SendEmailList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
