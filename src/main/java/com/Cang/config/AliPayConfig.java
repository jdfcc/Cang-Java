package com.Cang.config;



import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description AliPayConfig
 * @DateTime 2024/4/7 15:47
 */
@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "alipay")
public class AliPayConfig {
    private String appId;
    private String appPrivateKeyPath;
    private String alipayPublicKeyPath;
    private String notifyUrl;
    private String gatewayHost;
    private String signType;
    private String protocol;
    private String charSet;
    private String format;
    private String appPrivateKey;
    private String alipayPublicKey;


    /**
     * 项目初始化事件
     */
    @PostConstruct
    public void run() throws IOException {
        //初始化支付宝SDK
        Factory.setOptions(getOptions());
        log.info("*****支付宝SDK初始化完成*****");
    }

    private Config getOptions() throws IOException {
        //这里省略了一些不必要的配置，可参考文档的说明
        Config config = new Config();
        config.protocol = this.protocol;
        config.gatewayHost = this.gatewayHost;
        config.signType = this.signType;
        config.appId = this.appId;
        // 为避免私钥随源码泄露，推荐从文件中读取私钥字符串而不是写入源码中
        this.appPrivateKey=getSecurityKey(this.appPrivateKeyPath);
        config.merchantPrivateKey =this.appPrivateKey ;
        //注：如果采用非证书模式，则无需赋值上面的三个证书路径，改为赋值如下的支付宝公钥字符串即可
        this.alipayPublicKey=getSecurityKey(this.alipayPublicKeyPath);
        config.alipayPublicKey = this.alipayPublicKey;

//        //可设置异步通知接收服务地址（可选）
//        config.notifyUrl = notifyUrl;

        return config;
    }

    /**
     * 读取证书内容
     *
     * @param path 证书位于Resource目录下的位置 eg: security/alipay/alipay-public-key
     */
    String getSecurityKey(String path) throws IOException {
        // 使用ClassLoader获取资源文件的输入流
        InputStream inputStream = AliPayConfig.class.getClassLoader()
                .getResourceAsStream(path);
        StringBuilder content = new StringBuilder();

        // 将输入流转换为字符流，并读取内容
        if (inputStream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();

        }
        return content.toString();
    }
}