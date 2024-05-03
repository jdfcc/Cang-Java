package com.Cang.config;


import com.Cang.entity.MinIOConfigProperties;
import com.Cang.service.FileStorageService;
import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;


/**
 * @author Jdfcc
 */
@Data
@Configuration
@EnableConfigurationProperties({MinIOConfigProperties.class})
@ConditionalOnClass(FileStorageService.class)
public class MinIOConfig {

    @Resource
    private MinIOConfigProperties minIoConfigProperties;

    @Bean
    public MinioClient buildMinioClient() {
        return MinioClient
                .builder()
                .credentials(minIoConfigProperties.getAccessKey(), minIoConfigProperties.getSecretKey())
                .endpoint(minIoConfigProperties.getEndpoint())
                .build();
    }
}