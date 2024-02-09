package com.spring.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class CommonApplication {
    public static void main(String[] args) {
        String command = "start";

        if (args.length > 0) {
            command = args[args.length - 1];
        }

        switch (command) {
            case "start" -> SpringApplication.run(CommonApplication.class);
            case "stop" -> System.exit(0);
            default -> {

            }
        }
    }
}
