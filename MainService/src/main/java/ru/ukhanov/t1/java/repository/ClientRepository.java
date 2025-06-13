package ru.ukhanov.t1.java.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ukhanov.t1.java.model.clinet.Client;
import ru.ukhanov.t1.java.model.clinet.enums.ClientStatus;

import java.util.List;


public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findNByStatus(ClientStatus status, Pageable pageable);

    @Modifying
    @Query("UPDATE Client c SET c.status = :status WHERE c.id = :id")
    void updateStatusById(@Param("id") Long id, @Param("status") ClientStatus status);

    @Query("SELECT COUNT(c) FROM Client c WHERE c.status = 'BLOCKED'")
    int countByStatus(ClientStatus status);
}
