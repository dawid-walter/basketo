package com.dwalter.basketo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BasketoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasketoApplication.class, args);
    }

}
