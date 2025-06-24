package ru.ukhanov.t1.java.service.unblock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.ukhanov.t1.java.dto.AccountDto;
import ru.ukhanov.t1.java.dto.ClientDto;
import ru.ukhanov.t1.java.dto.UnlockRequest;
import ru.ukhanov.t1.java.exception.AccountNotFoundException;
import ru.ukhanov.t1.java.exception.ClientNotFoundException;
import ru.ukhanov.t1.java.mapper.AccountMapper;
import ru.ukhanov.t1.java.mapper.ClientMapper;
import ru.ukhanov.t1.java.model.account.enums.AccountStatus;
import ru.ukhanov.t1.java.model.clinet.enums.ClientStatus;
import ru.ukhanov.t1.java.repository.AccountRepository;
import ru.ukhanov.t1.java.repository.ClientRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnblockService {
    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final ClientMapper clientMapper;
    private final RestTemplate restTemplate;

    @Value("${client.record-limit}")
    private int numberOfClients;
    @Value("${account.record-limit}")
    private int numberOfAccounts;
    @Value("${unblock.service.url}")
    private String url;

    @Scheduled(fixedRateString = "${service.extract-period}")
    public void extractSeveralClientsFromDataBase() {
        List<ClientDto> clientsToUnlock = getClients(numberOfClients);
        log.info("Received - {} clients", numberOfClients);
        for (ClientDto clientDto : clientsToUnlock) {
            UnlockRequest request = new UnlockRequest(clientDto.getId(), true);
            try {
                ResponseEntity<Boolean> response = restTemplate.postForEntity(
                        url + "/api/unlock/client",
                        request,
                        Boolean.class
                );

                if (response.getStatusCode().is2xxSuccessful() && Boolean.TRUE.equals(response.getBody())) {
                    clientRepository.updateStatusById(clientDto.getId(), ClientStatus.OKAY);
                    log.info("Client {} successfully unblocked", clientDto.getId());
                }
            } catch (Exception ex) {
                log.error("Error unlocking client with id - {}", clientDto.getId());
                throw new ClientNotFoundException("Cannot find client with id = " + clientDto.getId());
            }
        }
    }

    @Scheduled(fixedRateString = "${service.extract-period}")
    public void extractSeveralAccountsFromDataBase() {
        List<AccountDto> accountsToUnlock = getAccounts(numberOfAccounts);
        log.info("Received - {} accounts", accountsToUnlock);
        for (AccountDto accountDto : accountsToUnlock) {
            UnlockRequest request = new UnlockRequest(accountDto.getId(), false);
            try {
                ResponseEntity<Boolean> response = restTemplate.postForEntity(
                        url + "/api/unlock/account",
                        request,
                        Boolean.class
                );

                if (response.getStatusCode().is2xxSuccessful() && Boolean.TRUE.equals(response.getBody())) {
                    accountRepository.updateStatusById(accountDto.getId(), AccountStatus.OPEN);
                    log.info("Account {} successfully unarrested", accountDto.getId());
                }
            } catch (Exception ex) {
                log.error("Error unlocking account with id - {}", accountDto.getId());
                throw new AccountNotFoundException("Cannot find account with id = " + accountDto.getId());
            }
        }
    }

    private List<ClientDto> getClients(int number) {
        return clientRepository.findNByStatus(ClientStatus.BLOCKED,
                PageRequest.of(0, number))
                .stream()
                .map(clientMapper::toClientDto)
                .collect(Collectors.toList());
    }

    private List<AccountDto> getAccounts(int number) {
        return accountRepository.findMByStatus(AccountStatus.ARRESTED,
                PageRequest.of(0, number))
                .stream()
                .map(accountMapper::toAccountDto)
                .collect(Collectors.toList());
    }
}
