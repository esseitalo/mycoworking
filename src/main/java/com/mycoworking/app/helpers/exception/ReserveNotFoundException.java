package com.mycoworking.app.helpers.exception;

import java.util.UUID;

public class ReserveNotFoundException extends RuntimeException {
  public ReserveNotFoundException(UUID id) {
    super("Reserve not found with ID: " + id);
  }
}
