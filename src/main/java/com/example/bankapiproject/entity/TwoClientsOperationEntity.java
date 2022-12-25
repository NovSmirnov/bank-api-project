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
@Table(name = "two_clients_operations")
@ToString
public class TwoClientsOperationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operation_id")
    private Long twoClientsOperationId;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "twoClientsOperationEntity")
    private List<TransactionEntity> clientOperations;

//    @OneToOne
//    @JoinColumn(name = "transaction_id", nullable = false, insertable = false, updatable = false)
//    private TransactionEntity secondClientOperation;


}
