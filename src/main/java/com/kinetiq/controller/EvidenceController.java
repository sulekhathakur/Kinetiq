package com.kinetiq.controller;

import com.kinetiq.dto.request.EvidenceRequestDTO;
import com.kinetiq.dto.response.EvidenceResponseDTO;
import com.kinetiq.service.EvidenceService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/evidence")
public class EvidenceController {

    private final EvidenceService evidenceService;

    public EvidenceController(EvidenceService evidenceService) {
        this.evidenceService = evidenceService;
    }

    @PostMapping
    public EvidenceResponseDTO submitEvidence(
            Authentication authentication,
            @Valid @RequestBody EvidenceRequestDTO request
    ) {
        String userEmail = authentication.getName();
        return evidenceService.submitEvidence(userEmail, request);
    }
}