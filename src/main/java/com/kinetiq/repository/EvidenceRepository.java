package com.kinetiq.repository;

import com.kinetiq.entity.CheckIn;
import com.kinetiq.entity.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EvidenceRepository extends JpaRepository<Evidence, UUID> {

    @Query("SELECT e FROM Evidence e WHERE e.checkIn = :checkIn")
    List<Evidence> findByCheckIn(@Param("checkIn") CheckIn checkIn);
}