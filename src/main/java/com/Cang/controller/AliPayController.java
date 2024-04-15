package com.Cang.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.Cang.config.AliPayConfig;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePrecreateResponse;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.factory.Factory.Payment;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePrecreateResponse;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Jdfcc
 * @HomePage <a href="https://github.com/Jdfcc">Jdfcc</a>
 * @Description AliPayController
 * @DateTime 2024/4/7 16:00
 */
@RestController
@Slf4j
@RequestMapping("/ali")
public class AliPayController {



    private static Config getOptions() {
        Config config = new Config();
        config.protocol = "https";
        config.gatewayHost = "openapi.alipay.com";
        config.signType = "RSA2";
        config.appId = "9021000135693774";

        // 为避免私钥随源码泄露，推荐从文件中读取私钥字符串而不是写入源码中
        config.merchantPrivateKey="MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCJV8Nt8mgoSkmioboyLiAPI9oZhCC/5vJNLoVWXeC+Wdyid+CthvzMsKsp/VF3Fog31ySsqqHbUuni6mHv+ChIRosGQ/EipnfpgOnC6pEB2rc7nqaWsVNvwmPMEDDXnegKqSilVm/nSg0wgpP9kpFTNzHjN9b0ZqLbW3Rbp3ccL+fOA9dyP3wMGGhX37LwXQqpn+CRtQwe2oLFAZmQBvFZcZ20uSaW0TQky82B7tVkIId83SFyJveeoP2O8LIx3u1Ckl63E+/dTB+GFw8yprD2dNk9ggeaGEwbeoC7fjt1SwXxgRneDB5JWw+3caRIHCQgPG/MtBW4MeIy+SsynsCTAgMBAAECggEAbCpAcbC9sy1+fKTeXXLSMNpNhFb46+nY3kxLGs/olkFzcEj1G/T518Mzqmgk5qNKOKAp9AqWWoMcTGfHJhTMtM9J4m9T07RDMBUlBStzdIBqWnwCOyAA1D3GFwqRTN7BxtOefUhr7T320UOlhckOxvjrQ0XQ9r52eEe4hg6H0hwInvcFHREdPIo75TwY2gfLLAQ3LQAfYByOC8ZCvEzSMv5dJYnWRcIe+0DhatYX2iO2N3V9ONkgbDWE8GVNddpasVoiPYef4H6901/dvHKEhMKUinjNpsvzxETZK0kugADdZ/de5PgOk7+ccq3yUOkqEM1s9H4/OqAQy1SurJUVwQKBgQDGlmwjYWLFEwx45wqrNxYiAxZ+MsCjC2Y6APJlDkpr6tDk0SXf1/zKJCzyWO9OJyCz4O/zbsYscsBWF7GSooqp+x5akp51ljK5bT0rsaNyNoMNI+/QZJuzpfmoHS/QVO3wANHs7SrqETKEAoG9v9mbhEpYMdZkI/wohcTqC8bg4wKBgQCxDJUI2ZFxHpDeRbDm4PFqAWo12tOdH7w7UCATfa5xS78+pdwCWbmtM8H6jsSIKN/NwrHJXYcEqO08lyW9P8Dm0XaOw2JvQZm/10DKg7MeQZIk6p03Ue2/bsjYxsR0gTGev/HK5ufVKLPRj3zqUT9QlRVtfaqE05Iwxsy90T8gkQKBgG1moNw84jS/cbeJUI9SIZdLLDEJtKYduYtqyh5x6P3atonXzo2qZ1DU7Q31gKAFXja6THZHKFWEQgErvF+Tu+A4v1gWPcV5oSgVzgUsL2bUMQ4kJhEf28b0QL0klxqmLw5w5TUo0uNv5exjlSX7ct4B2xMLIZi5WkjYqMpnAoERAoGALtP7H0occ+T3wDKCpa5dbAJPy0rDqc1ZBf8dXMpjN0gnnzRTSrPcrnkAF3tnc/QLreqPB4mbSpo7lUkNDE8ugF5qkzwObwrU1a7jTi0jVR9WvjjnuFgNIBL3QI7iK0Z4j1qJY3dLXk/Tr9vZ2EgBDRlBf6I7VcJ36jrKsHIF1mECgYEAvcSUFjEriSRJiiXbD/tjQThtGtHzk4B7pkwusp/XQD97p56qdkvQ2YZLEDTQMB/IEZsJS/Dz8CHTmhHarSAhJVNqUB5sZI/CCKn/rphlDJKE0M8RTw/3J5slhkJd+/TvaMObz0LAb603u6ylafp1lxyL55ZvhnkfeRk5W0R2wJ4=";


        //注：如果采用非证书模式，则无需赋值上面的三个证书路径，改为赋值如下的支付宝公钥字符串即可
        config.alipayPublicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA9huwASFpP4+IhSyXyoLFxk2ZGvqVISGsgGcSESXkOaQkhGFkNn7nB2WEYwjBLx+/0ZLHk0A8x7MUCLrR5DUzPXLUhxJ3tQEw+vHXQQnSvgkwBRcwAn+tLpwF3a5JxpXM3iXWxBr3teq0Ko3Eiz6w8yuwddeYzmz0a8eSi5KNBi/PiS3VpBv1H7twaiM1wRDKBptMbyS2m1ougde60CKbhqwNQHfZqcxFGEVL0TAyILvza5B3DPX10GGoDijqRKg9p3NV63C9QpVk/fMKknpJUfE+tmlCtGcU+18OGmL0UMcmbDm6tBTAu5PQbADJoYbS7rdEFOgGoW6k9mvAWy68WwIDAQAB";

        //可设置异步通知接收服务地址（可选）
//        config.notifyUrl = "<-- 请填写您的支付类接口异步通知接收服务地址，例如：https://www.test.com/callback -->";
        return config;
    }

    @GetMapping("/demo")
    public void demo()  {

        // 1. 设置参数（全局只需设置一次）
        Factory.setOptions(getOptions());

        try {
            // 2. 发起API调用（以创建当面付收款二维码为例）
            AlipayTradePrecreateResponse response =Payment.FaceToFace()
                    .preCreate("Apple iPhone11 128G", "2234567890", "5799.00");
            // 3. 处理响应或异常
            if (ResponseChecker.success(response)) {
                System.out.println("调用成功");
            } else {
                System.err.println("调用失败，原因：" + response.msg + "，" + response.subMsg);
            }
        } catch (Exception e) {
            System.err.println("调用遭遇异常，原因：" + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}