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

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AgentConfig {
    private final ApplicationTools applicationTools;
    private final CompanyTools companyTools;
    private final InterviewScheduleTools interviewScheduleTools;
    private final InterviewRecordTools interviewRecordTools;
    private final ObservationRegistry observationRegistry;

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;
    
    @Value("${spring.ai.dashscope.model}")
    private String model;
    
    @Value("${spring.ai.dashscope.temperature}")
    private Double temperature;
    
    @Value("${spring.ai.dashscope.max-tokens}")
    private Integer maxTokens;
    
    @Value("${spring.ai.dashscope.top-p}")
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



        // 创建 RedisSaver，使用默认配置
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
