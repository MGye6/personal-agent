package com.agent.ai.service;

import org.springframework.ai.chat.messages.AssistantMessage;

public interface AgentChatService {

    String chat(String message);

}
