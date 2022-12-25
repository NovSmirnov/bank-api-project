package com.example.bankapiproject.repository;

import com.example.bankapiproject.entity.ClientEntity;
import com.example.bankapiproject.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findAllByClientEntity(ClientEntity clientEntity);
    List<TransactionEntity> findAllByClientEntityAndTransactionDateTimeAfter(ClientEntity clientEntity, Calendar transactionDateTime);
    List<TransactionEntity> findAllByClientEntityAndTransactionDateTimeBefore(ClientEntity clientEntity, Calendar transactionDateTime);
    List<TransactionEntity> findAllByClientEntityAndTransactionDateTimeBetween(ClientEntity clientEntity, Calendar transactionDateTime, Calendar transactionDateTime2);
}
