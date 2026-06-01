package com.mycoworking.app.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mycoworking.app.dto.FreeSlot;
import com.mycoworking.app.dto.RoomAvailability;
import com.mycoworking.app.helpers.ReserveStatus;
import com.mycoworking.app.helpers.exception.ReserveNotFoundException;
import com.mycoworking.app.helpers.exception.RoomNotFoundException;
import com.mycoworking.app.helpers.exception.TimeAlreadyBookedException;
import com.mycoworking.app.helpers.exception.TimeNotAllowedException;
import com.mycoworking.app.model.Reserve;
import com.mycoworking.app.model.Room;
import com.mycoworking.app.repository.ReserveRepository;
import com.mycoworking.app.repository.RoomRepository;
import com.mycoworking.app.utils.CheckWorkHours;

@Service
public class ReserveService {

  private static final ZoneOffset DEFAULT_OFFSET = ZoneOffset.ofHours(-3);
  private static final LocalTime WORK_START = LocalTime.of(8, 0);
  private static final LocalTime WORK_END = LocalTime.of(18, 0);

  @Autowired
  private ReserveRepository reserveRepository;

  @Autowired
  private RoomRepository roomRepository;

  public Reserve createReserve(Reserve reserve, UUID id) {
    Room room = roomRepository.findById(id).orElseThrow(() -> new RoomNotFoundException(id));

    if (!CheckWorkHours.isWithinWorkHours(reserve.getStartTime(), reserve.getEndTime())) {
      throw new TimeNotAllowedException();
    }

    if (reserveRepository.timeSlotExists(id, reserve.getStartTime(), reserve.getEndTime())) {
      throw new TimeAlreadyBookedException();
    }

    reserve.setRoom(room);
    reserve.setStatus(ReserveStatus.RESERVED);
    reserve.setStartTime(normalizeOffset(reserve.getStartTime()));
    reserve.setEndTime(normalizeOffset(reserve.getEndTime()));

    return reserveRepository.save(reserve);
  }

  public Iterable<Reserve> getAllReserves() {
    List<Reserve> reserves = new ArrayList<>();
    for (Reserve reserve : reserveRepository.findAll()) {
      OffsetDateTime start = normalizeOffset(reserve.getStartTime());
      OffsetDateTime end = normalizeOffset(reserve.getEndTime());
      reserve.setStartTime(start);
      reserve.setEndTime(end);
      reserves.add(reserve);
    }
    return reserves;
  }

  public Reserve getReserveById(UUID id) {
    Reserve reserve = reserveRepository.findById(id).orElseThrow(() -> new ReserveNotFoundException(id));
    reserve.setStartTime(normalizeOffset(reserve.getStartTime()));
    reserve.setEndTime(normalizeOffset(reserve.getEndTime()));
    return reserve;
  }

  public void deleteReserve(UUID id) {
    reserveRepository.deleteById(id);
  }

  public Reserve updateReserve(Reserve reserve) {
    reserve.setStartTime(normalizeOffset(reserve.getStartTime()));
    reserve.setEndTime(normalizeOffset(reserve.getEndTime()));
    return reserveRepository.save(reserve);
  }

  public List<FreeSlot> getFreeSlotsForRoom(UUID roomId, LocalDate date) {
    roomRepository.findById(roomId).orElseThrow(() -> new RoomNotFoundException(roomId));

    OffsetDateTime dayStart = date.atTime(WORK_START).atOffset(DEFAULT_OFFSET);
    OffsetDateTime dayEnd = date.atTime(WORK_END).atOffset(DEFAULT_OFFSET);

    List<Reserve> reserves = reserveRepository.findOverlapping(roomId, dayStart, dayEnd);

    return buildFreeSlots(dayStart, dayEnd, reserves);
  }

  public List<RoomAvailability> getAvailableRooms(LocalDate date) {
    OffsetDateTime dayStart = date.atTime(WORK_START).atOffset(DEFAULT_OFFSET);
    OffsetDateTime dayEnd = date.atTime(WORK_END).atOffset(DEFAULT_OFFSET);

    List<RoomAvailability> availability = new ArrayList<>();

    for (Room room : roomRepository.findAll()) {
      List<Reserve> reserves = reserveRepository.findOverlapping(room.getId(), dayStart, dayEnd);
      List<FreeSlot> freeSlots = buildFreeSlots(dayStart, dayEnd, reserves);

      if (!freeSlots.isEmpty()) {
        availability.add(new RoomAvailability(
            room.getId(),
            room.getName(),
            room.getKind(),
            room.getDescription(),
            freeSlots
        ));
      }
    }

    return availability;
  }

  private static List<FreeSlot> buildFreeSlots(OffsetDateTime dayStart, OffsetDateTime dayEnd, List<Reserve> reserves) {
    List<Reserve> sorted = new ArrayList<>(reserves);
    sorted.sort(Comparator.comparing(reserve -> normalizeOffset(reserve.getStartTime())));

    List<FreeSlot> freeSlots = new ArrayList<>();
    OffsetDateTime cursor = dayStart;

    for (Reserve reserve : sorted) {
      OffsetDateTime start = normalizeOffset(reserve.getStartTime());
      OffsetDateTime end = normalizeOffset(reserve.getEndTime());

      if (start == null || end == null) {
        continue;
      }

      if (!end.isAfter(dayStart) || !start.isBefore(dayEnd)) {
        continue;
      }

      if (start.isBefore(dayStart)) {
        start = dayStart;
      }

      if (end.isAfter(dayEnd)) {
        end = dayEnd;
      }

      if (start.isAfter(cursor)) {
        freeSlots.add(new FreeSlot(cursor, start));
      }

      if (end.isAfter(cursor)) {
        cursor = end;
      }
    }

    if (cursor.isBefore(dayEnd)) {
      freeSlots.add(new FreeSlot(cursor, dayEnd));
    }

    return freeSlots;
  }

  private static OffsetDateTime normalizeOffset(OffsetDateTime value) {
    if (value == null) {
      return null;
    }
    return value.withOffsetSameInstant(DEFAULT_OFFSET);
  }
}
