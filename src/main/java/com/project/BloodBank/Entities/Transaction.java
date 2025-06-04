package com.project.BloodBank.Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private Recipient recipient;

    @Column(nullable = false)
    private String bloodType;
    private int quantity;
    private LocalDateTime transactionDate;
    private String status; 

    public Transaction(Recipient recipient, String bloodType, int quantity, String status) {
        this.recipient = recipient;
        this.bloodType = bloodType;
        this.quantity = quantity;
        this.transactionDate = LocalDateTime.now();
        this.status = status;
    }
}
