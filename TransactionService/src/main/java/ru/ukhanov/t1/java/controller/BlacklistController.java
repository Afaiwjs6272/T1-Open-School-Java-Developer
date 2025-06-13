package ru.ukhanov.t1.java.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.ukhanov.t1.java.dto.BlacklistStatus;
import ru.ukhanov.t1.java.service.BlacklistService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/blacklist")
public class BlacklistController {

    private final BlacklistService blacklistService;

    @GetMapping("/check")
    public BlacklistStatus checkClient(@RequestParam String clientId) {
        boolean isBlacklisted = blacklistService.isBlacklisted(clientId);
        return new BlacklistStatus(isBlacklisted ? "BLACKLISTED" : "OK");
    }
}
