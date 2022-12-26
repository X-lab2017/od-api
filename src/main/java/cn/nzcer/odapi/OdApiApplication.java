package cn.nzcer.odapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan(basePackages = {"cn.nzcer.odapi.mapper"})
public class OdApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OdApiApplication.class, args);
    }

}
