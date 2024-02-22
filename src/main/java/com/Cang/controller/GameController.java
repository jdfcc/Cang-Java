package com.Cang.controller;


import com.Cang.dto.Result;
import com.Cang.entity.Game;
import com.Cang.entity.Tag;
import com.Cang.service.GameService;
import com.Cang.service.TagService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description GameResource
 * @DateTime 2024/1/10 16:56
 */
@RestController
@RequestMapping("/game")
public class GameController {

    private static final int PAGE_SIZE = 20;
    @Resource
    private GameService gameService;
    @Resource
    private TagService tagService;

    @PostMapping("/add")
    public Result addGame(@RequestBody Game game) {
        gameService.save(game);
        return Result.ok();
    }

    @GetMapping("/list")
    public Result listGame(@RequestParam(value = "index", defaultValue = "1") Integer index) {
        Page<Game> page = gameService.query()
                .page(new Page<>(index, PAGE_SIZE));
        return Result.ok(page);
    }

    /**
     * 获取游戏tag
     *
     * @param index 当前坐标
     * @return tags
     */
    @GetMapping("/item")
    public Result getAllTags(@RequestParam(value = "index", defaultValue = "1") Integer index) {
        Page<Tag> page = tagService.getTags(index, PAGE_SIZE);
        return Result.ok(page);
    }

    /**
     * 根据tag获取到有此tag的游戏
     *
     * @param index   当前坐标
     * @param tagName 标签名称
     * @return list
     */
    @GetMapping("/byTag/{tagName}")
    public Result getByTag(@RequestParam(value = "index", defaultValue = "1") Integer index,
                           @PathVariable String tagName) {
        List<Game> gameByTag = gameService.getGameByTag(index, PAGE_SIZE, tagName);
        return Result.ok(gameByTag);
    }
}
