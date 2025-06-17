package ru.ukhanov.t1.java.aspects.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "time_limit_exceed_log")
public class TimeLimitExceedLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "method_signature")
    private String methodSignature;

    @Column(name = "execution_time")
    private long executionTime;
}
