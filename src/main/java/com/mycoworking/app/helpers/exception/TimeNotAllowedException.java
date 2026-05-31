package com.mycoworking.app.helpers.exception;

public class TimeNotAllowedException extends RuntimeException {
  public TimeNotAllowedException() {
    super("Reserve time must be within work hours (8 AM to 6 PM)");
  }
  
}
