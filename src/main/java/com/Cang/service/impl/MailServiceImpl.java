package com.Cang.service.impl;

import com.Cang.dto.Result;
import com.Cang.entity.Mail;
import com.Cang.entity.RespBean;
import com.Cang.entity.User;
import com.Cang.mapper.MailMapper;
import com.Cang.service.MailService;
import com.Cang.utils.MailUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;

/**
 * @author Jdfcc
 * @Description MailService实现类
 * @DateTime 2023/5/18 23:10
 */

@Service
public class MailServiceImpl extends ServiceImpl<MailMapper, Mail> implements MailService {

    private final MailUtil mailUtil;

    private final TemplateEngine templateEngine;

    public MailServiceImpl(MailUtil mailUtil, TemplateEngine templateEngine) {
        this.mailUtil = mailUtil;
        this.templateEngine = templateEngine;
    }

    /**
     * 发送验证码
     * @param email
     * @param code
     * @return
     */
    @Override
    public Result sendVerFicationMail(String email,String code) {

            //注意：Context 类是在org.thymeleaf.context.Context包下的。
            Context context = new Context();
            //html中填充动态属性值
            context.setVariable("code", code);
            context.setVariable("url", "https://www.aliyun.com/?utm_content=se_1000301881");
            //org.thymeleaf.exceptions.TemplateInputException: Error resolving template [email]
            String emailContent = templateEngine.process("verfication", context);

            Mail mailBean = new Mail();
            mailBean.setRecipient(email);
            mailBean.setSubject("验证你的邮箱");
            mailBean.setContent(emailContent);

            mailUtil.sendHTMLMail(mailBean);
            return Result.ok("发送成功");


    }

    @Override
    public Result sendMail(User user) {
        System.out.println("数据: " + user);
        if (user != null) {
            Mail mailBean = new Mail();
            mailBean.setRecipient(user.getEmail());//接收者
            mailBean.setSubject("用户信息");//标题
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
            context.setVariable("code", "9586");
            context.setVariable("url", "https://www.aliyun.com/?utm_content=se_1000301881");
            //org.thymeleaf.exceptions.TemplateInputException: Error resolving template [email]
            String emailContent = templateEngine.process("verfication", context);

            Mail mailBean = new Mail();
            mailBean.setRecipient(user.getEmail());
            mailBean.setSubject("这是一份测试邮件");
            mailBean.setContent(emailContent);

            mailUtil.sendHTMLMail(mailBean);
            return Result.ok("发送成功");
        }
        return Result.fail("发送失败！");
    }
}
