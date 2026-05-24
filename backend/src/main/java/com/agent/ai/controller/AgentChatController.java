package com.agent.ai.controller;

import com.agent.ai.service.AgentChatService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
@Tag(name = "Agent聊天", description = "与Agent助手的聊天接口")
@SecurityRequirement(name = "Bearer Authentication")
public class AgentChatController {
    private final AgentChatService agentChatService;

    @PostMapping("/chat")
    public String chat(@RequestParam String message) {
        return agentChatService.chat(message);
    }
}
