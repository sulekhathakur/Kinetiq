package com.kinetiq.repository;

import com.kinetiq.entity.CheckIn;
import com.kinetiq.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CheckInRepository extends JpaRepository<CheckIn, UUID> {

    List<CheckIn> findByUser(User user);
}