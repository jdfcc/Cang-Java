//package com.Cang.task;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
///**
// * @author Jdfcc
// * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
// * @Description LettuceConnectionValidTask
// * @DateTime 2023/8/7 16:54
// */
//
//@Component
//@Slf4j
//public class LettuceConnectionValidTask {
//    @Autowired
//    private RedisConnectionFactory redisConnectionFactory;
//
//    @Scheduled(cron="0/2 * * * * ?")
//
//    public void task() {
//
//        if(redisConnectionFactory instanceof LettuceConnectionFactory){
//
//            LettuceConnectionFactory c=(LettuceConnectionFactory)redisConnectionFactory;
//
//            c.validateConnection();
//
//        }
//
//    }
//
//
//}
