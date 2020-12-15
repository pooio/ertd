package com.metaShare.modules.bpm.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.metaShare.common.utils.StatusEnum;
import com.metaShare.modules.customize.service.CustomFormJsonTableService;
import com.metaShare.modules.customize.service.CustomFormService;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.NativeModelQuery;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.metaShare.common.tool.pageTool.PageTool;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.common.utils.DocUtil;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.bpm.util.WorkflowUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("api/bpm")
@Api(tags="流程模型")
public class APIBpmProcessModelerController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(APIBpmProcessModelerController.class);
	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private ProcessEngine processEngine;

	private ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	private CustomFormService customFormService;

 

	// 权限字符串前缀
	protected String identity = "bpm:api:APIBpmProcessModeler";
	protected String checkBeforeSaveMsg = "";
	protected String checkBeforeUpdateMsg = "";
	protected String checkBeforeDeleteMsg = "";

	/**
	 * 打开流程模型列表
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("modeler")
	public String modeler() throws Exception {
		return "/bpm/modeler/main";
	}

	/**
	 * 获得流程模型数据
	 * 
	 * @param modelName
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="modeler/list",method={RequestMethod.POST})
	@ApiOperation("获得流程模型数据")
	public Result list(@RequestParam(value = "processName", required = false) String processName,
			@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize,@RequestParam(value = "type",required = false)String type) {
		String sql = "select * from act_re_model";
		Boolean flag = false;
		if(StringUtils.isNotEmpty(processName)){
			sql += " where NAME_ like CONCAT('%',#{processName},'%')";
			flag = true;
		}
		if(StringUtils.isNotEmpty(type) && "2".equals(type)){
			if(flag){
				sql += " and DEPLOYMENT_ID_ is not null";
			}else{
				sql += " where DEPLOYMENT_ID_ is not null ";
			}
		}
		sql += " order by DEPLOYMENT_ID_ is null,CREATE_TIME_ desc";
		NativeModelQuery modelQuery = repositoryService.createNativeModelQuery().sql(sql);
		if(StringUtils.isNotEmpty(processName)){
			modelQuery.parameter("processName", processName);
		}
		/*if (StringUtils.isEmpty(processName)) {
			models = repositoryService.createModelQuery().orderByCreateTime().desc().list();// .listPage((pageNumber-1)*pageSize,pageNumber*pageSize);
		} else {
			models = repositoryService.createModelQuery().orderByCreateTime().desc().modelNameLike(processName).list();// listPage((pageNumber-1)*pageSize,pageNumber*pageSize);
		}*/
		List<Model> models = modelQuery.list();
		int total=models.size();
		return Result.resultInfo(ResultCode.SUCCESS,total, new PageTool<Model>().getPage(models, pageSize, pageNum).getData());

	}

	/**
	 * 获得流程模型数据
	 *
	 * @param pageSize
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value ="modeler/modelerDeployment",method={RequestMethod.POST})
	@ApiOperation("获得流程模型数据")
	public Result modelerList(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
		List<Model> models = repositoryService.createModelQuery().deployed().latestVersion().orderByCreateTime().desc()
				.list();
		int total =models.size();
		return Result.resultInfo(ResultCode.SUCCESS,total, new PageTool<Model>().getPage(models,  pageSize,pageNum).getData());
	}

	/**
	 * 新增页面
	 * 
	 * @return
	 * @throws Exception
	 * @return String
	 */
	@RequestMapping("modeler/create")
	public String create() throws Exception {

		return "/bpm/modeler/add";
	}

	/**
	 * 导入模型
	 * 
	 * @param file
	 * @param request
	 * @return
	 * @return Map<String,String>
	 */
	@ResponseBody
	@RequestMapping(value = "modeler/importFile",method={RequestMethod.POST})
	@ApiOperation("导入模型")
	public Result importFile(@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "modeKey") String modeKey, @RequestParam(value = "modeName") String modeName,
			@RequestParam(value = "modeDescription") String modeDescription, HttpServletRequest request)
			throws Exception {
		Map<String, String> retMap = new HashMap<String, String>();
		Result result = null;
		// 权限验证
		try {
			if ((boolean) this.checkModeltName(modeKey, "").getData()) {

				if(StringUtils.isEmpty(modeDescription)){
					modeDescription = "";
				}
				Map<String, String[]> param = request.getParameterMap();
				modeKey = URLDecoder.decode(modeKey, "UTF-8");
				modeName = URLDecoder.decode(modeName, "UTF-8");
				modeDescription = URLDecoder.decode(modeDescription, "UTF-8");

				Model model = repositoryService.newModel();
				model.setKey(modeKey);
				model.setName(modeName);
				ObjectNode modelObjectNode = objectMapper.createObjectNode();
				modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, modeName);
				modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
				modeDescription = StringUtils.defaultString(modeDescription);
				modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, modeDescription);
				model.setMetaInfo(modelObjectNode.toString());
				repositoryService.saveModel(model);
				String modeId = model.getId();
				JsonNode rootNode = objectMapper.readTree(file.getInputStream());
				StringBuilder jsonBuilder = new StringBuilder();
				jsonBuilder.append("{");
				Iterator<String> iterable = rootNode.fieldNames();
				while (iterable.hasNext()) {
					String field = iterable.next();
					if (field.equals("resourceId")) {
						jsonBuilder.append("\"resourceId\":\"").append(modeId).append("\",");
					} else {
						jsonBuilder.append("\"").append(field).append("\":").append(rootNode.get(field)).append(",");
					}
				}
				repositoryService.addModelEditorSource(modeId,
						(jsonBuilder.substring(0, jsonBuilder.length() - 1) + "}").getBytes("utf-8"));
				retMap.put("code", String.valueOf(ResultCode.SUCCESS));
				String operator = getUserInfo().getName();
				String info = "导入了" + modeName + "流程模型";
				saveOperatorInfo(info, StatusEnum.LogInfoType14.getIntValue(), operator);
				result = Result.resultInfo(ResultCode.SUCCESS, retMap);
			} else {
				retMap.put("code", String.valueOf(ResultCode.MODEL_KEY_REPEAT));
				result = Result.resultInfo(ResultCode.FAILURE, "模型标识已存在");
			}
		} catch (IOException e) {
			e.printStackTrace();
			result = Result.resultInfo(ResultCode.FAILURE, "导入失败");
		}

		return result;
	}

	/**
	 * 核查流程模型标识符重复
	 * 
	 * @param modeKey
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="modeler/checkModelName",method={RequestMethod.POST})
	@ApiOperation("核查流程模型标识符重复")
	public Result checkModelName(@RequestParam(value = "modeKey", required = true) String modeKey,
			@RequestParam(value = "id", required = false) String id) {
		return Result.resultInfo(ResultCode.SUCCESS, this.checkModeltName(modeKey, id));
	}

	/**
	 * 打开模型创建页面并保存空model
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="modeler/open",method={RequestMethod.POST})
	public Result open(@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "key", required = false) String key,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "description", required = false) String description, HttpServletResponse response)
			throws Exception {
		Result res = null;
		boolean flag = false;
		try {
			Model model = null;
			if (StringUtils.isNotEmpty(id)) {
				model = repositoryService.getModel(id);
			}

			if (StringUtils.isEmpty(id) || model == null) {
				if ((boolean) this.checkModeltName(key, id).getData()) {
					model = repositoryService.newModel();
					model.setKey(key);
					model.setName(name);
					ObjectNode modelObjectNode = objectMapper.createObjectNode();
					modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
					modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
					description = StringUtils.defaultString(description);
					modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
					model.setMetaInfo(modelObjectNode.toString());
					repositoryService.saveModel(model);
					flag = true;
					id = model.getId();
					res = Result.resultInfo(ResultCode.SUCCESS, id);
				} else {
					res = Result.resultInfo(ResultCode.MODEL_KEY_REPEAT, null);
				}

			}else{
				repositoryService.saveModel(model);
				id = model.getId();
				flag = true;
				res = Result.resultInfo(ResultCode.SUCCESS, id);
			}
			if(flag){
				String operator = getUserInfo().getName();
				String info = "新增了" + name + "流程模型";
				saveOperatorInfo(info, StatusEnum.LogInfoType14.getIntValue(), operator);
			}
		} catch (Exception e) {
			e.printStackTrace();
			res = Result.resultInfo(ResultCode.FAILURE, "保存失败");
		}
		return res;
	}

	/**
	 * 删除流程模型
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="modeler/delete",method={RequestMethod.POST})
	@ApiOperation("删除流程模型")
	public Result delete(@RequestParam(value = "id") String id) {
		Model model = repositoryService.getModel(id);
		String key = model.getKey();
		int count = customFormService.selectByDefinitionKey(key);
		if(count > 0){
			return Result.resultInfo(ResultCode.FAILURE,"该流程模型已关联自定义表单，不能删除");
		}
		repositoryService.deleteModel(id);
		String operator = getUserInfo().getName();
		String info = "删除了" + model.getName() + "流程模型";
		saveOperatorInfo(info, StatusEnum.LogInfoType14.getIntValue(), operator);
		return Result.resultInfo(ResultCode.SUCCESS, null);
	}

	/**
	 * 发布流程模型
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="modeler/deploy",method={RequestMethod.POST})
	@ApiOperation("流程发布")
	public Result deploy(@RequestParam(value = "id") String id) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		Result res = null;
		Model modelData = repositoryService.getModel(id);
		String modelKey = modelData.getKey();
		String modelName = modelData.getName();
		String metaInfo = modelData.getMetaInfo();

		if ("".equals(modelKey) || modelKey == null) {
			return Result.resultInfo(ResultCode.MODEL_MODELKEY_NULL, null);
		}

		byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());

		if (bytes == null) {
			map.put("code", ResultCode.MODEL_DATA_NULL);
			return Result.resultInfo(ResultCode.MODEL_DATA_NULL, "没有相应的权限");
		}

		try {

			JsonNode modelNode = new ObjectMapper().readTree(bytes);
			byte[] bpmnBytes = null;

			BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);

			// 由于web设计器生成的model不显示连接线上的名称故特殊处理
			WorkflowUtils.addLabelsToModel(model);

			Process process = model.getProcesses().get(0);
			// 如果process的id使用的默认值，或为空，则修改为model的key
			if (StringUtils.isEmpty(process.getId()) || process.getId().equals("process")) {
				process.setId(modelKey);
			}
			// 如果process名称为空则取对应model的名称
			if (StringUtils.isEmpty(process.getName())) {
				process.setName(modelName);
			}

			bpmnBytes = new BpmnXMLConverter().convertToXML(model);

			String processName = modelData.getName() + ".bpmn20.xml";
			Deployment deployment = repositoryService.createDeployment().name(modelData.getName())
					.addString(processName, new String(bpmnBytes, "UTF-8")).deploy();
			modelData.setDeploymentId(deployment.getId());
			repositoryService.saveModel(modelData);
			String operator = getUserInfo().getName();
			String info = "发布了" + modelData.getName() + "流程";
			saveOperatorInfo(info, StatusEnum.LogInfoType14.getIntValue(), operator);
			res = Result.resultInfo(ResultCode.SUCCESS, null);
		} catch (Exception e) {
			e.printStackTrace();
			res = Result.resultInfo(ResultCode.MODEL_PUBLISH_ERROR, null);
		}

		return res;
	}

	/**
	 * 获得流程模型数据
	 * 
	 * @param modelId
	 * @return
	 * @throws Exception
	 * 
	 */
	@RequestMapping(value="modeler/model/{modelId}/json",method={RequestMethod.POST})
	@ResponseBody
	public String openModel(@PathVariable(value = "modelId") String modelId) throws Exception {
		Map root = (Map) getModelInfo(modelId).getData();
		logger.info("model : {}", root);
		return objectMapper.writeValueAsString(root);
	}

	/**
	 * 导出模型的json数据
	 * 
	 * @param modelId
	 * @param response
	 * @param request
	 * @throws Exception
	 * @return void 获取json_xml
	 */
	@RequestMapping(value="modeler/model/{modelId}/export",method={RequestMethod.POST})
	public void exportModeler(@PathVariable(value = "modelId") String modelId, HttpServletResponse response,
			HttpServletRequest request) throws Exception {

		Map model = (Map) getModelInfo(modelId).getData();
		DocUtil.prepareDownloadFile((String) model.get("name") + ".json", request, response);
		OutputStream os = response.getOutputStream();
		try {
			os.write(objectMapper.writeValueAsString(model.get("model")).getBytes("utf-8"));
			os.flush();
		} finally {
			if (os != null) {
				os.close();
			}
		}
	}

	/**
	 * 根据id获取模型信息
	 * 
	 * @param modelId
	 * @return
	 * @throws Exception
	 * @return Map
	 */
	private Result getModelInfo(String modelId) throws Exception {
		Model model = repositoryService.getModel(modelId);

		if (model == null) {
			logger.info("model({}) is null", modelId);
			model = repositoryService.newModel();
			repositoryService.saveModel(model);
		}

		Map root = new HashMap();
		root.put("modelId", model.getId());
		root.put("name", model.getName());
		root.put("revision", 1);
		String info = model.getMetaInfo();
		String description = "description";
		if (StringUtils.isNotEmpty(info)) {
			description = (String) objectMapper.readValue(info, Map.class).get("description");
		}
		root.put("description", description);

		byte[] bytes = repositoryService.getModelEditorSource(model.getId());

		if (bytes != null) {
			String modelEditorSource = new String(bytes, "utf-8");
			logger.info("modelEditorSource : {}", modelEditorSource);

			Map modelNode = objectMapper.readValue(modelEditorSource, Map.class);
			root.put("model", modelNode);
		} else {
			Map modelNode = new HashMap();
			modelNode.put("id", "canvas");
			modelNode.put("resourceId", "canvas");

			Map stencilSetNode = new HashMap();
			stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
			modelNode.put("stencilset", stencilSetNode);

			model.setMetaInfo(objectMapper.writeValueAsString(root));
			model.setName("name");
			model.setKey("key");

			root.put("model", modelNode);
		}

		return Result.resultInfo(ResultCode.SUCCESS, root);

	}

	/**
	 * 获得配置信息
	 * 
	 * @return
	 * @throws Exception
	 *             获取保存svg_xml
	 */
	@RequestMapping(value="modeler/editor/stencilset",method={RequestMethod.POST})
	@ResponseBody
	public String stencilset() throws Exception {
		InputStream stencilsetStream = this.getClass().getClassLoader().getResourceAsStream("stencilset.json");
		try {
			return IOUtils.toString(stencilsetStream, "utf-8");
		} catch (Exception e) {
			throw new RuntimeException("Error while loading stencil set", e);
		}
	}

	/**
	 * 保存流程模型
	 * 
	 * @param modelId
	 * @param description
	 * @param jsonXml
	 * @param name
	 * @param svgXml
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="modeler/model/{modelId}/save",method={RequestMethod.POST})
	@ResponseBody
	@ApiOperation("保存流程模型")
	public String save(@PathVariable(value = "modelId") String modelId,
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "json_xml") String jsonXml, @RequestParam(value = "name") String name,
			@RequestParam(value = "svg_xml") String svgXml) throws Exception {
		Model model = repositoryService.getModel(modelId);
		model.setName(name);

		ObjectNode modelObjectNode = objectMapper.createObjectNode();
		modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
		modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
		description = StringUtils.defaultString(description);
		modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
		model.setMetaInfo(modelObjectNode.toString());
		String operator = getUserInfo().getName();
		String info = "保存了" + name + "流程模型数据";
		saveOperatorInfo(info, StatusEnum.LogInfoType14.getIntValue(), operator);
		logger.info("jsonXml : {}", jsonXml);
		repositoryService.saveModel(model);
		repositoryService.addModelEditorSource(model.getId(), jsonXml.getBytes("utf-8"));
		return "保存成功";
	}

	/**
	 * 核查标识符是否重复
	 * 
	 * @param modeKey
	 *            标识符
	 * @param id
	 *            主键
	 * @return
	 */
	public Result checkModeltName(String modeKey, String id) {
		Boolean flag = true;// 没有

		// 查询流程定义
		String sql = "select * from act_re_model where key_ = '" + modeKey + "'";
		List<Model> list = repositoryService.createNativeModelQuery().sql(sql).list();

		// 是否有重复
		if (list.size() > 0) {
			for (Model p : list) {
				if (p.getId().equals(id)) {
					continue;
				} else {
					flag = false;
					break;
				}
			}
		}

		return Result.resultInfo(ResultCode.SUCCESS, flag);
	}

	/**
	 * 获得流程模型数据
	 * 
	 * @param modelName
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="modeler/modelerInfo",method={RequestMethod.POST})
	public Result list(@RequestParam(value = "modelName", required = false) String modelName) {
		List<Model> models = null;
		if (StringUtils.isEmpty(modelName)) {
			models = repositoryService.createModelQuery().list();// .listPage((pageNumber-1)*pageSize,pageNumber*pageSize);
		} else {
			models = repositoryService.createModelQuery().modelNameLike(modelName).list();// listPage((pageNumber-1)*pageSize,pageNumber*pageSize);
		}
		Map<String, String> modelerMap = new HashMap<String, String>();

		for (Model m : models) {
			if (StringUtils.isNoneEmpty(m.getKey())) {
				modelerMap.put(m.getKey(), m.getName());
			}
		}
		return Result.resultInfo(ResultCode.SUCCESS, modelerMap);
	}

	@RequestMapping(value = "viewPic")
	public void viewPic(@RequestParam(value = "deploymentId")String deploymentId,HttpServletResponse response){
		try {
			//获取图片资源名称
			List<String> list = processEngine.getRepositoryService()//
					.getDeploymentResourceNames(deploymentId);
			//定义图片资源的名称
			String resourceName = "";
			if(list!=null && list.size()>0){
				for(String name:list){
					if(name.indexOf(".png")>=0){
						resourceName = name;
					}
				}
			}
			//获取图片的输入流
			InputStream in = processEngine.getRepositoryService()//
					.getResourceAsStream(deploymentId, resourceName);
			response.setContentType("image/png");
			IOUtils.copy(in, response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
