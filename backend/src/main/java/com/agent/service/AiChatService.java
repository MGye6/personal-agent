package com.agent.service;

import com.agent.dto.request.ChatRequest;
import com.agent.dto.response.ChatResponse;

public interface AiChatService {

    ChatResponse chat(ChatRequest request);

    ChatResponse processNaturalLanguageQuery(String message);
}
