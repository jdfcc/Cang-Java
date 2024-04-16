package com.Cang.component;

import cn.hutool.core.bean.BeanUtil;
import com.Cang.dto.ChatDto;
import com.Cang.dto.MessageDto;
import com.Cang.entity.Chat;
import com.Cang.exception.MessageException;
import com.Cang.repo.child.ChatSessions;
import com.Cang.service.ChatService;
import com.Cang.service.IUserService;
import com.Cang.utils.TokenUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

import java.util.List;


/**
 * @author Jdfcc
 * @Description HomeChatServer
 * @DateTime 2023/6/6 22:29
 */

@Slf4j
@Component
@SuppressWarnings("unused")
@ServerEndpoint("/websocket/{token}")
public class ChatServer {


    private static ChatService chatService;

    private static IUserService userService;

    @Autowired
    public void setHomeChatServer(ChatService chatService, IUserService userService) {
        ChatServer.chatService = chatService;
        ChatServer.userService = userService;
    }


    @OnOpen
    // TODO token建立连接
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        String sessionId = session.getId();
        // TODO 若AccessToken不可用，则验证RefreshToken的操作不应在此处进行，由统一controller进行续签管理从而减少与代码的耦合性
        Long userid = TokenUtil.verifyAccessToken(token);

        ChatSessions.addClient(String.valueOf(userid), session);
        ChatSessions.Session2IdMapping.addMapping(sessionId, String.valueOf(userid));
        log.info("首页有新连接加入：{} ,当前连接数为 {}", session.getId(), ChatSessions.getSize());
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        String userId = ChatSessions.Session2IdMapping.getUserId(session.getId());
        ChatSessions.removeClient(String.valueOf(userId), session);
        ChatSessions.Session2IdMapping.removeMapping(session.getId(), userId);
        log.info("首页有连接断开：{},当前连接数为 {}", userId,ChatSessions.getSize());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        try {
            String userId = ChatSessions.Session2IdMapping.getUserId(session.getId());
            MessageDto messageDto = JSON.parseObject(message, MessageDto.class);
            Long id = messageDto.getUserid();
            log.info("首页收到来自用户：" + id + "的信息   " + message);
            if ("heartbeat".equals(messageDto.getType())) {
                //  立刻向前台发送消息，代表后台正常运行
                log.info("首页心跳检测 {}", userId);
                session.getBasicRemote().sendText(MessageDto.heartBeat());
            } else if ("message".equals(messageDto.getType())) {
                //  收到了往前台发送的消息
                Chat chat = new Chat();
                chat.setReceive(messageDto.getTargetId());
                chat.setSend(id);
                chat.setMessage((String) messageDto.getData());
                chatService.sendMessage(chat);
                // 给自己发一份封装好的消息
                ChatDto chatDto = handleChat(message);
                session.getBasicRemote().sendText(MessageDto.receive(Long.valueOf(userId), chatDto));
                //  判断目标用户是否与服务器建立连接，如何建立了，则为其也发一份
                Long targetId = messageDto.getTargetId();
                List<Session> targetSessions = ChatSessions.getSession(targetId);
                if (targetSessions != null) {
                    for (Session targetSession : targetSessions) {
                        // 在线
                        targetSession.getBasicRemote().sendText(MessageDto.receive(Long.valueOf(userId), chatDto));
                        // TODO 首页发送一份提醒消息
                    }
                }
            }
            else if ("query".equals(messageDto.getType())) {
                Long targetId = messageDto.getTargetId();
                //      TODO 查询信息
                ChatSessions.Session2IdMapping.getUserId(session.getId());
                List<Object> chats = chatService.getMessage(id, targetId);
                session.getBasicRemote().sendText(MessageDto.query(targetId, chats));
            } else if ("list".equals(messageDto.getType())) {
                log.info("List");
                Long userid = messageDto.getUserid();
                List<ChatDto> data = chatService.getHomeChat(userid);
                session.getBasicRemote().sendText(MessageDto.list(userid, data));
            }
        } catch (Exception e) {
            session.getBasicRemote().sendText("消息发送失败，请刷新页面重试");
            throw new MessageException(e.getMessage());
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
        String avatar = userService.getAvatar(id);
        chatDto.setAvatar(avatar);
        return chatDto;
    }

    /**
     * @description: 当连接发生错误时，执行该方法
     **/
    @OnError
    public void onError(Throwable error) {
        log.info("系统错误 {}",error.getMessage());
        error.printStackTrace();
    }

}
