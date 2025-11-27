package com.luca.intern.common.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

data class ErrorResponseMessage(
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
  val timestamp: Date = Date(),
  var message: String
)