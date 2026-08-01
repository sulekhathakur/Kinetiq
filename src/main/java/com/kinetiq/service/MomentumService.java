package com.kinetiq.service;

import com.kinetiq.entity.CheckIn;
import com.kinetiq.entity.MomentumSnapshot;
import com.kinetiq.entity.User;
import com.kinetiq.enums.CheckInType;
import com.kinetiq.repository.CheckInRepository;
import com.kinetiq.repository.MomentumSnapshotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class MomentumService {

    private static final Map<CheckInType, Float> TYPE_WEIGHTS = Map.of(
            CheckInType.DSA, 3f,
            CheckInType.PROJECT, 5f,
            CheckInType.LEARNING, 2f
    );

    private static final float DECAY_WITH_CHECKIN = 0.85f;
    private static final float DECAY_WITHOUT_CHECKIN = 0.6f;

    private final CheckInRepository checkInRepository;
    private final MomentumSnapshotRepository momentumSnapshotRepository;

    public MomentumService(CheckInRepository checkInRepository, MomentumSnapshotRepository momentumSnapshotRepository) {
        this.checkInRepository = checkInRepository;
        this.momentumSnapshotRepository = momentumSnapshotRepository;
    }

    public float calculateDailyScore(List<CheckIn> checkIns) {
        float total = 0f;
        for (CheckIn checkIn : checkIns) {
            total += TYPE_WEIGHTS.get(checkIn.getType());
        }
        return total;
    }

    public float calculateMomentum(float dailyScore, float previousMomentum) {
        float decayFactor = dailyScore > 0 ? DECAY_WITH_CHECKIN : DECAY_WITHOUT_CHECKIN;
        return dailyScore + (previousMomentum * decayFactor);
    }

    public MomentumSnapshot recomputeForDate(User user, LocalDate date) {
        List<CheckIn> checkIns = checkInRepository.findByUserAndCheckinDate(user, date);
        float dailyScore = calculateDailyScore(checkIns);

        float previousMomentum = momentumSnapshotRepository
                .findTopByUserOrderBySnapshotDateDesc(user)
                .map(MomentumSnapshot::getMomentumScore)
                .orElse(0f);

        float decayFactor = dailyScore > 0 ? DECAY_WITH_CHECKIN : DECAY_WITHOUT_CHECKIN;
        float momentumScore = calculateMomentum(dailyScore, previousMomentum);

        MomentumSnapshot snapshot = momentumSnapshotRepository
                .findByUserAndSnapshotDate(user, date)
                .orElse(new MomentumSnapshot());

        snapshot.setUser(user);
        snapshot.setSnapshotDate(date);
        snapshot.setDailyScore(dailyScore);
        snapshot.setMomentumScore(momentumScore);
        snapshot.setDecayFactor(decayFactor);

        return momentumSnapshotRepository.save(snapshot);
    }
}