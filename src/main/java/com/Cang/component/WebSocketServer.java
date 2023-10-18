package com.Cang.component;


import cn.hutool.core.bean.BeanUtil;
import com.Cang.dto.ChatDto;
import com.Cang.dto.MessageDto;
import com.Cang.dto.Result;
import com.Cang.entity.Chat;
import com.Cang.service.ChatService;
import com.Cang.service.IUserService;

import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.thymeleaf.util.StringUtils;


import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Jdfcc
 *  TODO 拆分
 */
@Slf4j
@Component
@ServerEndpoint("/notice/{userId}")
public class WebSocketServer {

    private static ChatService chatService;

    private static RedisTemplate<String, Object> redisTemplate;
    private static IUserService userService;

    private Long userid;

    /**
     * 存储客户端session信息
     */
    public static Map<String, Session> clients = new ConcurrentHashMap<>();


    /**
     * 存储不同用户的客户端session信息集合
     */
    public static Map<String, Set<String>> connection = new ConcurrentHashMap<>();


    /**
     * 注入的时候，给类的 service 注入
     */
    @Autowired
    public void setChatService(ChatService chatService, RedisTemplate<String, Object> redisTemplate, IUserService userService) {
        WebSocketServer.chatService = chatService;
        WebSocketServer.redisTemplate = redisTemplate;
        WebSocketServer.userService = userService;
    }


    /***
     *  当前端用户与后台建立连接时执行此方法。
     * @param session 前端服务器session
     * @param id 当前用户id
     * @throws IOException
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long id) throws IOException {


//        当前用户id
        userid = id;
        //不管用户是否存在会话信息，都更新session
        clients.put(String.valueOf(id), session);
//        log.info("Session {}", session.toString());
        log.info("用户  {} 建立连接,当前用户有 {}", id, clients.size());

    }

    /**
     * @description: 当连接失败时，执行该方法
     **/
    @OnClose
    public void onClose() {
        clients.remove(String.valueOf(userid));
        log.info(userid + "连接断开");
    }

    /**
     * @description: 当收到前台发送的消息时，执行该方法
     **/
    @OnMessage
    public void onMessage(String message, Session session) throws IOException, EncodeException {

//        log.info("收到来自用户：" + userid + "的信息   " + message);
        MessageDto messageDto = JSON.parseObject(message, MessageDto.class);
//        log.info("@@@ {}", messageDto.toString());
        Long id = messageDto.getUserid();

        //判断该次请求的消息类型是心跳检测还是获取信息
        if ("heartbeat".equals(messageDto.getType())) {
            // TODO 立刻向前台发送消息，代表后台正常运行
//            sendMessageByUserId(this.userId, );
//            TODO 此方法目前只能单线程，多线程时无法区别前台session。需要根据UesrId将session存储于HashMap中，可借助redis实现
//            TODO 这样就可以区别出多个前台session
            log.info("消息列表心跳检测 {}",userid);
            session.getBasicRemote().sendText(MessageDto.heartBeat());
        } else if ("message".equals(messageDto.getType())) {
            // TODO 向目标用户发送消息
            Long targetId = messageDto.getTargetId();
            Chat chat = new Chat();
            chat.setReceive(targetId);
            chat.setSend(id);
            chat.setMessage((String) messageDto.getData());
            chat.setMessage((String) messageDto.getData());
//            log.info("sendText");
//            TODO 发送消息
            chatService.sendMessage(chat);
//          TODO  判断目标用户是否与服务器建立连接，如何建立了，则为其也发一份.需要将chat封装为chatDto再返回
            Session targetSession = clients.get(String.valueOf(targetId));
            ChatDto chatDto = new ChatDto();
            BeanUtil.copyProperties(chat, chatDto);
            Result avatar = userService.getAvatar(id);
            chatDto.setAvatar((String) avatar.getData());
            chatDto.setCreateTime(LocalDateTime.now());
//             为消息首页发送消息
            session.getBasicRemote().sendText(MessageDto.message(targetId, chatDto));

//           TODO 拆分了下一条代码
//            session.getBasicRemote().sendText(MessageDto.receive(targetId, chatDto));

            String key = chatService.getKey(targetId, userid);

//            redisTemplate.opsForList().rightPush(key, chatDto);
            long seconds = chatDto.getCreateTime().toEpochSecond(ZoneOffset.UTC);
            double score = seconds * 1000;
//      todo      redisTemplate.opsForZSet().add();
            if (targetSession != null) {
//                log.info("发送消息{}", chatDto);
                targetSession.getBasicRemote().sendText(MessageDto.receive(targetId, chatDto));
                targetSession.getBasicRemote().sendText(MessageDto.message(targetId, chatDto));
            }

        } else if ("query".equals(messageDto.getType())) {
            Long targetId = messageDto.getTargetId();

//            TODO 查询信息
            Result message1 = chatService.getMessage(userid, targetId);
            List<ChatDto> chats = (List<ChatDto>) message1.getData();
            session.getBasicRemote().sendText(MessageDto.query(targetId, chats));
        } else if ("list".equals(messageDto.getType())) {
            log.info("List");
            Long userid = messageDto.getUserid();
            Result result = chatService.getHomeChat(userid);
            Object data = result.getData();
            session.getBasicRemote().sendText(MessageDto.list(userid, data));
        }

    }

    /**
     * @description: 当连接发生错误时，执行该方法
     **/
    @OnError
    public void onError(Throwable error) {
        log.info("系统错误");
        error.printStackTrace();
    }

    /**
     * @description: 通过userId向用户发送信息
     * 该类定义成静态可以配合定时任务实现定时推送
     **/
    public static void sendMessageByUserId(Long userId, MessageDto message) {
        if (!StringUtils.isEmpty(String.valueOf(userId))) {
            Set<String> clientSet = connection.get(userId);
            //用户是否存在客户端连接
            if (Objects.nonNull(clientSet)) {
                Iterator<String> iterator = clientSet.iterator();
                while (iterator.hasNext()) {
                    String sid = iterator.next();
                    Session session = clients.get(sid);
                    //向每个会话发送消息
                    if (Objects.nonNull(session)) {
                        try {
                            String jsonString = JSON.toJSONString(message);
                            //同步发送数据，需要等上一个sendText发送完成才执行下一个发送
                            session.getBasicRemote().sendText(jsonString);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

}


