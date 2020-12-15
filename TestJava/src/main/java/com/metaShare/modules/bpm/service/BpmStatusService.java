package com.metaShare.modules.bpm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.bpm.dao.BpmStatusDao;
import com.metaShare.modules.bpm.entity.BpmStatus;


@Service
public class BpmStatusService extends ServiceImpl<BpmStatusDao,BpmStatus> {
	@Autowired
	BpmStatusDao bpmStatusDao;
	
	/**
	 * 根据流程实例id查找流程状态信息
	 * @param processInstinceId
	 * @return 
	 * @return BpmBusMiddle
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月16日 下午8:18:00
	 */
	public BpmStatus findByProcessInstinceId(String  processInstinceId){
		return bpmStatusDao.findByProcessInstinceId(processInstinceId);
	}
	
	/**
	 * 判断流程是否可以提交
	 * @param businessKey
	 * @return 
	 * @return boolean
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月16日 下午8:18:17
	 */
	public boolean checkProcessStart(String businessKey){
		List<BpmStatus> list = bpmStatusDao.findByBusinessKey(businessKey);
		boolean flag = true;
		for(BpmStatus status : list){
			//如果有状态为活跃且状态不为审批不通过的，则不允许提交流程
			if(status.isActive()&&status.getProcessStatus()!=2){
				flag = false;
				break;
			}
		}
		return flag;
	}
	
	/**
	 * 根据业务主键删除流程状态表信息
	 * @param businessKey 
	 * @param processDefinitionKey 
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年1月11日 上午9:41:38
	 */
	public void deleteByBusinessKeyAndDefineKey(String businessKey,String processDefinitionKey){
		bpmStatusDao.deleteByBusinessKeyAndDefineKey(businessKey, processDefinitionKey);
	}
	
	/**
	 * 根据流程实例id删除流程状态
	 * @param processInstanceId 
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年3月24日 下午5:37:38
	 */
	public void deleteByProcessInstanceId(String processInstanceId){
		bpmStatusDao.deleteByProcessInstanceId(processInstanceId);
	}
	
	
	/**
	 * 根据业务主键更新流程状态表是否活跃为否
	 * @param businessKey 
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年1月11日 下午3:51:03
	 */
	public void updateActiveFalseBybusinessKey(String businessKey){
		bpmStatusDao.updateActiveFalseBybusinessKey(businessKey);
	}
	
	/**
	 * 根据主键查询流程状态
	 * @param businessKey
	 * @return 
	 * @return List<BpmStatus>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年5月3日 下午3:19:15
	 */
	public List<BpmStatus> findByBusinessKey(String businessKey){
		return bpmStatusDao.findByBusinessKey(businessKey);
	}
	public List<BpmStatus> findByBusinessKey(String[] businessKey){
		return bpmStatusDao.findByBusinessKeyIn(businessKey);
	}
	/**
	 * 根据businessKey查找act_ru_execution运行时流程执行实例表的proc_inst_id
	 * @param businessKey
	 * @return 
	 * @return Integer
	 */
	public List<String> findactruexecution(String businessKey) {
		return bpmStatusDao.findactruexecution(businessKey);
	}
	/**
	 * 根据procinstid删除关联表act_ru_identitylink运行时流程人员表，主要存储任务节点与参与者的相关信息
	 * @param businessKey
	 * @return 
	 * @return Integer
	 */
	public void deleteactruidentitylink(String procinstid) {
		bpmStatusDao.deleteactruidentitylink(procinstid);		
	}
	/**
	 * 根据procinstid删除关联表act_ru_task运行时任务节点表
	 * @param businessKey
	 * @return 
	 * @return Integer
	 */
	public void deleteactrutask(String procinstid) {
		bpmStatusDao.deleteactrutask(procinstid);	
	}
	/**
	 * 根据procinstid删除关联表act_ru_variable运行时流程变量数据表
	 * @param businessKey
	 * @return 
	 * @return Integer
	 */
	public void deleteactruvariable(String procinstid) {
		bpmStatusDao.deleteactruvariable(procinstid);
	}
	/**
	 * 根据businessKey删除表act_ru_execution运行时流程执行实例表的数据
	 * @param businessKey
	 * @return 
	 * @return Integer
	 */
	public void deleteactruexecution(String businessKey) {
		bpmStatusDao.deleteactruexecution(businessKey);
	}
}
