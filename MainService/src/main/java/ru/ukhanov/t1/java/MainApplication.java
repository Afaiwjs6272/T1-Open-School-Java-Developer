package ru.ukhanov.t1.java;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@Slf4j
public class MainApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(MainApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
