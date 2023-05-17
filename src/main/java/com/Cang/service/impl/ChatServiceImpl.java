package com.Cang.service.impl;

import com.Cang.dto.Result;
import com.Cang.entity.Chat;
import com.Cang.entity.ChatKey;
import com.Cang.mapper.ChatKeyMapper;
import com.Cang.mapper.ChatMapper;
import com.Cang.service.ChatService;
import com.Cang.utils.IdGeneratorSnowflake;
import com.Cang.utils.UserHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import static com.Cang.utils.RedisConstants.CHAT_MESSAGE_KEY;

/**
 * @author Jdfcc
 * @Description ChatService 实现类
 * @DateTime 2023/5/15 18:20
 */

@Service
@Slf4j
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat> implements ChatService {

    private final ChatMapper chatMapper;
    private final ChatKeyMapper chatKeyMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public ChatServiceImpl(ChatMapper chatMapper, ChatKeyMapper chatKeyMapper, RedisTemplate<String, Object> redisTemplate) {
        this.chatMapper = chatMapper;
        this.chatKeyMapper = chatKeyMapper;
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
        int insert = chatMapper.insert(chat);
        Long userId = UserHolder.getUser().getId();
        String targetId = String.valueOf(chat.getReceive());
//        从hash中取出所有此用户向目标用户发送的chat并加入新的chat
//        TODO: 为两用户之间新增一个全局id用以维护聊天记录，类似于mysql的二级索引的回表查询

        ArrayList<Chat> chats = getChat(key, hashKey);
        chats.add(chat);
        log.info("%%%%%%% {}", chats);
//        将消息重新放入hash
        String key = CHAT_MESSAGE_KEY + userId;
        redisTemplate.opsForHash().put(key, hashKey, chats);
        return Result.ok(insert);
    }

    /**
     * 从hash中获取到相应的chat
     *
     * @param userId 当前用户id
     * @param targetId 目标用户id
     * @return  当前用户与目标用户的所有聊天记录
     */
    public List<Chat> getChat(Long userId, Long targetId) {
        String key = CHAT_MESSAGE_KEY + userId;
        List<Chat> chats = (List<Chat>) redisTemplate.opsForHash().get(key, targetId);
        if (chats == null) { //redis没有缓存,从数据库查找并重构缓存
            LambdaQueryWrapper<Chat> chatLambdaQueryWrapper = new LambdaQueryWrapper<>();
            chatLambdaQueryWrapper.eq(Chat::getId,userId).eq(Chat::getReceive,targetId);
            chats = chatMapper.selectList(chatLambdaQueryWrapper);

        }
        return chats;
    }

    @Transactional
    public void saveToRedis(Long userId){
        Long key=new IdGeneratorSnowflake().snowflakeId();
        ChatKey chatKey = new ChatKey();
        chatKey.setUserId(userId);
        chatKey.setUserKey(key);
        int insert = chatKeyMapper.insert(chatKey);
        if(insert !=0){ //在redis中缓存此数据

        }
    }

    /**
     * 获取到储存在UserHold中储存的用户与此id用户的所有对话,双向
     *
     * @param id
     * @return
     */
    @Override
    public Result getMessage(String id) {
        Long userId = UserHolder.getUser().getId();
//        double max = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        String key1 = CHAT_MESSAGE_KEY + userId;
        String key2 = CHAT_MESSAGE_KEY + id;
        ArrayList<Chat> chat1 = getChat(key1, id);
        ArrayList<Chat> chat2 = getChat(key2, String.valueOf(userId));

        return null;
    }


}
