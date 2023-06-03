package com.Cang.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author Jdfcc
 * @Description WebSocketConfig
 * @DateTime 2023/5/17 21:38
 */

@Configuration
public class WebSocketConfig {

    /**
     * ServerEndpointExporter类的作用是，会扫描所有的服务器端点，
     * 把带有@ServerEndpoint 注解的所有类都添加进来
     *
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}
