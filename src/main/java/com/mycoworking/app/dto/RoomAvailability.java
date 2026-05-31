package com.mycoworking.app.dto;

import java.util.List;
import java.util.UUID;

public class RoomAvailability {

  private UUID roomId;
  private String name;
  private String kind;
  private String description;
  private List<FreeSlot> freeSlots;

  public RoomAvailability() {
  }

  public RoomAvailability(UUID roomId, String name, String kind, String description, List<FreeSlot> freeSlots) {
    this.roomId = roomId;
    this.name = name;
    this.kind = kind;
    this.description = description;
    this.freeSlots = freeSlots;
  }

  public UUID getRoomId() {
    return roomId;
  }

  public String getName() {
    return name;
  }

  public String getKind() {
    return kind;
  }

  public String getDescription() {
    return description;
  }

  public List<FreeSlot> getFreeSlots() {
    return freeSlots;
  }
}
