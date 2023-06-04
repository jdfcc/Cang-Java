package com.Cang.mapper;

import com.Cang.dto.ChatDto;
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
     * 从数据库中找出与此用户id有关的最后一条消息
     *
     * @param userid userid
     * @return List<Chat>
     */
    List<Chat> selectLast(@Param("userid") Long userid);

    /**
     * 查询出指定的chatDto并返回
     *
     * @param userKey userKey
     * @return List<ChatDto>
     */
    List<ChatDto> selectDtos(@Param("userKey") String userKey);
}
