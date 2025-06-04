package com.project.BloodBank.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import com.project.BloodBank.Entities.BloodInventory;
import com.project.BloodBank.Service.BloodInventoryService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/blood-inventory")
public class BloodInventoryController {

    @Autowired
    private BloodInventoryService bloodInventoryService;

    //Get all blood inventory records
    @GetMapping("/all")
    public ResponseEntity<List<BloodInventory>> getAllBloodInventory() {
        return ResponseEntity.ok(bloodInventoryService.getAllBloodInventory());
    }

    @GetMapping("/{bloodType}")
    public ResponseEntity<?> getBloodQuantity(@PathVariable String bloodType) {
        // Ensure blood type is properly formatted
        bloodType = bloodType.trim().toUpperCase();

        // Fetch blood quantity from the service
        Optional<BloodInventory> bloodInventory = bloodInventoryService.getBloodQuantityByType(bloodType);

        // Check if blood inventory is found
        if (bloodInventory.isPresent()) { // ✅ Correct way to check Optional
            return ResponseEntity.ok(bloodInventory.get()); // ✅ Get the actual value
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Blood group not found.");
        }
    }


    // Update blood inventory (Only Admins)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBloodInventory(@PathVariable Long id, @RequestBody BloodInventory inventoryDetails) {
        BloodInventory updatedInventory = bloodInventoryService.updateBloodInventory(id, inventoryDetails);
        if (updatedInventory != null) {
            return ResponseEntity.ok(updatedInventory);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Blood inventory not found.");
    }

    // Delete blood inventory (Only Admins)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBloodInventory(@PathVariable Long id) {
        if (bloodInventoryService.deleteBloodInventory(id)) {
            return ResponseEntity.ok("Blood inventory deleted successfully.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Blood inventory not found.");
    }
}
