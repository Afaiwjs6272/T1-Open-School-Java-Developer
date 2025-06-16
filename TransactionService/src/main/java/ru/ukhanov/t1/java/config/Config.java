package ru.ukhanov.t1.java.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "transaction.rate.limit")
@Data
public class Config {
    private int count;
    private long time;
}