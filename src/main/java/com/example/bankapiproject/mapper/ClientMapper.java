package com.example.bankapiproject.mapper;


import com.example.bankapiproject.dto.ClientDto;
import com.example.bankapiproject.entity.ClientEntity;

public class ClientMapper {

    public static ClientDto toDto (ClientEntity clientEntity) {
        return ClientDto.builder()
                .id(clientEntity.getId())
                .money(clientEntity.getMoney())
                .build();
    }

    public static ClientEntity toEntity(ClientDto clientDto) {
        return ClientEntity.builder()
                .id(clientDto.getId())
                .money(clientDto.getMoney())
                .build();
    }

}
