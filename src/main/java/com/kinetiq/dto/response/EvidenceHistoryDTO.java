package com.kinetiq.dto.response;

import com.kinetiq.enums.CheckInType;
import com.kinetiq.enums.EvidenceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
public class EvidenceHistoryDTO {

    private UUID id;
    private EvidenceType evidenceType;
    private String url;
    private boolean verified;
    private CheckInType checkInType;
    private LocalDate checkinDate;
}