package com.kinetiq.controller;

import com.kinetiq.dto.response.MomentumSnapshotResponseDTO;
import com.kinetiq.entity.MomentumSnapshot;
import com.kinetiq.entity.User;
import com.kinetiq.repository.MomentumSnapshotRepository;
import com.kinetiq.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/momentum")
public class MomentumController {

    private final MomentumSnapshotRepository momentumSnapshotRepository;
    private final UserRepository userRepository;

    public MomentumController(MomentumSnapshotRepository momentumSnapshotRepository, UserRepository userRepository) {
        this.momentumSnapshotRepository = momentumSnapshotRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/latest")
    public MomentumSnapshotResponseDTO getLatestMomentum(Authentication authentication) {
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MomentumSnapshot snapshot = momentumSnapshotRepository
                .findTopByUserOrderBySnapshotDateDesc(user)
                .orElseThrow(() -> new RuntimeException("No momentum data yet"));

        return new MomentumSnapshotResponseDTO(
                snapshot.getSnapshotDate(),
                snapshot.getDailyScore(),
                snapshot.getMomentumScore(),
                snapshot.getDecayFactor()
        );
    }
}