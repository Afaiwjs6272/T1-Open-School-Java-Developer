package ru.ukhanov.t1.java.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.ukhanov.t1.java.model.transaction.enums.TransactionStatus;

@Data
@AllArgsConstructor
public class ResultDto {
    private Long clientId;
    private Long accountId;
    private Long transactionId;
    private TransactionStatus status;
}