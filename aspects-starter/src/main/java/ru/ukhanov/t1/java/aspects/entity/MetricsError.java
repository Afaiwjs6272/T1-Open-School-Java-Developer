package ru.ukhanov.t1.java.aspects.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "metrics_error")
public class MetricsError {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kafka_topic")
    private String kafkaTopic;

    @Column(name = "time_executed")
    private long time_executed;

    @Column(name = "method_signature")
    private String methodSignature;

    @Column(name = "message")
    private String message;
}
