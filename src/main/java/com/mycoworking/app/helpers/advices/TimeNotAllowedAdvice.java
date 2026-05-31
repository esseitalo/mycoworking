package com.mycoworking.app.helpers.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mycoworking.app.helpers.ErrorResponse;
import com.mycoworking.app.helpers.exception.TimeNotAllowedException;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestControllerAdvice
public class TimeNotAllowedAdvice {

  @ExceptionHandler(TimeNotAllowedException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @Operation(
      summary = "Horario nao permitido",
      description = "Retorna o erro quando o horario esta fora do permitido."
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "400",
          description = "Horario nao permitido",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  public ResponseEntity<ErrorResponse> BadRequestHandler(TimeNotAllowedException ex) {
    return new ResponseEntity<>(
        new ErrorResponse(ex.getClass().getSimpleName(), ex.getMessage()),
        HttpStatus.BAD_REQUEST
    );
  }
}
