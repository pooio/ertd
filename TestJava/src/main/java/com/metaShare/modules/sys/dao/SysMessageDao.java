package com.metaShare.modules.sys.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.sys.entity.SysMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
public interface SysMessageDao extends BaseMapper<SysMessage> {

    List<SysMessage> selectDataListByConditions(@Param("status") int status, @Param("subject")String subject, @Param("content")String content,@Param("userId") String userId,@Param("messageType")String messageType);

    List<SysMessage> selectAllList(String userId);

    int getReadedNumber(String userId);

    int getUnReadedNumber(String userId);

    void updateMessageStatus(String userId);

    void updateMessageStatusOnId(@Param("userId") String userId, @Param("id") String id);

    void emptyMessage(String userId);

    List<SysMessage> getMessageList(@Param("userId") String userId,@Param("loginName") String loginName);

    void deleteAllMessageByUserId(@Param("userId") String userId);
}
