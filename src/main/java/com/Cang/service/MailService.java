package com.Cang.service;

import com.Cang.dto.Result;
import com.Cang.entity.Mail;
import com.Cang.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author Jdfcc
 * @Description MailService
 * @DateTime 2023/5/18 23:09
 */
public interface MailService extends IService<Mail> {
    Result sendHardMail(User user);

    Result sendMail(User user);

    void sendVerFicationMail(String email, String code);
}
