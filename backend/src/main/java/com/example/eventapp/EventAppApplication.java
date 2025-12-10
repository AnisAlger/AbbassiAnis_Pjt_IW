package com.example.eventapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class EventAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventAppApplication.class, args);
    }
}
