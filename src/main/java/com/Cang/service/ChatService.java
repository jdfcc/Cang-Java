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

    /**
     * 将发送到的消息存储到数据库后将此消息以 CHAT_MESSAGE_KEY+ SEND为key, RECEIVE 为HashKey储存在RedisHash中.
     * 同时往聊天窗口主页的redis缓存中加入一条消息。即将该条消息设置为此用户与其他人聊天的最后一条消息。
     * 由于key是双向的，所以对所有人都有效.
     * 还需在此用户在redis中关联的set集合中加入此条聊天key。
     *
     * @param chat
     * @return
     */
    Result sendMessage(Chat chat);

    /**
     * 获取到储存在UserHold中储存的用户与此id用户的所有对话
     *
     * @param id
     * @return
     */
    Result getMessage(Long userid, Long id);


    /**
     * 查询当前用户与所有人聊天记录的最后一条消息.具体为查询该用户关联的所有Chat_KEY，然后再对key遍历。
     * 首先从redis中找此用户所关联的所有key，拿不到再从数据库中重建缓存。若都为空，该用户从未与任何人聊过天
     *
     * @param userId
     * @return 查询当前用户与所有人聊天记录的最后一条消息
     */
    Result getHomeChat(Long userId);

    /**
     * 获取key
     * @param a
     * @param b
     * @return
     */
    String getKey(Long a, Long b);
}
