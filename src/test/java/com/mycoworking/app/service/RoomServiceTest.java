package com.mycoworking.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.mycoworking.app.helpers.exception.RoomNotFoundException;
import com.mycoworking.app.model.Room;
import com.mycoworking.app.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

  @Mock
  private RoomRepository roomRepository;

  @InjectMocks
  private RoomService roomService;

  @Test
  void createRoomDelegatesToRepository() {
    Room room = buildRoom(null);

    when(roomRepository.save(room)).thenReturn(room);

    assertEquals(room, roomService.createRoom(room));
  }

  @Test
  void getAllRoomsReturnsRepositoryResult() {
    List<Room> rooms = List.of(buildRoom(UUID.randomUUID()));

    when(roomRepository.findAll()).thenReturn(rooms);

    assertEquals(rooms, roomService.getAllRooms());
  }

  @Test
  void getRoomByIdWhenMissingThrows() {
    UUID roomId = UUID.randomUUID();

    when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

    assertThrows(RoomNotFoundException.class, () -> roomService.getRoomById(roomId));
  }

  @Test
  void updateRoomDelegatesToRepository() {
    Room room = buildRoom(UUID.randomUUID());

    when(roomRepository.save(room)).thenReturn(room);

    assertEquals(room, roomService.updateRoom(room));
  }

  @Test
  void deleteRoomDelegatesToRepository() {
    UUID roomId = UUID.randomUUID();

    roomService.deleteRoom(roomId);

    verify(roomRepository).deleteById(roomId);
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
}
