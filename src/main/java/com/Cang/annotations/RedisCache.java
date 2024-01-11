package com.Cang.annotations;

import com.Cang.enums.RedisCacheType;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description 打上此注解时，该类或者方法的返回值会被写入redis缓存中，进入该方法优先查询redis，有则直接返回。仅供自动缓存用
 * 缓存key的规则: 若方法有入参则根据方法入参加value计算hash作为key存入redis，
 * 若方法没有入参则根据value+UserHolder里面的用户来计算hash。
 * @DateTime 2024/1/10 13:53
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCache {
    /**
     * 业务key，指定此key标识此接口业务。更新业务操作时根据指定的业务key删除缓存的数据
     */
    String value();

    /**
     * 过期时间
     */
    int expireTime() default 1;

    /**
     * 单位
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * 储存于redis中供查询的数据类型
     */
    RedisCacheType type();
}
