package org.example.projectjavaservice.exception;

import org.example.projectjavaservice.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .data(null)
                        .build());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .data(null)
                        .build());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Object>> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .data(null)
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", "Validation failed");
        error.put("status", 400);

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));

        error.put("errors", fieldErrors);

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.builder()
                        .success(false)
                        .message("Internal server error")
                        .data(null)
                        .build());
    }

        // Bắt lỗi kết nối Cloudinary (UC-05)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleCloudError(RuntimeException ex) {
        // Chỉ bắt những lỗi có chứa chuỗi thông báo Cloud của chúng ta
        if (ex.getMessage().contains("Cloud storage service")) {
            Map<String, Object> error = new HashMap<>();
            error.put("timestamp", java.time.LocalDateTime.now());
            error.put("status", 503);
            error.put("error", "Service Unavailable");
            error.put("message", ex.getMessage()); // Lấy đúng câu tiếng Anh ở FileServiceImpl
            error.put("path", ""); // Có thể inject HttpServletRequest để lấy path
            return ResponseEntity.status(503).body(error);
        }
        // Các lỗi RuntimeException khác thì trả 500 bình thường
        return ResponseEntity.status(500).body(Map.of("error", "Internal Server Error"));
    }
}