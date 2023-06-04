package com.Cang.component;


import cn.hutool.core.bean.BeanUtil;
import com.Cang.dto.ChatDto;
import com.Cang.dto.MessageDto;
import com.Cang.dto.Result;
import com.Cang.entity.Chat;
import com.Cang.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;


import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jdfcc
 */
@Slf4j
@Component
@ServerEndpoint("/notice/{userId}")
public class WebSocketServer {

    private static ChatService chatService;

    /**
     * 存储客户端session信息
     */
    public static Map<String, Session> clients = new ConcurrentHashMap<>();

    /**
     * 存储不同用户的客户端session信息集合
     */
    public static Map<String, Set<String>> connection = new ConcurrentHashMap<>();

    /**
     * 会话id
     */
    private String sid = null;

    /**
     * 建立连接的用户id
     */
    private Long userId;

    private Long targetId;

    /**
     * 注入的时候，给类的 service 注入
     */
    @Autowired
    public void setChatService(ChatService chatService) {
        WebSocketServer.chatService = chatService;
    }

    /**
     * @description: 当与前端的websocket连接成功时，执行该方法
     * @PathParam 获取ServerEndpoint路径中的占位符信息类似 控制层的 @PathVariable注解
     **/
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) throws IOException {
        this.sid = UUID.randomUUID().toString();
        this.userId = userId;

        clients.put(this.sid, session);
        //判断该用户是否存在会话信息，不存在则添加
        Set<String> clientSet = connection.get(userId);

        if (clientSet == null) {
            clientSet = new HashSet<>();

            connection.put(String.valueOf(userId), clientSet);
        }
        clientSet.add(this.sid);
        log.info("(((( {}", clients);
        log.info("(((( {}", clientSet);
        log.info(this.userId + "用户建立连接，" + this.sid + "连接开启！");
    }

    /**
     * @description: 当连接失败时，执行该方法
     **/
    @OnClose
    public void onClose() {
        clients.remove(this.sid);
        log.info(this.sid + "连接断开");
    }

    /**
     * @description: 当收到前台发送的消息时，执行该方法
     **/
    @OnMessage
    public void onMessage(String message, Session session) throws IOException, EncodeException {
        log.info("收到来自用户：" + this.userId + "的信息   " + message);
        MessageDto messageDto = JSON.parseObject(message, MessageDto.class);
        log.info("@@@ {}", messageDto.toString());
        Long targetId = messageDto.getTargetId();

        //判断该次请求的消息类型是心跳检测还是获取信息
        if ("heartbeat".equals(messageDto.getType())) {
            // TODO 立刻向前台发送消息，代表后台正常运行
//            sendMessageByUserId(this.userId, );
//            TODO 此方法目前只能单线程，多线程时无法区别前台session。需要根据UesrId将session存储于HashMap中，可借助redis实现
//            TODO 这样就可以区别出多个前台session
            session.getBasicRemote().sendText(MessageDto.heartBeat());
        } else if ("message".equals(messageDto.getType())) {
            // TODO 向目标用户发送消息
            Chat chat = new Chat();
            chat.setReceive(messageDto.getTargetId());
            chat.setSend(this.userId);
            chat.setMessage((String) messageDto.getData());
            log.info("sendText");
//            TODO 发送消息
//            chatService.sendMessage();

        } else if ("query".equals(messageDto.getType())) {
//            TODO 查询信息
            Result message1 = chatService.getMessage(targetId);
            List<ChatDto> chats = (List<ChatDto>) message1.getData();
            log.info("Message {}", chats.size());
            session.getBasicRemote().sendText(MessageDto.query(targetId, chats));
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


