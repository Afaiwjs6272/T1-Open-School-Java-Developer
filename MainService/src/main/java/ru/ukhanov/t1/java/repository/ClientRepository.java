package ru.ukhanov.t1.java.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ukhanov.t1.java.model.clinet.Client;


public interface ClientRepository extends JpaRepository<Client, Long> {
}
