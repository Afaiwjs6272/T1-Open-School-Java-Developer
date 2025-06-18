package ru.ukhanov.t1.java.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ukhanov.t1.java.model.timeLimitExceedLog.TimeLimitExceedLog;


public interface TimeLimitExceedLogRepository extends JpaRepository<TimeLimitExceedLog, Long> {
}
