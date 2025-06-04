package com.project.BloodBank.Controller;

import com.project.BloodBank.Service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    
    private final AIService aiService;
    
    @Autowired
    public AIController(AIService aiService) {
        this.aiService = aiService;
    }
    
    @GetMapping("/ask")
    public String askQuestion(@RequestParam String question) {
        return aiService.getAnswerForQuestion(question);
    }
    
    @PostMapping("/ask")
    public String askQuestionPost(@RequestBody String question) {
        return aiService.getAnswerForQuestion(question);
    }
}