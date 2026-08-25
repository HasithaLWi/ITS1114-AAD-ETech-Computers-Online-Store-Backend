package lk.ijse.etechbackend.exception;

import lk.ijse.etechbackend.dto.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    public CommonResponse handleServerException(CustomException ex, WebRequest request) {
        ex.printStackTrace();
        return new CommonResponse(500, "UNEXPECTED_ERROR");
    }

    @ExceptionHandler(value = {CustomException.class})
    public ResponseEntity<CommonResponse> handleCustomException(CustomException ex, WebRequest request) {
        ex.printStackTrace();
        return new ResponseEntity<>(new CommonResponse(ex.getStatus(),
                ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
