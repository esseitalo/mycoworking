package com.mycoworking.app.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.mycoworking.app.helpers.ReserveStatus;
import com.mycoworking.app.helpers.exception.ReserveNotFoundException;
import com.mycoworking.app.helpers.exception.TimeAlreadyBookedException;
import com.mycoworking.app.helpers.exception.TimeNotAllowedException;
import com.mycoworking.app.model.Reserve;
import com.mycoworking.app.model.Room;
import com.mycoworking.app.repository.ReserveRepository;
import com.mycoworking.app.repository.RoomRepository;
import com.mycoworking.app.service.ReserveService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReserveServiceIntegrationTest {

  @Autowired
  private ReserveService reserveService;

  @Autowired
  private RoomRepository roomRepository;

  @Autowired
  private ReserveRepository reserveRepository;

  @Test
  void createReservePersistsRoomAndStatus() {
    Room room = roomRepository.save(buildRoom("Sala 1", "Reuniao"));
    Reserve reserve = buildReserve("2026-05-31 09:00:00", "2026-06-01 10:00:00");

    Reserve saved = reserveService.createReserve(reserve, room.getId());

    assertNotNull(saved.getId());
    assertEquals(ReserveStatus.RESERVED, saved.getStatus());
    assertEquals(room.getId(), saved.getRoom().getId());
  }

  @Test
  void createReserveWhenTimeAlreadyBookedThrows() {
    Room room = roomRepository.save(buildRoom("Sala 1", "Reuniao"));

    Reserve existing = buildReserve("2026-05-31 00:00:00", "2026-06-01 00:00:00");
    existing.setRoom(room);
    existing.setStatus(ReserveStatus.RESERVED);
    reserveRepository.save(existing);

    Reserve reserve = buildReserve("2026-05-31 09:00:00", "2026-06-01 10:00:00");

    assertThrows(TimeAlreadyBookedException.class, () -> reserveService.createReserve(reserve, room.getId()));
  }

  @Test
  void createReserveWhenTimeNotAllowedThrows() {
    Room room = roomRepository.save(buildRoom("Sala 1", "Reuniao"));
    Reserve reserve = buildReserve("2026-05-31 07:00:00", "2026-05-31 08:30:00");

    assertThrows(TimeNotAllowedException.class, () -> reserveService.createReserve(reserve, room.getId()));
  }

  @Test
  void getReserveByIdWhenMissingThrows() {
    UUID reserveId = UUID.randomUUID();

    assertThrows(ReserveNotFoundException.class, () -> reserveService.getReserveById(reserveId));
  }

  private static Reserve buildReserve(String start, String end) {
    Reserve reserve = new Reserve();
    reserve.setStartTime(dateTime(start));
    reserve.setEndTime(dateTime(end));
    return reserve;
  }

  private static Room buildRoom(String name, String kind) {
    Room room = new Room();
    room.setName(name);
    room.setKind(kind);
    room.setDescription("Sala com tela");
    return room;
  }

  private static OffsetDateTime dateTime(String value) {
    LocalDateTime local = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    return local.atOffset(ZoneOffset.ofHours(-3));
  }
}
