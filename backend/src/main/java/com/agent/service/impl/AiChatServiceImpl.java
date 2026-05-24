package com.agent.service.impl;

import com.agent.dto.request.ChatRequest;
import com.agent.dto.response.ChatResponse;
import com.agent.service.AiChatService;
import com.agent.tools.ApplicationTools;
import com.agent.tools.CompanyTools;
import com.agent.tools.InterviewRecordTools;
import com.agent.tools.InterviewScheduleTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.net.SocketTimeoutException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {

    private final ChatModel chatModel;
    private final CompanyTools companyTools;
    private final ApplicationTools applicationTools;
    private final InterviewRecordTools interviewRecordTools;
    private final InterviewScheduleTools interviewScheduleTools;

    private static final String SYSTEM_PROMPT = """
            你是一个个人求职助手Agent，帮助用户管理暑期实习投递和面试安排。
                
            你可以执行以下操作：
            1. 查询公司列表、投递记录、面试安排
            2. 添加新公司
            3. 添加投递记录
            4. 添加面试安排
            5. 更新状态和记录
                
            重要操作指南：
            - 查询数据时尽量使用分页方法（getCompaniesByPage、getApplicationsByPage），不要一次性获取所有数据
            - 监听用户的需求，需要更多数据时发起下一页的请求
                
            当用户用自然語言提出请求时，你需要：
            1. 理解用户意图
            2. 自动调用相应的工具函数达成目标
            3. 将返回的结果用自然語言总结后回复用户
                
            请用中文与用户交流，提供友好、专业的帮助。
            """;
        

    @Override
    public ChatResponse chat(ChatRequest request) {
        return processNaturalLanguageQuery(request.getMessage());
    }

    @Override
    public ChatResponse processNaturalLanguageQuery(String message) {
        try {
            ChatClient chatClient = ChatClient.builder(chatModel)
                    .defaultTools(companyTools, applicationTools, interviewRecordTools, interviewScheduleTools)
                    .defaultSystem(SYSTEM_PROMPT)
                    .build();

            String reply = chatClient.prompt()
                    .user(message)
                    .call()
                    .content();
            log.info("AI Response: {}", reply);

            return ChatResponse.builder()
                    .reply(reply)
                    .conversationId(UUID.randomUUID().toString())
                    .build();
        } catch (Exception e) {
            log.error("AI processing error", e);
            
            // 分类处理异常
            String errorMessage;
            if (e.getCause() instanceof SocketTimeoutException || e.getMessage() != null && e.getMessage().contains("timeout")) {
                errorMessage = "AI服务响应超时，请稍后再试。";
            } else if (e.getMessage() != null && e.getMessage().contains("Connection refused")) {
                errorMessage = "无法连接到AI服务，请检查网络后重试。";
            } else {
                errorMessage = "抱歉，AI服务暂时不可用，请稍后再试。";
            }
            
            return ChatResponse.builder()
                    .reply(errorMessage)
                    .conversationId(UUID.randomUUID().toString())
                    .build();
        }
    }
}
