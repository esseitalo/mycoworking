package com.mycoworking.app.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycoworking.app.model.Reserve;
import com.mycoworking.app.service.ReserveService;
import com.mycoworking.app.helpers.ErrorResponse;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Reservas", description = "Endpoints para gerenciamento de reservas de salas no coworking")
@RestController
@RequestMapping("/api/reserves")
public class ReserveController {

  @Autowired
  private ReserveService reserveService;
  
  @GetMapping("/")
  @Operation(summary = "Listar reservas", description = "Retorna todas as reservas cadastradas.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Lista de reservas retornada com sucesso")
  })
  public ResponseEntity<Iterable<Reserve>> get() {
    return ResponseEntity.ok(reserveService.getAllReserves());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Buscar reserva", description = "Retorna os detalhes de uma reserva pelo ID.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Reserva encontrada"),
      @ApiResponse(
        responseCode = "404",
        description = "Reserva nao encontrada",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  public ResponseEntity<Reserve> getOne(
      @Parameter(description = "ID da reserva", required = true) @PathVariable() UUID id) {
    return ResponseEntity.ok(reserveService.getReserveById(id));
  }

  @PostMapping("/")
  @Operation(summary = "Criar reserva", description = "Cria uma nova reserva para uma sala.")
    @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Reserva criada"),
      @ApiResponse(
        responseCode = "400",
        description = "Dados invalidos",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Sala nao encontrada",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "409",
        description = "Horario ja reservado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
    public ResponseEntity<Reserve> create(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(
          mediaType = "application/json",
          examples = @ExampleObject(
            name = "CriarReserva",
            value = "{\n" +
              "  \"room\": {\n" +
              "    \"id\": \"fb8604b2-a4dd-49ba-a9a5-7e5e51a4cec7\"\n" +
              "  },\n" +
              "  \"startTime\": \"2026-06-10T10:00:00-03:00\",\n" +
              "  \"endTime\": \"2026-06-10T11:00:00-03:00\"\n" +
              "}"
          )
        )
      )
      @Valid @RequestBody Reserve reserve) {
    return ResponseEntity.status(201)
        .body(reserveService.createReserve(reserve, reserve.getRoom().getId()));
  }

  @PutMapping("/")
  @Operation(summary = "Atualizar reserva", description = "Atualiza os dados de uma reserva.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Reserva atualizada"),
      @ApiResponse(
        responseCode = "400",
        description = "Dados invalidos",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Reserva nao encontrada",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  public ResponseEntity<Reserve> update(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(
          mediaType = "application/json",
          examples = @ExampleObject(
            name = "AtualizarReserva",
            value = "{\n" +
              "  \"room\": {\n" +
              "    \"id\": \"fb8604b2-a4dd-49ba-a9a5-7e5e51a4cec7\"\n" +
              "  },\n" +
              "  \"startTime\": \"2026-06-10T10:00:00-03:00\",\n" +
              "  \"endTime\": \"2026-06-10T11:00:00-03:00\"\n" +
              "}"
          )
        )
      )
      @Valid @RequestBody Reserve reserve) {
    return ResponseEntity.ok(reserveService.updateReserve(reserve));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Excluir reserva", description = "Remove uma reserva pelo ID.")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Reserva removida"),
      @ApiResponse(
        responseCode = "404",
        description = "Reserva nao encontrada",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  public ResponseEntity<Void> delete(
      @Parameter(description = "ID da reserva", required = true) @PathVariable() UUID id) {
    reserveService.deleteReserve(id);
    return ResponseEntity.noContent().build();
  }
}
