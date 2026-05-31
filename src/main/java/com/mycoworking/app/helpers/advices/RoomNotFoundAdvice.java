package com.mycoworking.app.helpers.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mycoworking.app.helpers.ErrorResponse;
import com.mycoworking.app.helpers.exception.RoomNotFoundException;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestControllerAdvice
public class RoomNotFoundAdvice {

  @ExceptionHandler(RoomNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @Operation(
      summary = "Sala nao encontrada",
      description = "Retorna o erro quando a sala solicitada nao existe."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "404",
          description = "Sala nao encontrada",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  public ResponseEntity<ErrorResponse> NotFoundHandler(RoomNotFoundException ex) {
    return new ResponseEntity<>(
        new ErrorResponse(ex.getClass().getSimpleName(), ex.getMessage()),
        HttpStatus.NOT_FOUND
    );
  }

}