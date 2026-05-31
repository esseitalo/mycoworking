package com.mycoworking.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.mycoworking.app.helpers.ReserveStatus;
import com.mycoworking.app.helpers.exception.ReserveNotFoundException;
import com.mycoworking.app.helpers.exception.RoomNotFoundException;
import com.mycoworking.app.helpers.exception.TimeAlreadyBookedException;
import com.mycoworking.app.helpers.exception.TimeNotAllowedException;
import com.mycoworking.app.model.Reserve;
import com.mycoworking.app.model.Room;
import com.mycoworking.app.repository.ReserveRepository;
import com.mycoworking.app.repository.RoomRepository;
import com.mycoworking.app.dto.FreeSlot;
import com.mycoworking.app.dto.RoomAvailability;

@ExtendWith(MockitoExtension.class)
class ReserveServiceTest {

  @Mock
  private ReserveRepository reserveRepository;

  @Mock
  private RoomRepository roomRepository;

  @InjectMocks
  private ReserveService reserveService;

  @Test
  void createReserveSetsRoomAndStatus() {
    UUID roomId = UUID.randomUUID();
    Room room = buildRoom(roomId);
    Reserve reserve = buildReserve();

    when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
    when(reserveRepository.timeSlotExists(eq(roomId), any(OffsetDateTime.class), any(OffsetDateTime.class)))
        .thenReturn(false);
    when(reserveRepository.save(any(Reserve.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Reserve saved = reserveService.createReserve(reserve, roomId);

    assertSame(room, saved.getRoom());
    assertEquals(ReserveStatus.RESERVED, saved.getStatus());
    verify(reserveRepository).save(reserve);
  }

  @Test
  void createReserveWhenRoomMissingThrows() {
    UUID roomId = UUID.randomUUID();
    Reserve reserve = buildReserve();

    when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

    assertThrows(RoomNotFoundException.class, () -> reserveService.createReserve(reserve, roomId));
    verifyNoInteractions(reserveRepository);
  }

  @Test
  void createReserveWhenTimeNotAllowedThrows() {
    UUID roomId = UUID.randomUUID();
    Room room = buildRoom(roomId);
    Reserve reserve = buildReserve();
    reserve.setStartTime(dateTime("2026-05-31 07:00:00"));
    reserve.setEndTime(dateTime("2026-05-31 08:30:00"));

    when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

    assertThrows(TimeNotAllowedException.class, () -> reserveService.createReserve(reserve, roomId));
    verify(reserveRepository, never()).timeSlotExists(any(UUID.class), any(OffsetDateTime.class), any(OffsetDateTime.class));
    verify(reserveRepository, never()).save(any(Reserve.class));
  }

  @Test
  void createReserveWhenTimeAlreadyBookedThrows() {
    UUID roomId = UUID.randomUUID();
    Room room = buildRoom(roomId);
    Reserve reserve = buildReserve();

    when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
    when(reserveRepository.timeSlotExists(eq(roomId), any(OffsetDateTime.class), any(OffsetDateTime.class)))
        .thenReturn(true);

    assertThrows(TimeAlreadyBookedException.class, () -> reserveService.createReserve(reserve, roomId));
    verify(reserveRepository, never()).save(any(Reserve.class));
  }

  @Test
  void getAllReservesReturnsRepositoryResult() {
    List<Reserve> reserves = List.of(buildReserve());

    when(reserveRepository.findAll()).thenReturn(reserves);

    assertEquals(reserves, reserveService.getAllReserves());
  }

  @Test
  void getReserveByIdWhenMissingThrows() {
    UUID reserveId = UUID.randomUUID();

    when(reserveRepository.findById(reserveId)).thenReturn(Optional.empty());

    assertThrows(ReserveNotFoundException.class, () -> reserveService.getReserveById(reserveId));
  }

  @Test
  void updateReserveDelegatesToRepository() {
    Reserve reserve = buildReserve();

    when(reserveRepository.save(reserve)).thenReturn(reserve);

    assertEquals(reserve, reserveService.updateReserve(reserve));
  }

  @Test
  void deleteReserveDelegatesToRepository() {
    UUID reserveId = UUID.randomUUID();

    reserveService.deleteReserve(reserveId);

    verify(reserveRepository).deleteById(reserveId);
  }

  @Test
  void getFreeSlotsForRoomReturnsAllDayWhenNoReserves() {
    UUID roomId = UUID.randomUUID();
    Room room = buildRoom(roomId);
    LocalDate date = LocalDate.of(2026, 5, 31);
    OffsetDateTime dayStart = date.atTime(8, 0).atOffset(ZoneOffset.ofHours(-3));
    OffsetDateTime dayEnd = date.atTime(18, 0).atOffset(ZoneOffset.ofHours(-3));

    when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
    when(reserveRepository.findOverlapping(roomId, dayStart, dayEnd)).thenReturn(List.of());

    List<FreeSlot> slots = reserveService.getFreeSlotsForRoom(roomId, date);

    assertEquals(1, slots.size());
    assertEquals(dayStart, slots.get(0).getStart());
    assertEquals(dayEnd, slots.get(0).getEnd());
  }

  @Test
  void getFreeSlotsForRoomReturnsGapsBetweenReserves() {
    UUID roomId = UUID.randomUUID();
    Room room = buildRoom(roomId);
    LocalDate date = LocalDate.of(2026, 5, 31);
    OffsetDateTime dayStart = date.atTime(8, 0).atOffset(ZoneOffset.ofHours(-3));
    OffsetDateTime dayEnd = date.atTime(18, 0).atOffset(ZoneOffset.ofHours(-3));

    Reserve morning = buildReserve("2026-05-31 09:00:00", "2026-05-31 10:00:00");
    Reserve afternoon = buildReserve("2026-05-31 12:00:00", "2026-05-31 13:00:00");

    when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
    when(reserveRepository.findOverlapping(roomId, dayStart, dayEnd)).thenReturn(List.of(afternoon, morning));

    List<FreeSlot> slots = reserveService.getFreeSlotsForRoom(roomId, date);

    assertEquals(3, slots.size());
    assertEquals(dayStart, slots.get(0).getStart());
    assertEquals(dateTime("2026-05-31 09:00:00"), slots.get(0).getEnd());
    assertEquals(dateTime("2026-05-31 10:00:00"), slots.get(1).getStart());
    assertEquals(dateTime("2026-05-31 12:00:00"), slots.get(1).getEnd());
    assertEquals(dateTime("2026-05-31 13:00:00"), slots.get(2).getStart());
    assertEquals(dayEnd, slots.get(2).getEnd());
  }

  @Test
  void getFreeSlotsForRoomWhenRoomMissingThrows() {
    UUID roomId = UUID.randomUUID();
    LocalDate date = LocalDate.of(2026, 5, 31);

    when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

    assertThrows(RoomNotFoundException.class, () -> reserveService.getFreeSlotsForRoom(roomId, date));
    verify(reserveRepository, never()).findOverlapping(any(UUID.class), any(OffsetDateTime.class), any(OffsetDateTime.class));
  }

  @Test
  void getAvailableRoomsReturnsOnlyRoomsWithFreeSlots() {
    LocalDate date = LocalDate.of(2026, 5, 31);
    OffsetDateTime dayStart = date.atTime(8, 0).atOffset(ZoneOffset.ofHours(-3));
    OffsetDateTime dayEnd = date.atTime(18, 0).atOffset(ZoneOffset.ofHours(-3));

    Room availableRoom = buildRoom(UUID.randomUUID());
    Room fullRoom = buildRoom(UUID.randomUUID());

    Reserve fullDay = buildReserve("2026-05-31 08:00:00", "2026-05-31 18:00:00");

    when(roomRepository.findAll()).thenReturn(List.of(availableRoom, fullRoom));
    when(reserveRepository.findOverlapping(availableRoom.getId(), dayStart, dayEnd)).thenReturn(List.of());
    when(reserveRepository.findOverlapping(fullRoom.getId(), dayStart, dayEnd)).thenReturn(List.of(fullDay));

    List<RoomAvailability> availability = reserveService.getAvailableRooms(date);

    assertEquals(1, availability.size());
    assertEquals(availableRoom.getId(), availability.get(0).getRoomId());
    assertTrue(availability.get(0).getFreeSlots().size() > 0);
  }

  private static Reserve buildReserve() {
    return buildReserve("2026-05-31 09:00:00", "2026-05-31 10:00:00");
  }

  private static Reserve buildReserve(String start, String end) {
    Reserve reserve = new Reserve();
    reserve.setStartTime(dateTime(start));
    reserve.setEndTime(dateTime(end));
    return reserve;
  }

  private static Room buildRoom(UUID roomId) {
    Room room = new Room();
    room.setName("Sala 1");
    room.setKind("Reuniao");
    room.setDescription("Sala com tela");

    if (roomId != null) {
      ReflectionTestUtils.setField(room, "id", roomId);
    }

    return room;
  }

  private static OffsetDateTime dateTime(String value) {
    LocalDateTime local = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    return local.atOffset(ZoneOffset.ofHours(-3));
  }
}
