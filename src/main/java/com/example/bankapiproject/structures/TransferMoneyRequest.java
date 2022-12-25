package com.example.bankapiproject.structures;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferMoneyRequest {
    private Long firstClientId;
    private Long secondClientId;
    private double money;
}
