package ru.ukhanov.t1.java.service.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import ru.ukhanov.t1.java.service.UnblockService;

import java.util.Random;

@SpringBootTest
class UnblockServiceTest {

    @Mock
    private Random random;

    @InjectMocks
    private UnblockService unblockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRandomUnblockClientWhenConditionMet() {
        when(random.nextDouble(anyLong())).thenReturn(0.6);

        boolean result = unblockService.randomUnblockClient(123L);

        assertFalse(result);
    }

    @Test
    void testRandomUnblockClientWhenConditionNotMet() {
        when(random.nextDouble(anyLong())).thenReturn(0.8);

        boolean result = unblockService.randomUnblockClient(123L);

        assertFalse(result);
    }

    @Test
    void testRandomUnblockAccountWhenConditionMet() {
        when(random.nextDouble(anyLong())).thenReturn(0.7);

        boolean result = unblockService.randomUnblockAccount(456L);

        assertFalse(result);
    }

    @Test
    void testRandomUnblockAccountWhenConditionNotMet() {
        when(random.nextDouble(anyLong())).thenReturn(0.9);

        boolean result = unblockService.randomUnblockAccount(456L);

        assertFalse(result);
    }
}
