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
import ru.ukhanov.t1.java.dto.TransactionDto;
import ru.ukhanov.t1.java.service.transaction.TransactionService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /transaction/{id} - Found")
    void testGetTransactionByIdFound() throws Exception {
        TransactionDto dto = new TransactionDto();
        dto.setId(1L);
        Mockito.when(transactionService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/transaction/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /transaction - Found multiple")
    void testGetAllTransactions() throws Exception {
        TransactionDto dto1 = new TransactionDto();
        dto1.setId(1L);
        TransactionDto dto2 = new TransactionDto();
        dto2.setId(2L);
        List<TransactionDto> list = Arrays.asList(dto1, dto2);
        Mockito.when(transactionService.getALl()).thenReturn(list);

        mockMvc.perform(get("/transaction"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    @DisplayName("POST /transaction - Created")
    void testAddTransaction() throws Exception {
        TransactionDto input = new TransactionDto();
        TransactionDto saved = new TransactionDto();
        saved.setId(3L);
        Mockito.when(transactionService.addTransaction(any(TransactionDto.class))).thenReturn(saved);

        mockMvc.perform(post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    @DisplayName("PATCH /transaction/{id} - Updated")
    void testUpdateTransaction() throws Exception {
        TransactionDto update = new TransactionDto();
        TransactionDto updated = new TransactionDto();
        updated.setId(4L);
        Mockito.when(transactionService.updateTransaction(eq(4L), any(TransactionDto.class))).thenReturn(updated);

        mockMvc.perform(patch("/transaction/{id}", 4L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4));
    }

    @Test
    @DisplayName("DELETE /transaction/{id} - No Content")
    void testDeleteTransaction() throws Exception {
        Mockito.doNothing().when(transactionService).deleteTransaction(5L);

        mockMvc.perform(delete("/transaction/{id}", 5L))
                .andExpect(status().isNoContent());
    }
}
