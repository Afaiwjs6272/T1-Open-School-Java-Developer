package ru.ukhanov.t1.java.mapper;

import ru.ukhanov.t1.java.dto.ClientDto;
import ru.ukhanov.t1.java.model.clinet.Client;

public class ClientMapper {
    public ClientDto toClientDto(Client client) {
        return new ClientDto(client.getId(), client.getFirstName(), client.getMiddleName(),
                client.getLastName(), client.getClientID(), client.getStatus());
    }

    public Client toClient(ClientDto clientDto) {
        return new Client(clientDto.getId(), clientDto.getFirstName(), clientDto.getMiddleName(),
                clientDto.getLastName(), clientDto.getClientID(), clientDto.getStatus());
    }
}
