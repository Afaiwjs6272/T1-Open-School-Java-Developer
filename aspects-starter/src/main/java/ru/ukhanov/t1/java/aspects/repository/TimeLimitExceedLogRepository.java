package ru.ukhanov.t1.java.aspects.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ukhanov.t1.java.aspects.entity.TimeLimitExceedLog;


public interface TimeLimitExceedLogRepository extends JpaRepository<TimeLimitExceedLog, Long> {
}
