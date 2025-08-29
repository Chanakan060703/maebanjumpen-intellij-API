package com.itsci.mju.maebanjumpen.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // กำหนดให้คืนค่า HTTP 409 Conflict เมื่อเกิด Exception นี้
public class DuplicateUsernameException extends RuntimeException {
  public DuplicateUsernameException(String message) {
    super(message);
  }
}
