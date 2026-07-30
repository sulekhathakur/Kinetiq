package com.kinetiq.dto.request;

import com.kinetiq.enums.EvidenceType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class EvidenceRequestDTO {

    @NotNull(message = "Check-in ID is required")
    private UUID checkInId;

    @NotNull(message = "Evidence type is required")
    private EvidenceType evidenceType;

    private String url;
}