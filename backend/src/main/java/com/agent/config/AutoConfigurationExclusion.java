package com.agent.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置排除配置
 * 用于排除可能导致 factoryBeanObjectType 冲突的自动配置类
 */
@Configuration
public class AutoConfigurationExclusion {
    // 此类可以扩展以添加更多的配置排除规则
}
