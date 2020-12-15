package com.metaShare.modules.bpm.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.bpm.dao.BpmDelegateDao;
import com.metaShare.modules.bpm.dto.AllFormAppDTO;
import com.metaShare.modules.bpm.entity.BpmDelegate;


@Service
public class BpmDelegateService extends ServiceImpl<BpmDelegateDao,BpmDelegate> {
	
	@Autowired
	BpmDelegateDao bpmDelegateDao;
	/**
	 * 根据创建人的登录名查找其所有状态为启用<br/>
	 * 并且委托任务时间区间包含当前时间的的委托任务
	 * @param loginName
	 * @return 
	 * @return List<BpmDelegate>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年3月16日 上午8:08:49
	 * @throws ParseException 
	 */
	public List<BpmDelegate> findByLoginName(String loginName,String processKey) throws ParseException{
		return bpmDelegateDao.findAllWithNoPageNoSort(loginName,processKey,new Date());
	}
	
	public List<BpmDelegate> findByLoginName(List<String> loginNames,String processKey) throws ParseException{
		return bpmDelegateDao.findAllByLoginNames(loginNames,processKey,new Date());
	}
	
	/**
	 * 验证是否合法
	 * @param bpmDelegate
	 * @return 
	 * @return Boolean
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年4月22日 上午10:41:09
	 */
	public Boolean checkBpmdelegate(BpmDelegate bpmDelegate){
		boolean flag = true;
		
		Date checkStartDate = bpmDelegate.getStartDate();
		Date checkEndDate = bpmDelegate.getEndDate();
		String id = null;
		if (StringUtils.isNotEmpty(bpmDelegate.getId())) {
			id = bpmDelegate.getId();
		}
		
		List<BpmDelegate> list = bpmDelegateDao
				.findAllDateAndId(checkStartDate,checkEndDate,
						bpmDelegate.getUserId(),bpmDelegate.getProcessKey(),
						id);
		if(CollectionUtils.isNotEmpty(list)){
			flag = false;
		}
		return flag;
	}

	public List<AllFormAppDTO> findApp(String processName,String loginName,Integer approvalType,String startDate,String endDate,String processDefinitionName) {
		return bpmDelegateDao.findApp(processName,loginName,approvalType,startDate,endDate,processDefinitionName);
	}
}
