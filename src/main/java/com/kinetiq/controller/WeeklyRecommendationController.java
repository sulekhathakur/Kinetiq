package com.kinetiq.controller;

import com.kinetiq.entity.User;
import com.kinetiq.entity.WeeklyRecommendation;
import com.kinetiq.repository.UserRepository;
import com.kinetiq.service.WeeklyRecommendationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/recommendations")
public class WeeklyRecommendationController {

    private final WeeklyRecommendationService weeklyRecommendationService;
    private final UserRepository userRepository;

    public WeeklyRecommendationController(WeeklyRecommendationService weeklyRecommendationService, UserRepository userRepository) {
        this.weeklyRecommendationService = weeklyRecommendationService;
        this.userRepository = userRepository;
    }

    @GetMapping("/generate")
    public String generate(Authentication authentication) throws ExecutionException, InterruptedException {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CompletableFuture<WeeklyRecommendation> future = weeklyRecommendationService.generateRecommendation(user);
        WeeklyRecommendation recommendation = future.get();

        return recommendation.getContentJson();
    }
}