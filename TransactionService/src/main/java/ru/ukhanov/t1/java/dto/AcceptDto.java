package ru.ukhanov.t1.java.dto;


import ru.ukhanov.t1.java.dto.enums.TransactionStatus;

import java.util.UUID;

public record AcceptDto (
        Long clientId,
        UUID accountId,
        UUID transactionId,
        TransactionStatus status
){}