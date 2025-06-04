package ru.ukhanov.t1.java.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ukhanov.t1.java.model.transaction.enums.TransactionStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDto implements Serializable {
    private Long id;

    @JsonProperty("account_id")
    private Long accountId;

    @JsonProperty("transaction_sum")
    private BigDecimal transactionSum;

    @JsonProperty("transaction_time")
    private LocalDateTime transactionTime;

    @JsonProperty("status")
    private TransactionStatus status;

    @JsonProperty("transaction_id")
    private UUID transactionId;

    @JsonProperty("timestamp")
    private Instant timestamp;
}