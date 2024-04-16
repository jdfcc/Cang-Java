package com.Cang.config;



import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description AliPayConfig
 * @DateTime 2024/4/7 15:47
 */
@Data
@Slf4j
//@Configuration
//@ConfigurationProperties(prefix = "alipay")
public class AliPayConfig {
    private String appId;
    private String appPrivateKey;
    private String alipayPublicKey;
    private String notifyUrl;
    private String gatewayHost;
    private String signType;
    private String protocol;



    /**
     *  项目初始化事件
     * */
    @PostConstruct
    public void run() {
        //初始化支付宝SDK
        Factory.setOptions(getOptions());
        System.out.println("**********支付宝SDK初始化完成**********");
    }

    private Config getOptions() {
        //这里省略了一些不必要的配置，可参考文档的说明

        Config config = new Config();
        config.protocol = "https";
        config.gatewayHost = this.gatewayHost;
        config.signType = "RSA2";

        config.appId = this.appId;

        // 为避免私钥随源码泄露，推荐从文件中读取私钥字符串而不是写入源码中
        config.merchantPrivateKey = this.appPrivateKey;

        //注：如果采用非证书模式，则无需赋值上面的三个证书路径，改为赋值如下的支付宝公钥字符串即可
        config.alipayPublicKey = this.alipayPublicKey;

//        //可设置异步通知接收服务地址（可选）
//        config.notifyUrl = notifyUrl;

        return config;
    }

}
