package com.metaShare.modules.core.controller;

import com.metaShare.common.utils.StatusEnum;
import com.metaShare.common.utils.WebUtils;
import com.metaShare.modules.core.entity.SysFileAttach;
import com.metaShare.modules.core.service.SysFileAttachService;
import com.metaShare.modules.sys.entity.SysConfig;
import com.metaShare.modules.sys.service.SysconfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

@Controller
@RequestMapping("/api/download")
public class DownloadController {

    @Autowired
    private SysFileAttachService sysFileAttachService;
    @Autowired
    private SysconfigService sysconfigService;
    /**
     * 下载
     */
    @RequestMapping(value = "/downloadFile")
    @ResponseBody
    public void downloadFile(@RequestParam String fileId, HttpServletResponse response, HttpServletRequest request) {
        try {
            SysConfig sysConfig5 = sysconfigService.getConfigData(StatusEnum.SysConfigType5.getStrValue());
            SysFileAttach sysFileAttach = sysFileAttachService.selectById(fileId);
            String url = sysConfig5.getConfigContent() + sysFileAttach.getFilePath() + sysFileAttach.getFileName();
            String fileName = sysFileAttach.getFileName();
            File file = new File(url);
            if (!file.exists()) {
                return;
            }
            WebUtils.download(file, fileName, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
