package com.project.BloodBank.Controller;

import com.project.BloodBank.Entities.Recipient;
import com.project.BloodBank.Service.RecipientService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.ui.Model;

@RestController
@RequestMapping("/api/recipients")
public class RecipientController {

    private final RecipientService recipientService;

    public RecipientController(RecipientService recipientService) {
        this.recipientService = recipientService;
    }

    // Register a recipient
//    @PostMapping("/register")
//    public ResponseEntity<Recipient> registerRecipient(@RequestBody Recipient recipient) {
//        return ResponseEntity.ok(recipientService.registerRecipient(recipient));
//    }

//    @PostMapping("/register")
//    public ResponseEntity<String> registerRecipient(@RequestBody Recipient recipient) {
//        recipientService.registerRecipient(recipient);
//        return ResponseEntity.ok("Recipient added successfully!");
//    }

    //  Get all recipients
    @GetMapping("/all")
    public ResponseEntity<List<Recipient>> getAllRecipients() {
        return ResponseEntity.ok(recipientService.getAllRecipients());
    }
    
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getRecipientById(@PathVariable Long id, Authentication authentication) {
//        String username = authentication.getName(); 
//        Optional<Recipient> recipient = recipientService.getRecipientById(id);
//
//        if (recipient.isPresent()) {
//            Recipient foundRecipient = recipient.get();
//
//            if (authentication.getAuthorities().stream()
//                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")) ||
//                    foundRecipient.getUsername().equals(username)) {
//                return ResponseEntity.ok(foundRecipient);
//            } else {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body("You are not authorized to view this recipient's details.");
//            }
//        }
//        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body("Recipient not found.");
//    }
  
    @GetMapping("/{id}")
    public ResponseEntity<Object> getRecipientById(@PathVariable Long id) {
        Optional<Recipient> recipient = recipientService.getRecipientById(id);
        
        if (recipient.isPresent()) {
            return ResponseEntity.ok(recipient.get());  // Returns 200 OK with recipient data
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipient not found.");  // Returns 404 with message
        }
    }

//    @GetMapping("/me")
//    public ResponseEntity<?> getMyDetails(Authentication authentication) {
//        String username = authentication.getName(); // Get logged-in username
//
//        Optional<Recipient> recipient = recipientService.getRecipientByUsername(username);
//        return recipient.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body("Recipient not found."));
//    }



    @PostMapping("/request-blood")
    public ResponseEntity<String> requestBlood(@RequestBody Map<String, String> request, Authentication authentication) {
        // Extract recipient username from authentication
        String username = authentication.getName();
        
        // Fetch recipient details using username
        Optional<Recipient> recipientOptional = recipientService.getRecipientByUsername(username);
        if (recipientOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipient not found.");
        }

        Recipient recipient = recipientOptional.get();
        String bloodType = request.get("requiredBloodType");
        int quantity = Integer.parseInt(request.get("quantity"));

        return recipientService.requestBlood(authentication, bloodType, quantity);
    }

//    @GetMapping("/blood-request-form")
//    public String showBloodRequestForm(Model model, Authentication authentication) {
//        String username = authentication.getName();
//        Optional<Recipient> recipientOpt = recipientService.getRecipientByUsername(username);
//        
//        if (recipientOpt.isEmpty()) {
//            return "redirect:/recipient-registration"; // Thymeleaf route
//        }
//
//        model.addAttribute("recipient", recipientOpt.get());
//        return "blood-request-form"; // Thymeleaf template
//    }

    
    @PutMapping("/update")
    public ResponseEntity<?> updateRecipient(@RequestBody Recipient recipientDetails, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }

        String loggedInUsername = authentication.getName();
        Optional<Recipient> existingRecipient = recipientService.getRecipientByUsername(loggedInUsername);

        if (existingRecipient.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipient not found.");
        }

        Recipient updatedRecipient = recipientService.updateRecipientDetails(existingRecipient.get(), recipientDetails);
        return ResponseEntity.ok(updatedRecipient);
    }



    @GetMapping("/me")
    public ResponseEntity<?> getMyDetails(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }

        String username = authentication.getName();
        System.out.println("Authenticated User: " + username); // Debugging log

        Optional<Recipient> recipient = recipientService.getRecipientByUsername(username);

        if (recipient.isPresent()) {
            return ResponseEntity.ok(recipient.get());
        } else {
            System.out.println("Recipient not found for username: " + username); // Debugging log
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipient not found.");
        }
    }

	

}