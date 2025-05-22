package ru.ukhanov.t1.java.service.account;

import ru.ukhanov.t1.java.dto.AccountDto;
import java.util.List;

public interface AccountService {
    AccountDto addAccount(AccountDto accountDto);

    AccountDto updateAccount(Long id, AccountDto accountDto);

    AccountDto getById(Long id);

    List<AccountDto> getALl();

    void deleteAccount(Long id);
}
