package com.example.bankapiproject.repository;

import com.example.bankapiproject.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepo extends JpaRepository<ClientEntity, Long> {

}
