package com.Cang;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/**
 * @author Jdfcc
 */
@MapperScan("com.Cang.mapper")
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableWebSocket
public class CangApplication {

    public static void main(String[] args) {
        SpringApplication.run(CangApplication.class, args);
    }

}
