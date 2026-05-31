package com.mycoworking.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mycoworking.app.helpers.ReserveStatus;
import com.mycoworking.app.helpers.exception.ReserveNotFoundException;
import com.mycoworking.app.helpers.exception.TimeAlreadyBookedException;
import com.mycoworking.app.helpers.exception.TimeNotAllowedException;
import com.mycoworking.app.model.Reserve;
import com.mycoworking.app.model.Room;
import com.mycoworking.app.service.ReserveService;

@ExtendWith(MockitoExtension.class)
class ReserveControllerTest {

  @Mock
  private ReserveService reserveService;

  @InjectMocks
  private ReserveController reserveController;

  @Test
  void getAllReturnsReserves() {
    Reserve reserve = buildReserve(null, null);
    List<Reserve> reserves = List.of(reserve);

    when(reserveService.getAllReserves()).thenReturn(reserves);

    ResponseEntity<Iterable<Reserve>> response = reserveController.get();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(reserves, response.getBody());
  }

  @Test
  void getOneReturnsReserve() {
    UUID reserveId = UUID.randomUUID();
    Reserve reserve = buildReserve(reserveId, null);

    when(reserveService.getReserveById(reserveId)).thenReturn(reserve);

    ResponseEntity<Reserve> response = reserveController.getOne(reserveId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(reserve, response.getBody());
  }

  @Test
  void getOneWhenMissingThrows() {
    UUID reserveId = UUID.randomUUID();

    when(reserveService.getReserveById(reserveId)).thenThrow(new ReserveNotFoundException(reserveId));

    assertThrows(ReserveNotFoundException.class, () -> reserveController.getOne(reserveId));
  }

  @Test
  void createDelegatesToService() {
    UUID roomId = UUID.randomUUID();
    Reserve request = buildReserve(null, roomId);
    Reserve saved = buildReserve(UUID.randomUUID(), roomId);

    when(reserveService.createReserve(request, roomId)).thenReturn(saved);

    ResponseEntity<Reserve> response = reserveController.create(request);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertSame(saved, response.getBody());
    verify(reserveService).createReserve(request, roomId);
  }

  @Test
  void createWhenTimeAlreadyBookedThrows() {
    UUID roomId = UUID.randomUUID();
    Reserve request = buildReserve(null, roomId);

    when(reserveService.createReserve(request, roomId)).thenThrow(new TimeAlreadyBookedException());

    assertThrows(TimeAlreadyBookedException.class, () -> reserveController.create(request));
  }

  @Test
  void createWhenTimeNotAllowedThrows() {
    UUID roomId = UUID.randomUUID();
    Reserve request = buildReserve(null, roomId);

    when(reserveService.createReserve(request, roomId)).thenThrow(new TimeNotAllowedException());

    assertThrows(TimeNotAllowedException.class, () -> reserveController.create(request));
  }

  @Test
  void updateDelegatesToService() {
    Reserve request = buildReserve(UUID.randomUUID(), null);

    when(reserveService.updateReserve(request)).thenReturn(request);

    ResponseEntity<Reserve> response = reserveController.update(request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(request, response.getBody());
  }

  @Test
  void deleteDelegatesToService() {
    UUID reserveId = UUID.randomUUID();

    ResponseEntity<Void> response = reserveController.delete(reserveId);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(reserveService).deleteReserve(reserveId);
  }

  private static Reserve buildReserve(UUID reserveId, UUID roomId) {
    Room room = new Room();
    room.setName("Sala 1");
    room.setKind("Reuniao");
    room.setDescription("Sala com tela");

    if (roomId != null) {
      setId(room, roomId);
    }

    Reserve reserve = new Reserve();
    reserve.setRoom(room);
    reserve.setStartTime(dateTime("2026-05-31 09:00:00"));
    reserve.setEndTime(dateTime("2026-05-31 10:00:00"));
    reserve.setStatus(ReserveStatus.RESERVED);

    if (reserveId != null) {
      setId(reserve, reserveId);
    }

    return reserve;
  }

  private static void setId(Object target, UUID id) {
    try {
      Field field = target.getClass().getDeclaredField("id");
      field.setAccessible(true);
      field.set(target, id);
    } catch (NoSuchFieldException | IllegalAccessException ex) {
      throw new IllegalStateException("Could not set id", ex);
    }
  }

  private static OffsetDateTime dateTime(String value) {
    LocalDateTime local = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    return local.atOffset(ZoneOffset.ofHours(-3));
  }
}
