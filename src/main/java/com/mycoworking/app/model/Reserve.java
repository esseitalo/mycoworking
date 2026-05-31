package com.mycoworking.app.model;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mycoworking.app.helpers.ReserveStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "reserves")
public class Reserve {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private UUID id;

  @NotNull
  @JoinColumn(name = "room", nullable = false)
  @OneToOne(fetch = FetchType.LAZY)
  private Room room;

  @NotNull
  @Column(name = "start_time", nullable = false)
  @Schema(example = "31/05/2026 14:30")

  private OffsetDateTime startTime;

  @NotNull
  @Column(name = "end_time", nullable = false)
  @Schema(example = "31/05/2026 16:30")

  private OffsetDateTime endTime;

  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Enumerated
  @Column(name = "status", nullable = true)
  private ReserveStatus status;

  public UUID getId() {
    return id;
  }

  public Room getRoom() {
    return room;
  }

  public void setRoom(Room room) {
    this.room = room;
  }

  public OffsetDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(OffsetDateTime startTime) {
    this.startTime = normalizeOffset(startTime);
  }

  public OffsetDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(OffsetDateTime endTime) {
    this.endTime = normalizeOffset(endTime);
  }

  public ReserveStatus getStatus() {
    return status;
  }

  public void setStatus(ReserveStatus status) {
    this.status = status;
  }

  private static OffsetDateTime normalizeOffset(OffsetDateTime value) {
    if (value == null) {
      return null;
    }
    return value.withOffsetSameInstant(ZoneOffset.ofHours(-3));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Reserve other = (Reserve) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }
}
