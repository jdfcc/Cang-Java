package com.Cang.service.impl;

import com.Cang.dto.Result;
import com.Cang.entity.Chat;
import com.Cang.exception.SQLException;
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
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.Cang.utils.RedisConstants.*;

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


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result sendMessage(Chat chat) {
        chat.setCreateTime(LocalDateTime.now());
        Long userid = UserHolder.getUser().getId();
        Long targetId = chat.getReceive();
        String key = this.getKey(userid, targetId);
        chat.setUserKey(key);
        chat.setSend(userid);
        //TODO 加一个异常判断
        int insert = chatMapper.insert(chat);
        if (insert == 0) {
            throw new SQLException("插入失败");
        }
        redisTemplate.opsForList().rightPush(key, chat);
        long seconds = chat.getCreateTime().toEpochSecond(ZoneOffset.UTC);
        double score = seconds * 1000;
//        在当前用户与目标用户首页消息列表中添加此条消息
        redisTemplate.opsForZSet().add(CHAT_MESSAGE_USER_CACHE_KEY_LAST + UserHolder.getUser(), chat, score);
        redisTemplate.opsForZSet().add(CHAT_MESSAGE_USER_CACHE_KEY_LAST + chat.getReceive(), chat, score);
        return Result.ok(insert);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result getMessage(Long targetId) {
        Long userId = UserHolder.getUser().getId();
        String key = this.getKey(userId, targetId);
// TODO
        List<Object> chats = redisTemplate.opsForList().range(String.valueOf(key), 0L, -1L);

        if (chats.isEmpty()) {
//            从数据库中查询并重建缓存
            LambdaQueryWrapper<Chat> chatLambdaQueryWrapper = new LambdaQueryWrapper<>();
            chatLambdaQueryWrapper.eq(Chat::getUserKey, key).orderByAsc(Chat::getCreateTime);
            List<Chat> newChats = chatMapper.selectList(chatLambdaQueryWrapper);
            //数据库中也为空，两人第一次聊天
            if (newChats.isEmpty()) {
                chats = new ArrayList<>();
//                储存空缓存以解决缓存击穿
                redisTemplate.opsForList().leftPush(key, null);
            }
//            重建缓存
            for (Object temp : newChats) {
                redisTemplate.opsForList().rightPush(key, temp);
            }
            return Result.ok(newChats);
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


    @Override
    public Result getHomeChat() {
        Long id = UserHolder.getUser().getId();
        Set<Object> chats = redisTemplate.opsForZSet().range(CHAT_MESSAGE_USER_CACHE_KEY_LAST + id, 0, -1);
        if (chats.isEmpty()) {
//            需要从数据库中重建缓存
            LambdaQueryWrapper<Chat> chatLambdaQueryWrapper = new LambdaQueryWrapper<>();

            chatLambdaQueryWrapper.eq(Chat::getSend, id);
            chats = Collections.singleton(chatMapper.selectLast(UserHolder.getUser().getId()));
            for (Object tem : chats) {
//                重建缓存
                long seconds = ((Chat) tem).getCreateTime().toEpochSecond(ZoneOffset.UTC);
                double score = seconds * 1000;
                redisTemplate.opsForZSet().add(CHAT_MESSAGE_USER_CACHE_KEY_LAST + id, tem, score);
            }
        }
        return Result.ok(chats);
    }

}
