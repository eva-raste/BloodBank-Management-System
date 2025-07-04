package com.project.BloodBank.Service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.BloodBank.Entities.Authority;
import com.project.BloodBank.Entities.BloodDonation;
import com.project.BloodBank.Entities.BloodInventory;
import com.project.BloodBank.Entities.Donor;
import com.project.BloodBank.Repository.AuthorityRepository;
import com.project.BloodBank.Repository.BloodDonationRepository;
import com.project.BloodBank.Repository.BloodInventoryRepository;
import com.project.BloodBank.Repository.DonorRepository;
import com.project.BloodBank.Repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DonorService {

    private final DonorRepository donorRepository;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final BloodInventoryRepository bloodInventoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final BloodDonationRepository bloodDonationRepository; // Added BloodDonationRepository
    private final AIService aiService;
    public DonorService(DonorRepository donorRepository, 
            UserRepository userRepository,
            AuthorityRepository authorityRepository, 
            BloodInventoryRepository bloodInventoryRepository,
            BloodDonationRepository bloodDonationRepository, 
            PasswordEncoder passwordEncoder,
            AIService aiService) // Add this parameter
	{
	this.donorRepository = donorRepository;
	this.userRepository = userRepository;
	this.authorityRepository = authorityRepository;
	this.bloodInventoryRepository = bloodInventoryRepository;
	this.bloodDonationRepository = bloodDonationRepository;
	this.passwordEncoder = passwordEncoder;
	this.aiService = aiService; // Initialize the aiService
	}

    public String getDonationEligibilityInfo() {
        return aiService.getAnswerForQuestion("who can donate blood");
    }

    @Transactional
    public Donor registerDonor(Donor donor) {
        // Check if username already exists
        if (donorRepository.findByUsername(donor.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Encode password before saving
        donor.setPassword(passwordEncoder.encode(donor.getPassword()));

        // Save donor
        Donor savedDonor = donorRepository.save(donor);

        // Assign ROLE_DONOR authority
        Authority authority = new Authority("ROLE_DONOR", savedDonor);
        authorityRepository.save(authority);

        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority);
        savedDonor.setAuthorities(authorities);
        donorRepository.save(savedDonor);

        return savedDonor;
    }

    @Transactional
    public String donateBlood(Donor donor, boolean readyToDonate) {
        if (donor.getAge() < 18 || donor.getAge() > 65) return "Donor age must be between 18 and 65 years.";
        if (donor.getWeight() < 45) return "Donor must weigh at least 45 kg.";
        if (donor.getGender().equalsIgnoreCase("female") && donor.getHaemoglobinLevel() < 12) return "Female donors must have a hemoglobin level of at least 12 g/dL.";
        if (donor.getGender().equalsIgnoreCase("male") && donor.getHaemoglobinLevel() < 13) return "Male donors must have a hemoglobin level of at least 13 g/dL.";
        if (donor.isHasChronicDisease()) return "Donors with chronic diseases are not eligible to donate.";
        if (donor.getBloodType() == null || donor.getBloodType().isEmpty()) return "Donor blood type is required.";

        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        boolean hasRecentDonation = bloodDonationRepository.existsByDonorAndTransactionDateAfter(donor, sixMonthsAgo);
        if (hasRecentDonation) return "Donor has already donated in the last 6 months.";

        BloodDonation bloodDonation = new BloodDonation();
        bloodDonation.setDonor(donor);
        bloodDonation.setBloodType(donor.getBloodType());
        bloodDonation.setTransactionDate(LocalDateTime.now());
       
        bloodDonation.setQuantity(readyToDonate ? 1 : 0);
        bloodDonationRepository.save(bloodDonation);

        return readyToDonate ? "Blood donation recorded successfully." : "Donation attempt failed. Donor is not ready.";
    }



    //helper function
    private void updateBloodInventory(String bloodType) {
        Optional<BloodInventory> existingInventory = bloodInventoryRepository.findByBloodType(bloodType);

        if (existingInventory.isPresent()) {
        	BloodInventory inventory = existingInventory.get();
            inventory.setQuantity(inventory.getQuantity() + 1);
            bloodInventoryRepository.save(inventory);
        } else {
            BloodInventory newInventory = new BloodInventory(bloodType, 1);
            bloodInventoryRepository.save(newInventory);
        }
    }

    //Retrieve all donors
    public List<Donor> getAllDonors() {
        return donorRepository.findAll();
    }

    //Retrieve donor by ID
    public Optional<Donor> getDonorById(Long id) {
        return donorRepository.findById(id);
    }

    //Delete donor by ID
//    public void deleteDonor(Long id) {
//        donorRepository.deleteById(id);
//    }

    public Optional<Donor> getDonorByUsername(String username) {
        return donorRepository.findByUsername(username);
    }
    
    //update the donor
  
//    public Donor updateDonorDetails(Donor existingDonor, Donor donorDetails, Authentication authentication) {
//        // Security Check: Ensure user is the donor OR has admin authority
//        if (!authentication.getName().equals(existingDonor.getUsername()) && 
//            !authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
//            throw new AccessDeniedException("You are not authorized to update this donor.");
//        }
//
//        // Update only allowed fields
//        existingDonor.setAge(donorDetails.getAge());
//        existingDonor.setEmail(donorDetails.getEmail());
//
//        // Save updated donor
//        return donorRepository.save(existingDonor);
//    }

    public Donor updateDonorDetails(Donor existingDonor, Donor donorDetails, Authentication authentication) {
        // Security Check: Ensure user is the donor OR has admin authority
        if (!authentication.getName().equals(existingDonor.getUsername())) {
            throw new AccessDeniedException("You are not authorized to update this donor.");
        }

       
        // Update Allowed Fields
        existingDonor.setAge(donorDetails.getAge());
        existingDonor.setEmail(donorDetails.getEmail());
        existingDonor.setBloodType(donorDetails.getBloodType());
        existingDonor.setWeight(donorDetails.getWeight());
        existingDonor.setGender(donorDetails.getGender());
        existingDonor.setHaemoglobinLevel(donorDetails.getHaemoglobinLevel());
        existingDonor.setHasChronicDisease(donorDetails.isHasChronicDisease());

        // Save Updated Donor
        return donorRepository.save(existingDonor);
    }

    public void updateDonor(Donor donor) {
        donorRepository.save(donor);
    }


}
