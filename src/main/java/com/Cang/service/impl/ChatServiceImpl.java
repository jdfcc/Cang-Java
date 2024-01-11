package com.Cang.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.Cang.dto.ChatDto;
import com.Cang.dto.Result;
import com.Cang.entity.Chat;
import com.Cang.mapper.ChatMapper;
import com.Cang.service.ChatService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static com.Cang.constants.RedisConstants.*;

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

    @Autowired
    public ChatServiceImpl(ChatMapper chatMapper, RedisTemplate<String, Object> redisTemplate) {
        this.chatMapper = chatMapper;
        this.redisTemplate = redisTemplate;
    }


    /**
     * 保存消息
     *
     * @param chat 消息实体
     */

    @Override
    public void saveMessage(Chat chat) {
        save(chat);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result sendMessage(Chat chat) {
        chat.setCreateTime(LocalDateTime.now());
        Long userid = chat.getSend();
        Long targetId = chat.getReceive();
        String key = this.getKey(userid, targetId);
        chat.setUserKey(key);
        chat.setSend(userid);//TODO 加一个异常判断
        this.saveMessage(chat);
        long seconds = chat.getCreateTime().toEpochSecond(ZoneOffset.UTC);
        double score = seconds * 1000;
        chat.setCreateTime(null);
//        在当前用户与目标用户首页消息列表中添加此条消息
        redisTemplate.opsForZSet().add(CHAT_MESSAGE_USER_CACHE_KEY_LAST + userid, chat, score);
        redisTemplate.opsForZSet().add(CHAT_MESSAGE_USER_CACHE_KEY_LAST + targetId, chat, score);
        return Result.ok();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Object> getMessage(Long userId, Long targetId) {
        String key = this.getKey(userId, targetId);
//  TODO 删除此注释     List<Object> chats = redisTemplate.opsForList().range(String.valueOf(key), 0L, -1L);
        Set<Object> chats = redisTemplate.opsForZSet().range(key, 0L, -1L);
        if (CollectionUtil.isEmpty(chats)) {
//            从数据库中查询并重建缓存
            List<Object> newChats = Collections.singletonList(chatMapper.selectDtos(key));
            //数据库中也为空，两人第一次聊天
            if (CollectionUtil.isEmpty(newChats)) {
//                储存空缓存以解决缓存击穿
                redisTemplate.opsForZSet().add(key, new HashSet<>());
                return new ArrayList<>();
            }
//            重建缓存
            for (Object temp : newChats) {
                long seconds = ((ChatDto) temp).getCreateTime().toEpochSecond(ZoneOffset.UTC);
                double score = seconds * 1000;
                redisTemplate.opsForZSet().add(key, temp, score);
            }
            return newChats;
        }
        return new ArrayList<>(chats);
    }

    /**
     * 获取公共key 为两用户之间新增一个全局id用以维护聊天记录，类似于mysql的二级索引的回表查询
     *
     * @param a 用户id
     * @param b 目标用户id
     * @return {a+b} 由于两个参数均为雪花算法生成，故不存在相加后作为全局id重复的可能性
     */
    @Override
    public String getKey(Long a, Long b) {
        return CHAT_MESSAGE_USER_KEY + (a + b);
    }

    @Override
    public List<ChatDto> getHomeChat(Long userId) {
        return   chatMapper.selectLast(userId);

    }

//    TODO 想不明白使用redis存的数据结构，日后再填坑
//    @Override
//    public Result getHomeChat(Long userId) {
//        Set<Object> chats = redisTemplate.opsForZSet().range(CHAT_MESSAGE_USER_CACHE_KEY_LAST + userId, 0, -1);
//        Object o = redisTemplate.opsForHash().get(CHAT_MESSAGE_USER_CACHE_KEY_LAST, userId);
//        if (chats.isEmpty()) {
////            需要从数据库中重建缓存
//            List<ChatDto> newChats = chatMapper.selectLast(userId);
//            for (ChatDto tem : newChats) {
////                重建缓存
//                redisTemplate.opsForHash().put(CHAT_MESSAGE_USER_CACHE_KEY_LAST, userId, tem);
//            }
//            return Result.ok(newChats);
//        }
//        return Result.ok(reverseSet(chats));
//    }

    public static Set<Object> reverseSet(Set<Object> chats) {
        List<Object> list = new ArrayList<>(chats);
        Collections.reverse(list);
        return new LinkedHashSet<>(list);
    }


}
