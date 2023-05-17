package com.Cang.mapper;

import com.Cang.entity.Chat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Jdfcc
 * @Description chatMapper
 * @DateTime 2023/5/15 18:17
 */
public interface ChatMapper extends BaseMapper<Chat> {

    /**
     * 找出所有不重复的userKey
     * @return 所有不重复的userKey
     */
    List<String> queryChatList();

    /**
     *  找出userKey中靠后的最后一条消息
     * @param userKey
     * @return userKey中靠后的最后一条消息
     */
    Chat selectLast(@Param("userKey") String userKey);
}
