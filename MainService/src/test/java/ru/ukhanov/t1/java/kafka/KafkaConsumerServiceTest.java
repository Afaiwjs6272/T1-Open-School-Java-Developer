package ru.ukhanov.t1.java.kafka;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import ru.ukhanov.t1.java.dto.KafkaDto;
import ru.ukhanov.t1.java.dto.ResultDto;
import ru.ukhanov.t1.java.exception.AccountNotFoundException;
import ru.ukhanov.t1.java.exception.TransactionNotFoundException;
import ru.ukhanov.t1.java.exception.WrongTransactionStatusException;
import ru.ukhanov.t1.java.model.account.Account;
import ru.ukhanov.t1.java.model.account.enums.AccountStatus;
import ru.ukhanov.t1.java.model.clinet.Client;
import ru.ukhanov.t1.java.model.transaction.Transaction;
import ru.ukhanov.t1.java.model.transaction.enums.TransactionStatus;
import ru.ukhanov.t1.java.repository.AccountRepository;
import ru.ukhanov.t1.java.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private KafkaTemplate<String, KafkaDto> kafkaTemplate;

    @InjectMocks
    private KafkaConsumerService consumerService;

    private UUID accountId;
    private UUID businessTxId;
    private BigDecimal amount;
    private Instant timestamp;

    @BeforeEach
    void setup() {
        accountId = UUID.randomUUID();
        businessTxId = UUID.randomUUID();
        amount = new BigDecimal("100.00");
        timestamp = Instant.now();
    }

    @Test
    void whenAccountNotFound_thenThrow() {
        KafkaDto dto = new KafkaDto(businessTxId, 1L, accountId, amount, timestamp, null, null);
        when(accountRepository.findByAccountId(accountId)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> consumerService.transactionsListener(dto));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void whenAccountNotOpen_thenThrow() {
        Account acc = new Account();
        acc.setAccountId(accountId);
        acc.setStatus(AccountStatus.BLOCKED);
        when(accountRepository.findByAccountId(accountId)).thenReturn(Optional.of(acc));

        KafkaDto dto = new KafkaDto(businessTxId, 1L, accountId, amount, timestamp, null, null);
        assertThrows(WrongTransactionStatusException.class,
                () -> consumerService.transactionsListener(dto));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void whenAccountOpen_thenProcessAndSend() {
        Account acc = new Account();
        acc.setId(10L);
        acc.setAccountId(accountId);
        acc.setClient(new Client());
        acc.setStatus(AccountStatus.OPEN);
        acc.setBalance(new BigDecimal("500.00"));
        when(accountRepository.findByAccountId(accountId)).thenReturn(Optional.of(acc));

        KafkaDto dto = new KafkaDto(businessTxId, 20L, accountId, amount, timestamp, null, null);

        consumerService.transactionsListener(dto);

        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(txCaptor.capture());
        Transaction savedTx = txCaptor.getValue();
        assertEquals(TransactionStatus.REQUESTED, savedTx.getStatus());
        assertEquals(amount, savedTx.getTransactionSum());

        ArgumentCaptor<Account> accCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accCaptor.capture());
        Account updatedAcc = accCaptor.getValue();
        assertEquals(new BigDecimal("400.00"), updatedAcc.getBalance());

        ArgumentCaptor<KafkaDto> kafkaCaptor = ArgumentCaptor.forClass(KafkaDto.class);
        verify(kafkaTemplate).send(eq("t1_demo_transaction_accept"), kafkaCaptor.capture());
        KafkaDto sent = kafkaCaptor.getValue();
        assertEquals(TransactionStatus.REQUESTED.name(), sent.status());
        assertEquals(new BigDecimal("100.00"), sent.amount());
    }


    @Test
    void whenResultTransactionNotFound_thenThrow() {
        Long dbId = 1L;
        ResultDto dto = new ResultDto(1L, 1L, dbId, TransactionStatus.ACCEPTED);
        when(transactionRepository.findById(dbId)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class,
                () -> consumerService.TransactionResultListener(dto));
    }

    @Test
    void whenResultAccepted_thenUpdateStatus() {
        Long dbId = 2L;
        Account acc = new Account();
        acc.setBalance(BigDecimal.ZERO);
        Transaction tx = new Transaction();
        tx.setId(dbId);
        tx.setAccount(acc);
        tx.setTransactionSum(amount);
        when(transactionRepository.findById(dbId)).thenReturn(Optional.of(tx));

        ResultDto dto = new ResultDto(1L, 1L, dbId, TransactionStatus.ACCEPTED);
        consumerService.TransactionResultListener(dto);

        assertEquals(TransactionStatus.ACCEPTED, tx.getStatus());
        verify(accountRepository, never()).save(any());
        verify(transactionRepository).save(tx);
    }

    @Test
    void whenResultBlocked_thenBlockAccountAndFreeze() {
        Long dbId = 3L;
        Account acc = new Account();
        acc.setBalance(new BigDecimal("200.00"));
        acc.setFrozenAmount(BigDecimal.ZERO);
        Transaction tx = new Transaction();
        tx.setId(dbId);
        tx.setAccount(acc);
        tx.setTransactionSum(amount);
        when(transactionRepository.findById(dbId)).thenReturn(Optional.of(tx));

        ResultDto dto = new ResultDto(1L, 1L, dbId, TransactionStatus.BLOCKED);
        consumerService.TransactionResultListener(dto);

        assertEquals(TransactionStatus.BLOCKED, tx.getStatus());
        assertEquals(AccountStatus.BLOCKED, acc.getStatus());
        assertEquals(amount, acc.getFrozenAmount());
        verify(accountRepository).save(acc);
        verify(transactionRepository).save(tx);
    }

    @Test
    void whenResultRejected_thenRefundAccount() {
        Long dbId = 4L;
        Account acc = new Account();
        acc.setBalance(new BigDecimal("150.00"));
        Transaction tx = new Transaction();
        tx.setId(dbId);
        tx.setAccount(acc);
        tx.setTransactionSum(amount);
        when(transactionRepository.findById(dbId)).thenReturn(Optional.of(tx));

        ResultDto dto = new ResultDto(1L, 1L, dbId, TransactionStatus.REJECTED);
        consumerService.TransactionResultListener(dto);

        assertEquals(TransactionStatus.REJECTED, tx.getStatus());
        assertEquals(new BigDecimal("250.00"), acc.getBalance());
        verify(accountRepository).save(acc);
        verify(transactionRepository).save(tx);
    }
}

