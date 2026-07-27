package com.kinetiq.service;

import com.kinetiq.dto.request.CheckInRequestDTO;
import com.kinetiq.dto.response.CheckInResponseDTO;
import com.kinetiq.entity.CheckIn;
import com.kinetiq.entity.User;
import com.kinetiq.repository.CheckInRepository;
import com.kinetiq.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final UserRepository userRepository;

    public CheckInService(CheckInRepository checkInRepository, UserRepository userRepository) {
        this.checkInRepository = checkInRepository;
        this.userRepository = userRepository;
    }

    public CheckInResponseDTO submitCheckIn(String userEmail, CheckInRequestDTO request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CheckIn checkIn = new CheckIn();
        checkIn.setUser(user);
        checkIn.setType(request.getType());
        checkIn.setDescription(request.getDescription());
        checkIn.setCheckinDate(request.getCheckinDate());

        checkInRepository.save(checkIn);

        return new CheckInResponseDTO(
                checkIn.getId(),
                checkIn.getType(),
                checkIn.getDescription(),
                checkIn.getCheckinDate()
        );
    }
}