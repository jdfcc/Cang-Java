package com.Cang;


import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import javax.annotation.Resource;
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
public class CangApplication implements ApplicationRunner {

    @Value("${server.port}")
    private String port;
    @Resource
    private  TemplateEngine templateEngine;


    public static void main(String[] args) {
        SpringApplication.run(CangApplication.class, args);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        showBackendIpAddress();
        setUserName();
    }

    public void showBackendIpAddress() throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();
        log.info("the backend ip address is:  {} ", "http://" + localHost.getHostAddress() + ":" + port);
    }

    public void setUserName(){
        Context context = new Context();
        //html中填充动态属性值
        context.setVariable("nickName", "jdfcc");
        templateEngine.process("index", context);
    }
}
