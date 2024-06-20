package com.Cang;


import cn.smallbun.screw.core.Configuration;
import cn.smallbun.screw.core.engine.EngineConfig;
import cn.smallbun.screw.core.engine.EngineFileType;
import cn.smallbun.screw.core.engine.EngineTemplateType;
import cn.smallbun.screw.core.execute.DocumentationExecute;
import cn.smallbun.screw.core.process.ProcessConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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
import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

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
//        documentGeneration();

    }

    /**
     * 文档生成
     */
    void documentGeneration() {
        //数据源
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
        hikariConfig.setJdbcUrl("jdbc:mysql://120.55.80.133:3306/cang");
        hikariConfig.setUsername("cang");
        hikariConfig.setPassword("root");
        //设置可以获取tables remarks信息
        hikariConfig.addDataSourceProperty("useInformationSchema", "true");
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setMaximumPoolSize(5);
        DataSource dataSource = new HikariDataSource(hikariConfig);
        //生成配置
        EngineConfig engineConfig = EngineConfig.builder()
                //生成文件路径
                .fileOutputDir("C:\\Users\\Jdfcc\\Desktop\\graduation-design\\db")
                //打开目录
                .openOutputDir(true)
                //文件类型
                .fileType(EngineFileType.HTML)
                //生成模板实现
                .produceType(EngineTemplateType.freemarker)
                //自定义文件名称
                .fileName("自定义文件名称").build();

        //忽略表
        ArrayList<String> ignoreTableName = new ArrayList<>();
        ignoreTableName.add("test_user");
        ignoreTableName.add("test_group");
        //忽略表前缀
        ArrayList<String> ignorePrefix = new ArrayList<>();
        ignorePrefix.add("test_");
        //忽略表后缀
        ArrayList<String> ignoreSuffix = new ArrayList<>();
        ignoreSuffix.add("_test");
        ProcessConfig processConfig = ProcessConfig.builder()
                //指定生成逻辑、当存在指定表、指定表前缀、指定表后缀时，将生成指定表，其余表不生成、并跳过忽略表配置
                //根据名称指定表生成
                .designatedTableName(new ArrayList<>())
                //根据表前缀生成
                .designatedTablePrefix(new ArrayList<>())
                //根据表后缀生成
                .designatedTableSuffix(new ArrayList<>())
                //忽略表名
                .ignoreTableName(ignoreTableName)
                //忽略表前缀
                .ignoreTablePrefix(ignorePrefix)
                //忽略表后缀
                .ignoreTableSuffix(ignoreSuffix).build();
        //配置
        Configuration config = Configuration.builder()
                //版本
                .version("1.0.0")
                //描述
                .description("数据库设计文档生成")
                //数据源
                .dataSource(dataSource)
                //生成配置
                .engineConfig(engineConfig)
                //生成配置
                .produceConfig(processConfig)
                .build();
        //执行生成
        new DocumentationExecute(config).execute();
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
