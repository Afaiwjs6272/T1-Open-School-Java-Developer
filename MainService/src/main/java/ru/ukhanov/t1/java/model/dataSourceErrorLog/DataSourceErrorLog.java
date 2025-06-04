package ru.ukhanov.t1.java.model.dataSourceErrorLog;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "data_source_error_log")
public class DataSourceErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "stack_trace", nullable = false)
    private String stackTrace;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "method_signature", nullable = false)
    private String methodSignature;
}