
package com.example.graduate_project;
import com.example.graduate_project.utiles.RedisUtil;
import com.example.graduate_project.utiles.SnowflakeIdWorker;
import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Random;

@EnableSwagger2
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class GraduateProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraduateProjectApplication.class, args);
    }

    @Bean
    public SnowflakeIdWorker createIdWorker(){
        return new SnowflakeIdWorker(0,0);
    }

    @Bean
    public BCryptPasswordEncoder cryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RedisUtil createRedis(){
        return  new RedisUtil();
    }

    @Bean
    public Random createRandom(){return new Random();}

    @Bean
    public Gson createGson(){
        return new Gson();
    }
}
