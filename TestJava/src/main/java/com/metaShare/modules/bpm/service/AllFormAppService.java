package com.metaShare.modules.bpm.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.bpm.dao.BpmDelegateDao;
import com.metaShare.modules.bpm.dto.AllFormAppDTO;
import com.metaShare.modules.bpm.entity.BpmDelegate;


@Service(value="AllFormAppService")
public class AllFormAppService extends ServiceImpl<BpmDelegateDao,BpmDelegate>{
	
	public List<AllFormAppDTO> queryAllFormAppDTOList(String processName) {
		
		List<AllFormAppDTO> formApp = new ArrayList<AllFormAppDTO>() ;
		
		return formApp;
	}
}
