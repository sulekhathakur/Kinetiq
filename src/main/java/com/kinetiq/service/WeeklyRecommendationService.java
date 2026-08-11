package com.kinetiq.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinetiq.entity.CheckIn;
import com.kinetiq.entity.MomentumSnapshot;
import com.kinetiq.entity.User;
import com.kinetiq.entity.WeeklyRecommendation;
import com.kinetiq.repository.CheckInRepository;
import com.kinetiq.repository.MomentumSnapshotRepository;
import com.kinetiq.repository.WeeklyRecommendationRepository;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeeklyRecommendationService {

    private final CheckInRepository checkInRepository;
    private final MomentumSnapshotRepository momentumSnapshotRepository;
    private final WeeklyRecommendationRepository weeklyRecommendationRepository;
    private final ChatLanguageModel chatLanguageModel;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeeklyRecommendationService(
            CheckInRepository checkInRepository,
            MomentumSnapshotRepository momentumSnapshotRepository,
            WeeklyRecommendationRepository weeklyRecommendationRepository,
            ChatLanguageModel chatLanguageModel
    ) {
        this.checkInRepository = checkInRepository;
        this.momentumSnapshotRepository = momentumSnapshotRepository;
        this.weeklyRecommendationRepository = weeklyRecommendationRepository;
        this.chatLanguageModel = chatLanguageModel;
    }

    public WeeklyRecommendation generateRecommendation(User user) {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);

        List<CheckIn> recentCheckIns = checkInRepository.findByUser(user).stream()
                .filter(c -> !c.getCheckinDate().isBefore(weekAgo))
                .collect(Collectors.toList());

        float currentMomentum = momentumSnapshotRepository
                .findTopByUserOrderBySnapshotDateDesc(user)
                .map(MomentumSnapshot::getMomentumScore)
                .orElse(0f);

        String prompt = buildPrompt(recentCheckIns, currentMomentum);
        String rawResponse = chatLanguageModel.generate(prompt);
        String validatedJson = validateAndExtractJson(rawResponse);

        WeeklyRecommendation recommendation = new WeeklyRecommendation();
        recommendation.setUser(user);
        recommendation.setWeekStartDate(weekAgo);
        recommendation.setContentJson(validatedJson);

        return weeklyRecommendationRepository.save(recommendation);
    }

    private String buildPrompt(List<CheckIn> checkIns, float currentMomentum) {
        String checkInSummary = checkIns.isEmpty()
                ? "No check-ins in the last 7 days."
                : checkIns.stream()
                    .map(c -> String.format("- %s (%s): %s", c.getCheckinDate(), c.getType(), c.getDescription()))
                    .collect(Collectors.joining("\n"));

        return """
                You are a career coach analyzing a user's recent progress.

                Current momentum score: %.2f

                Check-ins from the last 7 days:
                %s

                Based on this data, respond with ONLY valid JSON in exactly this shape, no other text:
                {
                  "summary": "one sentence describing their recent pattern",
                  "focusAreas": ["area 1", "area 2"],
                  "reasoning": "one sentence explaining why these areas were chosen"
                }
                """.formatted(currentMomentum, checkInSummary);
    }

    private String validateAndExtractJson(String rawResponse) {
        String cleaned = rawResponse.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        }
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        cleaned = cleaned.trim();

        try {
            objectMapper.readTree(cleaned);
            return cleaned;
        } catch (Exception e) {
            return """
                    {
                      "summary": "Unable to generate a recommendation this week.",
                      "focusAreas": [],
                      "reasoning": "The AI response could not be parsed."
                    }
                    """;
        }
    }
}