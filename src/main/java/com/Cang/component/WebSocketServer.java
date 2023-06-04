package com.Cang.component;


import cn.hutool.core.bean.BeanUtil;
import com.Cang.dto.ChatDto;
import com.Cang.entity.Chat;
import com.Cang.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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

    /**
     * 存储客户端session信息
     */
    public static Map<String, Session> clients = new ConcurrentHashMap<>();

    /**
     * 存储把不同用户的客户端session信息集合
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

    /**
     * @description: 当与前端的websocket连接成功时，执行该方法
     * @PathParam 获取ServerEndpoint路径中的占位符信息类似 控制层的 @PathVariable注解
     **/
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
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
    public void onMessage(String message, Session session) throws IOException {
        log.info("收到来自用户：" + this.userId + "的信息   " + message);
        //自定义消息实体
        ChatDto chatDto = JSON.parseObject(message, ChatDto.class);
        chatDto.setId(this.userId);
        //判断该次请求的消息类型是心跳检测还是获取信息
//        TODO 当前只能支持单线程,可用redis存储session以支持多线程
        if ("heartbeat".equals(chatDto.getType())) {
            //立刻向前台发送消息，代表后台正常运行
            ChatDto chatDto1 = new ChatDto("heartbeat", "ok");
            String jsonString = JSON.toJSONString(chatDto1);
            session.getBasicRemote().sendText(jsonString);
        }
        if ("message".equals(chatDto.getType())) {
            //执行业务逻辑
            ChatDto chatDto1 = new ChatDto("message", "ok");
            String jsonString = JSON.toJSONString(chatDto1);
            //同步发送数据，需要等上一个sendText发送完成才执行下一个发送
            session.getBasicRemote().sendText(jsonString);
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
//    public static void sendMessageByUserId(String userId, MessageInfo message){
//        if (!StringUtils.isEmpty(userId)) {
//            Set<String> clientSet = connection.get(userId);
//            //用户是否存在客户端连接
//            if (Objects.nonNull(clientSet)) {
//                Iterator<String> iterator = clientSet.iterator();
//                while (iterator.hasNext()) {
//                    String sid = iterator.next();
//                    Session session = clients.get(sid);
//                    //向每个会话发送消息
//                    if (Objects.nonNull(session)){
//                        try {
//                            String jsonString = JSON.toJSONString(message);
//                            //同步发送数据，需要等上一个sendText发送完成才执行下一个发送
//                            session.getBasicRemote().sendText(jsonString);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }
//    }

}


