package com.macro.mall.tiny;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan({"com.macro.mall.tiny.mbg","com.macro.mall.tiny.dao"})
public class MallTiny01Application {

    public static void main(String[] args) {
        SpringApplication.run(MallTiny01Application.class, args);
    }

}
