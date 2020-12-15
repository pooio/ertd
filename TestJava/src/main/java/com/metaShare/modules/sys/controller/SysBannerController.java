package com.metaShare.modules.sys.controller;

import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.pageTool.PageTool;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.common.utils.DateUtil;
import com.metaShare.common.utils.StatusEnum;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.sys.entity.SysArea;
import com.metaShare.modules.sys.entity.SysBanner;
import com.metaShare.modules.sys.entity.SysConfig;
import com.metaShare.modules.sys.service.SysBannerService;
import com.metaShare.modules.sys.service.SysconfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.misc.BASE64Decoder;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

@Controller
@CrossOrigin
@Api(tags = "轮播图管理")
@RequestMapping(value = "/api/sys/banner")
public class SysBannerController  extends BaseController {

@Autowired
private SysBannerService sysBannerService;

    @Autowired
    private SysconfigService sysconfigService;
    @ResponseBody
    @ApiOperation(value = "轮播图数据")
    @RequestMapping(value = "/getList", method = { RequestMethod.POST })
    public Result getList(Integer pageNum, Integer pageSize) {
        StringBuffer url = getRequest().getRequestURL();
        String contextUrl = url.delete(url.length() - getRequest().getRequestURI().length(), url.length()).toString();
        if (contextUrl.endsWith("/")) {
            contextUrl = contextUrl.substring(0, contextUrl.length() - 1);
        }
        PageDTO<SysBanner> pageDTO = null;
        List<SysBanner> list = sysBannerService.getList();
//          if(list.size()==0){
//              for (int i=0;i<3;i++) {
//                  SysBanner sysBanner = new SysBanner();
//                  String fileName=i+".png";
//                  sysBanner.setImg(contextUrl+"/"+fileName);
//                  sysBanner.setTitle(i+"");
//                  list.add(sysBanner);
//              }
//          }
        pageDTO = new PageTool<SysBanner>().getPage(list, pageSize, pageNum);
        int total=list.size();
        return Result.resultInfo(ResultCode.SUCCESS, total,pageDTO.getData());
    }

    /**
     * @Description： base64字符串转化成图片
     * @param:     imgStr
     * @Return:
     */
    public static boolean GenerateImage(String imgStr,String photoname)
    {
        //对字节数组字符串进行Base64解码并生成图片
        //图像数据为空
        if (imgStr == null)
            return false;


        BASE64Decoder decoder = new BASE64Decoder();
        try
        {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {
                    //调整异常数据
                    b[i]+=256;
                }
            }
            //生成jpeg图片
            String imagePath= System.getProperty("java.class.path");
            //System.currentTimeMillis()
            //新生成的图片
            String imgFilePath = imagePath+photoname;
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }



    @ResponseBody
    @ApiOperation(value = "保存轮播图")
    @RequestMapping(value = "/save", method = { RequestMethod.POST })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "标题", required = true, dataType = "String"),
            @ApiImplicitParam(name = "img", value = "base64图片", required = true, dataType = "String") })
    public Result save(SysBanner sysBanner) {
        String userId = getUserId();
        String userName =getUserInfo().getName();
        sysBanner.setDeleted(0);
        sysBanner.setCreateId(userId);
        sysBanner.setCreateName(userName);
        sysBanner.setCreateTime(new Date());
        boolean result = sysBannerService.insert(sysBanner);
        if(result){
            String operator = getUserInfo().getName();
            String info = "新增了\"" +sysBanner.getTitle()+ "\"轮播图";
            saveOperatorInfo(info, StatusEnum.LogInfoType15.getIntValue(), operator);
            return Result.resultInfo(ResultCode.SUCCESS, "新增成功");
        }else {
            return Result.resultInfo(ResultCode.FAILURE, "新增失败");
        }

    }
    @ResponseBody
    @ApiOperation(value = "修改轮播图")
    @RequestMapping(value = "/updata", method = { RequestMethod.POST })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "标题", required = true, dataType = "String"),
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "String"),
            @ApiImplicitParam(name = "img", value = "base64图片", required = true, dataType = "String") })
    public Result updata(SysBanner sysBanner) {
        String userName =getUserInfo().getName();
        sysBanner.setDeleted(0);
        sysBanner.setUpdateName(userName);
        sysBanner.setCreateTime(new Date());
        boolean result = sysBannerService.updateById(sysBanner);
        if(result){
            String operator = getUserInfo().getName();
            String info = "修改了\"" +sysBanner.getTitle()+ "\"轮播图";
            saveOperatorInfo(info, StatusEnum.LogInfoType15.getIntValue(), operator);
            return Result.resultInfo(ResultCode.SUCCESS, "修改成功");
        }else {
            return Result.resultInfo(ResultCode.FAILURE, "修改失败");
        }

    }
    @ResponseBody
    @ApiOperation(value = "删除轮播图")
    @RequestMapping(value = "/del", method = { RequestMethod.POST })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "String") })
    public Result del(SysBanner sysBanner) {
        sysBanner.setDeleted(1);
        boolean result = sysBannerService.updateById(sysBanner);
        if(result){
            String operator = getUserInfo().getName();
            String info = "删除了\"" +sysBanner.getTitle()+ "\"轮播图";
            saveOperatorInfo(info, StatusEnum.LogInfoType15.getIntValue(), operator);
            return Result.resultInfo(ResultCode.SUCCESS, "删除成功");
        }else {
            return Result.resultInfo(ResultCode.FAILURE, "删除失败");
        }

    }
}
