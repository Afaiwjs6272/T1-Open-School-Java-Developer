package ru.ukhanov.t1.java.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.ukhanov.t1.java.dto.AccountDto;
import ru.ukhanov.t1.java.model.account.enums.AccountStatus;
import ru.ukhanov.t1.java.model.account.enums.Type;
import ru.ukhanov.t1.java.service.account.AccountService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /account/{id} - Found")
    void testGetAccountByIdFound() throws Exception {
        UUID accId = UUID.randomUUID();
        AccountDto dto = new AccountDto();
        dto.setId(1L);
        dto.setClientId(10L);
        dto.setType(Type.CREDIT);
        dto.setBalance(new BigDecimal("100.00"));
        dto.setStatus(AccountStatus.OPEN);
        dto.setAccountId(accId);
        dto.setFrozenAmount(new BigDecimal("0.00"));
        Mockito.when(accountService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/account/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.client_id").value(10))
                .andExpect(jsonPath("$.type").value("CREDIT"))
                .andExpect(jsonPath("$.balance").value(100.00))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.account_id").value(accId.toString()))
                .andExpect(jsonPath("$.frozen_amount").value(0.00));
    }

    @Test
    @DisplayName("GET /account - Found multiple")
    void testGetAllAccounts() throws Exception {
        AccountDto dto1 = new AccountDto();
        dto1.setId(1L);
        dto1.setClientId(10L);
        dto1.setType(Type.DEBIT);
        dto1.setBalance(new BigDecimal("50.00"));
        dto1.setStatus(AccountStatus.OPEN);
        dto1.setAccountId(UUID.randomUUID());
        dto1.setFrozenAmount(new BigDecimal("5.00"));

        AccountDto dto2 = new AccountDto();
        dto2.setId(2L);
        dto2.setClientId(20L);
        dto2.setType(Type.DEBIT);
        dto2.setBalance(new BigDecimal("200.00"));
        dto2.setStatus(AccountStatus.OPEN);
        dto2.setAccountId(UUID.randomUUID());
        dto2.setFrozenAmount(new BigDecimal("0.00"));

        List<AccountDto> list = Arrays.asList(dto1, dto2);
        Mockito.when(accountService.getALl()).thenReturn(list);

        mockMvc.perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    @DisplayName("POST /account - Created")
    void testAddAccount() throws Exception {
        UUID accId = UUID.randomUUID();
        AccountDto input = new AccountDto();
        input.setClientId(30L);
        input.setType(Type.CREDIT);
        input.setBalance(new BigDecimal("75.00"));
        input.setStatus(AccountStatus.OPEN);
        input.setFrozenAmount(new BigDecimal("10.00"));

        AccountDto saved = new AccountDto();
        saved.setId(3L);
        saved.setClientId(30L);
        saved.setType(Type.CREDIT);
        saved.setBalance(new BigDecimal("75.00"));
        saved.setStatus(AccountStatus.OPEN);
        saved.setAccountId(accId);
        saved.setFrozenAmount(new BigDecimal("10.00"));

        Mockito.when(accountService.addAccount(any(AccountDto.class))).thenReturn(saved);

        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.account_id").value(accId.toString()))
                .andExpect(jsonPath("$.client_id").value(30));
    }

    @Test
    @DisplayName("PATCH /account/{id} - Updated")
    void testUpdateAccount() throws Exception {
        UUID accId = UUID.randomUUID();
        AccountDto update = new AccountDto();
        update.setClientId(40L);
        update.setType(Type.CREDIT);
        update.setBalance(new BigDecimal("120.00"));
        update.setStatus(AccountStatus.OPEN);
        update.setFrozenAmount(new BigDecimal("0.00"));

        AccountDto updated = new AccountDto();
        updated.setId(4L);
        updated.setClientId(40L);
        updated.setType(Type.DEBIT);
        updated.setBalance(new BigDecimal("120.00"));
        updated.setStatus(AccountStatus.OPEN);
        updated.setAccountId(accId);
        updated.setFrozenAmount(new BigDecimal("0.00"));

        Mockito.when(accountService.updateAccount(eq(4L), any(AccountDto.class))).thenReturn(updated);

        mockMvc.perform(patch("/account/{id}", 4L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.account_id").value(accId.toString()))
                .andExpect(jsonPath("$.client_id").value(40));
    }

    @Test
    @DisplayName("DELETE /account/{id} - No Content")
    void testDeleteAccount() throws Exception {
        Mockito.doNothing().when(accountService).deleteAccount(5L);

        mockMvc.perform(delete("/account/{id}", 5L))
                .andExpect(status().isNoContent());
    }
}

