package app.mymemo.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Provides core A 401 Unauthorized Error.
 *
 * Author: Erkam Guresen
 */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedRequestException extends RuntimeException{
    public UnauthorizedRequestException() {super();}
    public UnauthorizedRequestException(String message, Throwable cause) {
        super(message, cause);
    }
    public UnauthorizedRequestException(String message) {
        super(message);
    }
    public UnauthorizedRequestException(Throwable cause) {
        super(cause);
    }
}
