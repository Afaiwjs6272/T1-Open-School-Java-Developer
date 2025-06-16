package ru.ukhanov.t1.java.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ukhanov.t1.java.model.clinet.Client;


@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}
