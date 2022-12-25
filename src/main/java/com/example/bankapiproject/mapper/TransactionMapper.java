package com.example.bankapiproject.mapper;

import com.example.bankapiproject.dto.TransactionDto;
import com.example.bankapiproject.entity.TransactionEntity;

import java.util.ArrayList;
import java.util.List;

public class TransactionMapper {

    public static TransactionDto toDto(TransactionEntity transactionEntity) {
        return TransactionDto.builder()
                .transactionId(transactionEntity.getTransactionId())
                .clientDto(ClientMapper.toDto(transactionEntity.getClientEntity()))
                .operationType(transactionEntity.getOperationType())
                .operationSum(transactionEntity.getOperationSum())
                .transactionDateTime(transactionEntity.getTransactionDateTime())
                .build();
    }

    public static List<TransactionDto> toDtoList(List<TransactionEntity> transactionEntities) {
        List<TransactionDto> transactionDtos = new ArrayList<>();
        for (TransactionEntity transactionEntity : transactionEntities) {
            transactionDtos.add(toDto(transactionEntity));
        }
        return transactionDtos;
    }

    public static TransactionEntity toEntity(TransactionDto transactionDto) {
        return TransactionEntity.builder()
                .transactionId(transactionDto.getTransactionId())
                .clientEntity(ClientMapper.toEntity(transactionDto.getClientDto()))
                .operationType(transactionDto.getOperationType())
                .operationSum(transactionDto.getOperationSum())
                .transactionDateTime(transactionDto.getTransactionDateTime())
                .build();
    }


}
