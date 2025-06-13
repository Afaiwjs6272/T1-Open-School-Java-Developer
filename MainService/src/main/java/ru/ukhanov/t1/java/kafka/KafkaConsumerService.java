package ru.ukhanov.t1.java.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.ukhanov.t1.java.dto.KafkaDto;
import ru.ukhanov.t1.java.dto.ResultDto;
import ru.ukhanov.t1.java.exception.AccountNotFoundException;
import ru.ukhanov.t1.java.exception.TransactionNotFoundException;
import ru.ukhanov.t1.java.exception.WrongTransactionStatusException;
import ru.ukhanov.t1.java.model.account.Account;
import ru.ukhanov.t1.java.model.account.enums.AccountStatus;
import ru.ukhanov.t1.java.model.transaction.Transaction;
import ru.ukhanov.t1.java.model.transaction.enums.TransactionStatus;
import ru.ukhanov.t1.java.repository.AccountRepository;
import ru.ukhanov.t1.java.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class KafkaConsumerService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final KafkaTemplate<String, KafkaDto> kafkaTemplate;

    @KafkaListener(topicPattern = "t1_demo_transactions")
    public void transactionsListener(KafkaDto dto) {
        log.info("Received message - {}", dto);

        Optional<Account> account = accountRepository.findByAccountId(dto.accountId());
        if (account.isEmpty()) {
            log.error("Account with id - {} not found", dto.accountId());
            throw new AccountNotFoundException("Account not found");
        }

        Account acc = account.get();
        if (acc.getStatus() == AccountStatus.OPEN) {
            Transaction transaction = new Transaction();
            transaction.setTransactionId(dto.transactionId());
            transaction.setAccount(acc);
            transaction.setTransactionSum(dto.amount());
            transaction.setTimestamp(dto.timestamp());
            transaction.setTransactionTime(LocalDateTime.now());
            transaction.setStatus(TransactionStatus.REQUESTED);

            transactionRepository.save(transaction);
            log.info("Transaction saved with id - {}", dto.transactionId());

            BigDecimal newBalance = acc.getBalance().subtract(dto.amount());
            acc.setBalance(newBalance);

            accountRepository.save(acc);
            log.info("Balance was changed for account with id - {}, new balance - {}", acc.getId(), newBalance);

            KafkaDto kafkaDto = new KafkaDto(
                    transaction.getTransactionId(),
                    transaction.getAccount().getClient().getClientID(),
                    transaction.getAccount().getAccountId(),
                    transaction.getTransactionSum(),
                    transaction.getTimestamp(),
                    transaction.getStatus().name(),
                    newBalance
            );

            kafkaTemplate.send("t1_demo_transaction_accept", kafkaDto);
            log.info("Sent message - {}", kafkaDto);
        } else {
            log.error("Account status is not OPEN");
            throw new WrongTransactionStatusException("Account status is not equals to OPEN " + acc.getStatus());
        }
    }

    @KafkaListener(topics = "t1_demo_transaction_result")
    public void TransactionResultListener(ResultDto dto) {
        log.info("Received message - {}", dto);
        Transaction transaction = transactionRepository.findById(dto.getTransactionId())
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id -{}"
                        + dto.getTransactionId()));

        Account account = transaction.getAccount();
        switch (dto.getStatus()) {
            case ACCEPTED -> {
                transaction.setStatus(TransactionStatus.ACCEPTED);
                log.info("Received message with status ACCEPTED");
            }
            case BLOCKED -> {
                transaction.setStatus(TransactionStatus.BLOCKED);
                log.info("Received message with status BLOCKED");
                account.setStatus(AccountStatus.BLOCKED);
                account.setFrozenAmount(account.getFrozenAmount().add(transaction.getTransactionSum()));
                accountRepository.save(account);
            }
            case REJECTED -> {
                transaction.setStatus(TransactionStatus.REJECTED);
                log.info("Received message with status REJECTED");
                account.setBalance(account.getBalance().add(transaction.getTransactionSum()));
                accountRepository.save(account);
            }
        }
        transactionRepository.save(transaction);
        log.info("Saved transaction in db");
    }
}
