package ru.ukhanov.t1.java.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.ukhanov.t1.java.model.account.enums.AccountStatus;
import ru.ukhanov.t1.java.model.clinet.enums.ClientStatus;
import ru.ukhanov.t1.java.repository.AccountRepository;
import ru.ukhanov.t1.java.repository.ClientRepository;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class BlockedMetrics {
    private final MeterRegistry meterRegistry;
    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;

    private final AtomicInteger blockedClients = new AtomicInteger(0);
    private final AtomicInteger arrestedAccounts = new AtomicInteger(0);

    @PostConstruct
    public void init() {
        Gauge.builder("bank.blocked.clients.count", blockedClients, AtomicInteger::get)
                .description("Number of blocked clients")
                .register(meterRegistry);

        Gauge.builder("bank.arrested.accounts.count", arrestedAccounts, AtomicInteger::get)
                .description("Number of arrested accounts")
                .register(meterRegistry);

        updateMetrics();
    }

    @Scheduled(fixedRate = 30000)
    public void updateMetrics() {
        blockedClients.set(clientRepository.countByStatus(ClientStatus.BLOCKED));
        arrestedAccounts.set(accountRepository.countByStatus(AccountStatus.ARRESTED));
    }
}