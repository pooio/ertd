package com.metaShare.common.utils;
import java.io.File;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class MailUtil {

    /**
     * Java邮件发送器
     */
    @Autowired
    private static JavaMailSender mailSender;

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
     * @param receiver
     *            收件人，可多个收件人 如：11111@qq.com,2222@163.com
     * @param carbonCopy
     *            抄送人，可多个抄送人 如：33333@sohu.com
     * @param subject
     *            邮件主题 如：您收到一封高大上的邮件，请查收。
     * @param text
     *            邮件内容 如：测试邮件逗你玩的。
     */
    public static void sendSimpleEmail(String deliver, String[] receivers, String[] carbonCopys, String subject, String text)
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
    public static void sendHtmlEmail(String deliver, String[] receivers, String[] carbonCopys, String subject, String text)
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
    public static void sendAttachmentsEmail(String deliver, String[] receivers, String[] carbonCopys, String subject,
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
    public static void sendHtmlAndAttachmentsEmail(String deliver, String[] receivers, String[] carbonCopys, String subject,
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
    public static void sendTemplateEmail(String deliver, String[] receivers, String[] carbonCopys, String subject, String thymeleafTemplatePath,
            Map<String, Object> thymeleafTemplateVariable) throws Exception {
        String text = null;
        if (thymeleafTemplateVariable != null && thymeleafTemplateVariable.size() > 0) {
            Context context = new Context();
            thymeleafTemplateVariable.forEach((key, value)->context.setVariable(key, value));
            text = templateEngine.process(thymeleafTemplatePath, context);
        }
        sendMimeMail(deliver, receivers, carbonCopys, subject, text, true, null);
    }

    /**
     * 发送的邮件(支持带附件/html类型的邮件)
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
     *            邮件内容 如：测试邮件逗你玩的。 <html><body><img
     *            src=\"cid:attchmentFileName\"></body></html>
     * @param attachmentFilePaths
     *            附件文件路径 如：
     *            需要注意的是addInline函数中资源名称attchmentFileName需要与正文中cid:attchmentFileName对应起来
     * @throws Exception
     *             邮件发送过程中的异常信息
     */
    private static void sendMimeMail(String deliver, String[] receivers, String[] carbonCopys, String subject, String text,
            boolean isHtml, String[] attachmentFilePaths) throws Exception {
        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(deliver);
            helper.setTo(receivers);
            helper.setCc(carbonCopys);
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
    
}
