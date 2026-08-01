package com.kinetiq.repository;

import com.kinetiq.entity.MomentumSnapshot;
import com.kinetiq.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface MomentumSnapshotRepository extends JpaRepository<MomentumSnapshot, UUID> {

    Optional<MomentumSnapshot> findTopByUserOrderBySnapshotDateDesc(User user);

    Optional<MomentumSnapshot> findByUserAndSnapshotDate(User user, LocalDate snapshotDate);
}