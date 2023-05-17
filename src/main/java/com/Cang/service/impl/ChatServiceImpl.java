package com.Cang.service.impl;

import com.Cang.dto.Result;
import com.Cang.entity.Chat;
import com.Cang.mapper.ChatMapper;
import com.Cang.service.ChatService;
import com.Cang.utils.UserHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import static com.Cang.utils.RedisConstants.CHAT_MESSAGE_USER_KEY;

/**
 * @author Jdfcc
 * @Description ChatService 实现类
 * @DateTime 2023/5/15 18:20
 */

@Service
@Slf4j
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat> implements ChatService {

    private final ChatMapper chatMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    public ChatServiceImpl(ChatMapper chatMapper, RedisTemplate<String, Object> redisTemplate) {
        this.chatMapper = chatMapper;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 将发送到的消息存储到数据库后将此消息以 CHAT_MESSAGE_KEY+ SEND为key, RECEIVE 为HashKey储存在RedisHash中
     *
     * @param chat
     * @return
     */
    @Override
    @Transactional
    public Result sendMessage(Chat chat) {
        chat.setCreateTime(LocalDateTime.now());
        Long userid = UserHolder.getUser().getId();
        Long targetId = chat.getReceive();
        String key = this.getKey(userid, targetId);
        chat.setUserKey(key);
        int insert = chatMapper.insert(chat); //TODO 加一个异常判断
        redisTemplate.opsForList().leftPush(key, chat);
        return Result.ok(insert);
    }


    /**
     * 获取到储存在UserHold中储存的用户与此id用户的所有对话
     *
     * @param targetId
     * @return
     */
    @Override
    @Transactional
    public Result getMessage(Long targetId) {
        Long userId = UserHolder.getUser().getId();
        String key = this.getKey(userId, targetId);

        List<Object> chats = redisTemplate.opsForList().range(String.valueOf(key), 0L, -1L);
        if (chats == null) {
//            从数据库中查询并重建缓存
            LambdaQueryWrapper<Chat> chatLambdaQueryWrapper = new LambdaQueryWrapper<>();
            chatLambdaQueryWrapper.eq(Chat::getUserKey, key);
            chats = Collections.singletonList(chatMapper.selectList(chatLambdaQueryWrapper));
            if (chats == null) { //数据库中也为空，两人第一次聊天
                chats = new ArrayList<>();
//                储存空缓存以解决缓存击穿
                redisTemplate.opsForList().leftPush(key, null);
            }
//            重建缓存
            for (Object temp : chats) {
                redisTemplate.opsForList().leftPush(key, temp);
            }
        }
        return Result.ok(chats);
    }

    /**
     * 获取公共key 为两用户之间新增一个全局id用以维护聊天记录，类似于mysql的二级索引的回表查询
     *
     * @param a 用户id
     * @param b 目标用户id
     * @return {a+b} 由于两个参数均为雪花算法生成，故不存在相加后作为全局id重复的可能性
     */
    public String getKey(Long a, Long b) {
        return CHAT_MESSAGE_USER_KEY + (a + b);
    }

    /**
     * 在数据库中找出不重复的userKey
     * @return 所有userKey中时间靠后的最后一条消息
     */
    @Override
    public Result getMessageList() {
        List<String> keys = chatMapper.queryChatList();
        List<Chat> list=new ArrayList<>();
        for(String key : keys){
            Chat chat = chatMapper.selectLast(key);
            list.add(chat);
        }
        return Result.ok(list);
    }

}
