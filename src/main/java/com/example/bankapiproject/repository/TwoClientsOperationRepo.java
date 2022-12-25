package com.example.bankapiproject.repository;

import com.example.bankapiproject.entity.TwoClientsOperationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TwoClientsOperationRepo extends  JpaRepository<TwoClientsOperationEntity, Long> {

}
