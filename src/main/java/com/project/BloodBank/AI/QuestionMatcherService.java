package com.project.BloodBank.AI;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class QuestionMatcherService {

    private final BloodDonationKnowledgeBase knowledgeBase;
    private final LevenshteinDistance levenshteinDistance;
    
    @Autowired
    public QuestionMatcherService(BloodDonationKnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
        this.levenshteinDistance = new LevenshteinDistance();
    }
    
    public String findBestAnswer(String userQuestion) {
        String lowerQuestion = userQuestion.toLowerCase().trim();
        Map<String, String> faqMap = knowledgeBase.getFaqMap();
        
        // Exact match check
        if (faqMap.containsKey(lowerQuestion)) {
            return faqMap.get(lowerQuestion);
        }
        
        // Fuzzy matching
        String bestMatch = null;
        int smallestDistance = Integer.MAX_VALUE;
        final int THRESHOLD = 15; // Adjust for sensitivity
        
        for (String question : faqMap.keySet()) {
            int distance = levenshteinDistance.apply(lowerQuestion, question);
            if (distance < smallestDistance && distance <= THRESHOLD) {
                smallestDistance = distance;
                bestMatch = question;
            }
        }
        
        return bestMatch != null ? faqMap.get(bestMatch) 
               : "I'm sorry, I couldn't find information about that. Could you try rephrasing your question about blood donation? " +
                 "For example, you could ask 'Who can donate blood?' or 'What are the requirements for donating blood?'";
    }
}