package com.agent.ai.config;


import com.agent.context.UserContext;
import com.alibaba.cloud.ai.graph.RunnableConfig;



public class RunnableConfiguration {

    public static final RunnableConfig runnableConfig=RunnableConfig.builder()
            .threadId(UserContext.getUserId().toString())
            .build();
}
