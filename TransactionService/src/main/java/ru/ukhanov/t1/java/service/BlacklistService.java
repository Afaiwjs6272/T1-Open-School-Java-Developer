package ru.ukhanov.t1.java.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class BlacklistService {
    private final Random random = new Random();

    public boolean isBlacklisted(String clientId) {
        return random.nextInt(10) == 0;
    }
}
