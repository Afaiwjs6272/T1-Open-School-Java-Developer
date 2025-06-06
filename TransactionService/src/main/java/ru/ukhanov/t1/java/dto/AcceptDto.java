package ru.ukhanov.t1.java.dto;

import ru.ukhanov.t1.java.dto.enums.TransactionsStatus;

import java.util.UUID;

public record AcceptDto (
        Long clientId,
        UUID accountId,
        UUID transactionId,
        TransactionsStatus status
){}