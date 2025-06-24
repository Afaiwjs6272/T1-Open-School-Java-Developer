package ru.ukhanov.t1.java.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ukhanov.t1.java.model.clinet.enums.ClientStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientDto {
    private Long id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("middle_name")
    private String middleName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("client_id")
    private Long clientID;

    @JsonProperty("status")
    private ClientStatus status;
}
