package com.Cang;


import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import javax.annotation.PostConstruct;
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
public class CangApplication {

    @Value("${server.port}")
    private String port;

    public static void main(String[] args) {
        SpringApplication.run(CangApplication.class, args);
    }

    @PostConstruct
    public void init() throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();
        log.info("the backend ip address is:  {} ","http://"+ localHost.getHostAddress() +":"+ port);
    }

}
