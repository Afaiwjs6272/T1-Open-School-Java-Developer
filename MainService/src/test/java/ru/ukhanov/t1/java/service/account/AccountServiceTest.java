package ru.ukhanov.t1.java.service.account;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ukhanov.t1.java.dto.AccountDto;
import ru.ukhanov.t1.java.exception.AccountNotFoundException;
import ru.ukhanov.t1.java.mapper.AccountMapper;
import ru.ukhanov.t1.java.model.account.Account;
import ru.ukhanov.t1.java.model.account.enums.Type;
import ru.ukhanov.t1.java.model.clinet.Client;
import ru.ukhanov.t1.java.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl service;

    @Test
    void addAccount_shouldSaveAndReturnDto() {
        AccountDto dtoIn = new AccountDto();
        Account account = new Account();
        account.setId(1L);
        AccountDto dtoOut = new AccountDto();

        when(accountMapper.toAccount(dtoIn)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toAccountDto(account)).thenReturn(dtoOut);

        AccountDto result = service.addAccount(dtoIn);

        assertEquals(dtoOut, result);
        verify(accountMapper).toAccount(dtoIn);
        verify(accountRepository).save(account);
        verify(accountMapper).toAccountDto(account);
    }

    @Test
    void updateAccount_whenExists_shouldUpdateFieldsProperly() {
        Long id = 10L;
        AccountDto dtoIn = new AccountDto();
        Account existing = new Account();
        existing.setClient(new Client());
        existing.setType(Type.CREDIT);
        existing.setBalance(BigDecimal.valueOf(10.0));

        Account updated = new Account();
        updated.setClient(null);
        updated.setType(null);
        updated.setBalance(null);

        Account saved = new Account();
        AccountDto dtoOut = new AccountDto();

        when(accountRepository.findById(id)).thenReturn(Optional.of(existing));
        when(accountMapper.toAccount(dtoIn)).thenReturn(updated);
        when(accountRepository.save(any(Account.class))).thenReturn(saved);
        when(accountMapper.toAccountDto(saved)).thenReturn(dtoOut);

        AccountDto result = service.updateAccount(id, dtoIn);

        assertEquals(dtoOut, result);
        verify(accountRepository).save(argThat(acc ->
                acc.getId().equals(id) && acc.getClient()==existing.getClient()
                        && acc.getType()==existing.getType() && acc.getBalance().equals(existing.getBalance())
        ));
    }

    @Test
    void updateAccount_whenNotFound_shouldThrowException() {
        Long id = 99L;
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> service.updateAccount(id, new AccountDto()));
    }

    @Test
    void getById_whenFound_shouldReturnDto() {
        Long id = 5L;
        Account account = new Account();
        AccountDto dtoOut = new AccountDto();

        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        when(accountMapper.toAccountDto(account)).thenReturn(dtoOut);

        AccountDto result = service.getById(id);

        assertEquals(dtoOut, result);
    }

    @Test
    void getById_whenNotFound_shouldThrowException() {
        Long id = 6L;
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> service.getById(id));
    }

    @Test
    void getAll_shouldReturnListOfDtos() {
        Account account = new Account();
        AccountDto dto = new AccountDto();

        when(accountRepository.findAll()).thenReturn(Collections.singletonList(account));
        when(accountMapper.toAccountDto(account)).thenReturn(dto);

        List<AccountDto> result = service.getALl();

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void deleteAccount_whenExists_shouldDelete() {
        Long id = 7L;
        when(accountRepository.existsById(id)).thenReturn(true);

        service.deleteAccount(id);

        verify(accountRepository).deleteById(id);
    }

    @Test
    void deleteAccount_whenNotFound_shouldThrowException() {
        Long id = 8L;
        when(accountRepository.existsById(id)).thenReturn(false);

        assertThrows(AccountNotFoundException.class, () -> service.deleteAccount(id));
    }
}
