/**
 * 
 */
package com.metaShare.modules.sys.service;


import java.io.File;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.common.utils.DateUtil;
import com.metaShare.modules.sys.dao.SysEmailDao;
import com.metaShare.modules.sys.entity.SysEmail;


/**
 * @author pc
 *
 */
@Service
public class SysEmailService extends ServiceImpl<SysEmailDao,SysEmail>{

	@Autowired
    private JavaMailSender mailSender;
    
	@Autowired
	private SysEmailDao sysEmailDao;
	public void SendEmailList() {
 	 List<SysEmail> list =  sysEmailDao.getEmailList();
		 for (SysEmail sysEmail : list) {
             try {
                 sysEmail.setSendTime(DateUtil.getDate(DateUtil.timeStampPattern));
                 SendEmail(sysEmail);
                 sysEmail.setState(1);
             } catch (Exception e) {
                 sysEmail.setState(2);
                 e.printStackTrace();
             }
             this.updateById(sysEmail);
         }
	}
//    //消息发送
	public void SendEmail(SysEmail email)throws Exception{

		switch (email.getType()) {
		case 1:
			//sendSimpleMail(email);
			//发送文本邮件
			sendSimpleEmail(email.getDeliver(), email.getReceiver().split(","), "", email.getSubject(), email.getText());
			break;
		case 2:
			//发送HTML邮件
			sendHtmlEmail(email.getDeliver(), email.getReceiver().split(","), "", email.getSubject(), email.getText());
			break;
		case 3:
			//发送含附件，含嵌入html静态资源页面的邮件
			sendAttachmentsEmail(email.getDeliver(), email.getReceiver().split(","), "", email.getSubject(), email.getText(),(email.getFileId()+" ").split(","));
			break;
		case 4:
			 //发送含附件，但不含嵌入html静态资源页面的邮件
            sendHtmlAndAttachmentsEmail(email.getDeliver(), email.getReceiver().split(","), "", email.getSubject(), email.getText(),(email.getFileId()+" ").split(","));
			break;
		default:
			break;
		}
	}
//	/**
//     * 简单文本邮件
//     * @param to 收件人
//     * @param subject 主题
//     * @param content 内容
//     */
    public void sendSimpleMail(SysEmail email) {
        //创建SimpleMailMessage对象
        SimpleMailMessage message = new SimpleMailMessage();
        //邮件发送人
        message.setFrom(email.getDeliver());
        //邮件接收人
        message.setTo(email.getReceiver());
        //邮件主题
        message.setSubject(email.getSubject());
        //邮件内容
        message.setText(email.getText());
        //发送邮件
        mailSender.send(message);
    }

    private   void sendMimeMail(String deliver, String[] receivers, String carbonCopys, String subject, String text,
            boolean isHtml, String[] attachmentFilePaths) throws Exception {
        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(deliver);
            helper.setTo(receivers);
            //helper.setCc(carbonCopys);
            helper.setSubject(subject);
            helper.setText(text, isHtml);
            // 添加邮件附件
            if (attachmentFilePaths != null && attachmentFilePaths.length > 0) {
                for (String attachmentFilePath : attachmentFilePaths) {
                    File file = new File(attachmentFilePath);
                    if (file.exists()) {
                        String attachmentFile = attachmentFilePath
                                .substring(attachmentFilePath.lastIndexOf(File.separator));
//                        long size = FileUtil.getDirSize(file);
//                        if (size > 1024 * 1024) {
//                            String msg = String.format("邮件单个附件大小不允许超过1MB，[%s]文件大小[%s]。", attachmentFilePath,
//                                    FileUtil.formatSize(size));
//                            throw new RuntimeException(msg);
//                        } else {
//                            FileSystemResource fileSystemResource = new FileSystemResource(file);
//                            helper.addInline(attachmentFile, fileSystemResource);
//                        }
                    }
                }
            }
            mailSender.send(mimeMessage);
            stopWatch.stop();
        } catch (Exception e) {
            throw e;
        }
    }
 
//     * 带附件的邮件
//     * @param to 收件人
//     * @param subject 主题
//     * @param content 内容
//     * @param filePath 附件
//     */
//    public void sendAttachmentsMail(SysEmail email, String filePath) {
//        MimeMessage message = mailSender.createMimeMessage();
//        try {
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            helper.setFrom(email.getDeliver());
//            helper.setTo(email.getReceiver());
//            helper.setSubject(email.getSubject());
//            helper.setText(email.getText(), true);
//
//            FileSystemResource file = new FileSystemResource(new File(filePath));
//            String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
//            helper.addAttachment(fileName, file);
//            mailSender.send(message);
//            //日志信息
//        } catch (MessagingException e) {
//        }
    
    /**
     * thymeleaf模板引擎
     */
    @Autowired
    private static TemplateEngine templateEngine;

    /**
     * 发送不含附件，且不含嵌入html静态资源页面的纯文本简单邮件
     * 
     * @param deliver
     *            发送人邮箱名 如： javalsj@163.com
     * @param receivers
     *            收件人，可多个收件人 如：11111@qq.com,2222@163.com
     * @param carbonCopys
     *            抄送人，可多个抄送人 如：33333@sohu.com
     * @param subject
     *            邮件主题 如：您收到一封高大上的邮件，请查收。
     * @param text
     *            邮件内容 如：测试邮件逗你玩的。
     */
    public  void sendSimpleEmail(String deliver, String[] receivers, String carbonCopys, String subject, String text)
            throws Exception {
        sendMimeMail(deliver, receivers, carbonCopys, subject, text, false, null);
    }

    /**
     * 发送含嵌入html静态资源页面， 但不含附件的邮件
     * 
     * @param deliver
     *            发送人邮箱名 如： javalsj@163.com
     * @param receivers
     *            收件人，可多个收件人 如：11111@qq.com,2222@163.com
     * @param carbonCopys
     *            抄送人，可多个抄送人 如：3333@sohu.com
     * @param subject
     *            邮件主题 如：您收到一封高大上的邮件，请查收。
     * @param text
     *            邮件内容 如： <html><body>
     *            <h1>213123</h1></body></html>
     */
    public  void sendHtmlEmail(String deliver, String[] receivers, String carbonCopys, String subject, String text)
            throws Exception {
        sendMimeMail(deliver, receivers, carbonCopys, subject, text, true, null);
    }

    /**
     * 发送含附件，但不含嵌入html静态资源页面的邮件
     * 
     * @param deliver
     *            发送人邮箱名 如： javalsj@163.com
     * @param receivers
     *            收件人，可多个收件人 如：11111@qq.com,2222@163.com
     * @param carbonCopys
     *            抄送人，可多个抄送人 如：3333@sohu.com.cn
     * @param subject
     *            邮件主题 如：您收到一封高大上的邮件，请查收。
     * @param text
     *            邮件内容 如：测试邮件逗你玩的。
     * @param attachmentFilePaths
     *            附件文件路径 如：http://www.javalsj.com/resource/test.jpg,
     *            http://www.javalsj.com/resource/test2.jpg
     */
    public  void sendAttachmentsEmail(String deliver, String[] receivers, String carbonCopys, String subject,
            String text, String[] attachmentFilePaths) throws Exception {
        sendMimeMail(deliver, receivers, carbonCopys, subject, text, false, attachmentFilePaths);
    }

    /**
     * 发送含附件，且含嵌入html静态资源页面的邮件
     * 
     * @param deliver
     *            发送人邮箱名 如： javalsj@163.com
     * @param receivers
     *            收件人，可多个收件人 如：11111@qq.com,2222@163.com
     * @param carbonCopys
     *            抄送人，可多个抄送人 如：33333@jiuqi.com.cn
     * @param subject
     *            邮件主题 如：您收到一封高大上的邮件，请查收。
     * @param text
     *            <html><body><img src=\"cid:test\"><img
     *            src=\"cid:test2\"></body></html>
     * @param attachmentFilePaths
     *            附件文件路径 如：http://www.javalsj.com/resource/test.jpg,
     *            http://www.javalsj.com/resource/test2.jpg
     *            需要注意的是addInline函数中资源名称attchmentFileName需要与正文中cid:attchmentFileName对应起来
     */
    public  void sendHtmlAndAttachmentsEmail(String deliver, String[] receivers, String carbonCopys, String subject,
            String text, String[] attachmentFilePaths) throws Exception {
        sendMimeMail(deliver, receivers, carbonCopys, subject, text, true, attachmentFilePaths);
    }

    /**
     * 发送thymeleaf模板邮件
     * 
     * @param deliver
     *            发送人邮箱名 如： javalsj@163.com
     * @param receivers
     *            收件人，可多个收件人 如：11111@qq.com,2222@163.com
     * @param carbonCopys
     *            抄送人，可多个抄送人 如：33333@sohu.com
     * @param subject
     *            邮件主题 如：您收到一封高大上的邮件，请查收。
     * @param thymeleafTemplatePath
     *            邮件模板 如：mail\mailTemplate.html。
     * @param thymeleafTemplateVariable
     *            邮件模板变量集 
     */
    public  void sendTemplateEmail(String deliver, String[] receivers, String carbonCopys, String subject, String thymeleafTemplatePath,
            Map<String, Object> thymeleafTemplateVariable) throws Exception {
        String text = null;
        if (thymeleafTemplateVariable != null && thymeleafTemplateVariable.size() > 0) {
            Context context = new Context();
            thymeleafTemplateVariable.forEach((key, value)->context.setVariable(key, value));
            text = templateEngine.process(thymeleafTemplatePath, context);
        }
        sendMimeMail(deliver, receivers, carbonCopys, subject, text, true, null);
    }
    
//  /**
// * html邮件
// * @param to 收件人
// * @param subject 主题
// * @param content 内容
//*/
//  public void sendHtmlMail(SysEmail email) throws Exception{
//  	 MimeMessage message = mailSender.createMimeMessage();
//  	 MimeMessageHelper messageHelper;
//	       messageHelper = new MimeMessageHelper(message, true);
//	       //邮件发送人
//	      messageHelper.setFrom(email.getDeliver());
//	       //邮件接收人
//	       messageHelper.setTo(email.getReceiver());
//	       //邮件主题
//	       message.setSubject(email.getSubject());
//	       //邮件内容，html格式
//	      messageHelper.setText(email.getText(), true);
//	       //发送
//	      mailSender.send(message);
//  }
}

