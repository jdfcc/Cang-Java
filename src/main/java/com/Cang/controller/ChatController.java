package com.Cang.controller;

import com.Cang.dto.Result;
import com.Cang.entity.Chat;
import com.Cang.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author Jdfcc
 * @Description ChatController
 * @DateTime 2023/5/15 18:22
 */

@RequestMapping("/chat")
@RestController
@Slf4j
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PutMapping("/send")
    public Result sendMessage(@RequestBody Chat chat) {

        return chatService.sendMessage(chat);
    }


    /**
     * 当用户进入聊天选项卡时会加载此方法。
     *
     * @return 当前用户与所有人聊天的最后一条消息。
     */
    @GetMapping("/home/{id}")
    public Result getHomeChat(@PathVariable Long id) {
        return Result.ok( chatService.getHomeChat(id));
    }

}
