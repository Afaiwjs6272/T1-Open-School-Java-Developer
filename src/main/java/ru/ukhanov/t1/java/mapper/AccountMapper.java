package ru.ukhanov.t1.java.mapper;

import lombok.RequiredArgsConstructor;
import ru.ukhanov.t1.java.dto.AccountDto;
import ru.ukhanov.t1.java.exception.ClientNotFoundException;
import ru.ukhanov.t1.java.model.account.Account;
import ru.ukhanov.t1.java.model.clinet.Client;
import ru.ukhanov.t1.java.repository.ClientRepository;

@RequiredArgsConstructor
public class AccountMapper {
    private final ClientRepository clientRepository;

    public AccountDto toAccountDto(Account account) {
        return new AccountDto(account.getId(), account.getClient().getId(),
                account.getType(), account.getBalance());
    }

    public Account toAccount(AccountDto accountDto) {
        Client client = clientRepository.findById(accountDto.getClientId())
                .orElseThrow(() -> new ClientNotFoundException(
                        "Client not found with id=" + accountDto.getClientId()
                        ));
        return new Account(accountDto.getId(), client, accountDto.getType(),
                accountDto.getBalance());
    }
}
