package com.kinetiq.service;

import com.kinetiq.dto.request.EvidenceRequestDTO;
import com.kinetiq.dto.response.EvidenceResponseDTO;
import com.kinetiq.entity.CheckIn;
import com.kinetiq.entity.Evidence;
import com.kinetiq.enums.EvidenceType;
import com.kinetiq.repository.CheckInRepository;
import com.kinetiq.repository.EvidenceRepository;
import org.springframework.stereotype.Service;

@Service
public class EvidenceService {

    private final EvidenceRepository evidenceRepository;
    private final CheckInRepository checkInRepository;
    private final EvidenceVerificationService evidenceVerificationService;

    public EvidenceService(EvidenceRepository evidenceRepository, CheckInRepository checkInRepository, EvidenceVerificationService evidenceVerificationService) {
        this.evidenceRepository = evidenceRepository;
        this.checkInRepository = checkInRepository;
        this.evidenceVerificationService = evidenceVerificationService;
    }

    public EvidenceResponseDTO submitEvidence(String userEmail, EvidenceRequestDTO request) {
        CheckIn checkIn = checkInRepository.findById(request.getCheckInId())
                .orElseThrow(() -> new RuntimeException("Check-in not found"));

        if (!checkIn.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("You do not have permission to add evidence to this check-in");
        }

        Evidence evidence = new Evidence();
        evidence.setCheckIn(checkIn);
        evidence.setEvidenceType(request.getEvidenceType());
        evidence.setUrl(request.getUrl());

        boolean verified = false;
        if (request.getEvidenceType() == EvidenceType.LINK && request.getUrl() != null) {
            verified = evidenceVerificationService.verifyGitHubCommit(request.getUrl(), checkIn.getCheckinDate());
        }
        evidence.setVerified(verified);
        if (verified) {
            evidence.setVerificationSource("GitHub API");
        }

        evidenceRepository.save(evidence);

        return new EvidenceResponseDTO(
                evidence.getId(),
                evidence.getEvidenceType(),
                evidence.getUrl(),
                evidence.isVerified()
        );
    }
}