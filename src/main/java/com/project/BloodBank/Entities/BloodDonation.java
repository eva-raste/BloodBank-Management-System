package com.project.BloodBank.Entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BloodDonation {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;

    @Column(nullable = false)
    private String bloodType;
    private int quantity;
    private LocalDateTime transactionDate;
    @Column(nullable = false)
    private String status = "PENDING";  // Values: PENDING, ACCEPTABLE, NOT_ACCEPTABLE

    @Column(nullable = false)
    private boolean isTested = false;

}
