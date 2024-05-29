package com.Cang.controller;

import com.Cang.dto.Result;
import com.Cang.entity.Chat;
import com.Cang.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashSet;

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
    public Result sendMessage(@RequestBody Chat chat, HttpServletRequest request) throws IOException {

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


    /**
     * 查询与此用户的聊天记录
     * @param id 目标用户id
     * @return 聊天记录
     */
    @GetMapping("/details/{id}")
    public Result getDetails(@PathVariable Long id){
        return Result.ok( chatService.getDetails(id));
    }

}
