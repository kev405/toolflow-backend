package com.codeflow.toolflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ToolflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToolflowApplication.class, args);
    }

}
