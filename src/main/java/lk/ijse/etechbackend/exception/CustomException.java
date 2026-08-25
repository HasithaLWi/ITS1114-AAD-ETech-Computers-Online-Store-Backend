package lk.ijse.etechbackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private final int status;
    private final String message;

    public CustomException(int status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public CustomException(HttpStatus httpStatus, String message) {
        super(message);
        this.status = httpStatus.value();
        this.message = message;
    }
}
