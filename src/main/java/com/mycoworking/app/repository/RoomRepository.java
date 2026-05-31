package com.mycoworking.app.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mycoworking.app.model.Room;

public interface RoomRepository extends JpaRepository<Room, UUID> {

}
