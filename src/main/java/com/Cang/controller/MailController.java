package com.Cang.controller;

import com.Cang.dto.Result;
import com.Cang.entity.Mail;
import com.Cang.entity.RespBean;
import com.Cang.entity.User;
import com.Cang.service.MailService;
import com.Cang.utils.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;

/**
 * @author Jdfcc
 * @Description 处理发送邮箱请求
 * @DateTime 2023/5/18 17:00
 */
@RestController
@RequestMapping("/mails")
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }


    /**
     * 发送普通文件接口
     * @param user
     * @return
     */
    @PostMapping("/normal")
    @ResponseBody
    public Result doMail(User user){
       return mailService.sendMail(user);
    }


    /**
     * 发送富文本邮件接口
     * @param user
     * @return
     */
    @PostMapping("/hard")
    @ResponseBody
    public Result doMail2(User user){
       return mailService.sendHardMail(user);
    }

}
