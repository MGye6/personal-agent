package com.agent.ai.config;


import com.agent.context.UserContext;
import com.alibaba.cloud.ai.graph.RunnableConfig;



public class RunnableConfiguration {

    /**
     * 动态构建 RunnableConfig。使用线程本地的 UserContext 获取当前用户 ID。
     * 请在请求线程中调用，以确保 UserContext 已正确设置。
     */
    public static RunnableConfig runnableConfig() {
        Long userId = UserContext.getUserId();
        String threadId = userId != null ? userId.toString() : "anonymous";
        return RunnableConfig.builder()
                .threadId(threadId)
                .build();
    }
}
