package com.agent.ai.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class LangFuseConfig {

    @Value("${langfuse.base-url}")
    private String endpoint;

    @Value("${langfuse.secret-key}")
    private String secretKey;

    @Value("${langfuse.public-key}")
    private String publicKey;
}
