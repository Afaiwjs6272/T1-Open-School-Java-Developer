package ru.ukhanov.t1.java.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnlockRequest {
    private Long id;
    private boolean isClient;
}
