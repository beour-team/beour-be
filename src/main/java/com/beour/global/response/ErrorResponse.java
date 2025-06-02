package com.beour.global.response;

import lombok.Getter;

@Getter
public class ErrorResponse {

  private final int code;
  private final String codeName;
  private final String message;

  public ErrorResponse(int code, String codeName, String message) {
    this.code = code;
    this.codeName = codeName;
    this.message = message;
  }
}
