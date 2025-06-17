package ru.ukhanov.t1.java.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.kafka.core.KafkaTemplate;

import ru.ukhanov.t1.java.config.Config;
import ru.ukhanov.t1.java.dto.AcceptDto;
import ru.ukhanov.t1.java.dto.KafkaDto;
import ru.ukhanov.t1.java.dto.enums.TransactionsStatus;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransactionAcceptServiceTest {

    @Mock
    private Config config;

    @Mock
    private KafkaTemplate<String, AcceptDto> kafkaTemplate;

    private TransactionAcceptService service;

    @BeforeEach
    void setUp() {
        service = new TransactionAcceptService(config, kafkaTemplate);
    }

    @Test
    void whenAmountLessOrEqualBalanceAndWithinRateLimit_thenSendAccepted() {
        UUID txId      = UUID.randomUUID();
        Long clientId  = 123L;
        UUID accountId = UUID.randomUUID();
        BigDecimal amount  = BigDecimal.valueOf(50);
        BigDecimal balance = BigDecimal.valueOf(100);
        Instant timestamp  = Instant.now();

        lenient().when(config.getTime()).thenReturn(10_000L);
        lenient().when(config.getCount()).thenReturn(5);

        KafkaDto dto = new KafkaDto(
                txId,
                clientId,
                accountId,
                amount,
                timestamp,
                null,
                balance
        );

        service.validateTransaction(dto);

        ArgumentCaptor<AcceptDto> captor = ArgumentCaptor.forClass(AcceptDto.class);
        verify(kafkaTemplate, times(1))
                .send(eq("t1_demo_transaction_result"), captor.capture());

        AcceptDto sent = captor.getValue();
        assertEquals(clientId, sent.clientId());
        assertEquals(accountId, sent.accountId());
        assertEquals(txId, sent.transactionId());
        assertEquals(TransactionsStatus.ACCEPTED, sent.status());
    }

    @Test
    void whenAmountGreaterThanBalance_thenSendRejected() {
        UUID txId      = UUID.randomUUID();
        Long clientId  = 456L;
        UUID accountId = UUID.randomUUID();
        BigDecimal amount  = BigDecimal.valueOf(150);
        BigDecimal balance = BigDecimal.valueOf(100);
        Instant timestamp  = Instant.now();

        lenient().when(config.getTime()).thenReturn(10_000L);
        lenient().when(config.getCount()).thenReturn(5);

        KafkaDto dto = new KafkaDto(
                txId,
                clientId,
                accountId,
                amount,
                timestamp,
                "",
                balance
        );

        service.validateTransaction(dto);

        ArgumentCaptor<AcceptDto> captor = ArgumentCaptor.forClass(AcceptDto.class);
        verify(kafkaTemplate, times(1))
                .send(eq("t1_demo_transaction_result"), captor.capture());

        AcceptDto sent = captor.getValue();
        assertEquals(TransactionsStatus.REJECTED, sent.status());
        assertEquals(txId, sent.transactionId());
    }

    @Test
    void whenRateLimitExceeded_thenSendBlocked() {
        UUID txId1      = UUID.randomUUID();
        UUID txId2      = UUID.randomUUID();
        Long clientId   = 789L;
        UUID accountId  = UUID.randomUUID();
        BigDecimal amount  = BigDecimal.valueOf(10);
        BigDecimal balance = BigDecimal.valueOf(100);

        lenient().when(config.getTime()).thenReturn(86_400_000L);
        lenient().when(config.getCount()).thenReturn(1);

        Instant t1 = Instant.now();
        KafkaDto firstDto = new KafkaDto(
                txId1, clientId, accountId,
                amount, t1, null, balance
        );
        service.validateTransaction(firstDto);

        Instant t2 = t1.plusMillis(1);
        KafkaDto secondDto = new KafkaDto(
                txId2, clientId, accountId,
                amount, t2, null, balance
        );
        service.validateTransaction(secondDto);

        ArgumentCaptor<AcceptDto> captor = ArgumentCaptor.forClass(AcceptDto.class);
        verify(kafkaTemplate, times(2))
                .send(eq("t1_demo_transaction_result"), captor.capture());

        AcceptDto secondSent = captor.getAllValues().get(1);
        assertEquals(TransactionsStatus.BLOCKED, secondSent.status());
        assertEquals(txId2, secondSent.transactionId());
    }
}
