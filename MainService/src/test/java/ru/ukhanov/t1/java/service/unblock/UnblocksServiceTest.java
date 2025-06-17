package ru.ukhanov.t1.java.service.unblock;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.data.domain.PageRequest;
import ru.ukhanov.t1.java.dto.ClientDto;
import ru.ukhanov.t1.java.dto.AccountDto;
import ru.ukhanov.t1.java.exception.AccountNotFoundException;
import ru.ukhanov.t1.java.exception.ClientNotFoundException;
import ru.ukhanov.t1.java.model.clinet.enums.ClientStatus;
import ru.ukhanov.t1.java.model.account.enums.AccountStatus;
import ru.ukhanov.t1.java.model.clinet.Client;
import ru.ukhanov.t1.java.model.account.Account;
import ru.ukhanov.t1.java.repository.ClientRepository;
import ru.ukhanov.t1.java.repository.AccountRepository;
import ru.ukhanov.t1.java.mapper.ClientMapper;
import ru.ukhanov.t1.java.mapper.AccountMapper;
import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(WireMockExtension.class)
class UnblocksServiceTest {
    @RegisterExtension
    static WireMockExtension wm = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().dynamicPort())
            .build();

    private final ClientRepository clientRepository = mock(ClientRepository.class);
    private final AccountRepository accountRepository = mock(AccountRepository.class);
    private final ClientMapper clientMapper = mock(ClientMapper.class);
    private final AccountMapper accountMapper = mock(AccountMapper.class);
    private UnblockService service;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "http://localhost:" + wm.getRuntimeInfo().getHttpPort();
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(baseUrl);
        restTemplate.setUriTemplateHandler(factory);

        service = new UnblockService(
                clientRepository,
                accountRepository,
                accountMapper,
                clientMapper,
                restTemplate
        );
        ReflectionTestUtils.setField(service, "numberOfClients", 1);
        ReflectionTestUtils.setField(service, "numberOfAccounts", 1);
        ReflectionTestUtils.setField(service, "url", baseUrl);
    }

    @Test
    void whenClientUnlockEndpointReturnsTrue_thenStatusUpdated() {
        Client clientEntity = new Client();
        clientEntity.setId(100L);
        ClientDto clientDto = new ClientDto();
        clientDto.setId(100L);
        when(clientRepository.findNByStatus(eq(ClientStatus.BLOCKED), any(PageRequest.class)))
                .thenReturn(Collections.singletonList(clientEntity));
        when(clientMapper.toClientDto(clientEntity)).thenReturn(clientDto);

        wm.stubFor(post(urlEqualTo("/api/unlock/client"))
                .withRequestBody(equalToJson("{ \"id\":100, \"client\":true }"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")));

        service.extractSeveralClientsFromDataBase();

        verify(clientRepository).updateStatusById(100L, ClientStatus.OKAY);
    }

    @Test
    void whenClientUnlockEndpointReturnsFalse_thenNoUpdate() {
        Client clientEntity = new Client();
        clientEntity.setId(100L);
        ClientDto clientDto = new ClientDto();
        clientDto.setId(100L);
        when(clientRepository.findNByStatus(eq(ClientStatus.BLOCKED), any(PageRequest.class)))
                .thenReturn(Collections.singletonList(clientEntity));
        when(clientMapper.toClientDto(clientEntity)).thenReturn(clientDto);

        wm.stubFor(post(urlEqualTo("/api/unlock/client"))
                .willReturn(aResponse().withStatus(200).withBody("false")));

        service.extractSeveralClientsFromDataBase();

        verify(clientRepository, never()).updateStatusById(anyLong(), any());
    }

    @Test
    void whenClientUnlockEndpointErrors_thenExceptionThrown() {
        Client clientEntity = new Client();
        clientEntity.setId(102L);
        ClientDto clientDto = new ClientDto();
        clientDto.setId(102L);
        when(clientRepository.findNByStatus(eq(ClientStatus.BLOCKED), any(PageRequest.class)))
                .thenReturn(Collections.singletonList(clientEntity));
        when(clientMapper.toClientDto(clientEntity)).thenReturn(clientDto);

        wm.stubFor(post(urlEqualTo("/api/unlock/client"))
                .willReturn(aResponse().withStatus(500)));

        assertThrows(ClientNotFoundException.class,
                () -> service.extractSeveralClientsFromDataBase());
    }

    @Test
    void whenAccountUnlockEndpointReturnsTrue_thenStatusUpdated() {
        Account accountEntity = new Account();
        accountEntity.setId(100L);
        AccountDto accountDto = new AccountDto();
        accountDto.setId(100L);
        when(accountRepository.findMByStatus(eq(AccountStatus.ARRESTED), any(PageRequest.class)))
                .thenReturn(Collections.singletonList(accountEntity));
        when(accountMapper.toAccountDto(accountEntity)).thenReturn(accountDto);

        wm.stubFor(post(urlEqualTo("/api/unlock/account"))
                .withRequestBody(equalToJson("{ \"id\":100, \"client\":false }"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("true")));

        service.extractSeveralAccountsFromDataBase();

        verify(accountRepository).updateStatusById(200L, AccountStatus.OPEN);
    }

    @Test
    void whenAccountUnlockEndpointErrors_thenExceptionThrown() {
        Account accountEntity = new Account();
        accountEntity.setId(201L);
        AccountDto accountDto = new AccountDto();
        accountDto.setId(201L);
        when(accountRepository.findMByStatus(eq(AccountStatus.ARRESTED), any(PageRequest.class)))
                .thenReturn(Collections.singletonList(accountEntity));
        when(accountMapper.toAccountDto(accountEntity)).thenReturn(accountDto);

        wm.stubFor(post(urlEqualTo("/api/unlock/account"))
                .willReturn(aResponse().withStatus(404)));

        assertThrows(AccountNotFoundException.class,
                () -> service.extractSeveralAccountsFromDataBase());
    }
}
