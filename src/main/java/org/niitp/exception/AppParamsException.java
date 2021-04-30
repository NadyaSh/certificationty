package org.niitp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class AppParamsException extends RuntimeException {
    public AppParamsException(String message) {
        super(message);
    }
}
