package com.Cang.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.Cang.dto.Result;
import com.Cang.entity.Mail;
import com.Cang.entity.User;
import com.Cang.mapper.MailMapper;
import com.Cang.service.MailService;
import com.Cang.utils.MailUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.util.Date;

/**
 * @author Jdfcc
 * @Description MailService实现类
 * @DateTime 2023/5/18 23:10
 */

@Service
public class MailServiceImpl extends ServiceImpl<MailMapper, Mail> implements MailService {

    private static final String SUBJECT_MESSAGE = "验证你的邮箱";
    private final MailUtil mailUtil;

    private static final String MAIL_SUBJECT = "验证你的邮箱";

    private final TemplateEngine templateEngine;

    @Value("${code-length}")
    private String codeLength;

    @Value("${spring.mail.username}")
    private String mailSender; //邮件发送者

    private static final String SEND_EMAIL_FAILED = "邮件发送失败";

    private final JavaMailSender javaMailSender;//注入QQ发送邮件的bean

    public MailServiceImpl(MailUtil mailUtil, TemplateEngine templateEngine, JavaMailSender javaMailSender) {
        this.mailUtil = mailUtil;
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
    }

    /**
     * 发送验证码
     *
     * @param email 邮箱
     * @param code  验证码
     */
    @Override
    public void sendVerFicationMail(String email, String code) {

        //注意：Context 类是在org.thymeleaf.context.Context包下的。
        Context context = new Context();
        //html中填充动态属性值
        context.setVariable("code", code);
        //org.thymeleaf.exceptions.TemplateInputException: Error resolving template [email]
        String emailContent = templateEngine.process("verification", context);


        MimeMessage mimeMailMessage = null;
        try {
            mimeMailMessage = javaMailSender.createMimeMessage();
            //true 表示需要创建一个multipart message
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, true);
            mimeMessageHelper.setFrom(mailSender);//发送者
            mimeMessageHelper.setTo(email);//接受者
            mimeMessageHelper.setSubject(SUBJECT_MESSAGE);//邮件标题
            //这里的 true，你加了的话，它发送你HTML页面里面的内容
            //不加的话，默认是 false，它发送整个HTML页面代码
            mimeMessageHelper.setText(emailContent, true);
            //邮件抄送
            javaMailSender.send(mimeMailMessage);//发送邮件
        } catch (Exception e) {
            log.error(SEND_EMAIL_FAILED + "{}", e);
        }
    }

    @Override
    public Result sendMail(User user) {
        System.out.println("数据: " + user);
        if (user != null) {
            Mail mailBean = new Mail();
            //接收者
            mailBean.setRecipient(user.getEmail());
            //标题
            mailBean.setSubject("用户信息");
            String message = "SpringBootMail发送一个简单格式的邮件，时间为：" + new Date();
            //内容主体
            mailBean.setContent(message);

            mailUtil.sendSimpleMail(mailBean);
            return Result.ok("查询成功");
        }
        return Result.fail("发送失败！");
    }

    @Override
    public Result sendHardMail(User user) {
        System.out.println("数据: " + user);
        //以HTML模板发送邮件
        if (user != null) {
            //注意：Context 类是在org.thymeleaf.context.Context包下的。
            Context context = new Context();
            //html中填充动态属性值
            context.setVariable("nickName", user.getNickName());
            String code = RandomUtil.randomNumbers(Integer.valueOf(codeLength));
//            String code = RandomUtil.randomString(Integer.valueOf(CODE_LENGTH));
            context.setVariable("code", code);
            String emailContent = templateEngine.process("verification", context);

            Mail mailBean = new Mail();
            mailBean.setRecipient(user.getEmail());
            mailBean.setSubject(MAIL_SUBJECT);
            mailBean.setContent(emailContent);

            mailUtil.sendHTMLMail(mailBean);
            return Result.ok("发送成功");
        }
        return Result.fail("发送失败！");
    }
}
