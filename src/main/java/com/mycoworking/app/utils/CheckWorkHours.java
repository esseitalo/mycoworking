package com.mycoworking.app.utils;

import java.time.OffsetDateTime;
import java.time.LocalTime;

public class CheckWorkHours {
  final static LocalTime WORK_START = LocalTime.of(8, 0);
  final static LocalTime WORK_END = LocalTime.of(18, 0);

  public static boolean isWithinWorkHours(OffsetDateTime startTime, OffsetDateTime endTime) {
    LocalTime startTimeOnly = toLocalTime(startTime);
    LocalTime endTimeOnly = toLocalTime(endTime);

    return !startTimeOnly.isBefore(WORK_START) && !endTimeOnly.isAfter(WORK_END);
  }

  private static LocalTime toLocalTime(OffsetDateTime value) {
    return value.toLocalTime();
  }
}
