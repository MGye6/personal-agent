package com.agent.service;

import com.agent.dto.request.ChatRequest;
import com.agent.dto.response.ChatResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AiChatService {

    ChatResponse chat(ChatRequest request);

    ChatResponse processNaturalLanguageQuery(String message);

    /**
     * 流式处理自然语言查询
     * @param message 用户消息
     * @param emitter SSE 发射器
     */
    void streamProcessNaturalLanguageQuery(String message, SseEmitter emitter);
}
