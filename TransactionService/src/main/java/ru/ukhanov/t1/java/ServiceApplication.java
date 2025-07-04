package ru.ukhanov.t1.java;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class ServiceApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(ServiceApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}