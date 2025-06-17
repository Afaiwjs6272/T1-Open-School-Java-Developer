package ru.ukhanov.t1.java.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ukhanov.t1.java.dto.AccountDto;
import ru.ukhanov.t1.java.service.account.AccountService;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable("id") Long id) {
        log.info("GET /account/{} called", id);
        AccountDto dto = accountService.getById(id);
        log.debug("Found account: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        log.info("GET /account called");
        List<AccountDto> list = accountService.getALl();
        log.debug("Returning {} accounts", list.size());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<AccountDto> addAccount(@RequestBody AccountDto accountDto) {
        log.info("POST /account called");
        AccountDto saved = accountService.addAccount(accountDto);
        log.info("Account created with id={}", saved.getId());
        return ResponseEntity
                .status(201)
                .body(saved);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AccountDto> updateAccount(
            @PathVariable("id") Long id,
            @RequestBody AccountDto accountDto) {
        log.info("PATCH /account/{} called", id);
        AccountDto updated = accountService.updateAccount(id, accountDto);
        log.info("Account id={} updated", id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable("id") Long id) {
        log.info("DELETE /account/{} called", id);
        accountService.deleteAccount(id);
        log.info("Account with id={} deleted", id);
        return ResponseEntity.noContent().build();
    }
}
