package com.Cang.service;

import com.Cang.dto.Result;
import com.Cang.entity.Chat;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author Jdfcc
 * @Description ChatService
 * @DateTime 2023/5/15 18:19
 */
public interface ChatService extends IService<Chat> {
    Result sendMessage(Chat chat);

    Result getMessage(Long id);

    Result getMessageList();
}
