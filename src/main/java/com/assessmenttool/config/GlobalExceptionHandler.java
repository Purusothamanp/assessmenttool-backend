package com.assessmenttool.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "MAX_UPLOAD_SIZE_EXCEEDED");
        response.put("message", "File size exceeds the maximum allowed limit (50MB). Please upload a smaller file.");
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception exc) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "SERVER_ERROR");
        response.put("message", exc.getMessage() != null ? exc.getMessage() : "An unexpected server error occurred.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
