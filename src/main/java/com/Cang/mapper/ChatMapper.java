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
    List<String> queryChatList(Long userid);

    /**
     * 从数据库中找出与此用户id有关的最后一条消息
     * @param userid
     * @return
     */
    List<Chat> selectLast(@Param("userid") Long userid);
}
