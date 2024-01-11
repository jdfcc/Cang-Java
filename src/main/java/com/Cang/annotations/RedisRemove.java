package com.Cang.annotations;

import java.lang.annotation.*;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description 配合RedisCache注解使用，此注解作为删除所使用 TODO 删除待定
 * @DateTime 2024/1/10 14:38
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisRemove {
    String[] value();
}
