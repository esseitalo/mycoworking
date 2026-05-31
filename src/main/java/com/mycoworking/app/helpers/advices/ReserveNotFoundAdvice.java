package com.mycoworking.app.helpers.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mycoworking.app.helpers.ErrorResponse;
import com.mycoworking.app.helpers.exception.ReserveNotFoundException;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestControllerAdvice
public class ReserveNotFoundAdvice {

  @ExceptionHandler(ReserveNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @Operation(
      summary = "Reserva nao encontrada",
      description = "Retorna o erro quando a reserva solicitada nao existe."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "404",
          description = "Reserva nao encontrada",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  public ResponseEntity<ErrorResponse> NotFoundHandler(ReserveNotFoundException ex) {
    return new ResponseEntity<>(
        new ErrorResponse(ex.getClass().getSimpleName(), ex.getMessage()),
        HttpStatus.NOT_FOUND
    );
  }

}