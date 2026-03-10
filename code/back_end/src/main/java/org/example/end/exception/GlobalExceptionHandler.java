package org.example.end.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;

@RestControllerAdvice
// 全局异常处理器
public class GlobalExceptionHandler {


    // API异常处理器
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApi(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(error(ex.getStatus(), ex.getMessage()));
    }

    // 验证异常处理器
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = error(HttpStatus.BAD_REQUEST, "Validation failed");
        Map<String, String> fields = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fields.put(fe.getField(), fe.getDefaultMessage());
        }
        body.put("fields", fields);
        return ResponseEntity.badRequest().body(body);
    }

    // 其他异常处理器
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleOther(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }

    // 错误响应
    private static Map<String, Object> error(HttpStatus status, String message) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("status", status.value());
        m.put("error", status.getReasonPhrase());
        m.put("message", message);
        return m;
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleJsonParse(HttpMessageNotReadableException ex) {
        // 一般是 JSON 格式不对、枚举值不匹配、字段类型不对
        return ResponseEntity.badRequest()
                .body(error(HttpStatus.BAD_REQUEST, "参数解析失败：" + ex.getMostSpecificCause().getMessage()));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> handleMediaType(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity.badRequest()
                .body(error(HttpStatus.BAD_REQUEST, "Content-Type 不支持，请使用 application/json"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDb(DataIntegrityViolationException ex) {
        // 打印根因（Unknown column / Data too long / constraint fails 等）
        Throwable root = ex;
        while (root.getCause() != null) root = root.getCause();
        root.printStackTrace();

        return ResponseEntity.badRequest()
                .body(error(HttpStatus.BAD_REQUEST, "数据库保存失败：" + root.getMessage()));
    }
}


