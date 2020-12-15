package com.metaShare.modules.core.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.common.utils.DateUtil;
import com.metaShare.common.utils.StatusEnum;
import com.metaShare.common.utils.StringUtils;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.core.entity.SysFileAttach;
import com.metaShare.modules.core.service.SysFileAttachService;
import com.metaShare.modules.sys.entity.SysConfig;
import com.metaShare.modules.sys.service.SysconfigService;

import io.swagger.annotations.Api;
import sun.misc.BASE64Encoder;

@Controller
@CrossOrigin
@Api(tags = "文件上传")
@RequestMapping("/api/upload")
public class UploadController extends BaseController {

	@Autowired
	private SysFileAttachService sysFileAttachService;
	@Autowired
	private SysconfigService sysconfigService;
	@Autowired
	private RepositoryService repositoryService;
	private ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * <p>
	 * Description: 附件上传
	 * </p>
	 * 
	 * @param file
	 *            上传附件
	 * @param type 为0时是上传个人头像，转为base64格式返回前台
	 * @return
	 */
	@PostMapping(value = "/fileupload")
	@ResponseBody
	public Result fileupload(@RequestParam("file") MultipartFile file,@RequestParam(value = "type",required = false)Integer type) {
		String month = DateUtil.toNextDate(new Date(), 0, "yyyy/MM/dd");
		Result result = null;
		if (file.isEmpty()) {
			return Result.resultInfo(ResultCode.FAILURE, "上传文件不能为空");
		}
		// 获取文件的大小
		long fileSize = file.getSize();
		if (fileSize > 10 * 1024 * 1024) {
			return Result.resultInfo(ResultCode.FAILURE, "上传文件大小超出限定大小10M");
		}
		if(type == null){
			type = 1;
		}
		try{
			if(type == 0){
				//上传个人头像
				BASE64Encoder base64Encoder = new BASE64Encoder();
				String imgBase64 = base64Encoder.encode(file.getBytes());
				result = Result.resultInfo(ResultCode.SUCCESS,imgBase64);
			}
			/*else if(type == 1){
				//上传流程模型
				Map<String,Object> map = new HashMap<>();
				Model model = repositoryService.newModel();
				repositoryService.saveModel(model);
				String modeId = model.getId();
				JsonNode rootNode = objectMapper.readTree(file.getInputStream());
				StringBuilder jsonBuilder = new StringBuilder();
				jsonBuilder.append("{");
				Iterator<String> iterable = rootNode.fieldNames();
				while (iterable.hasNext()) {
					String field = iterable.next();
					if (field.equals("resourceId")){
						jsonBuilder.append("\"resourceId\":\"").append(modeId).append("\",");
					} else {
						jsonBuilder.append("\"").append(field).append("\":").append(rootNode.get(field)).append(",");
					}
				}
				repositoryService.addModelEditorSource(modeId,(jsonBuilder.substring(0, jsonBuilder.length()-1)+"}").getBytes("utf-8"));
				map.put("modeId", modeId);
				map.put("jsonBuilder", jsonBuilder);
				result = Result.resultInfo(ResultCode.SUCCESS,map);
			}*/
			else{
				// 获取配置路径
				SysConfig sysConfig = sysconfigService.getConfigData(StatusEnum.SysConfigType5.getStrValue());
				String path = "";
				if (sysConfig != null) {
					path = sysConfig.getConfigContent();
				}
				String filePath = "\\" + "upload" + "\\" + month + "\\";
				filePath = filePath.replace('\\', '/');
				String newPath = path + filePath;
				File newDir = new File(newPath);
				if (!newDir.exists()) {
					newDir.mkdirs(); // 目录不存在的情况下，创建目录
				}
				// 获取文件名称
				String fileName = file.getOriginalFilename();
				if (StringUtils.isEmpty(fileName)) {
					return Result.resultInfo(ResultCode.FAILURE, "文件不能为空");
				}
				String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);

				//新名称
				fileName = payRecordNo()+"."+prefix;

				//旧名称
				String oldname  =file.getOriginalFilename();


				File newFile = new File(newDir, fileName);
				// 通过CommonsMultipartFile的方法直接写文件（注意这个时候）

				file.transferTo(newFile);

				SysFileAttach sysFileAttach=saveFileAttach(fileName, filePath,prefix,oldname);
				if(sysFileAttach!= null){
					SysConfig sysConfig6 = sysconfigService.getConfigData(StatusEnum.SysConfigType6.getStrValue());
					String Urlpath = "";
					if (sysConfig6 != null) {
						Urlpath = sysConfig6.getConfigContent();
					}
					sysFileAttach.setUrl(Urlpath+sysFileAttach.getFilePath()+sysFileAttach.getFileName());
				}

				result = Result.resultInfo(ResultCode.SUCCESS, sysFileAttach);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return result;
	}

	public SysFileAttach saveFileAttach(String fileName, String filePath,String prefix,String oldname) {
		int dotIndex = fileName.lastIndexOf(".");

		SysFileAttach fileAttach = new SysFileAttach();
		fileAttach.setFileName(fileName);
		fileAttach.setFilePath(filePath);
		fileAttach.setCreateTime(DateUtil.getDate(DateUtil.timeStampPattern));
		fileAttach.setExt(fileName.substring(dotIndex + 1));
		fileAttach.setFileName(fileName);
		fileAttach.setFileType(prefix);
		fileAttach.setUserId("1");
		fileAttach.setUsrName("admin");
		fileAttach.setCategory("upload");
		fileAttach.setOldFileName(oldname);
		sysFileAttachService.insert(fileAttach);
		return fileAttach;
		// 存储文件
		// fileAttach.set("FILE_NAME", fileName).set("FILE_PATH",
		// url).set("CREATE_TIME", DateKit.getLongTime())
		// .set("EXT", fileName.substring(dotIndex + 1)).set("FILE_TYPE",
		// "jpg").set("NOTE", 0)
		// .set("USER_ID", "1").set("usr_name", "admin").set("CATEGORY",
		// "upload").set("IN_USE", "1")
		// .set("OLD_FILE_NAME", fileName);
		// fileAttach.save();
	}
}
