package com.project.BloodBank.AI;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class BloodDonationKnowledgeBase {
    
    private final Map<String, String> faqMap;
    
    public BloodDonationKnowledgeBase() {
        this.faqMap = initializeKnowledgeBase();
    }
    
    private Map<String, String> initializeKnowledgeBase() {
        Map<String, String> knowledge = new HashMap<>();
        
        // Eligibility Questions
        knowledge.put("who can donate blood", 
            "Most people in good health can donate blood. Generally, you should be:\n" +
            "- At least 17 years old (16 with parental consent in some places)\n" +
            "- Weigh at least 110 pounds (50 kg)\n" +
            "- Have a hemoglobin level of at least 12.5 g/dL\n" +
            "- Not have donated blood in the last 8 weeks (56 days)\n" +
            "- Free from any infectious diseases\n\n" +
            "Would you like more specific information about any of these requirements?");
        
        // Add all other FAQs from previous example here
        // (Include all the knowledge base entries from my first response)
        
        return knowledge;
    }
    
    public Map<String, String> getFaqMap() {
        return faqMap;
    }
}