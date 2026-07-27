package com.kinetiq.dto.request;

import com.kinetiq.enums.CheckInType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class CheckInRequestDTO {

    @NotNull(message = "Check-in type is required")
    private CheckInType type;

    private String description;

    @NotNull(message = "Check-in date is required")
    private LocalDate checkinDate;
}