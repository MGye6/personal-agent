package com.agent.controller;

import com.agent.context.UserContext;
import com.agent.dto.request.ChatRequest;
import com.agent.dto.response.ApiResponse;
import com.agent.dto.response.ChatResponse;
import com.agent.service.AiChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ForkJoinPool;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "AI聊天", description = "与AI助手的聊天接口")
@SecurityRequirement(name = "Bearer Authentication")
public class AiChatController {

    private final AiChatService aiChatService;

    @PostMapping
    @Operation(summary = "AI聊天", description = "与AI助手进行自然语言对话")
    public ApiResponse<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        ChatResponse response = aiChatService.chat(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/query")
    @Operation(summary = "AI查询", description = "直接通过查询参数与AI助手对话")
    public ApiResponse<ChatResponse> query(@RequestParam String message) {
        log.info("Received query request: {}", message);
        ChatResponse response = aiChatService.processNaturalLanguageQuery(message);
        return ApiResponse.success(response);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "AI流式聊天", description = "SSE流式响应，逐字返回AI回复")
    public SseEmitter streamChat(@RequestParam String message) {
        log.info("Received stream request: {}", message);
        
        // 1. 获取当前用户（此时在主线程/Tomcat 线程，ThreadLocal 有效）
        Long userId = UserContext.getUserId();
        if (userId == null) {
            log.warn("Anonymous access attempt to /api/chat/stream");
            // 创建一个立即关闭的 emitter
            SseEmitter emitter = new SseEmitter(1000L);
            emitter.complete();
            return emitter;
        }
        
        log.debug("Stream chat authorized for user: {}", userId);
        
        // 2. 创建 SseEmitter，设置超时时间（60秒）
        SseEmitter emitter = new SseEmitter(60000L);
        
        // 配置超时与错误回调
        emitter.onTimeout(() -> {
            log.warn("SSE connection timeout for user: {}", userId);
            emitter.complete();
        });
        emitter.onError((ex) -> {
            log.error("SSE connection error for user: {}", userId, ex);
            emitter.complete();
        });
        emitter.onCompletion(() -> {
            log.info("SSE connection completed for user: {}", userId);
        });

        // 3. 显式将需要用到的安全上下文/用户数据作为"快照"传给异步任务
        ForkJoinPool.commonPool().submit(() -> {
            try {
                log.debug("Starting AI stream query for user: {}, message length: {}", userId, message.length());
                
                // 调用 AI 服务的流式处理方法
                aiChatService.streamProcessNaturalLanguageQuery(message, emitter);
                
            } catch (Exception e) {
                log.error("Unexpected error in SSE stream for user: {}", userId, e);
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("{\"error\": \"服务器内部错误\"}"));
                } catch (Exception ex) {
                    log.error("Failed to send error message", ex);
                }
                emitter.completeWithError(e);
            }
        });

        // 4. 主线程立即返回 emitter 对象，连接保持开放
        return emitter;
    }
}
