package mx.com.invex.employee_invex.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import mx.com.invex.employee_invex.dto.ApiResult;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResult<Object>> handleApiException(ApiException ex) {

        ApiResult<Object> response = new ApiResult<>(
                ex.getStatus(),
                ex.getMessage(),
                null);

        return ResponseEntity.status(ex.getStatus()).body(response);
    }
}
