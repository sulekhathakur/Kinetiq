package com.kinetiq.repository;

import com.kinetiq.entity.User;
import com.kinetiq.entity.WeeklyRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WeeklyRecommendationRepository extends JpaRepository<WeeklyRecommendation, UUID> {

    Optional<WeeklyRecommendation> findTopByUserOrderByWeekStartDateDesc(User user);
}