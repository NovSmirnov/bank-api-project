package com.example.bankapiproject.repository;

import com.example.bankapiproject.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ClientRepo extends JpaRepository<ClientEntity, Long> {

}
