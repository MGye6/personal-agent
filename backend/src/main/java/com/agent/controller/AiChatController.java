package com.agent.controller;

import com.agent.dto.request.ChatRequest;
import com.agent.dto.response.ApiResponse;
import com.agent.dto.response.ChatResponse;
import com.agent.service.AiChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
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
        ChatResponse response = aiChatService.processNaturalLanguageQuery(message);
        return ApiResponse.success(response);
    }
}
