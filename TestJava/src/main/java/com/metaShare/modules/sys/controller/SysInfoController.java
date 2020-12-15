package com.metaShare.modules.sys.controller;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.pageTool.PageTool;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.common.utils.StatusEnum;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.sys.entity.SysInfo;
import com.metaShare.modules.sys.service.PoSysInfoService;

import io.swagger.annotations.Api;

@Controller
@RequestMapping(value = "api/sys/info")
@Api(tags = "系统日志")
public class SysInfoController extends BaseController {

    @Autowired
    private PoSysInfoService poSysInfoService;


    @RequestMapping(value = "/getSysInfoList", method= RequestMethod.POST)
    @ResponseBody
    public Result getSysInfoList(String userName, Date startDate, Date endDate, String module,int pageNum,int pageSize){
    	String startTime = null;
        String endTime = null;
        if(startDate != null && endDate != null){
            startTime = parseTime(startDate.toString())+" 00:00:00";
            endTime = parseTime(endDate.toString())+" 23:59:59";
        }

        List<SysInfo> dataList = poSysInfoService.querySysInfoList(userName,startTime,endTime,module);
        PageDTO<SysInfo> pageDTO = null;
        pageDTO = new PageTool<SysInfo>().getPage(dataList, pageSize, pageNum);
        return Result.resultInfo(ResultCode.SUCCESS,dataList.size(),pageDTO.getData());
    }

    @RequestMapping(value = "/getModuleList" , method= RequestMethod.POST)
    @ResponseBody
    public Result getModuleList(){
    	 List map =StatusEnum.findEnumList(StatusEnum.LogInfoType);
        return Result.resultInfo(ResultCode.SUCCESS,map);
    }

    //转换时间
    public static String parseTime(String dateString) {
        dateString = dateString.replace("GMT", "").replaceAll("\\(.*\\)", "");
        //将字符串转化为date类型，格式2019-12-5 00:00:00
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        Date dateTrans = null;
        try {
            dateTrans = format.parse(dateString);
            return new SimpleDateFormat("yyyy-MM-dd").format(dateTrans);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;

    }
}
