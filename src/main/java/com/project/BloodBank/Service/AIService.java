package com.project.BloodBank.Service;

import com.project.BloodBank.AI.QuestionMatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AIService {
    
    private final QuestionMatcherService questionMatcher;
    
    @Autowired
    public AIService(QuestionMatcherService questionMatcher) {
        this.questionMatcher = questionMatcher;
    }
    
    public String getAnswerForQuestion(String question) {
        String answer = questionMatcher.findBestAnswer(question);
        
        // Add friendly wrapping
        if (!answer.startsWith("I'm sorry")) {
            answer = "Thank you for your question about blood donation! Here's what I found:\n\n" + 
                    answer + 
                    "\n\nIf you have any more questions, I'd be happy to help!";
        }
        
        return answer;
    }
}