package com.mycoworking.app.dto;

import java.time.OffsetDateTime;

public class FreeSlot {

  private OffsetDateTime start;
  private OffsetDateTime end;

  public FreeSlot() {
  }

  public FreeSlot(OffsetDateTime start, OffsetDateTime end) {
    this.start = start;
    this.end = end;
  }

  public OffsetDateTime getStart() {
    return start;
  }

  public OffsetDateTime getEnd() {
    return end;
  }
}
