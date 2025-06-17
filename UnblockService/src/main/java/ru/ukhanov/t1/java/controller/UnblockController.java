package ru.ukhanov.t1.java.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.ukhanov.t1.java.dto.UnlockRequest;
import ru.ukhanov.t1.java.service.UnblockService;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UnblockController {
    private final UnblockService unblockService;

    @PostMapping("/api/unlock/client")
    public ResponseEntity<Boolean> unblockClient(@RequestBody UnlockRequest request) {
        boolean decision = unblockService.randomUnblockClient(request.getId());
        log.info("Received decision for client - {}", decision);
        return ResponseEntity.ok(decision);
    }

    @PostMapping("/api/unlock/account")
    public ResponseEntity<Boolean> unblockAccount(@RequestBody UnlockRequest request) {
        boolean decision = unblockService.randomUnblockAccount(request.getId());
        log.info("Received decision for account - {}", decision);
        return ResponseEntity.ok(decision);
    }
}