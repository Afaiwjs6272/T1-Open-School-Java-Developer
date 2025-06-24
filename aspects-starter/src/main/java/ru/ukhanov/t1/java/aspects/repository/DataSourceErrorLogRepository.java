package ru.ukhanov.t1.java.aspects.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ukhanov.t1.java.aspects.entity.DataSourceErrorLog;


@Repository
public interface DataSourceErrorLogRepository extends JpaRepository<DataSourceErrorLog, Long> {
}
