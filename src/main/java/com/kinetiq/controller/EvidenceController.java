package com.kinetiq.controller;

import com.kinetiq.dto.request.EvidenceRequestDTO;
import com.kinetiq.dto.response.EvidenceHistoryDTO;
import com.kinetiq.dto.response.EvidenceResponseDTO;
import com.kinetiq.entity.User;
import com.kinetiq.repository.EvidenceRepository;
import com.kinetiq.repository.UserRepository;
import com.kinetiq.service.EvidenceService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/evidence")
public class EvidenceController {

    private final EvidenceService evidenceService;
    private final EvidenceRepository evidenceRepository;
    private final UserRepository userRepository;

    public EvidenceController(EvidenceService evidenceService, EvidenceRepository evidenceRepository, UserRepository userRepository) {
        this.evidenceService = evidenceService;
        this.evidenceRepository = evidenceRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public EvidenceResponseDTO submitEvidence(
            Authentication authentication,
            @Valid @RequestBody EvidenceRequestDTO request
    ) {
        String userEmail = authentication.getName();
        return evidenceService.submitEvidence(userEmail, request);
    }

    @GetMapping
    public List<EvidenceHistoryDTO> getHistory(Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return evidenceRepository.findAllForUser(user).stream()
                .map(e -> new EvidenceHistoryDTO(
                        e.getId(),
                        e.getEvidenceType(),
                        e.getUrl(),
                        e.isVerified(),
                        e.getCheckIn().getType(),
                        e.getCheckIn().getCheckinDate()
                ))
                .collect(Collectors.toList());
    }
}