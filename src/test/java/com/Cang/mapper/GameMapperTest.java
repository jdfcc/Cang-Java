package com.Cang.mapper;

import com.Cang.entity.Tag;
import com.Cang.service.TagService;
import com.Cang.utils.ParseUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.*;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description GameMapperTest
 * @DateTime 2024/2/22 18:45
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameMapperTest {

    @Resource
    GameMapper gameMapper;
    @Resource
    TagService tagService;

    @Test
    void getTags() {
        List<String> tags = gameMapper.getTags();
        System.out.println(tags);
    }

    @Test
    void showTags() {
        List<String> tags = gameMapper.getTags();
        ArrayList<String> newTags = new ArrayList<>();
        // 写入数据库
        for (String tag : tags) {
            newTags.addAll(ParseUtil.convertToList(tag));
        }
        List<String> strings = removeDuplicatesByFrequency(newTags);

        System.out.println(strings.size());
        for (String t : strings) {
            Tag tag = new Tag();
            tag.setName(t);
            tagService.save(tag);
        }
    }

    public static List<String> removeDuplicatesByFrequency(List<String> originalList) {
        Map<String, Integer> frequencyMap = new HashMap<>();

        // 统计每个字符串的出现次数
        for (String str : originalList) {
            frequencyMap.put(str, frequencyMap.getOrDefault(str, 0) + 1);
        }

        // 对Map按值（出现次数）进行排序
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(frequencyMap.entrySet());
        entryList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // 构建去重后的列表
        List<String> uniqueList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : entryList) {
            uniqueList.add(entry.getKey());
        }

        return uniqueList;
    }
}