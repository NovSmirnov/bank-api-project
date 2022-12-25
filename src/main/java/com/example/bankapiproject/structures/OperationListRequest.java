package com.example.bankapiproject.structures;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OperationListRequest {
    private Long clientId;
    private String firstDate; //2022-12-12
    private String  lastDate;
}
