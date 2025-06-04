package com.project.BloodBank.Controller;

import com.project.BloodBank.Entities.BloodDonation;
import com.project.BloodBank.Entities.Donor;
import com.project.BloodBank.Service.DonorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/donors")
public class DonorController {

    private final DonorService donorService;

    public DonorController(DonorService donorService) {
        this.donorService = donorService;
    }

    // Donor registration (No authentication required)
    @PostMapping("/register")
    public ResponseEntity<String> registerDonor(@RequestBody Donor donor) {
        donorService.registerDonor(donor);
        return ResponseEntity.status(HttpStatus.CREATED).body("Donor added successfully");
    }

    //Get all donors (Only DONOR or ADMIN can access)
    @GetMapping("/all")
    public ResponseEntity<List<Donor>> getAllDonors() {
        List<Donor> donors = donorService.getAllDonors();
        if (donors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(donors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDonorById(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName(); 
        Optional<Donor> donor = donorService.getDonorById(id);

        if (donor.isPresent()) {
            Donor foundDonor = donor.get();

            if (authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")) ||
                    foundDonor.getUsername().equals(username)) {
                return ResponseEntity.ok(foundDonor);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You are not authorized to view this donor's details.");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Donor not found.");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyDonorDetails(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }

        String username = authentication.getName();
        System.out.println("Authenticated User: " + username); // Debugging log

        Optional<Donor> donor = donorService.getDonorByUsername(username);

        if (donor.isPresent()) {
            return ResponseEntity.ok(donor.get());
        } else {
            System.out.println("Donor not found for username: " + username); // Debugging log
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Donor not found.");
        }
    }


    // Delete donor (Only ADMIN can delete)
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<String> deleteDonor(@PathVariable Long id) {
//        Optional<Donor> donor = donorService.getDonorById(id);
//        if (donor.isPresent()) {
//            donorService.deleteDonor(id);
//            return ResponseEntity.ok("Donor deleted successfully");
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Donor not found");
//        }
//    }
    
    @PutMapping("/update")
    public ResponseEntity<?> updateDonor(@RequestBody Donor donorDetails, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }

        String loggedInUsername = authentication.getName();  
        Optional<Donor> existingDonor = donorService.getDonorByUsername(loggedInUsername);

        if (existingDonor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Donor not found.");
        }

        Donor updatedDonor = donorService.updateDonorDetails(existingDonor.get(), donorDetails, authentication);
        return ResponseEntity.ok(updatedDonor);
    }

    @PostMapping("/donate-blood")
    public ResponseEntity<String> donateBlood(
            @RequestBody Map<String, Boolean> requestBody, // Accept JSON Body
            Authentication authentication) {

        // Extract readyToDonate from request body
        boolean readyToDonate = requestBody.getOrDefault("readyToDonate", false);
//        
//        System.out.println("Ready to donate: " + readyToDonate);

        // Retrieve the logged-in donor
        String username = authentication.getName();
        Optional<Donor> existingDonor = donorService.getDonorByUsername(username);

        if (existingDonor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Donor not found.");
        }

        Donor donor = existingDonor.get();
        String response = donorService.donateBlood(donor, readyToDonate);

        return response.equals("Blood donation recorded successfully.")
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


}
