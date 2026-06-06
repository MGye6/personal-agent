package com.agent.ai.config;

import com.agent.tools.ApplicationTools;
import com.agent.tools.CompanyTools;
import com.agent.tools.InterviewRecordTools;
import com.agent.tools.InterviewScheduleTools;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.redis.RedisSaver;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.ai.dashscope.api-key", matchIfMissing = false)
public class AgentConfig {
    private final ApplicationTools applicationTools;
    private final CompanyTools companyTools;
    private final InterviewScheduleTools interviewScheduleTools;
    private final InterviewRecordTools interviewRecordTools;
    private final ObservationRegistry observationRegistry;

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    @Value("${spring.ai.dashscope.model:qwen-plus}")
    private String model;

    @Value("${spring.ai.dashscope.temperature:0.7}")
    private Double temperature;

    @Value("${spring.ai.dashscope.max-tokens:2000}")
    private Integer maxTokens;

    @Value("${spring.ai.dashscope.top-p:0.9}")
    private Double topP;

    @Bean
    public ReactAgent reactAgent(RedissonClient redisson){
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(apiKey)
                .build();

        ChatModel chatModel = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .defaultOptions(DashScopeChatOptions.builder()
                        .model(model)
                        .temperature(temperature)
                        .maxToken(maxTokens)
                        .topP(topP)
                        .build())
                .build();

        RedisSaver redisSaver = RedisSaver.builder()
                .redisson(redisson)
                .build();

        return ReactAgent.builder()
                .name("reactAgent")
                .model(chatModel)
                .methodTools(companyTools, applicationTools, interviewRecordTools, interviewScheduleTools)
                .saver(redisSaver)
                .build();
    }
}
