package com.mycoworking.app.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycoworking.app.dto.FreeSlot;
import com.mycoworking.app.dto.RoomAvailability;
import com.mycoworking.app.model.Room;
import com.mycoworking.app.service.ReserveService;
import com.mycoworking.app.service.RoomService;
import com.mycoworking.app.helpers.ErrorResponse;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Salas", description = "Endpoints para gerenciamento de salas no coworking")
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

  @Autowired
  private RoomService roomService;

  @Autowired
  private ReserveService reserveService;

  @GetMapping("/")
  @Operation(summary = "Listar salas", description = "Retorna todas as salas cadastradas.")
  @ApiResponses({
      @ApiResponse(
        responseCode = "200", 
        description = "Lista de salas retornada com sucesso",
        content = @Content(array = @ArraySchema(arraySchema = @Schema(implementation = Room.class)))
      )
  })
  public ResponseEntity<Iterable<Room>> get() {
    return ResponseEntity.ok(roomService.getAllRooms());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Buscar sala", description = "Retorna os detalhes de uma sala pelo ID.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Sala encontrada"),
      @ApiResponse(
        responseCode = "404",
        description = "Sala nao encontrada",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  public ResponseEntity<Room> getOne(
      @Parameter(description = "ID da sala", required = true) @PathVariable() UUID id) {
    return ResponseEntity.ok(roomService.getRoomById(id));
  }

  @PostMapping("/")
  @Operation(summary = "Criar sala", description = "Cadastra uma nova sala.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Sala criada"),
      @ApiResponse(
        responseCode = "400",
        description = "Dados invalidos",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  public ResponseEntity<Room> create(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(
          mediaType = "application/json",
          examples = @ExampleObject(
            name = "CriarSala",
            value = "{\n" +
              "  \"name\": \"Sala Ocean\",\n" +
              "  \"kind\": \"MEETING\",\n" +
              "  \"description\": \"Sala com projetor e quadro branco\"\n" +
              "}"
          )
        )
      )
      @Valid @RequestBody Room room) {
    return ResponseEntity.created(null).body(roomService.createRoom(room));
  }

  @PutMapping("/")
  @Operation(summary = "Atualizar sala", description = "Atualiza os dados de uma sala.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Sala atualizada"),
      @ApiResponse(
        responseCode = "400",
        description = "Dados invalidos",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Sala nao encontrada",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  public ResponseEntity<Room> update(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(
          mediaType = "application/json",
          examples = @ExampleObject(
            name = "AtualizarSala",
            value = "{\n" +
              "  \"id\": \"fb8604b2-a4dd-49ba-a9a5-7e5e51a4cec7\",\n" +
              "  \"name\": \"Sala Ocean\",\n" +
              "  \"kind\": \"MEETING\",\n" +
              "  \"description\": \"Sala com projetor e quadro branco\"\n" +
              "}"
          )
        )
      )
      @Valid @RequestBody Room room) {
    return ResponseEntity.ok(roomService.updateRoom(room));
  }

  @GetMapping("/free-slots")
  @Operation(
    summary = "Salas livres",
    description = "Retorna salas com horários livres em um dia. Opcionalmente filtra por sala."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Salas livres retornadas"),
      @ApiResponse(
        responseCode = "404",
        description = "Sala nao encontrada",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  public ResponseEntity<List<RoomAvailability>> getAvailableRooms(
      @Parameter(
        description = "Data no formato dd/MM/aaaa",
        example = "31/05/2026"
      )
      @RequestParam("date") @DateTimeFormat(pattern = "dd/MM/aaaa") LocalDate date,
      @Parameter(description = "ID da sala (opcional)")
      @RequestParam(value = "roomId", required = false) UUID roomId) {
    if (roomId == null) {
      return ResponseEntity.ok(reserveService.getAvailableRooms(date));
    }

    Room room = roomService.getRoomById(roomId);
    List<FreeSlot> freeSlots = reserveService.getFreeSlotsForRoom(roomId, date);
    List<RoomAvailability> availability = new ArrayList<>();

    if (!freeSlots.isEmpty()) {
      availability.add(new RoomAvailability(
          room.getId(),
          room.getName(),
          room.getKind(),
          room.getDescription(),
          freeSlots
      ));
    }

    return ResponseEntity.ok(availability);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Excluir sala", description = "Remove uma sala pelo ID.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Sala removida"),
      @ApiResponse(
        responseCode = "404",
        description = "Sala nao encontrada",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  public ResponseEntity<Void> delete(
      @Parameter(description = "ID da sala", required = true) @PathVariable() UUID id) {
    roomService.deleteRoom(id);
    return ResponseEntity.noContent().build();
  }
}
