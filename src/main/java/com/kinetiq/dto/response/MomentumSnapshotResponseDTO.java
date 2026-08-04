package com.kinetiq.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@AllArgsConstructor
public class MomentumSnapshotResponseDTO {

    private LocalDate snapshotDate;
    private float dailyScore;
    private float momentumScore;
    private float decayFactor;
}