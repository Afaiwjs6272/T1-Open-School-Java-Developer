package ru.ukhanov.t1.java.service.transaction;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.ukhanov.t1.java.util.JwtUtil;

@ExtendWith(MockitoExtension.class)
class ClientBlackListTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig().dynamicPort())
            .build();

    @Mock
    private JwtUtil jwtUtil;

    private ClientBlackList clientBlackList;

    @BeforeEach
    void setUp() {
        clientBlackList = new ClientBlackList();
        String baseUrl = "http://localhost:" + wireMock.getPort();
        ReflectionTestUtils.setField(clientBlackList, "blacklistServiceUrl", baseUrl);
        ReflectionTestUtils.setField(clientBlackList, "jwtUtil", jwtUtil);
    }

    @Test
    void whenServiceReturnsBlacklisted_thenIsClientBlacklistedReturnsTrue() {
        String clientId = "client123";
        String fakeToken = "fake-jwt-token";

        when(jwtUtil.generateToken("MainService")).thenReturn(fakeToken);

        wireMock.stubFor(get(urlPathEqualTo("/api/blacklist/check"))
                .withQueryParam("clientId", equalTo(clientId))
                .withHeader("Authorization", equalTo("Bearer " + fakeToken))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"BLACKLISTED\"}")));

        boolean result = clientBlackList.isClientBlacklisted(clientId);
        assertTrue(result, "Ожидаем, что клиент будет в чёрном списке");
    }

    @Test
    void whenServiceReturnsNotBlacklisted_thenIsClientBlacklistedReturnsFalse() {
        String clientId = "client456";
        String fakeToken = "fake-jwt-token-2";

        when(jwtUtil.generateToken("MainService")).thenReturn(fakeToken);

        wireMock.stubFor(get(urlPathEqualTo("/api/blacklist/check"))
                .withQueryParam("clientId", equalTo(clientId))
                .withHeader("Authorization", equalTo("Bearer " + fakeToken))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"NOT_BLACKLISTED\"}")));

        boolean result = clientBlackList.isClientBlacklisted(clientId);
        assertFalse(result, "Ожидаем, что клиент не будет в чёрном списке");
    }
}
