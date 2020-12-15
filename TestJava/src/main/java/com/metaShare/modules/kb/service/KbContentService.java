package com.metaShare.modules.kb.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.common.utils.StrUtils;
import com.metaShare.modules.kb.dao.KbContentDao;
import com.metaShare.modules.kb.entity.KbContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by pc on 2020/2/18.
 */
@Service
public class KbContentService extends ServiceImpl<KbContentDao, KbContent> {
    @Autowired
    private KbContentDao kbContentDao;

    public List<KbContent> getList(String kbDirPk,String kbName) {
        return kbContentDao.getList(kbDirPk,kbName);
    }

    public int getKbContentCountNum() {
        return kbContentDao.getKbContentCountNum();
    }

    public List<KbContent> getContentListByKeyWord(String type, String keyWord,String kbDirPk) {
//        String strWhere = " where kb_dir_pk=" + kbDirPk;
//        if ("1".equals(type)) {
//            strWhere += " AND kb_name LIKE '%" + keyWord + "%'";
//        } else if ("2".equals(type)) {
//            strWhere += " AND key_word LIKE '%" + keyWord + "%'";
//        } else {
//            strWhere += " AND (kb_name LIKE '%" + keyWord + "%'";
//            strWhere += " OR key_word LIKE '%" + keyWord + "%'";
//            strWhere += " OR kb_content LIKE '%" + keyWord + "%')";
//        }
        return kbContentDao.getContentListByKeyWord(type,keyWord,kbDirPk);
    }

    public void updateQueryNumber(String kbPk) {
        //String where = " AND kb_pk IN(" + kbPk + ")";
        kbContentDao.updateQueryNumber(kbPk);
    }

    public List<KbContent> getLinkContentList(String linkCode) {
        String sql = "";
        if (!StrUtils.isEmpty(linkCode)) {
            linkCode = linkCode.replaceAll(",", "','");
            sql += " AND kb_code in('" + linkCode + "')";
        }
        return kbContentDao.getContentListByKeyWord(sql,"","");

    }
    
}
