package com.metaShare.modules.bpm.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.metaShare.common.utils.StatusEnum;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import  org.activiti.bpmn.model.Process;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

/**
 * 工作流web设计器controller
 * 
 *
 */
@Controller
@RequestMapping("bpm")
@Api(tags = "WEB接口")
public class BpmProcessModelerController  extends BaseController {
    private static Logger logger = LoggerFactory
            .getLogger(BpmProcessModelerController.class);
    
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ManagementService managementService;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    
    /**
     * 打开流程模型列表
     * @return
     * @throws Exception
     */
    @RequestMapping("modeler")
    public String modeler() throws Exception {
    	return "/bpm/modeler/main";
    }
    
    /**
     * 获得流程模型数据
     * @param modelName
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @ResponseBody
    @RequestMapping("modeler/list")
    public Result list(@RequestParam(value="modelName",required = false) String modelName,
    					@RequestParam("pageNumber") int pageNumber,
    					@RequestParam("pageSize") int pageSize) {
    	List<Model> models = null;
    	if(StringUtils.isEmpty(modelName)){
    		models = repositoryService.createModelQuery().list();//.listPage((pageNumber-1)*pageSize,pageNumber*pageSize);
    	}else{
    		models = repositoryService.createModelQuery().modelNameLike(modelName).list();//listPage((pageNumber-1)*pageSize,pageNumber*pageSize);
    	}
		return Result.resultInfo(ResultCode.SUCCESS, new PageTool<Model>().getPage(models, pageSize, pageNumber));
    }

    
    /**
     * 新增页面
     * @return
     * @throws Exception 
     * @return String
     */
    @RequestMapping("modeler/create")
    public String create()
            throws Exception {
        return "/bpm/modeler/add";
    }
    
    /**
     * 导入页面
     * @return
     * @throws Exception 
     * @return String
     */
    @RequestMapping("modeler/import")
    public String importModel()
            throws Exception {
        return "/bpm/modeler/import";
    }
    
    /**
     * 导入模型
     * @param file
     * @param modeKey
     * @param modeName
     * @param modeDescription
     * @param request
     * @return 
     * @return Map<String,String>
     */
    @ResponseBody
    @RequestMapping(value="modeler/importFile")
    public Map<String, String> importFile(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value="modeKey") String modeKey,
            @RequestParam(value="modeName") String modeName,
            @RequestParam(value="modeDescription",required = false) String modeDescription,
            HttpServletRequest request) throws Exception {
        Map<String, String> retMap = new HashMap<String,String>();
        Map<String, String[]> param = request.getParameterMap();
        modeKey = URLDecoder.decode(modeKey,"UTF-8");
        modeName = URLDecoder.decode(modeName,"UTF-8");
        modeDescription = URLDecoder.decode(modeDescription,"UTF-8");
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
            if (field.equals("resourceId")){
                jsonBuilder.append("\"resourceId\":\"").append(modeId).append("\",");
            } else {
               jsonBuilder.append("\"").append(field).append("\":").append(rootNode.get(field)).append(",");
            }
        }
        repositoryService.addModelEditorSource(modeId,(jsonBuilder.substring(0, jsonBuilder.length()-1)+"}").getBytes("utf-8"));
        
        return retMap;
    }
    /**
     * 打开模型创建页面并保存空model
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping("modeler/open")
    public String open(@RequestParam(value = "id",required=false) String id,
    		@RequestParam(value = "key",required=false) String key,
    		@RequestParam(value = "name",required=false) String name,
    		@RequestParam(value = "description",required=false) String description)throws Exception {
        Model model = null;
        if (StringUtils.isNotEmpty(id)){
        	model = repositoryService.getModel(id);
        }

        if (StringUtils.isEmpty(id)||model == null) {
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
            id = model.getId();
        }

        return "redirect:/bpm/modelerIndex.html?modelId=" + id;
    }

    /**
     * 删除流程模型
     * @param ids
     * @return
     */
    @ResponseBody
    @RequestMapping("modeler/delete")
    public Result  delete(@RequestParam(value="id") String[] ids) {
    	for(String id : ids){
    		repositoryService.deleteModel(id);
    	}
		return  Result.resultInfo(ResultCode.SUCCESS,  "删除流程模型成功");
    }
    
    
   

    /**
     * 发布流程模型
     * @param id
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("modeler/deploy")
    public Result deploy(@RequestParam(value="id") String id) throws Exception {
        Map<String, String> map = new HashMap<String,String>();
        Result res = null;
        Model modelData = repositoryService.getModel(id);
        String modelKey = modelData.getKey();
        String modelName = modelData.getName();
        String metaInfo = modelData.getMetaInfo();
        
        byte[] bytes = repositoryService
                .getModelEditorSource(modelData.getId());

        
        if (bytes == null) {
            return Result.resultInfo(ResultCode.FAILURE,"模型数据为空，请先设计流程并成功保存，再进行发布。");
        }
        
        try {
        	JsonNode modelNode = new ObjectMapper().readTree(bytes);
            byte[] bpmnBytes = null;

           
            BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
           
            //由于web设计器生成的model不显示连接线上的名称故特殊处理
            WorkflowUtils.addLabelsToModel(model);
            
            Process process = model.getProcesses().get(0);
            //如果process的id使用的默认值，或为空，则修改为model的key
            if(StringUtils.isEmpty(process.getId())||process.getId().equals("process")){
            	process.setId(modelKey);
            }
            //如果process名称为空则取对应model的名称
            if(StringUtils.isEmpty(process.getName())){
            	process.setName(modelName);
            }
            
            bpmnBytes = new BpmnXMLConverter().convertToXML(model);
            
            String processName = modelData.getName() + ".bpmn20.xml";
            Deployment deployment = repositoryService.createDeployment()
                    .name(modelData.getName())
                    .addString(processName, new String(bpmnBytes, "UTF-8")).deploy();
            modelData.setDeploymentId(deployment.getId());
            repositoryService.saveModel(modelData);
            res = Result.resultInfo(ResultCode.SUCCESS,  "流程模型发布成功！");
		} catch (Exception e) {
			 res = Result.resultInfo(ResultCode.FAILURE,  "流程模型发布失败！");
		}
        return res;
    }

    /**
     * 获得流程模型数据
     * @param modelId
     * @return
     * @throws Exception
     */
    @RequestMapping("modeler/model/{modelId}/json")
    @ResponseBody
    public void openModel(@PathVariable(value = "modelId") String modelId,HttpServletResponse response)
            throws Exception {
        Map root = (Map) getModelInfo(modelId).getData();
        logger.info("model : {}", root);
        response.setCharacterEncoding("UTF-8"); //设置编码格式
        response.setContentType("text/html");   //设置数据格式
        PrintWriter out = response.getWriter(); //获取写入对象
        String json = objectMapper.writeValueAsString(root);
        out.print(json); //将json数据写入流中
        out.flush();
        //return objectMapper.writeValueAsString(root);
    }
    
    /**
     * 导出模型的json数据
     * @param modelId
     * @param response
     * @param request
     * @throws Exception 
     * @return void
     */
    @RequestMapping("modeler/model/{modelId}/export")
    public void exportModeler(@PathVariable(value = "modelId") String modelId,HttpServletResponse response,HttpServletRequest request)
            throws Exception {
        Map model = (Map) getModelInfo(modelId).getData();
        DocUtil.prepareDownloadFile((String)model.get("name")+".json", request, response);
        OutputStream os=response.getOutputStream();  
        try {
            os.write( objectMapper.writeValueAsString(model.get("model")).getBytes("utf-8"));
            os.flush();
        } finally {
           if(os!=null){
               os.close();
           }
        }
    }
    
    
    /**
     * 根据id获取模型信息
     * @param modelId
     * @return
     * @throws Exception 
     * @return Map
     */
    private Result getModelInfo( String modelId) throws Exception{
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
        if(StringUtils.isNotEmpty(info)){
            description = (String) objectMapper.readValue(info, Map.class).get("description");
        }
        root.put("description",  description);

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
            stencilSetNode.put("namespace",
                    "http://b3mn.org/stencilset/bpmn2.0#");
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
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "modeler/editor/stencilset",produces="application/json;charset=UTF-8")
    @ResponseBody
    public String stencilset() throws Exception {
        InputStream stencilsetStream = this.getClass().getClassLoader()
                .getResourceAsStream("stencilset.json");
        try {
        	String str = IOUtils.toString(stencilsetStream, "utf-8");
            return str;
        } catch (Exception e) {
            throw new RuntimeException("Error while loading stencil set", e);
        }
    }

    /**
     * 保存流程模型
     * @param modelId
     * @param description
     * @param jsonXml
     * @param name
     * @param svgXml
     * @return
     * @throws Exception
     */
    @RequestMapping("modeler/model/{modelId}/save")
    @ResponseBody
    public String save(@PathVariable(value = "modelId") String modelId,
    	    @RequestParam(value="description", required = false)String description,
            @RequestParam(value="json_xml") String jsonXml,
            @RequestParam(value="name") String name,
            @RequestParam(value="svg_xml") String svgXml) throws Exception {
        Model model = repositoryService.getModel(modelId);
        model.setName(name);
        
        ObjectNode modelObjectNode = objectMapper.createObjectNode();
        modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        description = StringUtils.defaultString(description);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
        model.setMetaInfo(modelObjectNode.toString());
        logger.info("jsonXml : {}", jsonXml);
        repositoryService.saveModel(model);
        repositoryService.addModelEditorSource(model.getId(),jsonXml.getBytes("utf-8"));
        return "{\"data\":true}";
    }

}
