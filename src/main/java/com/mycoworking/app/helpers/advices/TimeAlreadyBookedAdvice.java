package com.mycoworking.app.helpers.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mycoworking.app.helpers.ErrorResponse;
import com.mycoworking.app.helpers.exception.TimeAlreadyBookedException;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestControllerAdvice
public class TimeAlreadyBookedAdvice {

  @ExceptionHandler(TimeAlreadyBookedException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  @Operation(
      summary = "Horario ja reservado",
      description = "Retorna o erro quando o horario ja esta ocupado."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "409",
          description = "Horario ja reservado",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  public ResponseEntity<ErrorResponse> ConflictHandler(TimeAlreadyBookedException ex) {
    return new ResponseEntity<>(
        new ErrorResponse(ex.getClass().getSimpleName(), ex.getMessage()),
        HttpStatus.CONFLICT
    );
  }
}
