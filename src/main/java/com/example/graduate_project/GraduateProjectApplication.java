
package com.example.graduate_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class GraduateProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraduateProjectApplication.class, args);
    }

}
