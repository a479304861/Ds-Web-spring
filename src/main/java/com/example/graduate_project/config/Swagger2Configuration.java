package com.example.graduate_project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class Swagger2Configuration {
    public static final String VERSION = "1.0.0";

    /**
     * 门户api，接口前缀：portal
     *
     * @return
     */


    /**
     * 管理中心api，接口前缀：admin
     *
     * @return
     */
    @Bean
    public Docket adminApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(adminApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.graduate_project.controller"))
                .paths(PathSelectors.any()) // 可以根据url路径设置哪些请求加入文档，忽略哪些请求
                .build()
                .groupName("管理中心");
    }


    private ApiInfo adminApiInfo() {
        return new ApiInfoBuilder()
                .title("阳光沙滩管理中心接口文档") //设置文档的标题
                .description("管理中心接口") // 设置文档的描述
                .version(VERSION) // 设置文档的版本信息-> 1.0.0 Version information
                .build();
    }




}
