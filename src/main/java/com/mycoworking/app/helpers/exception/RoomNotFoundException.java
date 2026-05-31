package com.mycoworking.app.helpers.exception;

import java.util.UUID;

public class RoomNotFoundException extends RuntimeException {
  public RoomNotFoundException(UUID id) {
    super("Room not found with ID: " + id);
  }
}
