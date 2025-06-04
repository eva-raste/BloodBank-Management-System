package com.project.BloodBank.Controller;

import com.project.BloodBank.Entities.BloodDonation;
import com.project.BloodBank.Entities.BloodInventory;
import com.project.BloodBank.Repository.BloodDonationRepository;
import com.project.BloodBank.Repository.BloodInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/tester")
public class TesterController {

    @Autowired
    private BloodDonationRepository bloodDonationRepository;

    @Autowired
    private BloodInventoryRepository bloodInventoryRepository;

    @PutMapping("/verify/{donationId}")
    public ResponseEntity<String> verifyDonation(@PathVariable Long donationId, @RequestParam String status) {
        Optional<BloodDonation> optionalDonation = bloodDonationRepository.findById(donationId);

        if (optionalDonation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Donation not found.");
        }

        BloodDonation donation = optionalDonation.get();

        if (donation.isTested()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This donation has already been verified.");
        }

        if (!status.equalsIgnoreCase("ACCEPTABLE") && !status.equalsIgnoreCase("NOT_ACCEPTABLE")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid status. Use ACCEPTABLE or NOT_ACCEPTABLE.");
        }

        donation.setStatus(status.toUpperCase());
        donation.setTested(true);
        bloodDonationRepository.save(donation);

        if (status.equalsIgnoreCase("ACCEPTABLE")) {
            BloodInventory inventory = bloodInventoryRepository.findByBloodType(donation.getBloodType())
                    .orElse(new BloodInventory(donation.getBloodType(), 0));
            inventory.setQuantity(inventory.getQuantity() + donation.getQuantity());
            bloodInventoryRepository.save(inventory);
        }

        return ResponseEntity.ok("Donation status updated successfully.");
    }
}
