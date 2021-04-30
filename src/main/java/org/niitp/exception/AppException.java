package org.niitp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class AppException extends RuntimeException {
    public AppException(String message) {
        super(message);
    }
}
