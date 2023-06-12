package com.Cang.component;

import cn.hutool.core.bean.BeanUtil;
import com.Cang.dto.ChatDto;
import com.Cang.dto.MessageDto;
import com.Cang.dto.Result;
import com.Cang.entity.Chat;
import com.Cang.service.ChatService;
import com.Cang.service.IUserService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jdfcc
 * @Description TODO
 * @DateTime 2023/6/6 22:29
 */

@Slf4j
@Component
@ServerEndpoint("/home/{userId}")
public class HomeChatServer {

    private static Long userId;
    public static Map<String, Session> clients = new ConcurrentHashMap<>();
    private static ChatService chatService;
    private static IUserService userService;

    @Autowired
    public void setHomeChatServer(ChatService chatService, IUserService userService) {
        HomeChatServer.chatService = chatService;
        HomeChatServer.userService = userService;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long id) {
        userId = id;
        clients.put(String.valueOf(id), session);
        log.info("首页有新连接加入：{} ,当前连接数为", session.getId(), clients.size());
    }

    @OnClose
    public void onClose() {
        clients.remove(String.valueOf(userId));
        log.info("首页有连接断开：{},当前连接数为", userId, clients.size());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {

        MessageDto messageDto = JSON.parseObject(message, MessageDto.class);
        Long id = messageDto.getUserid();
        log.info("首页收到来自用户：" + id + "的信息   " + message);


        if ("heartbeat".equals(messageDto.getType())) {
            //  立刻向前台发送消息，代表后台正常运行
            log.info("首页心跳检测 {}",userId);
            session.getBasicRemote().sendText(MessageDto.heartBeat());
        } else if ("message".equals(messageDto.getType())) {
//            收到了往前台发送的消息
            ChatDto chatDto = handleChat(message);
            session.getBasicRemote().sendText(MessageDto.receive(userId, chatDto));
            //            判断目标用户是否与服务器建立连接，如何建立了，则为其也发一份.需要将chat封装为chatDto再返回
            Long targetId = messageDto.getTargetId();
            Session targetSession = clients.get(String.valueOf(targetId));
            if (targetSession != null) {
//                目标用户在线
                targetSession.getBasicRemote().sendText(MessageDto.receive(userId, chatDto));
            }
        }
    }

    /**
     * 处理消息
     */
    public ChatDto handleChat(String message) {
        MessageDto messageDto = JSON.parseObject(message, MessageDto.class);
        Long id = messageDto.getUserid();
        Long targetId = messageDto.getTargetId();
        Chat chat = new Chat();
        chat.setReceive(targetId);
        chat.setSend(id);
        chat.setMessage((String) messageDto.getData());
        chat.setMessage((String) messageDto.getData());
        ChatDto chatDto = new ChatDto();
        BeanUtil.copyProperties(chat, chatDto);
        Result avatar = userService.getAvatar(id);
        chatDto.setAvatar((String) avatar.getData());
        return chatDto;
    }

}
