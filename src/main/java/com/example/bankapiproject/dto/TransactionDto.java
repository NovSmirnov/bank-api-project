package com.example.bankapiproject.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Calendar;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDto {
    @JsonIgnore
    private Long transactionId;
    private ClientDto clientDto;
    private String operationType;
    private double operationSum;
    private Calendar transactionDateTime;
}
