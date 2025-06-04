package com.project.BloodBank.Service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.project.BloodBank.Entities.BloodInventory;
import com.project.BloodBank.Repository.BloodInventoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BloodInventoryService {

    @Autowired
    private BloodInventoryRepository bloodInventoryRepository;

    // Fetch all blood inventory records
    public List<BloodInventory> getAllBloodInventory() {
        return bloodInventoryRepository.findAll();
    }

    public Optional<BloodInventory> getBloodQuantityByType(String bloodType) {
        return bloodInventoryRepository.findByBloodType(bloodType);
    }
    
    // Update blood inventory
    public BloodInventory updateBloodInventory(Long id, BloodInventory inventoryDetails) {
        return bloodInventoryRepository.findById(id)
                .map(inventory -> {
                    inventory.setQuantity(inventoryDetails.getQuantity()); 
                    return bloodInventoryRepository.save(inventory);
                }).orElse(null);
    }

    // Delete blood inventory
    public boolean deleteBloodInventory(Long id) {
        if (bloodInventoryRepository.existsById(id)) {
            bloodInventoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
