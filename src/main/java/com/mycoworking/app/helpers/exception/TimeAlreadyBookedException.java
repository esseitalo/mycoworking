package com.mycoworking.app.helpers.exception;

public class TimeAlreadyBookedException extends RuntimeException {
  public TimeAlreadyBookedException() {
    super("Time slot is already reserved for this room");
  }
  
}
