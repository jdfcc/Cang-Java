package com.Cang.component;

/**
 * @author Jdfcc
 * @Description 单人聊天
 * @DateTime 2023/5/17 21:41
 */

import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
    private String userId;

    /**
     * @description: 当与前端的websocket连接成功时，执行该方法
     * @PathParam 获取ServerEndpoint路径中的占位符信息类似 控制层的 @PathVariable注解
     **/
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.sid = UUID.randomUUID().toString();
        this.userId = userId;
        clients.put(this.sid, session);
        //判断该用户是否存在会话信息，不存在则添加
        Set<String> clientSet = connection.get(userId);
        if (clientSet == null) {
            clientSet = new HashSet<>();
            connection.put(userId, clientSet);
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
    public void onMessage(String message, Session session) {
        log.info("收到来自用户：" + this.userId + "的信息   " + message);
        //自定义消息实体
//        ViewQueryInfoDTO viewQueryInfoDTO = JSON.parseObject(message, ViewQueryInfoDTO.class);
//        viewQueryInfoDTO.setUserId(this.userId);
//        //判断该次请求的消息类型是心跳检测还是获取信息
//        if (viewQueryInfoDTO.getType().equals("heartbeat")){
//            //立刻向前台发送消息，代表后台正常运行
//            sendMessageByUserId(this.userId,new MessageInfo("heartbeat","ok"));
//        }
//        if (viewQueryInfoDTO.getType().equals("message")){
//            //执行业务逻辑
//            MessageInfo messageInfo = xxService.list(viewQueryInfoDTO);
//            sendMessageByUserId(this.userId,messageInfo);
//        }

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


