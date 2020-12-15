package com.metaShare.modules.bpm.util;

import java.util.Collection;
import java.util.List;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.GraphicInfo;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class WorkflowUtils {

	private static Logger log = LoggerFactory.getLogger(WorkflowUtils.class);
    /**
     * 为model中的sequenceFlow添加label
     * <br/>由于web设计器生成的model没有保存label信息，
     * <br/>造成流程图中不显示sequenceFlow连接线的名称
     * @param model
     * @return
     */
    public static BpmnModel addLabelsToModel(BpmnModel model){
    	if(MapUtils.isEmpty(model.getLabelLocationMap())){
    		 Collection<FlowElement> elements =  model.getProcesses().get(0).getFlowElements();
             for(FlowElement element : elements){
    	      	  if("SequenceFlow".equals(element.getClass().getSimpleName())
    	      			  && StringUtils.isNotEmpty(element.getName()) ){
    	      		 List<GraphicInfo> flowGraphicInfos = model.getFlowLocationMap().get(element.getId());
    	      		 int width=100;
    	      		 Double max = 0.0;
    	      		 GraphicInfo start=null;
    	      		 GraphicInfo end = null;
    	      		 for(int i=0;i<flowGraphicInfos.size()-1;i++){
    	      			 Double xtemp = Math.abs(flowGraphicInfos.get(i).getX()-flowGraphicInfos.get(i+1).getX());
    	      			 if(max<=xtemp){
    	      				max = xtemp;
    	      				start = flowGraphicInfos.get(i);
    	      				end = flowGraphicInfos.get(i+1);
    	      			 }
    	      		 }
    	      		
    	      		 if(start!=null && end!=null){
    	      			Double dblTemp = (start.getX()-end.getX()); 
    	      			GraphicInfo point = null;
    	      			Double xPoint = 0.0;
    	      			Double yPoint = 0.0;
    	      			
    	      			//竖线
    	      			if(Math.abs(dblTemp)<Math.abs(start.getY()-end.getY())){
    	      				if (start.getY()-end.getY()>0) {
    	      					point= start;
    	      				} else {
    	      					point= end;
    	      				}
    	      				xPoint = point.getX()+2;
    	      				yPoint = point.getY()-Math.abs(start.getY()-end.getY())/2;
	      				}else{//横线
	      					if (dblTemp<0) {
	    	      				point= start;
		      				} else {
		      					point= end;
		      				}
    	      				xPoint = point.getX()+2;
    	      				yPoint = point.getY()+2;
    	      				if(width<max){
    	      					width = max.intValue();
    	      				}
	      				}
    	      			
	      				
						GraphicInfo labelGraphicInfo = new GraphicInfo();
						labelGraphicInfo.setElement(element);
						labelGraphicInfo.setX(xPoint);
						labelGraphicInfo.setY(yPoint);
						labelGraphicInfo.setWidth(width);
						labelGraphicInfo.setHeight(10);
						labelGraphicInfo.setXmlColumnNumber(element.getXmlColumnNumber());
						labelGraphicInfo.setXmlRowNumber(element.getXmlRowNumber());
						model.addLabelGraphicInfo(element.getId(), labelGraphicInfo);
    	      		 }
    	      		
    	      		
    	      	  }
             }
    	}
         return model;
    }

}
