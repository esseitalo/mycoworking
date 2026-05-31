package com.mycoworking.app.service;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mycoworking.app.helpers.exception.RoomNotFoundException;
import com.mycoworking.app.model.Room;
import com.mycoworking.app.repository.RoomRepository;

@Service
public class RoomService {

  @Autowired
  private RoomRepository RoomRepository;

  public Room createRoom(Room Room) {
    return RoomRepository.save(Room);
  }

  public Iterable<Room> getAllRooms() {
    return RoomRepository.findAll();
  }

  public Room getRoomById(UUID id) {
    return RoomRepository.findById(id).orElseThrow(() -> new RoomNotFoundException(id));
  }

  public void deleteRoom(UUID id) {
    RoomRepository.deleteById(id);
  }

  public Room updateRoom(Room Room) {
    return RoomRepository.save(Room);
  }
}
