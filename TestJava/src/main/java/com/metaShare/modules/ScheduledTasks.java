package com.metaShare.modules;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.metaShare.modules.sys.controller.SysCalendarController;
import com.metaShare.modules.sys.controller.SysEmailController;
import com.metaShare.modules.sys.entity.SysCalendar;
import com.metaShare.modules.sys.service.SysEmailService;

import java.util.Properties;
/**
 * 
* @ClassName: ScheduledTasks 
* @Description: 任务调度平台
* @author A18ccms a18ccms_gmail_com 
* @date 2019年11月5日 上午8:26:54 
*
 */
@Component
public class ScheduledTasks {
	 
	@Autowired
	private SysEmailController sysEmailController;
	@Autowired
	private SysCalendarController sysCalendarController;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    //@Scheduled(fixedRate = 5000)
    @Scheduled(cron = "0 0/1 * * * ?") // 每分钟执行一次
    public void EmailTaskInfo() {
    	try {
    		sysEmailController.SendEmailList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	log.info("The time is now {}", dateFormat.format(new Date()));
    }
    @Scheduled(cron = "0 38 16 * * ?") // 每分钟执行一次
    public void calendarInfo() {
    	try {
    		//sysCalendarController.SendCalendarInfo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	log.info("The time is now {}", dateFormat.format(new Date()));
    }
    
    /**
     * 接收邮件 
     */
    @Scheduled(cron="0 10 8 * * ?")
    public void timerTaskInfo(){
//    	//邮件配置信息
//        String host=Constants.MAIL_HOST;
//        String userName=Constants.MAIL_USER_NAME;
//        String passWord=Constants.MAIL_PASS_WORD;
//        Properties props = System.getProperties();
//        props.setProperty("mail.imap.host", "imap.partner.outlook.cn");
//        props.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
//        props.setProperty("mail.imap.socketFactory.fallback", "false");
//        props.setProperty("mail.imap.port", "993");
//        props.setProperty("mail.imap.socketFactory.port", "993");
//        props.setProperty("mail.imap.auth", "true");
//        //邮件配置类
//        //Properties properties=new Properties();
//        //邮件配置缓存
//        Session session=Session.getDefaultInstance(properties);
//        session.setDebug(true);
        String fileName=null;
    	
    }

}
