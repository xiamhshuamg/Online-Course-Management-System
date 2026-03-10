package org.example.end.exception;

import org.springframework.http.HttpStatus;

// API异常
public class ApiException extends RuntimeException {
    private final HttpStatus status;
  
    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
    // 获取状态
    public HttpStatus getStatus() {
        return status;
    }
}


