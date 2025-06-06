package ru.ukhanov.t1.java.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ukhanov.t1.java.model.account.enums.AccountStatus;
import ru.ukhanov.t1.java.model.account.enums.Type;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDto implements Serializable {
    private Long id;

    @JsonProperty("client_id")
    private Long clientId;

    @JsonProperty("type")
    private Type type;

    @JsonProperty("balance")
    private BigDecimal balance;

    @JsonProperty("status")
    private AccountStatus status;

    @JsonProperty("account_id")
    private UUID accountId;

    @JsonProperty("frozen_amount")
    private BigDecimal frozenAmount;
}