package com.luca.intern.common.exception

import com.luca.intern.common.response.ErrorResponseMessage
import com.luca.intern.common.response.HttpResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
class RestExceptionHandler{

  @ExceptionHandler(value = [(BadRequestException::class)])
  protected fun badRequestException(
    ex: BadRequestException
  ): ResponseEntity<Any> {
    val apiError = ErrorResponseMessage(message = ex.message ?: "Bad Request")
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError)
  }

  @ExceptionHandler(value = [(NotFoundException::class)])
  protected fun notFoundException(
    ex: NotFoundException
  ): ResponseEntity<Any> {
    val apiError = ErrorResponseMessage(message = ex.message ?: "Not Found")
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError)
  }

  @ExceptionHandler(value = [(InternalServerException::class)])
  protected fun internalServerException(
    ex: InternalServerException
  ): ResponseEntity<Any> {
    val apiError = ErrorResponseMessage(message = ex.message ?: "Internal Server Error")
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError)
  }

  @ExceptionHandler(value = [(Exception::class)])
  protected fun exception(
    ex: Exception
  ): ResponseEntity<Any> {
    val apiError = ErrorResponseMessage(message = ex.message ?: "Internal Server Error")
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError)
  }

  @ExceptionHandler(value = [MethodArgumentNotValidException::class])
  protected fun handleMethodArgumentNotValid(
    ex: MethodArgumentNotValidException
  ): ResponseEntity<Any> {
    val errors = ex.bindingResult
      .fieldErrors
      .map { "${it.field}: ${it.defaultMessage ?: "required available"}" }

    val body = mutableMapOf<String, Any>("errors" to errors)

    val res = HttpResponse(
      false,
      "Invalid request",
      body
    )

    return ResponseEntity.badRequest().body(res)
  }
}