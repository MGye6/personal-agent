package com.agent;

import com.agent.dto.request.ChatRequest;
import com.agent.dto.response.ChatResponse;
import com.agent.service.AiChatService;
import com.agent.tools.ApplicationTools;
import com.agent.tools.CompanyTools;
import com.agent.tools.InterviewRecordTools;
import com.agent.tools.InterviewScheduleTools;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.ai.dashscope.api-key=sk-265d535de5124a2dbe6d1ed1600538d5",
    "spring.ai.dashscope.model=qwen-plus",
    "spring.datasource.url=jdbc:mysql://localhost:3306/personal_agent?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true",
    "spring.datasource.username=root",
    "spring.datasource.password=1234"
})
public class AiChatServiceTest {

    @Autowired
    private AiChatService aiChatService;

    @Test
    public void testChat() {
        ChatRequest request = ChatRequest.builder()
                .message("帮我查看所有公司")
                .build();

        ChatResponse response = aiChatService.chat(request);
        System.out.println("AI回复: " + response.getReply());
        System.out.println("会话ID: " + response.getConversationId());
    }

    @Test
    public void testQueryApplications() {
        ChatRequest request = ChatRequest.builder()
                .message("查看我的投递记录")
                .build();

        ChatResponse response = aiChatService.chat(request);
        System.out.println("AI回复: " + response.getReply());
    }

    @Test
    public void testAddCompany() {
        ChatRequest request = ChatRequest.builder()
                .message("添加一个新公司：字节跳动，官网是https://jobs.bytedance.com，行业是互联网")
                .build();

        ChatResponse response = aiChatService.chat(request);
        System.out.println("AI回复: " + response.getReply());
    }
}
