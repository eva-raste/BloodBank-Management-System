package com.project.BloodBank.Controller;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.BloodBank.Entities.Donor;
import com.project.BloodBank.Entities.Recipient;
import com.project.BloodBank.Service.DonorService;
import com.project.BloodBank.Service.RecipientService;

@Controller
public class WebViewController {
	
	private final RecipientService recipientService;
	private final DonorService donorService;

	public WebViewController(RecipientService recipientService, DonorService donorService) {
	    this.recipientService = recipientService;
	    this.donorService = donorService;
	}

	 @GetMapping("/blood-request-form")
	    public String showBloodRequestForm(Model model, Authentication authentication) {
	        String username = authentication.getName();
	        Optional<Recipient> recipientOpt = recipientService.getRecipientByUsername(username);
	        
	        if (recipientOpt.isEmpty()) {
	            return "redirect:/recipient-registration"; // Thymeleaf route
	        }

	        model.addAttribute("recipient", recipientOpt.get());
	        return "blood-request-form"; // Thymeleaf template
	    }
	 
	 @GetMapping("/recipient-registration")
	 public String showRecipientRegistrationForm() {
	     return "recipient-registration"; // name of Thymeleaf template (without `.html`)
	 }
	 
	 @PostMapping("/api/recipients/register")
	 public String registerRecipient(@ModelAttribute Recipient recipient, Model model) {
	     try {
	         recipientService.registerRecipient(recipient);
	         return "redirect:/blood-request-form";
	     } catch (RuntimeException e) {
	         System.out.println("Error during registration: " + e.getMessage());
	         model.addAttribute("error", e.getMessage());
	         model.addAttribute("recipient", recipient);
	         return "recipient-registration";
	     }
	 }

	 @GetMapping("/donor-registration")
	 public String showDonorRegistrationPage() {
	     return "donor-registration"; // Your Thymeleaf page for donor registration
	 }
	 
	 @GetMapping("/donate-blood")
	 public String donateBloodRedirect(Authentication authentication) {
	     if (authentication == null || !authentication.isAuthenticated()) {
	         return "redirect:/login";
	     }

	     String username = authentication.getName();
	     Optional<Donor> donorOpt = donorService.getDonorByUsername(username);

	     return donorOpt.isPresent() ? "redirect:/are-you-ready" : "redirect:/donor-registration";
	 }
	 
	 @GetMapping("/are-you-ready")
	 public String showReadyToDonatePage() {
	     return "are-you-ready"; // This maps to templates/are-you-ready.html
	 }
	 
	 @PostMapping("/set-ready-to-donate")
	 public String setReadyToDonate(@RequestParam("ready") boolean ready, Authentication authentication, Model model) {
	     if (authentication == null || !authentication.isAuthenticated()) {
	         return "redirect:/login";
	     }

	     String username = authentication.getName();
	     Optional<Donor> donorOpt = donorService.getDonorByUsername(username);

	     if (donorOpt.isPresent()) {
	         Donor donor = donorOpt.get();
	         donor.setReadyToDonate(ready);
	         donorService.updateDonor(donor);

	         // ✅ Create blood donation record if ready
	         if (ready) {
	             String donationStatus = donorService.donateBlood(donor, true);
	             model.addAttribute("message", donationStatus); // Optionally show success/failure
	         }
	     }

	     return "redirect:/thank-you"; // or return a success page
	 }

	 @GetMapping("/thank-you")
	 public String thankYouPage() {
	     return "thank-you"; // maps to `thank-you.html` in templates
	 }



	  @GetMapping("/login")
	    public String loginPage() {
	        return "login";  // This resolves to login.html in templates/
	    }

}
