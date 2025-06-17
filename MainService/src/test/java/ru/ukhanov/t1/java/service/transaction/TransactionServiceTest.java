package ru.ukhanov.t1.java.service.transaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import ru.ukhanov.t1.java.dto.TransactionDto;
import ru.ukhanov.t1.java.exception.TransactionNotFoundException;
import ru.ukhanov.t1.java.mapper.TransactionMapper;
import ru.ukhanov.t1.java.model.account.Account;
import ru.ukhanov.t1.java.model.account.enums.AccountStatus;
import ru.ukhanov.t1.java.model.clinet.Client;
import ru.ukhanov.t1.java.model.transaction.Transaction;
import ru.ukhanov.t1.java.model.transaction.enums.TransactionStatus;
import ru.ukhanov.t1.java.repository.AccountRepository;
import ru.ukhanov.t1.java.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ClientBlackList clientBlackList;

    @InjectMocks
    private TransactionServiceImpl service;

    @Value("${transactions.reject.limit}")
    private int limit;


    @Test
    void addTransaction_whenClientBlacklisted_shouldBlockAccountAndRejectTransaction() {
        Account account = new Account();
        account.setStatus(AccountStatus.OPEN);
        account.setClient(new Client());
        account.getClient().setId(1L);
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        TransactionDto dtoIn = new TransactionDto();
        TransactionDto dtoOut = new TransactionDto();

        when(transactionMapper.toTransaction(dtoIn)).thenReturn(transaction);
        when(clientBlackList.isClientBlacklisted("1")).thenReturn(true);
        when(transactionMapper.toTransactionDto(any(Transaction.class))).thenReturn(dtoOut);

        // Act
        TransactionDto result = service.addTransaction(dtoIn);

        // Assert
        assertEquals(dtoOut, result);
        assertEquals(AccountStatus.BLOCKED, account.getStatus());
        assertEquals(TransactionStatus.REJECTED, transaction.getStatus());
        verify(accountRepository).save(account);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void addTransaction_whenRejectCountExceedsLimit_shouldArrestAccount() {
        // Arrange
        Account account = new Account();
        account.setStatus(AccountStatus.OPEN);
        account.setClient(new Client());
        account.getClient().setId(2L);
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        TransactionDto dtoIn = new TransactionDto();
        TransactionDto dtoOut = new TransactionDto();

        when(transactionMapper.toTransaction(dtoIn)).thenReturn(transaction);
        when(clientBlackList.isClientBlacklisted("2")).thenReturn(false);
        when(transactionRepository.countByAccountAndStatus(account, TransactionStatus.REJECTED)).thenReturn(2);
        when(transactionMapper.toTransactionDto(any(Transaction.class))).thenReturn(dtoOut);

        // Act
        TransactionDto result = service.addTransaction(dtoIn);

        // Assert
        assertEquals(dtoOut, result);
        assertEquals(AccountStatus.ARRESTED, account.getStatus());
        assertEquals(TransactionStatus.REJECTED, transaction.getStatus());
        verify(accountRepository).save(account);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void addTransaction_whenWithinLimit_shouldRejectTransactionAndLeaveAccountOpen() {
        // Arrange
        Account account = new Account();
        account.setStatus(AccountStatus.OPEN);
        account.setClient(new Client());
        account.getClient().setId(3L);
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        TransactionDto dtoIn = new TransactionDto();
        TransactionDto dtoOut = new TransactionDto();

        when(transactionMapper.toTransaction(dtoIn)).thenReturn(transaction);
        when(clientBlackList.isClientBlacklisted("3")).thenReturn(false);
        when(transactionRepository.countByAccountAndStatus(account, TransactionStatus.REJECTED)).thenReturn(1);
        when(transactionMapper.toTransactionDto(any(Transaction.class))).thenReturn(dtoOut);

        // Act
        TransactionDto result = service.addTransaction(dtoIn);

        // Assert
        assertEquals(dtoOut, result);
        assertEquals(AccountStatus.ARRESTED, account.getStatus());
        assertEquals(TransactionStatus.REJECTED, transaction.getStatus());
        verify(accountRepository).save(account);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void updateTransaction_whenExists_shouldUpdateFieldsProperly() {
        // Arrange
        Long id = 10L;
        Transaction existing = new Transaction();
        existing.setAccount(new Account());
        existing.setTransactionSum(BigDecimal.valueOf(10.0));
        existing.setTransactionTime(LocalDateTime.now());

        TransactionDto dtoIn = new TransactionDto();
        Transaction updated = new Transaction();
        updated.setAccount(null);
        updated.setTransactionSum(null);
        updated.setTransactionTime(null);
        TransactionDto dtoOut = new TransactionDto();

        when(transactionRepository.findById(id)).thenReturn(Optional.of(existing));
        when(transactionMapper.toTransaction(dtoIn)).thenReturn(updated);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));
        when(transactionMapper.toTransactionDto(any(Transaction.class))).thenReturn(dtoOut);

        // Act
        TransactionDto result = service.updateTransaction(id, dtoIn);

        // Assert
        assertEquals(dtoOut, result);
        verify(transactionRepository).save(argThat(tx ->
                tx.getId().equals(id) && tx.getAccount() == existing.getAccount()
                        && tx.getTransactionSum().equals(existing.getTransactionSum())
                        && tx.getTransactionTime().equals(existing.getTransactionTime())
        ));
    }

    @Test
    void updateTransaction_whenNotFound_shouldThrowException() {
        // Arrange
        Long id = 99L;
        when(transactionRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TransactionNotFoundException.class, () -> service.updateTransaction(id, new TransactionDto()));
    }

    @Test
    void getById_whenFound_shouldReturnDto() {
        // Arrange
        Long id = 5L;
        Transaction tx = new Transaction();
        TransactionDto dtoOut = new TransactionDto();
        when(transactionRepository.findById(id)).thenReturn(Optional.of(tx));
        when(transactionMapper.toTransactionDto(tx)).thenReturn(dtoOut);

        // Act
        TransactionDto result = service.getById(id);

        // Assert
        assertEquals(dtoOut, result);
    }

    @Test
    void getById_whenNotFound_shouldThrowException() {
        // Arrange
        when(transactionRepository.findById(6L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TransactionNotFoundException.class, () -> service.getById(6L));
    }

    @Test
    void getAll_shouldReturnAllDtos() {
        // Arrange
        Transaction tx = new Transaction();
        TransactionDto dto = new TransactionDto();
        when(transactionRepository.findAll()).thenReturn(Collections.singletonList(tx));
        when(transactionMapper.toTransactionDto(tx)).thenReturn(dto);

        // Act
        List<TransactionDto> result = service.getALl();

        // Assert
        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void deleteTransaction_whenExists_shouldDelete() {
        // Arrange
        Long id = 7L;
        when(transactionRepository.existsById(id)).thenReturn(true);

        // Act
        service.deleteTransaction(id);

        // Assert
        verify(transactionRepository).deleteById(id);
    }

    @Test
    void deleteTransaction_whenNotFound_shouldThrowException() {
        // Arrange
        Long id = 8L;
        when(transactionRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(TransactionNotFoundException.class, () -> service.deleteTransaction(id));
    }
}
