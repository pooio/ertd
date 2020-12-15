package com.metaShare.modules.sys.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.sys.dao.SysMenuDao;
import com.metaShare.modules.sys.entity.SysMenu;

/**
 * 系统菜单
 */
@Service
public class SysMenuService extends ServiceImpl<SysMenuDao, SysMenu> {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	public List<SysMenu> listUserMenus(Integer userId) {
		return baseMapper.listUserMenus(userId);
	}

	public void insertOrUpdateByMenu(SysMenu menu) {
		Timestamp timestamp = new Timestamp((new Date()).getTime());
		 String dateStr = timestamp.toString();
		 dateStr = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(timestamp);
		 String url = menu.getUrl() !=null &&  !menu.getUrl().equals("") ? "'"+ menu.getUrl() +"'" : "NULL" ;
		 String icon = menu.getIcon() !=null &&  !menu.getIcon().equals("") ? "'"+ menu.getIcon() +"'" : "NULL" ;
		 if(menu.getId() > 0){
			 jdbcTemplate.update("update sys_menu set menu_name='" + menu.getMenuName() + "', url=" + url + ", icon=" + icon + ", update_date ='" + dateStr + "' WHERE id=" + menu.getId());
		 }else{
			 Integer sort = 0;
			 Integer parentId = Integer.parseInt( menu.getTarget());
			 if(menu.getAddType().equals("sibling")){
				 SysMenu sm = this.selectById(menu.getTarget());
				 parentId =sm.getParentId();
				 sort = jdbcTemplate.queryForObject("SELECT sort FROM sys_menu where parent_id = " + parentId +" ORDER BY sort DESC LIMIT 0,1;", Integer.class);
			 }else{
				if(jdbcTemplate.queryForObject("SELECT count(1) FROM sys_menu where parent_id = " + parentId +";", Integer.class) > 0 ){
					 sort = jdbcTemplate.queryForObject("SELECT sort FROM sys_menu where parent_id = " + parentId +" ORDER BY sort DESC LIMIT 0,1;", Integer.class);
				}
			 }
			 sort++;
		
			 String sql ="INSERT INTO sys_menu (menu_name,parent_id , sort, url, icon, is_show, create_date, update_date) "+
					 " VALUES ('" + menu.getMenuName()+ "', '" + parentId + "', '" + sort + "',"+url+"," + icon + ",b'1','" + dateStr + "','"+dateStr+"')";
			 jdbcTemplate.execute(sql);
		}
	}
}
