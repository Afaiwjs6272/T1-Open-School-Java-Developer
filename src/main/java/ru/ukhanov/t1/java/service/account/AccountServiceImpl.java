package ru.ukhanov.t1.java.service.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ukhanov.t1.java.aop.annotation.Cached;
import ru.ukhanov.t1.java.aop.annotation.LogDataSourceError;
import ru.ukhanov.t1.java.aop.annotation.Metric;
import ru.ukhanov.t1.java.dto.AccountDto;
import ru.ukhanov.t1.java.exception.AccountNotFoundException;
import ru.ukhanov.t1.java.mapper.AccountMapper;
import ru.ukhanov.t1.java.model.account.Account;
import ru.ukhanov.t1.java.repository.AccountRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @LogDataSourceError
    @Metric
    @Override
    public AccountDto addAccount(AccountDto accountDto) {
        log.info("Adding new account: {}", accountDto);
        Account account = accountMapper.toAccount(accountDto);
        Account savedAccount = accountRepository.save(account);
        AccountDto result = accountMapper.toAccountDto(savedAccount);
        log.info("Account saved with id={}", savedAccount.getId());
        return result;
    }

    @LogDataSourceError
    @Metric
    @Override
    public AccountDto updateAccount(Long id, AccountDto accountDto) {
        log.info("Updating account id={}", id);
        Account existing = accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Account not found with id={}", id);
                    return new AccountNotFoundException("Account not found with id = " + id);
                });

        Account updated = accountMapper.toAccount(accountDto);
        updated.setId(id);

        if (updated.getClient() == null) {
            updated.setClient(existing.getClient());
        }
        if (updated.getType() == null) {
            updated.setType(existing.getType());
        }
        if (updated.getBalance() == null) {
            updated.setBalance(existing.getBalance());
        }

        Account saved = accountRepository.save(updated);
        AccountDto result = accountMapper.toAccountDto(saved);
        log.info("Account id={} updated", id);
        return result;
    }

    @LogDataSourceError
    @Metric
    @Cached
    @Override
    public AccountDto getById(Long id) {
        log.debug("Receiving account by id={}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Account not found with id={}", id);
                    return new AccountNotFoundException("Account not found with id = " + id);
                });
        AccountDto result = accountMapper.toAccountDto(account);
        log.debug("Found account: {}", result);
        return result;
    }

    @LogDataSourceError
    @Metric
    @Override
    public List<AccountDto> getALl() {
        List<AccountDto> list = accountRepository.findAll().stream()
                .map(accountMapper::toAccountDto)
                .toList();
        log.info("Receive {} accounts", list.size());
        return list;
    }

    @LogDataSourceError
    @Metric
    @Override
    public void deleteAccount(Long id) {
        log.info("Deleting account with id={}", id);
        if (!accountRepository.existsById(id)) {
            log.warn("Account with id={} not found", id);
            throw new AccountNotFoundException("Account not found with id = " + id);
        }
        accountRepository.deleteById(id);
        log.info("Account id={} deleted", id);
    }
}
