package ru.ukhanov.t1.java.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record KafkaDto(
        UUID transactionId,
        Long clientId,
        UUID accountId,
        BigDecimal amount,
        Instant timestamp,
        String status,
        BigDecimal balance
) {}
