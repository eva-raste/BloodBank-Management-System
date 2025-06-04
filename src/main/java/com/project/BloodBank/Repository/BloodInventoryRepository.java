package com.project.BloodBank.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.BloodBank.Entities.BloodInventory;
import java.util.Optional;

@Repository
public interface BloodInventoryRepository extends JpaRepository<BloodInventory, Long> {
	  Optional<BloodInventory> findByBloodType(String bloodType);
}
