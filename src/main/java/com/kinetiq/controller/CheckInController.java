package com.kinetiq.controller;

import com.kinetiq.dto.request.CheckInRequestDTO;
import com.kinetiq.dto.response.CheckInResponseDTO;
import com.kinetiq.service.CheckInService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkins")
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping
    public CheckInResponseDTO submitCheckIn(
            Authentication authentication,
            @Valid @RequestBody CheckInRequestDTO request
    ) {
        String userEmail = authentication.getName();
        return checkInService.submitCheckIn(userEmail, request);
    }
}