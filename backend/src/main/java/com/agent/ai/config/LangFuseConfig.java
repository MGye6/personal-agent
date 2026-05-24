package com.agent.ai.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class LangFuseConfig {

    @Value("${spring.ai.observation.langfuse.endpoint}")
    private String endpoint;

    @Value("${spring.ai.observation.langfuse.secret-key}")
    private String secretKey;

    @Value("${spring.ai.observation.langfuse.public-key}")
    private String publicKey;
}
