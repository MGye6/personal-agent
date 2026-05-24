package com.agent.ai.service.impl;

import com.agent.ai.service.AgentChatService;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.agent.ai.config.RunnableConfiguration.runnableConfig;

@Service
@RequiredArgsConstructor
public class AgentChatServiceImpl implements AgentChatService {

    private final ReactAgent agent;

    @Override
    public String chat(String message) {
        try {
            return agent.call(message,runnableConfig).getText();
        } catch (GraphRunnerException e) {
            throw new RuntimeException(e);
        }
    }
}
