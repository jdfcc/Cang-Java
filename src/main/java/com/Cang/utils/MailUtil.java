package com.Cang.utils;

import com.Cang.entity.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.mail.internet.MimeMessage;


/**
 * @author Jdfcc
 * @Description 邮箱工具类
 * @DateTime 2023/5/18 16:57
 */

@Component
public class MailUtil {

    @Value("${spring.mail.username}")
    private String MAIL_SENDER; //邮件发送者

    private final JavaMailSender javaMailSender;//注入QQ发送邮件的bean

    private Logger logger = LoggerFactory.getLogger(MailUtil.class);

    public MailUtil(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * 发送文本邮件
     *
     * @param mailBean
     */
    public void sendSimpleMail(Mail mailBean) {
        try {
            SimpleMailMessage mailMessage= new SimpleMailMessage();
            mailMessage.setFrom(MAIL_SENDER);//发送者
            mailMessage.setTo(mailBean.getRecipient());//接收者
            mailMessage.setSubject(mailBean.getSubject());//邮件标题
            mailMessage.setText(mailBean.getContent());//邮件内容
            javaMailSender.send(mailMessage);//发送邮箱
        } catch (Exception e) {
            logger.error("邮件发送失败", e.getMessage());
        }
    }

    /**
     * 发送HTML模板
     * @param mailBean
     */
    public void sendHTMLMail(Mail mailBean) {
        MimeMessage mimeMailMessage = null;
        try {
            mimeMailMessage = javaMailSender.createMimeMessage();
            //true 表示需要创建一个multipart message
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, true);
            mimeMessageHelper.setFrom(MAIL_SENDER);//发送者
            mimeMessageHelper.setTo(mailBean.getRecipient());//接受者
            mimeMessageHelper.setSubject(mailBean.getSubject());//邮件标题
            //这里的 true，你加了的话，它发送你HTML页面里面的内容
            //不加的话，默认是 false，它发送整个HTML页面代码
            mimeMessageHelper.setText(mailBean.getContent(), true);
            //邮件抄送
            javaMailSender.send(mimeMailMessage);//发送邮件
        } catch (Exception e) {
            logger.error("邮件发送失败", e.getMessage());
        }
    }

}