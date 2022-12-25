package com.example.bankapiproject.entity;


import lombok.*;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "transactions")
@ToString
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinColumn(name = "id", nullable = false)
    private ClientEntity clientEntity;

    @Column(name = "operation_type")
    private String operationType;

    @Column(name = "poeration_sum")
    private double operationSum;

    @Column(name = "transaction_date_time")
//    @Temporal(TemporalType.TIMESTAMP)
    private Calendar transactionDateTime;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "operation_id")
    private TwoClientsOperationEntity twoClientsOperationEntity;
}
