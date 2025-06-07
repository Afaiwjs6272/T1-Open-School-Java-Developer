package ru.ukhanov.t1.java.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ukhanov.t1.java.dto.TransactionDto;
import ru.ukhanov.t1.java.service.transaction.TransactionService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable Long id) {
        log.info("GET /transaction/{} called", id);
        TransactionDto dto = transactionService.getById(id);
        log.debug("Found transaction: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<TransactionDto>> getAllTransactions() {
        log.info("GET /transaction called");
        List<TransactionDto> list = transactionService.getALl();
        log.debug("Returning {} transactions", list.size());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<TransactionDto> addTransaction(@RequestBody TransactionDto transactionDto) {
        log.info("POST /transaction called");
        TransactionDto saved = transactionService.addTransaction(transactionDto);
        log.info("Transaction created with id={}", saved.getId());
        return ResponseEntity
                .status(201)
                .body(saved);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TransactionDto> updateTransaction(
            @PathVariable Long id,
            @RequestBody TransactionDto transactionDto) {
        log.info("PATCH /transaction/{} called", id);
        TransactionDto updated = transactionService.updateTransaction(id, transactionDto);
        log.info("Transaction with id={} updated", id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        log.info("DELETE /transaction/{} called", id);
        transactionService.deleteTransaction(id);
        log.info("Transaction with id={} deleted", id);
        return ResponseEntity.noContent().build();
    }
}
