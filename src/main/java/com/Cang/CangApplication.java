package com.Cang;


import com.Cang.exception.DeleteException;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Jdfcc
 */
@MapperScan("com.Cang.mapper")
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableWebSocket
@Slf4j
public class CangApplication  {

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication.run(CangApplication.class, args);
        InetAddress localHost = InetAddress.getLocalHost();
        log.info("CangApplication: {} ",localHost.getHostAddress());
        throw new DeleteException("8888888888888888888");
    }

}
