package com.Cang.controller;


import com.Cang.dto.Result;
import com.Cang.service.IFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author jdfcc
 * @since 2023-2-8
 */
@RestController
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private IFollowService followService;

    /**
     * 关注功能
     * @param id,操作目标用户id
     * @param isFollow,如果为true,则关注,false则取关
     * @return
     */
    @PutMapping("/{id}/{isFollow}")
    public Result follow(@PathVariable Long id,@PathVariable Boolean isFollow){
        return followService.follow(id,isFollow);
    }

    /**
     * 查询是否关注
     * @param id
     * @return
     */
    @GetMapping("/or/not/{id}")
    public Result isFollow(@PathVariable Long id){
        return followService.isFollow(id);
    }

    @GetMapping("/common/{id}")
    public Result common(@PathVariable Long id){
        return followService.common(id);
    }


}
