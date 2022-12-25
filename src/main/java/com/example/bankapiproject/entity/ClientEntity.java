package com.example.bankapiproject.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "clients")
public class ClientEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
//    @OneToMany(fetch = FetchType.EAGER, mappedBy = "clients")
    private long id;

    @Column(name = "money")
    private double money;


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "clientEntity")
    private Set<TransactionEntity> transactions;
}
