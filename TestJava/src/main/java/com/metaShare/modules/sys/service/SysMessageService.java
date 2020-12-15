package com.metaShare.modules.sys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.sys.dao.SysMessageDao;
import com.metaShare.modules.sys.dao.SysUserDao;
import com.metaShare.modules.sys.entity.SysMessage;
import com.metaShare.modules.sys.entity.SysUser;

import java.util.List;

@Service
public class SysMessageService extends ServiceImpl<SysMessageDao,SysMessage> {
	@Autowired
	protected SysMessageDao messageDao;
	@Autowired
	protected SysUserDao userDao;
	
	/**
	 * 更新签收状态，status为0：未签收，1：已签收
	 * @param id
	 * @param status 
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年2月15日 上午9:28:33
	 */
	public void updateStatus(String id,Long status){
		SysMessage message = messageDao.selectById(id);
		message.setStatus(status);
		messageDao.updateAllColumnById(message);
	}
	
	public SysMessage save(SysMessage m) {
		messageDao.insert(m);
        if(m.getReceiver()!=null){
        	SysUser user = userDao.selectById(m.getReceiver());
        }
	    return m;
	}
	
	public SysMessage saveAndFlush(SysMessage m) {
	    return save(m);
	}
	
	public Iterable<SysMessage> save(Iterable<SysMessage> list) {
		for(SysMessage m:list){
			messageDao.insert(m);
		}
	    for(SysMessage message : list){
	        if(message.getReceiver()!=null){
	        	SysUser user = userDao.selectById(message.getReceiver());
	        }
	    }
	    return list;
	}

//	public List<SysMessage> queryDataListByConditions(int status, String subject, String content,String userId) {
//		return messageDao.selectDataListByConditions(status,subject,content,userId);
//	}
	public List<SysMessage> queryDataListByConditions(int status, String subject, String content,String userId,String messageType) {
		return messageDao.selectDataListByConditions(status,subject,content,userId,messageType);
	}

	public List<SysMessage> queryAllList(String userId) {

		return messageDao.selectAllList(userId);

	}

	public int getReadedNumber(String userId) {

		return messageDao.getReadedNumber(userId);

	}

	public int getUnReaderNumber(String userId) {

		return messageDao.getUnReadedNumber(userId);

	}

	public void updateMessageStatus(String userId) {

		messageDao.updateMessageStatus(userId);
	}

	public void updateMessageStatusOnId(String userId, String id) {

		messageDao.updateMessageStatusOnId(userId,id);
	}

	public void emptyMessage(String userId) {
		messageDao.emptyMessage(userId);
	}

	public List<SysMessage> getMessageList(String userId, String loginName) {

		return messageDao.getMessageList(userId,loginName);
	}

	public void deleteAllMessageByUserId(String userId) {
		messageDao.deleteAllMessageByUserId(userId);
	}
}
