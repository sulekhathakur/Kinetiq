package com.kinetiq.dto.response;

import com.kinetiq.enums.EvidenceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
public class EvidenceResponseDTO {

    private UUID id;
    private EvidenceType evidenceType;
    private String url;
    private boolean verified;
}