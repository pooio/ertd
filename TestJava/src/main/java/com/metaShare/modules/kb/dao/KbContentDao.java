package com.metaShare.modules.kb.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.kb.entity.KbContent;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by pc on 2020/2/18.
 */
public interface KbContentDao  extends BaseMapper <KbContent>{
    public List<KbContent> getList(@Param("kbDirPk") String kbDirPk,@Param("kbName") String kbName);
    public int getKbContentCountNum();
    //type,keyWord,kbDirPk
    public List<KbContent> getContentListByKeyWord(@Param("type") String type,@Param("keyWord") String keyWord,@Param("kbDirPk") String kbDirPk);
    public void updateQueryNumber(@Param("kbPk") String kbPk);
}
