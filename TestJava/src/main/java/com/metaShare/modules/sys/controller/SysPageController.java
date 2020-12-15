package com.metaShare.modules.sys.controller;

import java.io.File;
import java.util.Base64;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.metaShare.common.utils.WebUtils;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.core.excel.ImportTemplate;

/**
 * 系统页面视图
 */
@Controller
public class SysPageController extends BaseController {

    @RequestMapping("modules/{module}/{function}/{url}.html")
    public String subModule(@PathVariable("module") String module,
                            @PathVariable("function") String function,
                            @PathVariable("url") String url,
                            ModelMap modelMap,
                            @RequestParam(required = false) Map<String, Object> param,
                            HttpServletResponse response) throws Exception {
        String path = String.format("modules/%s/%s", module, function);
        if (!SecurityUtils.getSubject().isPermitted(path)) {
            response.setCharacterEncoding("UTF-8");
            response.sendError(HttpStatus.SC_FORBIDDEN);
        }
        modelMap.put("param", param);
        return String.format("modules/%s/%s/%s", module, function, url);
    }

    @RequestMapping("modules/{module}/{url}.html")
    public String module(@PathVariable("module") String module,
                         @PathVariable("url") String url,
                         ModelMap modelMap,
                         @RequestParam(required = false) Map<String, Object> param,
                         HttpServletResponse response) throws Exception {

        String path = String.format("modules/%s", module);
        if (!SecurityUtils.getSubject().isPermitted(path)) {
            response.setCharacterEncoding("UTF-8");
            response.sendError(HttpStatus.SC_FORBIDDEN);
        }
        modelMap.put("param", param);
        return String.format("modules/%s/%s", module, url);
    }

    @RequestMapping("{url}.html")
    public String url(@PathVariable("url") String url) {
        return url;
    }

    @RequestMapping("/manage")
    public String index() {
        return "index";
    }

    @RequestMapping("/export/excel/{b64path}")
    public void downloadFile(@PathVariable String b64path,
                             HttpServletResponse response,
                             HttpServletRequest request) throws Exception {
        String realPath = new String(Base64.getDecoder().decode(b64path), "UTF-8");
        File file = new File(realPath);
        if (!file.exists()) {
            return;
        }
        WebUtils.download(file, request, response);
    }

    @Autowired
    private ImportTemplate importTemplate;

    @RequestMapping("/importtemplate/{fileCode}")
    public void downloadTemplate(@PathVariable String fileCode,
                                 HttpServletResponse response,
                                 HttpServletRequest request) throws Exception {
    	System.out.println(fileCode);
    	String fileName = "";
    	if(fileCode.equals("1")) {
    		fileName = "外籍雇员岗位导入模板";
    	} else if(fileCode.equals("2")){
    		fileName = "外籍雇员账户信息";
    	} else if(fileCode.equals("3")){
    		fileName = "外聘人员个人劳务费导入模板";
    	} else if(fileCode.equals("4")){
    		fileName = "境内单独发放奖模板";
    	} else if(fileCode.equals("5")){
    		fileName = "境内工资补差模板";
    	} else if(fileCode.equals("6")){
    		fileName = "境内工资导入模板";
    	} else if(fileCode.equals("7")){
    		fileName = "外聘人员劳务费（发票）导入模板";
    	} else if(fileCode.equals("8")){
    		fileName = "外聘人员中介公司管理费导入模板";
    	} else if(fileCode.equals("9")){
    		fileName = "境外单独发放奖模板";
    	} else if(fileCode.equals("10")){
    		fileName = "境外工资补差模板";
    	} else if(fileCode.equals("11")){
    		fileName = "境外工资导入模板";
    	} else if(fileCode.equals("12")){
    		fileName = "外聘员工导入模板";
    	} else if(fileCode.equals("13")){
    		fileName = "自聘外籍雇员人员信息库";
    	} else if(fileCode.equals("14")){
    		fileName = "员工导入模板";
    	} else if(fileCode.equals("15")){
    		fileName = "外聘人员考勤模板";
    	} else if(fileCode.equals("16")){
    		fileName = "自聘外籍雇员考勤导入模板";
    	} else if(fileCode.equals("17")){
    		fileName = "境内考勤模板";
    	} else if(fileCode.equals("18")){
    		fileName = "劳务派遣外籍雇员劳务费-编制";
    	} else if(fileCode.equals("19")){
    		fileName = "境外考勤模板";
    	}
    	System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@" + fileName);
        String filePath = importTemplate.sync(fileName);
    	//File path = new File(ResourceUtils.getURL("classpath:").getPath());
        //String filePath = path.getAbsolutePath() + File.separator + "static" + File.separator + "excel" + File.separator + fileName + ".xlsx";
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        WebUtils.download(file, request, response);
    }
}
