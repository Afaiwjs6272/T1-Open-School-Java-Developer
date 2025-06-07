package ru.ukhanov.t1.java.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.ukhanov.t1.java.config.Config;
import ru.ukhanov.t1.java.dto.AcceptDto;
import ru.ukhanov.t1.java.dto.KafkaDto;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionAcceptService {
    private final Config config;
    private final KafkaTemplate<String, AcceptDto> kafkaTemplate;
    private final Map<String, List<Instant>> clientTransactionTimestamps = new ConcurrentHashMap<>();

    @KafkaListener(topics = "t1_demo_transaction_accept")
    public void validateTransaction(KafkaDto dto) {
        log.info("Received new message - {}", dto);
        if (isRateLimitExceeded(dto.clientId().toString(), dto.accountId().toString(), dto.timestamp())) {
            AcceptDto message= new AcceptDto(
                    dto.clientId(),
                    dto.accountId(),
                    dto.transactionId(),
                    ru.ukhanov.t1.java.dto.enums.TransactionStatus.BLOCKED
            );
            kafkaTemplate.send("t1_demo_transaction_result", message);
            log.info("Message - {} sent with status BLOCKED", message);
            return;
        }
        if (dto.amount().compareTo(dto.balance()) > 0) {
            AcceptDto message = new AcceptDto(
                    dto.clientId(),
                    dto.accountId(),
                    dto.transactionId(),
                    ru.ukhanov.t1.java.dto.enums.TransactionStatus.REJECTED
            );
            kafkaTemplate.send("t1_demo_transaction_result", message);
            log.info("Message - {} sent with status REJECTED", message);
            return;
        }
        AcceptDto message = new AcceptDto(
                dto.clientId(),
                dto.accountId(),
                dto.transactionId(),
                ru.ukhanov.t1.java.dto.enums.TransactionStatus.ACCEPTED
        );
        kafkaTemplate.send("t1_demo_transaction_result", message);
        log.info("Message - {} sent with status ACCEPTED", message);
    }


    private boolean isRateLimitExceeded(String clientId, String accountId, Instant timestamp) {
        String key = clientId + ":" + accountId;
        List<Instant> timestamps = clientTransactionTimestamps.getOrDefault(key, new CopyOnWriteArrayList<>());

        timestamps.removeIf(t -> Instant.now().minusMillis(config.getTime()).isAfter(t));
        timestamps.add(timestamp);

        clientTransactionTimestamps.put(key, timestamps);
        return timestamps.size() > config.getCount();
    }
}