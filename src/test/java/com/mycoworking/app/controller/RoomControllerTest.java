package com.mycoworking.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mycoworking.app.dto.FreeSlot;
import com.mycoworking.app.dto.RoomAvailability;
import com.mycoworking.app.helpers.exception.RoomNotFoundException;
import com.mycoworking.app.model.Room;
import com.mycoworking.app.service.ReserveService;
import com.mycoworking.app.service.RoomService;

@ExtendWith(MockitoExtension.class)
class RoomControllerTest {

  @Mock
  private RoomService roomService;

  @Mock
  private ReserveService reserveService;

  @InjectMocks
  private RoomController roomController;

  @Test
  void getAllReturnsRooms() {
    List<Room> rooms = List.of(buildRoom());

    when(roomService.getAllRooms()).thenReturn(rooms);

    ResponseEntity<Iterable<Room>> response = roomController.get();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(rooms, response.getBody());
  }

  @Test
  void getOneReturnsRoom() {
    UUID roomId = UUID.randomUUID();
    Room room = buildRoom();

    when(roomService.getRoomById(roomId)).thenReturn(room);

    ResponseEntity<Room> response = roomController.getOne(roomId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(room, response.getBody());
  }

  @Test
  void getOneWhenMissingThrows() {
    UUID roomId = UUID.randomUUID();

    when(roomService.getRoomById(roomId)).thenThrow(new RoomNotFoundException(roomId));

    assertThrows(RoomNotFoundException.class, () -> roomController.getOne(roomId));
  }

  @Test
  void createReturnsRoom() {
    Room request = buildRoom();
    Room saved = buildRoom();

    when(roomService.createRoom(request)).thenReturn(saved);

    ResponseEntity<Room> response = roomController.create(request);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertSame(saved, response.getBody());
  }

  @Test
  void updateReturnsRoom() {
    Room request = buildRoom();

    when(roomService.updateRoom(request)).thenReturn(request);

    ResponseEntity<Room> response = roomController.update(request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(request, response.getBody());
  }

  @Test
  void deleteReturnsNoContent() {
    UUID roomId = UUID.randomUUID();

    ResponseEntity<Void> response = roomController.delete(roomId);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(roomService).deleteRoom(roomId);
  }

  @Test
  void getAvailableRoomsReturnsAvailability() {
    LocalDate date = LocalDate.of(2026, 5, 31);
    List<RoomAvailability> availability = List.of(new RoomAvailability());

    when(reserveService.getAvailableRooms(date)).thenReturn(availability);

    ResponseEntity<List<RoomAvailability>> response = roomController.getAvailableRooms(date, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(availability, response.getBody());
  }

  @Test
  void getFreeSlotsForRoomReturnsSlots() {
    UUID roomId = UUID.randomUUID();
    LocalDate date = LocalDate.of(2026, 5, 31);
    List<FreeSlot> slots = List.of(new FreeSlot());
    Room room = buildRoom(roomId);

    when(roomService.getRoomById(roomId)).thenReturn(room);
    when(reserveService.getFreeSlotsForRoom(roomId, date)).thenReturn(slots);

    ResponseEntity<List<RoomAvailability>> response = roomController.getAvailableRooms(date, roomId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().size());
    RoomAvailability result = response.getBody().get(0);
    assertEquals(roomId, result.getRoomId());
    assertEquals("Sala 1", result.getName());
    assertEquals("Reuniao", result.getKind());
    assertEquals("Sala com tela", result.getDescription());
    assertSame(slots, result.getFreeSlots());
  }

  private static Room buildRoom() {
    return buildRoom(UUID.randomUUID());
  }

  private static Room buildRoom(UUID roomId) {
    Room room = new Room();
    room.setId(roomId);
    room.setName("Sala 1");
    room.setKind("Reuniao");
    room.setDescription("Sala com tela");
    return room;
  }
}
