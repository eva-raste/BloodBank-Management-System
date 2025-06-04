package com.project.BloodBank.Service;

import com.project.BloodBank.Entities.Authority;
import com.project.BloodBank.Entities.BloodInventory;
import com.project.BloodBank.Entities.Recipient;
import com.project.BloodBank.Entities.Transaction;
import com.project.BloodBank.Repository.AuthorityRepository;
import com.project.BloodBank.Repository.BloodInventoryRepository;
import com.project.BloodBank.Repository.RecipientRepository;
import com.project.BloodBank.Repository.TransactionRepository;
import com.project.BloodBank.Repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class RecipientService {

    private final RecipientRepository recipientRepository;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionRepository transactionRepository;
    private final BloodInventoryRepository bloodInventoryRepository;
    private final AIService aiService;

    public RecipientService(BloodInventoryRepository bloodInventoryRepository,
            TransactionRepository transactionRepository,
            RecipientRepository recipientRepository, 
            UserRepository userRepository,
            AuthorityRepository authorityRepository, 
            PasswordEncoder passwordEncoder,
            AIService aiService) // Add this parameter
{
this.recipientRepository = recipientRepository;
this.userRepository = userRepository;
this.authorityRepository = authorityRepository;
this.passwordEncoder = passwordEncoder;
this.bloodInventoryRepository = bloodInventoryRepository;
this.transactionRepository = transactionRepository;
this.aiService = aiService; // Initialize the aiService
}
    
    public String getDonationFrequencyInfo() {
        return aiService.getAnswerForQuestion("how often can i donate blood");
    }
 
    
    //Register Recipient
    public Recipient registerRecipient(Recipient recipient) {
        if (userRepository.findByUsername(recipient.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        recipient.setPassword(passwordEncoder.encode(recipient.getPassword()));

        Recipient savedRecipient = recipientRepository.save(recipient);

        Authority authority = new Authority("ROLE_RECIPIENT", savedRecipient);
        authorityRepository.save(authority);

        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority);
        savedRecipient.setAuthorities(authorities);
        recipientRepository.save(savedRecipient);

        return savedRecipient;
    }

    // Retrieve all recipients
    public List<Recipient> getAllRecipients() {
        return recipientRepository.findAll();
    }

    // Retrieve recipient by ID
    public Optional<Recipient> getRecipientById(Long id) {
        return recipientRepository.findById(id);
    }

    public Optional<Recipient> getRecipientByUsername(String username) {
        return recipientRepository.findByUsername(username);
    }

    
    public ResponseEntity<String> requestBlood(Authentication authentication, String requiredBloodType, int quantity) {
        try {
            requiredBloodType = requiredBloodType.trim().toUpperCase();

            // Get logged-in user
            String loggedInUsername = authentication.getName();
            Optional<Recipient> loggedInRecipientOpt = recipientRepository.findByUsername(loggedInUsername);

            if (loggedInRecipientOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access.");
            }

            Recipient loggedInRecipient = loggedInRecipientOpt.get();


            // Blood compatibility mapping
            Map<String, List<String>> bloodCompatibility = Map.of(
                "A+", List.of("A+", "A-", "O+", "O-"),
                "O+", List.of("O+", "O-"),
                "B+", List.of("B+", "B-", "O+", "O-"),
                "AB+", List.of("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"),
                "A-", List.of("A-", "O-"),
                "O-", List.of("O-"),
                "B-", List.of("B-", "O-"),
                "AB-", List.of("AB-", "A-", "B-", "O-")
            );

            List<String> compatibleBloodTypes = bloodCompatibility.getOrDefault(loggedInRecipient.getRequiredBloodType(), List.of());

            if (!compatibleBloodTypes.contains(requiredBloodType)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid blood type request. You can request for: " + compatibleBloodTypes);
            }

            // Check if requested blood type is available
            Optional<BloodInventory> requestedBloodOpt = bloodInventoryRepository.findByBloodType(requiredBloodType);
            if (requestedBloodOpt.isPresent() && requestedBloodOpt.get().getQuantity() >= quantity) {
                // Sufficient requested blood type available
                BloodInventory inventory = requestedBloodOpt.get();
                inventory.setQuantity(inventory.getQuantity() - quantity);
                bloodInventoryRepository.save(inventory);

                transactionRepository.save(new Transaction(loggedInRecipient, requiredBloodType, quantity, "SUCCESS"));
                return ResponseEntity.ok("Blood request successful! " + quantity + " units of " + requiredBloodType + " provided.");
            }

            // If the exact requested blood type is unavailable, suggest alternatives
            for (String alternative : compatibleBloodTypes) {
                Optional<BloodInventory> alternativeBloodOpt = bloodInventoryRepository.findByBloodType(alternative);
                if (alternativeBloodOpt.isPresent() && alternativeBloodOpt.get().getQuantity() >= quantity) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requested blood type is not available. However, you can request " + alternative);
                }
            }

            // No compatible blood available
            transactionRepository.save(new Transaction(loggedInRecipient, requiredBloodType, quantity, "FAILED"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requested blood type is not available, and no compatible alternatives are found.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong: " + e.getMessage());
        }
    }


    
   
  
//
//    public Optional<Recipient> getRecipientByUsername(String username) {
//        return recipientRepository.findByUsername(username);
//    }

    
    @Transactional
    public Recipient updateRecipientDetails(Recipient existingRecipient, Recipient updatedDetails) {
        existingRecipient.setReasonForRequest(updatedDetails.getReasonForRequest());
        existingRecipient.setEmail(updatedDetails.getEmail());

        return recipientRepository.save(existingRecipient);
    }

	


}