package ru.ukhanov.t1.java.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ukhanov.t1.java.model.MetricsError.MetricsError;

public interface MetricsErrorRepository extends JpaRepository<MetricsError, Long> {
}
