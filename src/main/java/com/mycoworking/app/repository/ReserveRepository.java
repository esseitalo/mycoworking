package com.mycoworking.app.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mycoworking.app.model.Reserve;

public interface ReserveRepository extends JpaRepository<Reserve, UUID> {

  @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reserve r WHERE r.room.id = :roomId AND ((r.startTime < :endTime AND r.endTime > :startTime))")
  Boolean timeSlotExists(UUID roomId, OffsetDateTime startTime, OffsetDateTime endTime);

  @Query("SELECT r FROM Reserve r WHERE r.room.id = :roomId AND ((r.startTime < :endTime AND r.endTime > :startTime))")
  List<Reserve> findOverlapping(UUID roomId, OffsetDateTime startTime, OffsetDateTime endTime);
}
