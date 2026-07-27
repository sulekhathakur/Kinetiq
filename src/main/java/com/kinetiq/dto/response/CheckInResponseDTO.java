package com.kinetiq.dto.response;

import com.kinetiq.enums.CheckInType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
public class CheckInResponseDTO {

    private UUID id;
    private CheckInType type;
    private String description;
    private LocalDate checkinDate;
}