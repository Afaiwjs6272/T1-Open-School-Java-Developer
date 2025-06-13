package ru.ukhanov.t1.java.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UnblockService {
    private final Random random = new Random();

    public boolean randomUnblockClient(Long clientId) {
        return random.nextDouble(clientId) < 0.7;
    }

    public boolean randomUnblockAccount(Long accountId) {
        return random.nextDouble(accountId) < 0.8;
    }
}
