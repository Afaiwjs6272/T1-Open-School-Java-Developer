package ru.ukhanov.t1.java.service.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.ukhanov.t1.java.dto.BlacklistStatusResponse;
import ru.ukhanov.t1.java.util.JwtUtil;

@RequiredArgsConstructor
@Service
public class ClientBlackList {

    @Value("${blacklist.service.url}")
    private String blacklistServiceUrl;

    private JwtUtil jwtUtil;

    public boolean isClientBlacklisted(String clientId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = blacklistServiceUrl + "/api/blacklist/check?clientId=" + clientId;
        HttpHeaders headers = new HttpHeaders();
        String token = jwtUtil.generateToken("MainService");
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<BlacklistStatusResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, BlacklistStatusResponse.class
        );
        return "BLACKLISTED".equals(response.getBody().status());
    }
}
