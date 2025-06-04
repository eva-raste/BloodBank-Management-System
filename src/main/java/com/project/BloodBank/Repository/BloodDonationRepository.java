package com.project.BloodBank.Repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.BloodBank.Entities.BloodDonation;
import com.project.BloodBank.Entities.Donor;

@Repository
public interface BloodDonationRepository extends JpaRepository<BloodDonation, Long> {
	boolean existsByDonorAndTransactionDateAfter(Donor donor, LocalDateTime date);
}