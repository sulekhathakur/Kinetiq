package com.kinetiq.repository;

import com.kinetiq.entity.CheckIn;
import com.kinetiq.entity.Evidence;
import com.kinetiq.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EvidenceRepository extends JpaRepository<Evidence, UUID> {

    @Query("SELECT e FROM Evidence e WHERE e.checkIn = :checkIn")
    List<Evidence> findByCheckIn(@Param("checkIn") CheckIn checkIn);

    @Query("SELECT e FROM Evidence e WHERE e.checkIn.user = :user ORDER BY e.checkIn.checkinDate DESC")
    List<Evidence> findAllForUser(@Param("user") User user);
}