package com.itsci.mju.maebanjumpen.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
public class AlreadyReportedException extends RuntimeException {
    public AlreadyReportedException(String message) {
        super(message);
    }
}
