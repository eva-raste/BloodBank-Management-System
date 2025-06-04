package com.project.BloodBank.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.BloodBank.Entities.BloodInventory;
import com.project.BloodBank.Entities.Donor;

@Repository
public interface DonorRepository extends JpaRepository<Donor,Long>{
	 Optional<BloodInventory> findByBloodType(String bloodType);
	 Optional<Donor> findByUsername(String username);
}