package com.kinetiq.service;

import com.kinetiq.entity.CheckIn;
import com.kinetiq.enums.CheckInType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MomentumServiceTest {

    private final MomentumService momentumService = new MomentumService(null, null);

    @Test
    void calculateDailyScore_returnsZero_whenNoCheckIns() {
        float result = momentumService.calculateDailyScore(List.of());

        assertEquals(0f, result, 0.001f);
    }

    @Test
    void calculateDailyScore_returnsCorrectWeight_forSingleDsaCheckIn() {
        CheckIn dsaCheckIn = new CheckIn();
        dsaCheckIn.setType(CheckInType.DSA);

        float result = momentumService.calculateDailyScore(List.of(dsaCheckIn));

        assertEquals(3f, result, 0.001f);
    }

    @Test
    void calculateDailyScore_sumsWeights_forMultipleCheckIns() {
        CheckIn dsaCheckIn = new CheckIn();
        dsaCheckIn.setType(CheckInType.DSA);

        CheckIn projectCheckIn = new CheckIn();
        projectCheckIn.setType(CheckInType.PROJECT);

        float result = momentumService.calculateDailyScore(List.of(dsaCheckIn, projectCheckIn));

        assertEquals(8f, result, 0.001f);
    }

    @Test
    void calculateMomentum_appliesLowDecay_whenNoCheckInToday() {
        float result = momentumService.calculateMomentum(0f, 10f);

        assertEquals(6f, result, 0.001f);
    }

    @Test
    void calculateMomentum_appliesHighDecay_whenCheckInHappenedToday() {
        float result = momentumService.calculateMomentum(5f, 10f);

        assertEquals(13.5f, result, 0.001f);
    }
}