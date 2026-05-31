package com.mycoworking.app.helpers;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ErrorResponse", description = "Modelo padrao de erro da API")
public class ErrorResponse {

  @Schema(description = "Nome curto do erro", example = "ExampleException")
  private String error;

  @Schema(description = "Mensagem descritiva do erro", example = "Example error message")
  private String message;

  public ErrorResponse() {
  }

  public ErrorResponse(String error, String message) {
    this.error = error;
    this.message = message;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
