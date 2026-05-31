package com.mycoworking.app.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.mycoworking.app.helpers.exception.RoomNotFoundException;
import com.mycoworking.app.model.Room;
import com.mycoworking.app.repository.RoomRepository;
import com.mycoworking.app.service.RoomService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RoomServiceIntegrationTest {

  @Autowired
  private RoomService roomService;

  @Autowired
  private RoomRepository roomRepository;

  @Test
  void createRoomPersists() {
    Room room = buildRoom("Sala 1", "Reuniao");

    Room saved = roomService.createRoom(room);

    assertNotNull(saved.getId());
  }

  @Test
  void getAllRoomsReturnsStoredRooms() {
    Room room = roomRepository.save(buildRoom("Sala 1", "Reuniao"));

    Iterable<Room> result = roomService.getAllRooms();

    List<Room> rooms = new ArrayList<>();
    result.forEach(rooms::add);

    assertEquals(1, rooms.size());
    assertEquals(room.getId(), rooms.get(0).getId());
  }

  @Test
  void getRoomByIdReturnsRoom() {
    Room room = roomRepository.save(buildRoom("Sala 1", "Reuniao"));

    Room result = roomService.getRoomById(room.getId());

    assertEquals(room.getId(), result.getId());
  }

  @Test
  void getRoomByIdWhenMissingThrows() {
    UUID roomId = UUID.randomUUID();

    assertThrows(RoomNotFoundException.class, () -> roomService.getRoomById(roomId));
  }

  @Test
  void deleteRoomRemovesRecord() {
    Room room = roomRepository.save(buildRoom("Sala 1", "Reuniao"));

    roomService.deleteRoom(room.getId());

    assertFalse(roomRepository.existsById(room.getId()));
  }

  private static Room buildRoom(String name, String kind) {
    Room room = new Room();
    room.setName(name);
    room.setKind(kind);
    room.setDescription("Sala com tela");
    return room;
  }
}
