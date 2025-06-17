package ru.ukhanov.t1.java.service.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.ukhanov.t1.java.controller.UnblockController;
import ru.ukhanov.t1.java.dto.UnlockRequest;
import ru.ukhanov.t1.java.service.UnblockService;

@WebMvcTest(UnblockController.class)
class UnblockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UnblockService unblockService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void whenUnblockClientReturnsTrue_thenStatusOkAndTrueBody() throws Exception {
        long clientId = 42L;
        when(unblockService.randomUnblockClient(clientId)).thenReturn(true);

        UnlockRequest req = new UnlockRequest();
        req.setId(clientId);

        mockMvc.perform(post("/api/unlock/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void whenUnblockClientReturnsFalse_thenStatusOkAndFalseBody() throws Exception {
        long clientId = 43L;
        when(unblockService.randomUnblockClient(clientId)).thenReturn(false);

        UnlockRequest req = new UnlockRequest();
        req.setId(clientId);

        mockMvc.perform(post("/api/unlock/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void whenUnblockAccountReturnsTrue_thenStatusOkAndTrueBody() throws Exception {
        long accountId = 100L;
        when(unblockService.randomUnblockAccount(accountId)).thenReturn(true);

        UnlockRequest req = new UnlockRequest();
        req.setId(accountId);

        mockMvc.perform(post("/api/unlock/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void whenUnblockAccountReturnsFalse_thenStatusOkAndFalseBody() throws Exception {
        long accountId = 101L;
        when(unblockService.randomUnblockAccount(accountId)).thenReturn(false);

        UnlockRequest req = new UnlockRequest();
        req.setId(accountId);

        mockMvc.perform(post("/api/unlock/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}
