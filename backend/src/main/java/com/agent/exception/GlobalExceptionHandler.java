package com.agent.exception;

import com.agent.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ApiResponse.error(400, "参数校验失败");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Illegal argument: {}", e.getMessage());
        return ApiResponse.error(400, e.getMessage());
    }

    /**
     * 处理权限异常（来自 AOP 切面）
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<?> handleSecurityException(SecurityException e, HttpServletRequest request) {
        log.warn("Security exception: {}", e.getMessage());
        
        // 检查是否是SSE请求
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.contains("text/event-stream")) {
            // 对于SSE请求，返回错误事件
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.TEXT_EVENT_STREAM)
                    .body("event: error\ndata: {\"error\": \"" + e.getMessage() + "\"}\n\n");
        }
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(403, e.getMessage()));
    }

    /**
     * 处理 Spring Security 的访问拒绝异常
     * 降低日志级别为 WARN，避免干扰正常操作
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.warn("Access denied for {}: {} (This is expected for anonymous SSE reconnection attempts)", 
                request.getRequestURI(), e.getMessage());
        
        // 检查是否是SSE请求
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.contains("text/event-stream")) {
            // 对于SSE请求，静默关闭连接
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(403, "权限不足"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception e, HttpServletRequest request) {
        log.error("Unexpected error", e);
        
        // 检查是否是SSE请求
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.contains("text/event-stream")) {
            // 对于SSE请求，返回错误事件
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_EVENT_STREAM)
                    .body("event: error\ndata: {\"error\": \"服务器内部错误\"}\n\n");
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "服务器内部错误"));
    }
}
